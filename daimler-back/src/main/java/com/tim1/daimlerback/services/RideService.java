package com.tim1.daimlerback.services;

import com.tim1.daimlerback.dtos.common.*;
import com.tim1.daimlerback.dtos.panic.PanicListDTO;
import com.tim1.daimlerback.dtos.panic.PanicRideDTO;
import com.tim1.daimlerback.dtos.passenger.PassengerShortDTO;
import com.tim1.daimlerback.dtos.ride.CreateRideDTO;
import com.tim1.daimlerback.dtos.ride.CreatedRideDTO;
import com.tim1.daimlerback.entities.*;
import com.tim1.daimlerback.entities.enumeration.ERole;
import com.tim1.daimlerback.repositories.*;
import com.tim1.daimlerback.utils.SweepLineEvent;
import com.tim1.daimlerback.utils.TaskExecutor;
import com.tim1.daimlerback.utils.TimestampProvider;
import com.tim1.daimlerback.utils.WebsocketMessage;
import com.tim1.daimlerback.websockets.AndroidSocketHandler;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RideService {

    @Autowired
    AndroidSocketHandler androidSocketHandler;
    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    IRideRepository rideRepository;
    @Autowired
    IDriverRepository driverRepository;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    IRejectionRepository rejectionRepository;
    @Autowired
    IPassengerRepository passengerRepository;
    @Autowired
    IVehicleMovementRepository vehicleMovementRepository;
    @Autowired
    ILocationRepository locationRepository;
    @Autowired
    EstimationService estimationService;
    @Autowired
    DriverService driverService;
    @Autowired
    VehicleService vehicleService;
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private IMessageRepository messageRepository;
    @Autowired
    private IFavoriteRouteRepository routeRepository;
    @Autowired
    private TimestampProvider timestampProvider;
    @Autowired
    private TaskExecutor taskExecutor;


    private boolean isDriverApplicable(Driver driver, CreateRideDTO dto) {
        return (dto.getBabyTransport() ? driver.getVehicle().getBabyTransport() : true)
                && (dto.getPetTransport() ? driver.getVehicle().getPetTransport() : true)
                && driver.getVehicle().getVehicleType().equals(dto.getVehicleType())
                && driver.getVehicle().getPassengerSeats() >= dto.getPassengers().size()
                && !driver.getBusy() && driverService.getTodayWorkingHours(driver) < 8.0;
    }

    private Integer driverDistance(Driver driver, CreateRideDTO dto) {
        Location driverLocation = driver.getVehicle().getCurrentLocation();
        LocationDTO departureLocation = dto.getLocations().get(0).getDeparture();
        DepartureDestinationDTO route = new DepartureDestinationDTO(new LocationDTO(driverLocation), departureLocation);
        try {
            EstimationDTO estimation = estimationService.osrmRequest(route);
            return estimation.getEstimatedTimeInMinutes();
        } catch(ResponseStatusException exception) {
            return 30;
        }
    }

    private int calculateMaxOverlap(List<Ride> rides) {
        List<SweepLineEvent> events = new ArrayList<>();
        long minutesBefore = 10 * 60 * 1000;
        for (Ride ride : rides) {
            if (ride.getScheduledTimestamp() != null && ride.getEstimatedTimeInMinutes() != null) {
                events.add(new SweepLineEvent(ride.getScheduledTimestamp() - minutesBefore, 1));
                events.add(new SweepLineEvent(ride.getScheduledTimestamp() + (long) (ride.getEstimatedTimeInMinutes() + 5) * 60 * 1000, -1));
            }
        }
        events.sort(SweepLineEvent::compareTo);
        int balance = 0;
        int maxOverlap = 0;
        for (SweepLineEvent event : events) {
            balance += event.getAdd();
            maxOverlap = Math.max(maxOverlap, balance);
        }
        return maxOverlap;
    }

    private int calculateReservedDrivers() {
        List<Ride> pendingRides = rideRepository.findByStatus("PENDING");
        List<Ride> acceptedRides = rideRepository.findByStatus("ACCEPTED");
        List<Ride> ongoingRides = rideRepository.findByStatus("ACTIVE");
        int busyDrivers = acceptedRides.size() + ongoingRides.size();
        List<Ride> rides = new ArrayList<Ride>();
        rides.addAll(pendingRides);
        rides.addAll(acceptedRides);
        rides.addAll(ongoingRides);
        return calculateMaxOverlap(rides) - busyDrivers;
    }

    private Driver findApplicableDriver(CreateRideDTO dto, boolean isScheduled) {
        System.out.println("Finding possible drivers");
        List<Driver> possibleDrivers = driverRepository.findAllByIsBlockedAndIsActive(false, true);
        System.out.println("Found " + possibleDrivers.size() + " drivers");
        possibleDrivers = possibleDrivers.stream().filter(driver -> isDriverApplicable(driver, dto)).collect(Collectors.toList());
        System.out.println("Applicable are " + possibleDrivers.size() + " drivers");
        possibleDrivers.sort((driver1, driver2) -> driverDistance(driver1, dto).compareTo(driverDistance(driver2, dto)));
        System.out.println("Sorted drivers");
        if (!isScheduled) {
            int reservedDrivers = calculateReservedDrivers();
            if (possibleDrivers.size() > reservedDrivers) {
                return possibleDrivers.get(0);
            }
        } else {
            if (possibleDrivers.size() > 0) {
                return possibleDrivers.get(0);
            }
        }
        return null;
    }

    private void assignDriverToRide(Ride ride, Driver driver) {
        CreatedRideDTO rideDTO = new CreatedRideDTO(ride);
        CreateRideDTO dto = new CreateRideDTO();
        dto.setBabyTransport(rideDTO.getBabyTransport());
        dto.setLocations(rideDTO.getLocations());
        dto.setScheduledTimestamp(rideDTO.getScheduledTimestamp());
        dto.setPetTransport(rideDTO.getPetTransport());
        dto.setVehicleType(rideDTO.getVehicleType());
        dto.setPassengers(ride.getPassengers().stream().map(PassengerShortDTO::new).collect(Collectors.toList()));

        ride.setDriver(driver);
        ride.setStatus("ACCEPTED");
        driver.setBusy(true);
        driverRepository.save(driver);

        ArrayList<DepartureDestinationDTO> route = new ArrayList<DepartureDestinationDTO>();
        Location vehicleLocation = driver.getVehicle().getCurrentLocation();
        Location departure = new Location(dto.getLocations().get(0).getDeparture());
        route.add(new DepartureDestinationDTO(vehicleLocation, departure));

        AssumptionDTO assumptionDto = new AssumptionDTO();
        assumptionDto.setBabyTransport(dto.getBabyTransport());
        assumptionDto.setPetTransport(dto.getPetTransport());
        assumptionDto.setVehicleType(dto.getVehicleType());
        assumptionDto.setLocations(route);

        EstimationDTO estimationDto = estimationService.getEstimate(assumptionDto);
        ride.setDistance(estimationDto.getDistance());

        rideRepository.save(ride);
        makeVehicleMovement(ride);
        System.out.println("=============================================SENT A MESSAGE TO: " + driver.getId());
        rideUpdated(ride);
    }

    private void findDriverForScheduledRide(Integer id) {
        System.out.println("Finding driver for scheduled ride!");
        Ride ride = rideRepository.findById(id).get();

        if (!ride.getStatus().equals("PENDING")) {
            System.out.println("Ride is not pending!");
            return;
        }

        CreatedRideDTO rideDTO = new CreatedRideDTO(ride);
        CreateRideDTO dto = new CreateRideDTO();
        dto.setBabyTransport(rideDTO.getBabyTransport());
        dto.setLocations(rideDTO.getLocations());
        dto.setScheduledTimestamp(rideDTO.getScheduledTimestamp());
        dto.setPetTransport(rideDTO.getPetTransport());
        dto.setVehicleType(rideDTO.getVehicleType());
        dto.setPassengers(ride.getPassengers().stream().map(PassengerShortDTO::new).collect(Collectors.toList()));

        Driver driver = findApplicableDriver(dto, true);

        if (driver != null) {
            System.out.println("Found driver " + driver.getEmail() + " id " + driver.getId());
            assignDriverToRide(ride, driver);
        } else {
            System.out.println("Driver not found :(");
            // Driver not found, try again in 5 minutes
            taskExecutor.schedule(() -> findDriverForScheduledRide(ride.getId()), 5, TimeUnit.MINUTES);
        }
    }
    
    public CreatedRideDTO createRide(CreateRideDTO dto) {
        if (dto == null || dto.getPassengers() == null || dto.getPetTransport() == null || dto.getBabyTransport() == null
            || dto.getLocations() == null || dto.getVehicleType() == null || dto.getScheduledTimestamp() == null
                || dto.getPassengers().isEmpty() || dto.getPassengers().get(0) == null || dto.getPassengers().get(0).getId() == null)
        {
            String value = "Bad input data";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        if (userRepository.findById(dto.getPassengers().get(0).getId()).isEmpty()) {
            String value = "Passenger does not exist!";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        // Check if passenger already has pending ride
        List<Ride> pendingRides = rideRepository.findPendingRideByPassengerId(dto.getPassengers().get(0).getId());
        if (pendingRides.size() > 0) {
            String value = "message: Cannot create a ride while you have one already pending!";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }

        Driver driver = null;
        boolean isScheduled = dto.getScheduledTimestamp() > timestampProvider.getCurrentTimeMillis();
        if (!isScheduled) {
            // Find driver
            driver = findApplicableDriver(dto, false);
            // Second condition is failsafe
            if (driver == null || driver.getBusy()) {
                String value = "Cannot create a ride since no driver is available";
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
            }
            driver.setBusy(true);
            driverRepository.save(driver);
        }

        //Create ride
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setPassengers(dto.getPassengers().stream().map(p -> passengerRepository.findById(p.getId()).get()).collect(Collectors.toList()));
        ride.setLocations(dto.getLocations());
        ride.setStatus(isScheduled ? "PENDING" : "ACCEPTED");
        ride.setPanic(false);
        ride.setBabyTransport(dto.getBabyTransport());
        ride.setPetTransport(dto.getPetTransport());
        ride.setVehicleType(dto.getVehicleType());
        ride.setScheduledTimestamp(dto.getScheduledTimestamp());

        //Estimated time and cost
        AssumptionDTO assumptionDto = new AssumptionDTO();
        assumptionDto.setBabyTransport(dto.getBabyTransport());
        assumptionDto.setPetTransport(dto.getPetTransport());
        assumptionDto.setVehicleType(dto.getVehicleType());
        assumptionDto.setLocations(dto.getLocations());
        EstimationDTO estimationDto = estimationService.getEstimate(assumptionDto);
        ride.setEstimatedTimeInMinutes(estimationDto.getEstimatedTimeInMinutes());
        ride.setTotalCost(estimationDto.getEstimatedCost());
        ride.setDistance(estimationDto.getDistance() / 1000);

        if (driver != null) {
            ArrayList<DepartureDestinationDTO> route = new ArrayList<DepartureDestinationDTO>();
            Location vehicleLocation = driver.getVehicle().getCurrentLocation();
            Location departure = new Location(dto.getLocations().get(0).getDeparture());
            route.add(new DepartureDestinationDTO(vehicleLocation, departure));
            assumptionDto.setLocations(route);
        }

        ride.setStartTime(null);
        ride.setEndTime(null);
        Ride saved = rideRepository.save(ride);

        if (isScheduled) { // Ride is scheduled for future
            long triggerInMillis = Math.max(0L, dto.getScheduledTimestamp() - System.currentTimeMillis() - 10 * 60 * 1000);
            System.out.println("Find driver in " + triggerInMillis + " ms");
            taskExecutor.schedule(() -> findDriverForScheduledRide(saved.getId()), triggerInMillis, TimeUnit.MILLISECONDS);
        }

        if (driver != null) {
            makeVehicleMovement(saved);
            System.out.println("=============================================SENT A MESSAGE TO: " + driver.getId());
        }
        rideUpdated(saved);
        return new CreatedRideDTO(ride);
    }

    public CreatedRideDTO withdraw(Integer id) {
        Optional<Ride> rideOpt = rideRepository.findById(id);
        if (rideOpt.isEmpty()) {
            String value = "Ride does not exist";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        Ride ride = rideOpt.get();
        if (!ride.getStatus().equals("PENDING") && !ride.getStatus().equals("ACCEPTED")) {
            String value = "Bad ride state";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        ride.setStatus("CANCELED");
        ride.setEndTime(null);
        Driver d = ride.getDriver();
        d.setBusy(false);
        driverRepository.save(d);
        rideRepository.save(ride);
        rideUpdated(ride);
        deleteVehicleMovement(id);
        return new CreatedRideDTO(ride);
    }

    public CreatedRideDTO acceptRide(Integer id) {
        Optional<Ride> rideOpt = rideRepository.findById(id);
        if (rideOpt.isEmpty()) {
            String value = "Ride does not exist";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        Ride ride = rideOpt.get();
        if (!ride.getStatus().equals("PENDING") && !ride.getStatus().equals("ACCEPTED")) {
            String value = "Bad ride state";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        ride.setStartTime(timestampProvider.getCurrentDateTime());
        ride.setStatus("ACCEPTED");
        rideRepository.save(ride);
        rideUpdated(ride);
        return new CreatedRideDTO(ride);
    }

    public Ride get(Integer id) {
        Optional<Ride> ride = rideRepository.findById(id);
        if (ride.isEmpty()) {
            String value = "Ride does not exist";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        return ride.get();
    }

    public Ride getPassengerActiveRide(Integer passengerId) {
        List<Ride> pendingRides = rideRepository.findPendingRideByPassengerId(passengerId);
        if (pendingRides.size() == 0) {
            String value = "Active ride does not exist!";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        return pendingRides.get(0);
    }

    public Ride getDriverActiveRide(Integer driverId) {
        List<Ride> pendingRides = rideRepository.findPendingRideByDriverId(driverId);
        if (pendingRides.size() == 0) {
            String value = "Active ride does not exist!";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        return pendingRides.get(0);
    }

    private void assignNewScheduledRide(Driver driver) {
        List<Ride> pendingRides = rideRepository.findByStatus("PENDING");
        for (Ride ride : pendingRides) {
            if (ride.getScheduledTimestamp() == null) continue;
            if (ride.getScheduledTimestamp() - 10 * 60 * 1000 < timestampProvider.getCurrentTimeMillis()) {
                assignDriverToRide(ride, driver);
                break;
            }
        }
    }

    public CreatedRideDTO endRide(Integer id) {
        Ride ride = get(id);
        if (!ride.getStatus().equals("ACTIVE")) {
            String value = "Bad ride state";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        ride.setStatus("FINISHED");
        ride.setEndTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        Driver d = ride.getDriver();
        d.setBusy(false);
        driverRepository.save(d);
        Ride saved = rideRepository.save(ride);
        rideUpdated(saved);
        assignNewScheduledRide(d);
        deleteVehicleMovement(id);
        return new CreatedRideDTO(saved);
    }

    public CreatedRideDTO cancelRide(Integer rideId, ReasonDTO dto) {
        Ride ride = get(rideId);
        if (!ride.getStatus().equals("ACCEPTED")) {
            String value = "Bad ride state";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        ride.setStatus("REJECTED");
        ride.setEndTime(null);
        Rejection r = new Rejection();
        r.setReason(dto.getReason());
        ride.setRejection(r);
        Driver d = ride.getDriver();
        if (d != null) {
            d.setBusy(false);
            driverRepository.save(d);
            deleteVehicleMovement(rideId);
        }
        rejectionRepository.save(r);
        Ride saved = rideRepository.save(ride);
        rideUpdated(saved);
        return new CreatedRideDTO(saved);
    }

    public CreatedRideDTO startRide(Integer id) {
        Ride ride = get(id);
        if (!ride.getStatus().equals("ACCEPTED") && !ride.getStatus().equals("PENDING")) {
            String value = "Bad ride state";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        System.out.println("================STARTED RIDE");
        ride.setStatus("ACTIVE");
        ride.setStartTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        Ride saved = rideRepository.save(ride);
        rideUpdated(saved);
        updateVehicleMovement(saved);
        return new CreatedRideDTO(saved);
    }

    public void rideUpdated(Ride ride) {
        if (ride.getDriver() != null) {
            template.convertAndSend("queue/driver/" + ride.getDriver().getId(), "ride," + ride.getId());
            androidSocketHandler.sendMessage(ride.getDriver().getId(), "ride," + String.valueOf(ride.getId()));
            for (Passenger p : ride.getPassengers()) {
                template.convertAndSend("queue/passenger/" + p.getId(), "ride," + ride.getId());
                androidSocketHandler.sendMessage(p.getId(), "ride," + String.valueOf(ride.getId()));
            }
        }
    }

    public PanicRideDTO panic(ReasonDTO dto, Integer rideId, Integer userId) {
        Ride ride = get(rideId);
        if (ride.getStatus().equals("FINISHED")) {
            String value = "Cannot panic ride that is finished";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            String value = "User not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        User u = user.get();
        ride.setStatus("PANIC");
        ride.setRejection(new Rejection(dto.getReason(), String.valueOf(System.currentTimeMillis())));
        Driver d = ride.getDriver();
        d.setBusy(false);
        driverRepository.save(d);
        rideRepository.save(ride);
        PanicRideDTO dt = new PanicRideDTO();
        dt.setId(1);
        dt.setReason(dto.getReason());
        dt.setRide(new CreatedRideDTO(ride));
        dt.setUser(u.getRole().equals(ERole.ROLE_DRIVER) ? new RegisterDTO(d) : new RegisterDTO(passengerRepository.findById(userId).get()));
        dt.setTime(timestampProvider.getCurrentDateTime());
        dt.setVehicleId(d.getVehicle().getId());
        // Send initial message
        messageRepository.save(new Message(5, userId, "We are sorry for inconvenience. How can we help?", "PANIC", System.currentTimeMillis(), rideId));
        // Notify Admin
        template.convertAndSend("queue/admin/5", new WebsocketMessage("panic", dt));
        rideUpdated(ride);
        deleteVehicleMovement(rideId);
        return dt;
    }

    public PanicListDTO getPanicList() {
        PanicListDTO dto = new PanicListDTO();
        List<Ride> panicRides = rideRepository.findByStatus("PANIC");
        if (panicRides.isEmpty()) {
            String value = "No panics";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        PanicRideDTO d = new PanicRideDTO();
        Ride r = panicRides.get(0);
        d.setRide(new CreatedRideDTO(r));
        d.setReason(r.getRejection().getReason());
        ArrayList<PanicRideDTO> panicRideList = new ArrayList<PanicRideDTO>();
        panicRideList.add(d);
        dto.setTotalCount(panicRideList.size());
        dto.setResults(panicRideList);
        return dto;
    }

    public RidesDTO getRidesForUser(Integer userId, Integer page, Integer size) {
        RidesDTO dto = new RidesDTO();
        ArrayList<CreatedRideDTO> createdRideDTOS = new ArrayList<CreatedRideDTO>();
        List<Ride> rides;
        Optional<User> u = userRepository.findById(userId);
        if (u.isEmpty()){
            String value = "User not found!";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        User user = u.get();
        PageRequest query = PageRequest.of(page, size);
        if (user.getRole() == ERole.ROLE_PASSENGER)
            rides = rideRepository.findByPassengerId(userId, query).stream().toList();
        else
            rides = rideRepository.findByDriverId(userId, query).stream().toList();
        for (Ride r : rides)
            createdRideDTOS.add(new CreatedRideDTO(r));
        dto.setResults(createdRideDTOS);
        dto.setTotalCount(createdRideDTOS.size());
        return dto;
    }

    public VehicleMovement makeVehicleMovement(Ride ride) {
        ArrayList<Location> locations = (ArrayList<Location>) ride.getLocations().stream().collect(Collectors.toList());
        Location departure = locations.get(0);
        String uri = "http://router.project-osrm.org/route/v1/driving/"+ ride.getDriver().getVehicle().getCurrentLocation().getLongitude() +
                "," + ride.getDriver().getVehicle().getCurrentLocation().getLatitude() + ";" +
                departure.getLongitude() + "," + departure.getLatitude() + "?geometries=geojson";
        try {
            HttpGet request = new HttpGet(uri);
            CloseableHttpClient client = HttpClients.createDefault();
            String response = client.execute(request, new BasicResponseHandler());
            JSONObject jsonObject = new JSONObject(response);
            JSONArray locationsArray= jsonObject.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONArray("coordinates");
            ArrayList<Location> vehicleLocations = new ArrayList<Location>();
            for (int i = 0; i < locationsArray.length(); i++){
                JSONArray innerArray = locationsArray.getJSONArray(i);
                Double lon = innerArray.getDouble(0);
                Double lat = innerArray.getDouble(1);
                Location l = new Location("", lon, lat);
                locationRepository.save(l);
                vehicleLocations.add(l);
            }
            VehicleMovement vm = new VehicleMovement();
            vm.setRideId(ride.getId());
            vm.setCurrent(0);
            vm.setLocationList(vehicleLocations);
            vehicleMovementRepository.save(vm);
            return vm;
        } catch (Exception ex) {
            ex.printStackTrace();
            String value = "message: OSRM request failed";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
    }

    public VehicleMovement updateVehicleMovement(Ride ride) {
        Optional<VehicleMovement> vehicleMovement = vehicleMovementRepository.findByRideId(ride.getId());
        if (vehicleMovement.isEmpty()) {
            String value = "Vehicle movement couldnt be found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        VehicleMovement vm = vehicleMovement.get();
        ArrayList<Location> locations = (ArrayList<Location>) ride.getLocations().stream().collect(Collectors.toList());
        Location departure = locations.get(0);
        Location destination = locations.get(1);
        String uri = "http://router.project-osrm.org/route/v1/driving/"+ departure.getLongitude() +
                "," + departure.getLatitude() + ";" + destination.getLongitude() + "," + destination.getLatitude() + "?geometries=geojson";
        try {
            HttpGet request = new HttpGet(uri);
            CloseableHttpClient client = HttpClients.createDefault();
            String response = client.execute(request, new BasicResponseHandler());
            JSONObject jsonObject = new JSONObject(response);
            JSONArray locationsArray= jsonObject.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONArray("coordinates");
            ArrayList<Location> vehicleLocations = new ArrayList<Location>();
            for (int i = 0; i < locationsArray.length(); i++){
                JSONArray innerArray = locationsArray.getJSONArray(i);
                Double lon = innerArray.getDouble(0);
                Double lat = innerArray.getDouble(1);
                Location l = new Location("", lon, lat);
                locationRepository.save(l);
                vehicleLocations.add(l);
            }
            vm.setCurrent(0);
            vm.setLocationList(vehicleLocations);
            vehicleMovementRepository.save(vm);
            return vm;
        } catch (Exception ex) {
            ex.printStackTrace();
            String value = "message: OSRM request failed";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
    }

    public void deleteVehicleMovement(Integer rideId) {
        Optional<VehicleMovement> vehicleMovement = vehicleMovementRepository.findByRideId(rideId);
        if (vehicleMovement.isEmpty()) {
            String value = "Vehicle movement couldnt be found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        vehicleMovementRepository.delete(vehicleMovement.get());
    }

    @Scheduled(fixedRate = 3000)
    public void moveVehicles() {
        List<VehicleMovement> movements = vehicleMovementRepository.findAll();
        for (VehicleMovement v : movements) {
            Ride ride = rideRepository.findById(v.getRideId()).get();
            if (v.getCurrent() >= v.getLocationList().size()) continue;
            Location location = v.getLocationList().get(v.getCurrent());
            v.setCurrent(v.getCurrent() + 1);
            vehicleMovementRepository.save(v);
            Double longitude = location.getLongitude();
            Double latitude = location.getLatitude();
            template.convertAndSend("queue/driver/" + ride.getDriver().getId(), "vehicle," + longitude + "," + latitude);
            androidSocketHandler.sendMessage(ride.getDriver().getId(), "vehicle," + String.valueOf(longitude) + "," + String.valueOf(latitude));
            for (Passenger p : ride.getPassengers()) {
                template.convertAndSend("queue/passenger/" + p.getId(), "vehicle," + longitude + "," + latitude);
                androidSocketHandler.sendMessage(p.getId(), "vehicle," + String.valueOf(longitude) + "," + String.valueOf(latitude));
            }
            vehicleService.updateLocation(new LocationDTO(longitude, latitude), ride.getDriver().getVehicle().getId());
        }
    }

    public FavoriteRoute insertFavoriteRoute(Integer passengerId, Integer departureId, Integer destinationId) {

        Optional<FavoriteRoute> existing = routeRepository.findByDeparture_IdAndDestination_Id(departureId, destinationId);
        if (existing.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Route already added to favorites");

        Optional<Location> departure = locationRepository.findById(departureId);
        Optional<Location> destination = locationRepository.findById(destinationId);
        if(departure.isEmpty() || destination.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid locations");

        Optional<Passenger> passenger = passengerRepository.findById(passengerId);
        if (passenger.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger not found!");

        FavoriteRoute route = new FavoriteRoute();
        route.setDeparture(departure.get());
        route.setDestination(destination.get());
        route.setPassenger(passenger.get());

        return routeRepository.save(route);
    }

    public FavoriteRoute deleteFavoriteRoute(Integer passengerId, Integer id) {
        Optional<FavoriteRoute> favoriteRouteOpt = routeRepository.findById(id);
        if (favoriteRouteOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite route not found!");
        }
        FavoriteRoute favoriteRoute = favoriteRouteOpt.get();
        if (!favoriteRoute.getPassenger().getId().equals(passengerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request!");
        }
        routeRepository.delete(favoriteRoute);
        return favoriteRoute;
    }

    public List<FavoriteRoute> getFavoriteRoutes(Integer passengerId) {
        return routeRepository.findAllByPassenger_Id(passengerId);
    }
}
