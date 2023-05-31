package com.tim1.daimlerback.services;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tim1.daimlerback.dtos.common.*;
import com.tim1.daimlerback.dtos.panic.PanicListDTO;
import com.tim1.daimlerback.dtos.panic.PanicRideDTO;
import com.tim1.daimlerback.dtos.passenger.PassengerShortDTO;
import com.tim1.daimlerback.dtos.ride.CreateRideDTO;
import com.tim1.daimlerback.dtos.ride.CreatedRideDTO;
import com.tim1.daimlerback.entities.*;
import com.tim1.daimlerback.entities.enumeration.ERole;
import com.tim1.daimlerback.repositories.*;
import com.tim1.daimlerback.utils.TaskExecutor;
import com.tim1.daimlerback.utils.TimestampProvider;
import com.tim1.daimlerback.utils.WebsocketMessage;
import com.tim1.daimlerback.websockets.AndroidSocketHandler;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.NullString;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class RideServiceTest {

    // Repository mocks
    @MockBean
    IUserRepository userRepository;
    @MockBean
    IRideRepository rideRepository;
    @MockBean
    IDriverRepository driverRepository;
    @MockBean
    IPassengerRepository passengerRepository;
    @MockBean
    ILocationRepository locationRepository;
    @MockBean
    IVehicleMovementRepository vehicleMovementRepository;
    @MockBean
    IFavoriteRouteRepository routeRepository;
    @MockBean
    IMessageRepository messageRepository;
    @MockBean
    IRejectionRepository rejectionRepository;

    // Service mocks
    @MockBean
    EstimationService estimationService;
    @MockBean
    DriverService driverService;

    // Util mocks
    @MockBean
    TimestampProvider timestampProvider;
    @SpyBean
    TaskExecutor taskExecutor;
    @SpyBean
    SimpMessagingTemplate template;
    @SpyBean
    AndroidSocketHandler androidSocketHandler;

    // Class we are testing
    @Autowired
    RideService rideService;

    // Common test data
    private CreateRideDTO validCreateRideInput;
    private List<DepartureDestinationDTO> validRideLocations;
    private AssumptionDTO validRideAssumption;
    private EstimationDTO validRideEstimation;
    private static final int validRideId = 2;
    private static final int validFavoriteRouteId = 1;
    private static final int invalidFavoriteRouteId = 2;
    private static final long currentMockTimestamp = 10000;
    private static final String currentDateTime = "28.01.2023 01:11";
    private static final long secondTimestamp = 1000;
    private static final long minuteTimestamp = 60 * secondTimestamp;
    private static final int invalidPassengerId = 1123456;
    private static final int invalidDriverId = 942121;
    private static final int validPassengerId = 5;
    private Ride pendingRide;
    private Ride activeRide;
    private Driver validDriver;
    private Passenger validPassenger;
    private static final int passengerWithPendingRide = 12344;
    private static final int validDriverId = 98325;
    private static final int invalidRideId = 1233322;
    private static final int activeRideId = 12256223;
    private static final int pendingRideId = 12256553;

    void setUpPassengerAndUserRepository() {
        when(passengerRepository.findById(invalidPassengerId)).thenReturn(Optional.empty());
        when(userRepository.findById(invalidPassengerId)).thenReturn(Optional.empty());

        validPassenger = new Passenger();
        validPassenger.setAddress("Some address");
        validPassenger.setBlocked(false);
        validPassenger.setEnabled(true);
        validPassenger.setBusy(false);
        validPassenger.setProfilePicture("");
        validPassenger.setTelephoneNumber("123456789");
        validPassenger.setPassword("123");
        validPassenger.setEmail("passengerWithPendingRide@gmail.com");
        validPassenger.setName("Passenger");
        validPassenger.setSurname("Passenger");
        validPassenger.setId(passengerWithPendingRide);
        when(passengerRepository.findById(passengerWithPendingRide)).thenReturn(Optional.of(validPassenger));
        when(userRepository.findById(passengerWithPendingRide)).thenReturn(Optional.of(validPassenger));

        validPassenger.setEmail("p@gmail.com");
        validPassenger.setId(validPassengerId);
        when(passengerRepository.findById(validPassengerId)).thenReturn(Optional.of(validPassenger));
        when(userRepository.findById(validPassengerId)).thenReturn(Optional.of(validPassenger));
    }
    void setUpDriverAndUserRepository() {
        when(driverRepository.findById(invalidDriverId)).thenReturn(Optional.empty());

        validDriver = new Driver();
        validDriver.setId(validDriverId);
        validDriver.setBusy(false);
        validDriver.setBlocked(false);
        validDriver.setAddress("Driver address");
        validDriver.setEmail("d@gmail.com");
        validDriver.setActive(true);
        validDriver.setProfilePicture("");
        validDriver.setTelephoneNumber("123456789");
        validDriver.setName("Driver");
        validDriver.setSurname("Driver");
        validDriver.setPassword("123");
        Vehicle vehicle = new Vehicle();
        vehicle.setPassengerSeats(3);
        vehicle.setId(7);
        vehicle.setDriver(validDriver);
        vehicle.setModel("Model");
        vehicle.setLicenseNumber("");
        vehicle.setVehicleType(validRideAssumption.getVehicleType());
        vehicle.setBabyTransport(true);
        vehicle.setPetTransport(true);
        Location location = new Location();
        location.setId(111);
        location.setAddress("Vehicle address");
        location.setLongitude(19.82);
        location.setLatitude(45.23);
        vehicle.setCurrentLocation(location);
        validDriver.setVehicle(vehicle);
        when(driverRepository.findById(validDriverId)).thenReturn(Optional.of(validDriver));
        when(userRepository.findById(validDriverId)).thenReturn(Optional.of(validDriver));

        List<Driver> availableDrivers = new ArrayList<Driver>();
        availableDrivers.add(validDriver);
        when(driverRepository.findAllByIsBlockedAndIsActive(false, true)).thenReturn(availableDrivers);
    }
    void setUpRideRepository() {
        pendingRide = new Ride();
        pendingRide.setId(pendingRideId);
        pendingRide.setStatus("PENDING");
        List<Ride> pendingRidesList = new ArrayList<Ride>();
        pendingRidesList.add(pendingRide);
        when(rideRepository.findPendingRideByPassengerId(passengerWithPendingRide)).thenReturn(pendingRidesList);
        when(rideRepository.findById(pendingRideId)).thenReturn(Optional.of(pendingRide));

        when(rideRepository.save(any())).thenAnswer(call -> {
            Ride ride = (Ride) call.getArguments()[0];
            if (ride.getId() == null) {
                ride.setId(validRideId);
                when(rideRepository.findById(validRideId)).thenReturn(Optional.of(ride));
            }
            return ride;
        });

        ArrayList<Passenger> passengers = new ArrayList<Passenger>();
        passengers.add(validPassenger);
        activeRide = new Ride();
        activeRide.setStatus("ACTIVE");
        activeRide.setScheduledTimestamp(currentMockTimestamp);
        activeRide.setEstimatedTimeInMinutes(5);
        activeRide.setPanic(false);
        activeRide.setDistance(500.0);
        activeRide.setId(activeRideId);
        activeRide.setLocations(new ArrayList<DepartureDestinationDTO>());
        activeRide.setPassengers(passengers);
        activeRide.setEndTime("");
        activeRide.setStartTime("");
        activeRide.setRejection(new Rejection());
        activeRide.setBabyTransport(false);
        activeRide.setPetTransport(false);
        activeRide.setTotalCost(411);
        activeRide.setVehicleType("COUPE");
        activeRide.setDriver(validDriver);
        when(rideRepository.findById(activeRideId)).thenReturn(Optional.of(activeRide));
    }
    void setUpVehicleMovementRepository() {
        VehicleMovement vehicleMovement = new VehicleMovement();
        when(vehicleMovementRepository.findByRideId(activeRideId)).thenReturn(Optional.of(vehicleMovement));
    }
    void setUpLocations() {
        validRideLocations = new ArrayList<DepartureDestinationDTO>();
        DepartureDestinationDTO departureDestinationDTO = new DepartureDestinationDTO();
        LocationDTO departure = new LocationDTO();
        departure.setId(1);
        departure.setAddress("Address 1");
        departure.setLongitude(45.45);
        departure.setLatitude(30.21);
        departureDestinationDTO.setDeparture(departure);
        LocationDTO destination = new LocationDTO();
        destination.setId(2);
        destination.setAddress("Address 2");
        destination.setLongitude(46.2);
        destination.setLatitude(29.89);
        departureDestinationDTO.setDestination(destination);
        validRideLocations.add(departureDestinationDTO);
    }
    void setUpEstimationService() {
        setUpLocations();

        validRideAssumption = new AssumptionDTO();
        validRideAssumption.setLocations(validRideLocations);
        validRideAssumption.setBabyTransport(true);
        validRideAssumption.setPetTransport(false);
        validRideAssumption.setVehicleType("COUPE");

        validRideEstimation = new EstimationDTO();
        validRideEstimation.setEstimatedCost(750);
        validRideEstimation.setDistance(2.5);
        validRideEstimation.setEstimatedTimeInMinutes(15);

        // TODO: It seems that mockito compares AssumptionDTO by reference instead of value so it fails to return estimation in rideService
        //when(estimationService.getEstimate(validRideAssumption)).thenReturn(validRideEstimation);
        when(estimationService.getEstimate(any())).thenReturn(validRideEstimation);
    }
    void setUpTimestampProvider() {
        when(timestampProvider.getCurrentTimeMillis()).thenReturn(currentMockTimestamp);
        when(timestampProvider.getCurrentDateTime()).thenReturn(currentDateTime);
    }
    void setUpTaskExecutor() {
        // Immediately call tasks for test purposes
        doAnswer(call -> {
            // Prevent infinite loop when task schedules itself again on fail
            Mockito.reset(taskExecutor);
            doNothing().when(taskExecutor).schedule(any(), anyLong(), any());

            // Run the task
            Runnable command = (Runnable) call.getArguments()[0];
            command.run();
            return null;
        }).when(taskExecutor).schedule(any(), anyLong(), any());
    }
    void setUpMocks() {
        setUpPassengerAndUserRepository();
        setUpEstimationService();
        setUpDriverAndUserRepository();
        setUpRideRepository();
        setUpVehicleMovementRepository();
        setUpTimestampProvider();
        setUpTaskExecutor();
    }
    @BeforeEach
    void setUp() {
        setUpMocks();
        validCreateRideInput = new CreateRideDTO();

        List<PassengerShortDTO> passengerList = new ArrayList<PassengerShortDTO>();
        PassengerShortDTO passenger = new PassengerShortDTO();
        passenger.setEmail("p@gmail.com");
        passenger.setId(validPassengerId);
        passengerList.add(passenger);
        validCreateRideInput.setPassengers(passengerList);

        validCreateRideInput.setLocations(validRideLocations);

        validCreateRideInput.setBabyTransport(validRideAssumption.getBabyTransport());
        validCreateRideInput.setPetTransport(validRideAssumption.getPetTransport());
        validCreateRideInput.setVehicleType(validRideAssumption.getVehicleType());
        validCreateRideInput.setScheduledTimestamp(currentMockTimestamp);
    }

    // --------------------------------------------- createRide ------------------------------------------------- //

    @Test
    void createRideWithNullInput() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.createRide(null);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
    @Test
    void createRideWithNullPassengerListTest() {
        CreateRideDTO inputDto = validCreateRideInput;
        inputDto.setPassengers(null);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.createRide(inputDto);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void createRideWithEmptyPassengerListTest(){
        CreateRideDTO inputDto = validCreateRideInput;
        inputDto.setPassengers(new ArrayList<PassengerShortDTO>());
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.createRide(inputDto);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void createRideWithNullPassengerTest(){
        CreateRideDTO inputDto = validCreateRideInput;
        List<PassengerShortDTO> passengers = new ArrayList<PassengerShortDTO>();
        passengers.add(null);
        inputDto.setPassengers(passengers);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.createRide(inputDto);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void createRideWithNullPassengerIdTest(){
        CreateRideDTO inputDto = validCreateRideInput;
        List<PassengerShortDTO> passengers = new ArrayList<PassengerShortDTO>();
        PassengerShortDTO passenger = new PassengerShortDTO();
        passenger.setId(null);
        passenger.setEmail("email");
        passengers.add(passenger);
        inputDto.setPassengers(passengers);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.createRide(inputDto);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void createRideWithInvalidPassengerIdTest(){
        CreateRideDTO inputDto = validCreateRideInput;
        List<PassengerShortDTO> passengers = new ArrayList<PassengerShortDTO>();
        PassengerShortDTO passenger = new PassengerShortDTO();
        passenger.setId(invalidPassengerId);
        passenger.setEmail("email");
        passengers.add(passenger);
        inputDto.setPassengers(passengers);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.createRide(inputDto);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void createRideWhenThereIsPendingRideTest(){
        CreateRideDTO inputDto = validCreateRideInput;
        List<PassengerShortDTO> passengers = new ArrayList<PassengerShortDTO>();
        PassengerShortDTO passenger = new PassengerShortDTO();
        passenger.setId(passengerWithPendingRide);
        passenger.setEmail("some@email.com");
        passengers.add(passenger);
        inputDto.setPassengers(passengers);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.createRide(inputDto);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void createRideWithNullLocationsTest(){
        CreateRideDTO inputDto = validCreateRideInput;
        inputDto.setLocations(null);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.createRide(inputDto);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void createRideWithNullBabyTransportTest(){
        CreateRideDTO inputDto = validCreateRideInput;
        inputDto.setBabyTransport(null);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.createRide(inputDto);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void createRideWithNullPetTransportTest(){
        CreateRideDTO inputDto = validCreateRideInput;
        inputDto.setPetTransport(null);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.createRide(inputDto);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void createRideWithNullVehicleTypeTest(){
        CreateRideDTO inputDto = validCreateRideInput;
        inputDto.setVehicleType(null);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.createRide(inputDto);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void createRideWithNullScheduledTimestampTest(){
        CreateRideDTO inputDto = validCreateRideInput;
        inputDto.setScheduledTimestamp(null);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.createRide(inputDto);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void createScheduledRideTest() {
        Mockito.reset(taskExecutor);
        doNothing().when(taskExecutor).schedule(any(), anyLong(), any());
        CreateRideDTO inputDto = validCreateRideInput;
        long scheduledTimestamp = currentMockTimestamp + 30 * minuteTimestamp;
        inputDto.setScheduledTimestamp(scheduledTimestamp);
        CreatedRideDTO createdRide = rideService.createRide(inputDto);
        Assertions.assertEquals("PENDING", createdRide.getStatus());
        Assertions.assertEquals(validRideId, createdRide.getId());
        Assertions.assertEquals(scheduledTimestamp, createdRide.getScheduledTimestamp());
        Assertions.assertEquals(validRideAssumption.getPetTransport(), createdRide.getPetTransport());
        Assertions.assertEquals(validRideAssumption.getBabyTransport(), createdRide.getBabyTransport());
        Assertions.assertEquals(validRideAssumption.getVehicleType(), createdRide.getVehicleType());
        Assertions.assertEquals(validRideEstimation.getEstimatedCost(), createdRide.getTotalCost());
        Assertions.assertEquals(validRideEstimation.getEstimatedTimeInMinutes(), createdRide.getEstimatedTimeInMinutes());
        Assertions.assertNull(createdRide.getDriver());
    }

    @Test
    void createScheduledRideAndRunAssignDriverTaskTest() {
        CreateRideDTO inputDto = validCreateRideInput;
        long scheduledTimestamp = currentMockTimestamp + 30 * minuteTimestamp;
        inputDto.setScheduledTimestamp(scheduledTimestamp);
        CreatedRideDTO createdRide = rideService.createRide(inputDto);
        Assertions.assertEquals("ACCEPTED", createdRide.getStatus());
        Assertions.assertEquals(validRideId, createdRide.getId());
        Assertions.assertEquals(scheduledTimestamp, createdRide.getScheduledTimestamp());
        Assertions.assertEquals(validRideAssumption.getPetTransport(), createdRide.getPetTransport());
        Assertions.assertEquals(validRideAssumption.getBabyTransport(), createdRide.getBabyTransport());
        Assertions.assertEquals(validRideAssumption.getVehicleType(), createdRide.getVehicleType());
        Assertions.assertEquals(validRideEstimation.getEstimatedCost(), createdRide.getTotalCost());
        Assertions.assertEquals(validRideEstimation.getEstimatedTimeInMinutes(), createdRide.getEstimatedTimeInMinutes());
        Assertions.assertNotNull(createdRide.getDriver());
        Assertions.assertEquals(validDriverId, createdRide.getDriver().getId());

        // Verify that users are notified about ride change through websockets
        verify(template, atLeast(1)).convertAndSend("queue/driver/" + createdRide.getDriver().getId(), "ride," + createdRide.getId());
        verify(androidSocketHandler, atLeast(1)).sendMessage(createdRide.getDriver().getId(), "ride," + createdRide.getId());
        verify(template, atLeast(1)).convertAndSend("queue/passenger/" + validPassengerId, "ride," + createdRide.getId());
        verify(androidSocketHandler, atLeast(1)).sendMessage(validPassengerId, "ride," + createdRide.getId());
    }

    @Test
    void createScheduledRideNoDriverAvailableTaskTest() {
        when(driverRepository.findAllByIsBlockedAndIsActive(false, true)).thenReturn(new ArrayList<Driver>());
        CreateRideDTO inputDto = validCreateRideInput;
        long scheduledTimestamp = currentMockTimestamp + 30 * minuteTimestamp;
        inputDto.setScheduledTimestamp(scheduledTimestamp);
        CreatedRideDTO createdRide = rideService.createRide(inputDto);
        Assertions.assertEquals("PENDING", createdRide.getStatus());
        Assertions.assertEquals(validRideId, createdRide.getId());
        Assertions.assertEquals(scheduledTimestamp, createdRide.getScheduledTimestamp());
        Assertions.assertEquals(validRideAssumption.getPetTransport(), createdRide.getPetTransport());
        Assertions.assertEquals(validRideAssumption.getBabyTransport(), createdRide.getBabyTransport());
        Assertions.assertEquals(validRideAssumption.getVehicleType(), createdRide.getVehicleType());
        Assertions.assertEquals(validRideEstimation.getEstimatedCost(), createdRide.getTotalCost());
        Assertions.assertEquals(validRideEstimation.getEstimatedTimeInMinutes(), createdRide.getEstimatedTimeInMinutes());
        Assertions.assertNull(createdRide.getDriver());
    }

    @Test
    void createRideDriverNotFoundTest() {
        Mockito.reset(driverRepository);
        CreateRideDTO inputDto = validCreateRideInput;
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.createRide(inputDto);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void createRideDriverNotApplicableTest() {
        Mockito.reset(driverRepository);
        List<Driver> driverList = new ArrayList<Driver>();
        Driver nonApplicableDriver = new Driver();
        Vehicle vehicle = new Vehicle();
        vehicle.setDriver(nonApplicableDriver);
        vehicle.setVehicleType(validRideAssumption.getVehicleType());
        vehicle.setBabyTransport(true);
        vehicle.setPetTransport(true);
        vehicle.setPassengerSeats(1);
        nonApplicableDriver.setVehicle(vehicle);
        nonApplicableDriver.setBusy(true);
        nonApplicableDriver.setId(653);
        driverList.add(nonApplicableDriver);
        when(driverRepository.findAllByIsBlockedAndIsActive(false, true)).thenReturn(driverList);
        CreateRideDTO inputDto = validCreateRideInput;
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.createRide(inputDto);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void createRideDriverWorked8HoursTest() {
        Mockito.reset(driverRepository);
        int driverId = 653;
        List<Driver> driverList = new ArrayList<Driver>();
        Driver driver = new Driver();
        Vehicle vehicle = new Vehicle();
        vehicle.setDriver(driver);
        vehicle.setVehicleType(validRideAssumption.getVehicleType());
        vehicle.setBabyTransport(true);
        vehicle.setPetTransport(true);
        vehicle.setPassengerSeats(1);
        driver.setVehicle(vehicle);
        driver.setBusy(false);
        driver.setId(driverId);
        driverList.add(driver);

        Mockito.reset(driverService);
        when(driverService.getTodayWorkingHours(any())).thenReturn(8.1);

        when(driverRepository.findAllByIsBlockedAndIsActive(false, true)).thenReturn(driverList);
        CreateRideDTO inputDto = validCreateRideInput;
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.createRide(inputDto);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void createRideAllDriversReservedTest() {
        List<Ride> pendingRides = new ArrayList<Ride>();
        Ride ride = new Ride();
        ride.setScheduledTimestamp(currentMockTimestamp);
        ride.setEstimatedTimeInMinutes(20);
        pendingRides.add(ride);
        pendingRides.add(ride);
        pendingRides.add(ride);
        when(rideRepository.findByStatus("PENDING")).thenReturn(pendingRides);
        CreateRideDTO inputDto = validCreateRideInput;
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.createRide(inputDto);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void createValidRideTest() {
        CreateRideDTO inputDto = validCreateRideInput;
        CreatedRideDTO createdRide = rideService.createRide(inputDto);
        Assertions.assertEquals("ACCEPTED", createdRide.getStatus());
        Assertions.assertEquals(validRideId, createdRide.getId());
        Assertions.assertEquals(currentMockTimestamp, createdRide.getScheduledTimestamp());
        Assertions.assertEquals(validRideAssumption.getPetTransport(), createdRide.getPetTransport());
        Assertions.assertEquals(validRideAssumption.getBabyTransport(), createdRide.getBabyTransport());
        Assertions.assertEquals(validRideAssumption.getVehicleType(), createdRide.getVehicleType());
        Assertions.assertEquals(validRideEstimation.getEstimatedCost(), createdRide.getTotalCost());
        Assertions.assertEquals(validRideEstimation.getEstimatedTimeInMinutes(), createdRide.getEstimatedTimeInMinutes());
        Assertions.assertNotNull(createdRide.getDriver());

        // Verify that users are notified about ride change through websockets
        verify(template).convertAndSend("queue/driver/" + createdRide.getDriver().getId(), "ride," + createdRide.getId());
        verify(androidSocketHandler).sendMessage(createdRide.getDriver().getId(), "ride," + createdRide.getId());
        verify(template).convertAndSend("queue/passenger/" + validPassengerId, "ride," + createdRide.getId());
        verify(androidSocketHandler).sendMessage(validPassengerId, "ride," + createdRide.getId());
    }

    @Test
    void createRideAssertClosestDriverWasAssignedTest() {
        Driver driver1 = new Driver();
        Vehicle vehicle1 = new Vehicle();
        Location location1 = new Location();
        driver1.setId(91);
        driver1.setVehicle(vehicle1);
        driver1.setBusy(false);
        vehicle1.setCurrentLocation(location1);
        vehicle1.setVehicleType(validRideAssumption.getVehicleType());
        vehicle1.setBabyTransport(true);
        vehicle1.setPetTransport(true);
        vehicle1.setPassengerSeats(1);
        location1.setAddress("Address1");
        location1.setLongitude(19.82);
        location1.setLatitude(45.23);

        Driver driver2 = new Driver();
        Vehicle vehicle2 = new Vehicle();
        Location location2 = new Location();
        driver2.setId(92);
        driver2.setVehicle(vehicle2);
        driver2.setBusy(false);
        vehicle2.setCurrentLocation(location2);
        vehicle2.setVehicleType(validRideAssumption.getVehicleType());
        vehicle2.setBabyTransport(true);
        vehicle2.setPetTransport(true);
        vehicle2.setPassengerSeats(1);
        location2.setAddress("Address2");
        location2.setLongitude(19.82);
        location2.setLatitude(45.23);

        Driver driver3 = new Driver();
        Vehicle vehicle3 = new Vehicle();
        Location location3 = new Location();
        driver3.setId(93);
        driver3.setVehicle(vehicle3);
        driver3.setBusy(false);
        vehicle3.setCurrentLocation(location3);
        vehicle3.setVehicleType(validRideAssumption.getVehicleType());
        vehicle3.setBabyTransport(true);
        vehicle3.setPetTransport(true);
        vehicle3.setPassengerSeats(1);
        location3.setAddress("Address3");
        location3.setLongitude(19.82);
        location3.setLatitude(45.23);

        List<Driver> drivers = new ArrayList<Driver>();
        drivers.add(driver1);
        drivers.add(driver2);
        drivers.add(driver3);
        when(driverRepository.findAllByIsBlockedAndIsActive(false, true)).thenReturn(drivers);

        when(estimationService.osrmRequest(any())).thenAnswer(call -> {
            DepartureDestinationDTO assumption = (DepartureDestinationDTO) call.getArguments()[0];
            EstimationDTO estimation = new EstimationDTO();
            switch (assumption.getDeparture().getAddress()) {
                case "Address1" -> estimation.setEstimatedTimeInMinutes(15);
                case "Address2" -> estimation.setEstimatedTimeInMinutes(4);
                case "Address3" ->
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message: OSRM request failed");
                default -> estimation = validRideEstimation;
            }
            return estimation;
        });

        CreateRideDTO inputDto = validCreateRideInput;
        CreatedRideDTO createdRide = rideService.createRide(inputDto);
        Assertions.assertEquals("ACCEPTED", createdRide.getStatus());
        Assertions.assertEquals(validRideId, createdRide.getId());
        Assertions.assertEquals(currentMockTimestamp, createdRide.getScheduledTimestamp());
        Assertions.assertEquals(validRideAssumption.getPetTransport(), createdRide.getPetTransport());
        Assertions.assertEquals(validRideAssumption.getBabyTransport(), createdRide.getBabyTransport());
        Assertions.assertEquals(validRideAssumption.getVehicleType(), createdRide.getVehicleType());
        Assertions.assertEquals(validRideEstimation.getEstimatedCost(), createdRide.getTotalCost());
        Assertions.assertEquals(validRideEstimation.getEstimatedTimeInMinutes(), createdRide.getEstimatedTimeInMinutes());
        Assertions.assertNotNull(createdRide.getDriver());
        Assertions.assertEquals(driver2.getId(), createdRide.getDriver().getId());

        // Verify that users are notified about ride change through websockets
        verify(template).convertAndSend("queue/driver/" + createdRide.getDriver().getId(), "ride," + createdRide.getId());
        verify(androidSocketHandler).sendMessage(createdRide.getDriver().getId(), "ride," + createdRide.getId());
        verify(template).convertAndSend("queue/passenger/" + validPassengerId, "ride," + createdRide.getId());
        verify(androidSocketHandler).sendMessage(validPassengerId, "ride," + createdRide.getId());
    }

    // --------------------------------------------- get ------------------------------------------------- //

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {invalidRideId, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void getWithInvalidIdTest(Integer invalidId) {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.get(invalidId);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void getValidRideTest() {
        Ride ride = rideService.get(activeRideId);
        Assertions.assertEquals(activeRide.getId(), ride.getId());
        Assertions.assertEquals(activeRide.getDriver(), ride.getDriver());
        Assertions.assertEquals(activeRide.getStatus(), ride.getStatus());
        Assertions.assertEquals(activeRide.getScheduledTimestamp(), ride.getScheduledTimestamp());
        Assertions.assertEquals(activeRide.getTotalCost(), ride.getTotalCost());
        Assertions.assertEquals(activeRide.getEstimatedTimeInMinutes(), ride.getEstimatedTimeInMinutes());
        Assertions.assertEquals(activeRide.getDistance(), ride.getDistance());
    }

    // --------------------------------------------- endRide ------------------------------------------------- //

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {invalidRideId, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void endRideWithInvalidIdTest(Integer invalidId) {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.endRide(invalidId);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void endNonActiveRideTest() {
        int pendingRideId = 2;
        when(rideRepository.findById(pendingRideId)).thenReturn(Optional.of(pendingRide));

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.endRide(pendingRideId);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void endActiveRideTest() {
        CreatedRideDTO createdRide = rideService.endRide(activeRideId);
        Assertions.assertEquals("FINISHED", createdRide.getStatus());

        // Verify that users are notified about ride change through websockets
        verify(template).convertAndSend("queue/driver/" + createdRide.getDriver().getId(), "ride," + createdRide.getId());
        verify(androidSocketHandler).sendMessage(createdRide.getDriver().getId(), "ride," + createdRide.getId());
    }

    @Test
    void endActiveRideAndAssignNewRideToDriverTest() {
        List<Ride> pendingRides = new ArrayList<Ride>();
        Ride ride = new Ride();
        ride.setScheduledTimestamp(currentMockTimestamp);
        ride.setEstimatedTimeInMinutes(20);
        ride.setStatus("PENDING");
        ride.setPanic(false);
        ride.setId(1230092);
        List<DepartureDestinationDTO> locations = new ArrayList<DepartureDestinationDTO>();
        DepartureDestinationDTO location = new DepartureDestinationDTO();
        LocationDTO departure = new LocationDTO();
        departure.setId(45);
        departure.setLongitude(19.82);
        departure.setLatitude(45.12);
        departure.setAddress("Departure address");
        LocationDTO destination = new LocationDTO();
        destination.setId(47);
        destination.setLongitude(19.86);
        destination.setLatitude(45.19);
        destination.setAddress("Destination address");
        location.setDeparture(departure);
        location.setDestination(destination);
        locations.add(location);
        ride.setLocations(locations);
        ride.setPassengers(new ArrayList<Passenger>());
        ride.setBabyTransport(false);
        ride.setPetTransport(false);
        ride.setVehicleType("COUPE");

        pendingRides.add(ride);
        when(rideRepository.findByStatus("PENDING")).thenReturn(pendingRides);

        CreatedRideDTO createdRide = rideService.endRide(activeRideId);
        Assertions.assertEquals("FINISHED", createdRide.getStatus());

        // Verify that users are notified about ride change through websockets
        verify(template).convertAndSend("queue/driver/" + createdRide.getDriver().getId(), "ride," + createdRide.getId());
        verify(androidSocketHandler).sendMessage(createdRide.getDriver().getId(), "ride," + createdRide.getId());

        // Verify that pending ride is also updated and that users are notified
        verify(template).convertAndSend("queue/driver/" + createdRide.getDriver().getId(), "ride," + ride.getId());
        verify(androidSocketHandler).sendMessage(createdRide.getDriver().getId(), "ride," + ride.getId());
    }

    @Test
    void endActiveRideWithPendingRideTooLateTest() {
        List<Ride> pendingRides = new ArrayList<Ride>();
        Ride ride = new Ride();
        ride.setScheduledTimestamp(currentMockTimestamp + 60 * minuteTimestamp);
        ride.setEstimatedTimeInMinutes(20);
        ride.setStatus("PENDING");
        ride.setPanic(false);
        ride.setId(1230092);
        List<DepartureDestinationDTO> locations = new ArrayList<DepartureDestinationDTO>();
        DepartureDestinationDTO location = new DepartureDestinationDTO();
        LocationDTO departure = new LocationDTO();
        departure.setId(45);
        departure.setLongitude(19.82);
        departure.setLatitude(45.12);
        departure.setAddress("Departure address");
        LocationDTO destination = new LocationDTO();
        destination.setId(47);
        destination.setLongitude(19.86);
        destination.setLatitude(45.19);
        destination.setAddress("Destination address");
        location.setDeparture(departure);
        location.setDestination(destination);
        locations.add(location);
        ride.setLocations(locations);
        ride.setPassengers(new ArrayList<Passenger>());
        ride.setBabyTransport(false);
        ride.setPetTransport(false);
        ride.setVehicleType("COUPE");

        pendingRides.add(ride);
        when(rideRepository.findByStatus("PENDING")).thenReturn(pendingRides);

        CreatedRideDTO createdRide = rideService.endRide(activeRideId);
        Assertions.assertEquals("FINISHED", createdRide.getStatus());

        // Verify that users are notified about ride change through websockets
        verify(template).convertAndSend("queue/driver/" + createdRide.getDriver().getId(), "ride," + createdRide.getId());
        verify(androidSocketHandler).sendMessage(createdRide.getDriver().getId(), "ride," + createdRide.getId());

        // Verify that pending ride not assigned to driver that just finished its ride
        verify(template, never()).convertAndSend("queue/driver/" + createdRide.getDriver().getId(), "ride," + ride.getId());
        verify(androidSocketHandler, never()).sendMessage(createdRide.getDriver().getId(), "ride," + ride.getId());
    }

    // --------------------------------------------- getPassengerActiveRide ------------------------------------------------- //

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {invalidPassengerId, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void getPassengerActiveRideWithInvalidIdTest(Integer invalidId) {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.getPassengerActiveRide(invalidId);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void getPassengerActiveRideValidTest() {
        List<Ride> rides = new ArrayList<Ride>();
        Ride ride = new Ride();
        ride.setId(1);
        ride.setDriver(validDriver);
        ride.setStatus("PENDING");
        ride.setScheduledTimestamp(currentMockTimestamp);
        rides.add(ride);
        when(rideRepository.findPendingRideByPassengerId(validPassengerId)).thenReturn(rides);
        Ride activeRide = rideService.getPassengerActiveRide(validPassengerId);
        Assertions.assertEquals(ride.getId(), activeRide.getId());
        Assertions.assertEquals(ride.getDriver(), activeRide.getDriver());
        Assertions.assertEquals(ride.getStatus(), activeRide.getStatus());
        Assertions.assertEquals(ride.getScheduledTimestamp(), activeRide.getScheduledTimestamp());
    }

    // --------------------------------------------- getDriverActiveRide ------------------------------------------------- //

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {invalidDriverId, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void getDriverActiveRideWithInvalidIdTest(Integer invalidId) {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.getDriverActiveRide(invalidId);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void getDriverActiveRideValidTest() {
        List<Ride> rides = new ArrayList<Ride>();
        Ride ride = new Ride();
        ride.setId(1);
        ride.setDriver(validDriver);
        ride.setStatus("PENDING");
        ride.setScheduledTimestamp(currentMockTimestamp);
        rides.add(ride);
        when(rideRepository.findPendingRideByDriverId(validDriverId)).thenReturn(rides);
        Ride activeRide = rideService.getDriverActiveRide(validDriverId);
        Assertions.assertEquals(ride.getId(), activeRide.getId());
        Assertions.assertEquals(ride.getDriver(), activeRide.getDriver());
        Assertions.assertEquals(ride.getStatus(), activeRide.getStatus());
        Assertions.assertEquals(ride.getScheduledTimestamp(), activeRide.getScheduledTimestamp());
    }

    // --------------------------------------------- acceptRide ------------------------------------------------- //

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {invalidRideId, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void acceptRideWithInvalidIdTest(Integer invalidId) {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.acceptRide(invalidId);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void acceptNonPendingRideTest() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.acceptRide(activeRideId);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void acceptPendingRideTest() {
        int pendingRideId = 111220;
        Ride pendingRide = new Ride();
        pendingRide.setStatus("PENDING");
        pendingRide.setStartTime("");
        pendingRide.setDriver(validDriver);
        when(rideRepository.findById(pendingRideId)).thenReturn(Optional.of(pendingRide));

        CreatedRideDTO createdRide = rideService.acceptRide(pendingRideId);
        Assertions.assertEquals("ACCEPTED", createdRide.getStatus());
        Assertions.assertEquals(currentDateTime, createdRide.getStartTime());

        // Verify that users are notified about ride change through websockets
        verify(template).convertAndSend("queue/driver/" + createdRide.getDriver().getId(), "ride," + createdRide.getId());
        verify(androidSocketHandler).sendMessage(createdRide.getDriver().getId(), "ride," + createdRide.getId());
    }

    // --------------------------------------------- withdraw ------------------------------------------------- //

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {invalidRideId, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void withdrawRideWithInvalidIdTest(Integer invalidId) {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.withdraw(invalidId);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void withdrawNonPendingRideTest() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.withdraw(activeRideId);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void withdrawPendingRideTest() {
        int pendingRideId = 111220;
        Ride pendingRide = new Ride();
        pendingRide.setStatus("PENDING");
        pendingRide.setEndTime("");
        pendingRide.setDriver(validDriver);
        when(rideRepository.findById(pendingRideId)).thenReturn(Optional.of(pendingRide));

        when(vehicleMovementRepository.findByRideId(pendingRideId)).thenReturn(Optional.of(new VehicleMovement()));

        CreatedRideDTO createdRide = rideService.withdraw(pendingRideId);
        Assertions.assertEquals("CANCELED", createdRide.getStatus());
        Assertions.assertNull(createdRide.getEndTime());

        // Verify that users are notified about ride change through websockets
        verify(template).convertAndSend("queue/driver/" + createdRide.getDriver().getId(), "ride," + createdRide.getId());
        verify(androidSocketHandler).sendMessage(createdRide.getDriver().getId(), "ride," + createdRide.getId());
    }


    void resetMocks() {
        Mockito.reset(driverRepository);
        Mockito.reset(passengerRepository);
        Mockito.reset(userRepository);
        Mockito.reset(rideRepository);
        Mockito.reset(locationRepository);
        Mockito.reset(vehicleMovementRepository);

        Mockito.reset(estimationService);
        Mockito.reset(driverService);

        Mockito.reset(timestampProvider);
        Mockito.reset(taskExecutor);
        Mockito.reset(template);
        Mockito.reset(androidSocketHandler);
    }
    @AfterEach
    void tearDown() {
        resetMocks();
    }

    // ------------------------------------------ insertFavoriteRoute ----------------------------------------------- //
    @Test
    void insertFavoriteRouteInvalidDuplicatedTest() {
        int departureId = validRideLocations.get(0).getDeparture().getId();
        Location departure = new Location(validRideLocations.get(0).getDeparture());
        departure.setId(departureId);
        int destinationId = validRideLocations.get(0).getDestination().getId();
        Location destination = new Location(validRideLocations.get(0).getDestination());
        destination.setId(destinationId);

        FavoriteRoute route = new FavoriteRoute();
        route.setDeparture(departure);
        route.setDestination(destination);

        when(routeRepository.findByDeparture_IdAndDestination_Id(departureId, destinationId)).thenReturn(Optional.of(route));
        when(locationRepository.findById(departureId)).thenReturn(Optional.of(departure));
        when(locationRepository.findById(destinationId)).thenReturn(Optional.of(destination));
        when(routeRepository.save(any())).thenAnswer(call -> call.getArguments()[0]);

        ResponseStatusException thrown = Assertions.assertThrows(ResponseStatusException.class, () -> rideService.insertFavoriteRoute(validPassengerId, departureId, destinationId));
        Assertions.assertEquals("Route already added to favorites", thrown.getReason());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
    }
    @Test
    void insertFavoriteRouteInvalidDepartureTest() {
        int invalidDepartureId = 5;
        int destinationId = validRideLocations.get(0).getDestination().getId();
        Location destination = new Location(validRideLocations.get(0).getDestination());
        destination.setId(destinationId);

        when(routeRepository.findByDeparture_IdAndDestination_Id(invalidDepartureId, destinationId)).thenReturn(Optional.empty());
        when(locationRepository.findById(invalidDepartureId)).thenReturn(Optional.empty());
        when(locationRepository.findById(destinationId)).thenReturn(Optional.of(destination));
        when(routeRepository.save(any())).thenAnswer(call -> call.getArguments()[0]);

        ResponseStatusException thrown = Assertions.assertThrows(ResponseStatusException.class, () -> rideService.insertFavoriteRoute(validPassengerId, invalidDepartureId, destinationId));
        Assertions.assertEquals("Invalid locations", thrown.getReason());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
    }
    @Test
    void insertFavoriteRouteInvalidDestinationTest() {
        int departureId = validRideLocations.get(0).getDeparture().getId();
        Location departure = new Location(validRideLocations.get(0).getDeparture());
        departure.setId(departureId);
        int invalidDestinationId = 5;

        when(routeRepository.findByDeparture_IdAndDestination_Id(departureId, invalidDestinationId)).thenReturn(Optional.empty());
        when(locationRepository.findById(departureId)).thenReturn(Optional.of(departure));
        when(locationRepository.findById(invalidDestinationId)).thenReturn(Optional.empty());
        when(routeRepository.save(any())).thenAnswer(call -> call.getArguments()[0]);

        ResponseStatusException thrown = Assertions.assertThrows(ResponseStatusException.class, () -> rideService.insertFavoriteRoute(validPassengerId, departureId, invalidDestinationId));
        Assertions.assertEquals("Invalid locations", thrown.getReason());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
    }
    @Test
    void insertFavoriteRouteInvalidPassengerTest() {
        int departureId = validRideLocations.get(0).getDeparture().getId();
        Location departure = new Location(validRideLocations.get(0).getDeparture());
        departure.setId(departureId);
        int destinationId = validRideLocations.get(0).getDestination().getId();
        Location destination = new Location(validRideLocations.get(0).getDestination());
        destination.setId(destinationId);

        when(routeRepository.findByDeparture_IdAndDestination_Id(departureId, destinationId)).thenReturn(Optional.empty());
        when(locationRepository.findById(departureId)).thenReturn(Optional.of(departure));
        when(locationRepository.findById(destinationId)).thenReturn(Optional.of(destination));
        when(routeRepository.save(any())).thenAnswer(call -> call.getArguments()[0]);

        ResponseStatusException thrown = Assertions.assertThrows(ResponseStatusException.class, () -> rideService.insertFavoriteRoute(invalidPassengerId, departureId, destinationId));
        Assertions.assertEquals("Passenger not found!", thrown.getReason());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
    }
    @Test
    void insertFavoriteRouteValidTest() {
        int departureId = validRideLocations.get(0).getDeparture().getId();
        Location departure = new Location(validRideLocations.get(0).getDeparture());
        departure.setId(departureId);
        int destinationId = validRideLocations.get(0).getDestination().getId();
        Location destination = new Location(validRideLocations.get(0).getDestination());
        destination.setId(destinationId);

        when(routeRepository.findByDeparture_IdAndDestination_Id(departureId, destinationId)).thenReturn(Optional.empty());
        when(locationRepository.findById(departureId)).thenReturn(Optional.of(departure));
        when(locationRepository.findById(destinationId)).thenReturn(Optional.of(destination));
        when(routeRepository.save(any())).thenAnswer(call -> call.getArguments()[0]);

        FavoriteRoute favoriteRoute = rideService.insertFavoriteRoute(validPassengerId, departureId, destinationId);
        Assertions.assertEquals(validPassengerId, favoriteRoute.getPassenger().getId());
        Assertions.assertEquals(departureId, favoriteRoute.getDeparture().getId());
        Assertions.assertEquals(destinationId, favoriteRoute.getDestination().getId());
    }

    // ------------------------------------------ deleteFavoriteRoute ----------------------------------------------- //
    @Test
    void deleteFavoriteRouteInvalidRouteTest() {
        when(routeRepository.findById(invalidFavoriteRouteId)).thenReturn(Optional.empty());

        ResponseStatusException thrown = Assertions.assertThrows(ResponseStatusException.class, () -> rideService.deleteFavoriteRoute(validPassengerId, invalidFavoriteRouteId));
        Assertions.assertEquals("Favorite route not found!", thrown.getReason());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
    }
    @Test
    void deleteFavoriteRouteInvalidUserTest() {
        int departureId = validRideLocations.get(0).getDeparture().getId();
        Location departure = new Location(validRideLocations.get(0).getDeparture());
        departure.setId(departureId);

        int destinationId = validRideLocations.get(0).getDestination().getId();
        Location destination = new Location(validRideLocations.get(0).getDestination());
        destination.setId(destinationId);

        FavoriteRoute route = new FavoriteRoute();
        route.setId(validFavoriteRouteId);
        route.setDeparture(departure);
        route.setDestination(destination);
        route.setPassenger(validPassenger);

        when(routeRepository.findById(validFavoriteRouteId)).thenReturn(Optional.of(route));

        ResponseStatusException thrown = Assertions.assertThrows(ResponseStatusException.class, () -> rideService.deleteFavoriteRoute(invalidPassengerId, validFavoriteRouteId));
        Assertions.assertEquals("Invalid request!", thrown.getReason());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());

    }
    @Test
    void deleteFavoriteRouteValidTest() {
        int departureId = validRideLocations.get(0).getDeparture().getId();
        Location departure = new Location(validRideLocations.get(0).getDeparture());
        departure.setId(departureId);

        int destinationId = validRideLocations.get(0).getDestination().getId();
        Location destination = new Location(validRideLocations.get(0).getDestination());
        destination.setId(destinationId);

        FavoriteRoute route = new FavoriteRoute();
        route.setId(validFavoriteRouteId);
        route.setDeparture(departure);
        route.setDestination(destination);
        route.setPassenger(validPassenger);

        when(routeRepository.findById(validFavoriteRouteId)).thenReturn(Optional.of(route));

        FavoriteRoute deleted = rideService.deleteFavoriteRoute(validPassengerId, validFavoriteRouteId);

        Assertions.assertEquals(route, deleted);
    }

    // ------------------------------------------- getFavoriteRoutes ------------------------------------------------ //
    @Test
    void getFavoriteRoutesValidTest() {
        int departureId = validRideLocations.get(0).getDeparture().getId();
        Location departure = new Location(validRideLocations.get(0).getDeparture());
        departure.setId(departureId);

        int destinationId = validRideLocations.get(0).getDestination().getId();
        Location destination = new Location(validRideLocations.get(0).getDestination());
        destination.setId(destinationId);

        List<FavoriteRoute> favorite = new ArrayList<>();
        FavoriteRoute route = new FavoriteRoute();
        route.setId(validFavoriteRouteId);
        route.setDeparture(departure);
        route.setDestination(destination);
        route.setPassenger(validPassenger);
        favorite.add(route);

        when(routeRepository.findAllByPassenger_Id(validPassengerId)).thenReturn(favorite);

        List<FavoriteRoute> result = routeRepository.findAllByPassenger_Id(validPassengerId);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(route, result.get(0));
    }

    // ----------------------------------------------- cancelRide --------------------------------------------------- //
    @Test
    void cancelRideInvalidStateTest() {
        int invalidRideId = 555;
        Ride invalid = new Ride();
        invalid.setStatus("INVALID");
        invalid.setId(invalidRideId);
        when(rideRepository.findById(invalidRideId)).thenReturn(Optional.of(invalid));

        ResponseStatusException thrown = Assertions.assertThrows(ResponseStatusException.class, () ->
                rideService.cancelRide(invalidRideId, null));
        Assertions.assertEquals("Bad ride state", thrown.getReason());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
    }
    @Test
    void cancelRideInvalidPendingTest() {
        ReasonDTO reasonDTO = new ReasonDTO();
        reasonDTO.setReason("Cancellation reason");

        ResponseStatusException thrown = Assertions.assertThrows(ResponseStatusException.class, () ->
                rideService.cancelRide(pendingRideId, reasonDTO));
        Assertions.assertEquals("Bad ride state", thrown.getReason());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
    }
    @Test
    void cancelRideValidActiveTest() {
        ReasonDTO reasonDTO = new ReasonDTO();
        reasonDTO.setReason("Cancellation reason");

        int validRideID = 555;
        Ride invalid = new Ride();
        invalid.setStatus("ACCEPTED");
        invalid.setId(validRideID);
        when(rideRepository.findById(validRideID)).thenReturn(Optional.of(invalid));

        CreatedRideDTO canceledRide = rideService.cancelRide(validRideID, reasonDTO);
        Assertions.assertEquals(canceledRide.getStatus(), "REJECTED");
        verify(rideRepository).save(any());
        verify(rejectionRepository).save(any());
    }
    // ----------------------------------------------- startRide ---------------------------------------------------- //
    @Test
    void startRideValidPendingTest() {
        int rideId = 666;
        int vehicleMovementId = 6666;

        Ride ride = new Ride();
        Location locationOne = new Location();
        Location locationTwo = new Location();
        VehicleMovement vehicleMovement = new VehicleMovement();
        List<Location> locations = new ArrayList<Location>();

        locationTwo.setLatitude(55.555556);
        locationTwo.setLongitude(13.333333);
        locationOne.setLongitude(13.333333);
        locationOne.setLatitude(55.555555);
        locations.add(locationOne);
        locations.add(locationTwo);

        ride.setId(rideId);
        ride.setLocations(locations);
        ride.setStatus("PENDING");
        vehicleMovement.setId(vehicleMovementId);
        vehicleMovement.setRideId(rideId);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(vehicleMovementRepository.findByRideId(rideId)).thenReturn(Optional.of(vehicleMovement));

        CreatedRideDTO canceledRide = rideService.startRide(rideId);
        Assertions.assertEquals(canceledRide.getStatus(), "ACTIVE");
    }
    @Test
    void startRideValidTest() {
        int rideId = 666;
        int vehicleMovementId = 6666;

        Ride ride = new Ride();
        Location locationOne = new Location();
        Location locationTwo = new Location();
        VehicleMovement vehicleMovement = new VehicleMovement();
        List<Location> locations = new ArrayList<Location>();

        locationTwo.setLatitude(55.555556);
        locationTwo.setLongitude(13.333333);
        locationOne.setLongitude(13.333333);
        locationOne.setLatitude(55.555555);
        locations.add(locationOne);
        locations.add(locationTwo);

        ride.setId(rideId);
        ride.setLocations(locations);
        ride.setStatus("ACCEPTED");
        vehicleMovement.setId(vehicleMovementId);
        vehicleMovement.setRideId(rideId);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(vehicleMovementRepository.findByRideId(rideId)).thenReturn(Optional.of(vehicleMovement));

        CreatedRideDTO canceledRide = rideService.startRide(rideId);
        Assertions.assertEquals(canceledRide.getStatus(), "ACTIVE");
    }
    @Test
    void startRideInvalidStateTest() {
        int invalidRideId = 555;
        Ride invalid = new Ride();
        invalid.setStatus("INVALID");
        invalid.setId(invalidRideId);
        when(rideRepository.findById(invalidRideId)).thenReturn(Optional.of(invalid));

        ResponseStatusException thrown = Assertions.assertThrows(ResponseStatusException.class, () ->
                rideService.startRide(invalidRideId));
        Assertions.assertEquals("Bad ride state", thrown.getReason());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
    }
    // -------------------------------------------- getRidesForUser ------------------------------------------------- //
    @Test
    void getRidesForUserInvalidUserTest() {
        ResponseStatusException thrown = Assertions.assertThrows(ResponseStatusException.class, () ->
                rideService.getRidesForUser(invalidPassengerId, 0, 1));
        Assertions.assertEquals("User not found!", thrown.getReason());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
    }
    @Test
    void getRidesForPassengerValidTest() {
        List<Ride> rides = new ArrayList<>();
        rides.add(activeRide);
        Pageable requestPageable = PageRequest.of(1, 1);
        when(rideRepository.findByPassengerId(validPassengerId, requestPageable)).thenReturn(new PageImpl<>(rides));

        RidesDTO ridesDTO = rideService.getRidesForUser(validPassengerId, 1, 1);

        Assertions.assertEquals(1, ridesDTO.getTotalCount());
        Assertions.assertEquals(validPassengerId, ridesDTO.getResults().get(0).getPassengers().get(0).getId());
    }
    @Test
    void getRidesForDriverValidTest() {
        List<Ride> rides = new ArrayList<>();
        rides.add(activeRide);
        Pageable requestPageable = PageRequest.of(1, 1);
        when(rideRepository.findByDriverId(validDriverId, requestPageable)).thenReturn(new PageImpl<>(rides));

        RidesDTO ridesDTO = rideService.getRidesForUser(validDriverId, 1, 1);

        Assertions.assertEquals(1, ridesDTO.getTotalCount());
        Assertions.assertEquals(validDriverId, ridesDTO.getResults().get(0).getDriver().getId());
    }
    // ---------------------------------------------- rideUpdated --------------------------------------------------- //

    @Test
    void rideUpdatedWhenDriverIsNullTest() {
        int driverNullRideId = 666;
        Ride driverNullRide = new Ride();
        driverNullRide.setId(driverNullRideId);

        rideService.rideUpdated(driverNullRide);

        verify(template, never()).convertAndSend(any());
        verify(androidSocketHandler, never()).sendMessage(any(), any());
    }

    @Test
    void rideUpdatedWhenPassengerListIsEmptyTest() {
        int passengerListEmptyRideId = 666;
        int passengerListEmptyDriverId = 666;

        Ride passengerListEmptyRide = new Ride();
        Driver passengerListEmptyDriver = new Driver();
        List<Passenger> passengerListEmptyList = new ArrayList<Passenger>();

        passengerListEmptyDriver.setId(passengerListEmptyDriverId);
        passengerListEmptyRide.setId(passengerListEmptyRideId);
        passengerListEmptyRide.setDriver(passengerListEmptyDriver);
        passengerListEmptyRide.setPassengers(passengerListEmptyList);

        rideService.rideUpdated(passengerListEmptyRide);

        // Notifiy the driver
        verify(template).convertAndSend("queue/driver/" + passengerListEmptyDriverId, "ride," + passengerListEmptyRideId);
        verify(androidSocketHandler).sendMessage(passengerListEmptyDriverId, "ride," + passengerListEmptyRideId);
    }

    @Test
    void rideUpdatedWithOnePassengerTest() {
        int onePassengerRideId = 666;
        int onePassengerDriverId = 6666;
        int onePassengerPassengerId = 66666;

        Ride onePassengerRide = new Ride();
        Driver onePassengerDriver = new Driver();
        Passenger onePassengerPassenger = new Passenger();
        List<Passenger> onePassengerPassengerList = new ArrayList<Passenger>();

        onePassengerPassenger.setId(onePassengerPassengerId);
        onePassengerPassengerList.add(onePassengerPassenger);
        onePassengerDriver.setId(onePassengerDriverId);
        onePassengerRide.setId(onePassengerRideId);
        onePassengerRide.setDriver(onePassengerDriver);
        onePassengerRide.setPassengers(onePassengerPassengerList);

        rideService.rideUpdated(onePassengerRide);

        // Notified the driver
        verify(template).convertAndSend("queue/driver/" + onePassengerDriverId, "ride," + onePassengerRideId);
        verify(androidSocketHandler).sendMessage(onePassengerDriverId, "ride," + onePassengerRideId);

        // Notified the passenger
        verify(template).convertAndSend("queue/passenger/" + onePassengerPassengerId, "ride," + onePassengerRideId);
        verify(androidSocketHandler).sendMessage(onePassengerPassengerId, "ride," + onePassengerRideId);
    }

    @Test
    void rideUpdatedWithThreePassengersTest() {
        int threePassengerRideId = 666;
        int threePassengerDriverId = 6666;
        int threePassengerOnePassengerId = 66666;
        int threePassengerTwoPassengerId = 666666;
        int threePassengerThreePassengerId = 6666666;

        Ride threePassengerRide = new Ride();
        Driver threePassengerDriver = new Driver();
        Passenger threePassengerOnePassenger = new Passenger();
        Passenger threePassengerTwoPassenger = new Passenger();
        Passenger threePassengerThreePassenger = new Passenger();
        List<Passenger> threePassengerPassengerList = new ArrayList<Passenger>();

        threePassengerOnePassenger.setId(threePassengerOnePassengerId);
        threePassengerTwoPassenger.setId(threePassengerTwoPassengerId);
        threePassengerThreePassenger.setId(threePassengerThreePassengerId);
        threePassengerPassengerList.add(threePassengerOnePassenger);
        threePassengerPassengerList.add(threePassengerTwoPassenger);
        threePassengerPassengerList.add(threePassengerThreePassenger);
        threePassengerDriver.setId(threePassengerDriverId);
        threePassengerRide.setId(threePassengerRideId);
        threePassengerRide.setDriver(threePassengerDriver);
        threePassengerRide.setPassengers(threePassengerPassengerList);

        rideService.rideUpdated(threePassengerRide);

        // Notified the driver
        verify(template).convertAndSend("queue/driver/" + threePassengerDriverId, "ride," + threePassengerRideId);
        verify(androidSocketHandler).sendMessage(threePassengerDriverId, "ride," + threePassengerRideId);

        // Notified the passengers
        verify(template).convertAndSend("queue/passenger/" + threePassengerOnePassengerId, "ride," + threePassengerRideId);
        verify(androidSocketHandler).sendMessage(threePassengerOnePassengerId, "ride," + threePassengerRideId);
        verify(template).convertAndSend("queue/passenger/" + threePassengerTwoPassengerId, "ride," + threePassengerRideId);
        verify(androidSocketHandler).sendMessage(threePassengerTwoPassengerId, "ride," + threePassengerRideId);
        verify(template).convertAndSend("queue/passenger/" + threePassengerThreePassengerId, "ride," + threePassengerRideId);
        verify(androidSocketHandler).sendMessage(threePassengerThreePassengerId, "ride," + threePassengerRideId);
    }

    // ------------------------------------------------- panic ------------------------------------------------------ //

    @Test
    void panicRideNotActiveTest() {
        int rideId = 666;

        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setStatus("FINISHED");

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.panic(new ReasonDTO(), rideId, 0);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        Assertions.assertEquals("404 NOT_FOUND \"Cannot panic ride that is finished\"", exception.getMessage());
    }

    @Test
    void panicRideUserNotFoundTest() {
        int rideId = 666;
        int userId = 6666;

        Ride ride = new Ride();

        ride.setId(rideId);
        ride.setStatus("ACTIVE");

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.panic(new ReasonDTO(), rideId, userId);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        Assertions.assertEquals("404 NOT_FOUND \"User not found\"", exception.getMessage());
    }

    @Test
    void panicRideFromPassengerTest() {
        int rideId = 666;
        int userId = 6666;
        int driverId = 66666;
        int vehicleId = 666;
        int vehicleMovementId = 6666;
        String reasonString = "testReason";

        Ride ride = new Ride();
        Driver driver = new Driver();
        Vehicle vehicle = new Vehicle();
        Passenger passenger = new Passenger();
        ReasonDTO reason = new ReasonDTO();
        VehicleMovement vehicleMovement = new VehicleMovement();

        vehicle.setId(vehicleId);
        driver.setId(driverId);
        driver.setVehicle(vehicle);
        passenger.setId(userId);
        passenger.setRole(ERole.ROLE_PASSENGER);
        ride.setId(rideId);
        ride.setStatus("ACTIVE");
        ride.setDriver(driver);
        reason.setReason(reasonString);
        vehicleMovement.setRideId(rideId);
        vehicleMovement.setId(vehicleMovementId);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(userRepository.findById(userId)).thenReturn(Optional.of(passenger));
        when(passengerRepository.findById(userId)).thenReturn(Optional.of(passenger));
        when(vehicleMovementRepository.findByRideId(rideId)).thenReturn(Optional.of(vehicleMovement));

        PanicRideDTO dto = rideService.panic(reason, rideId, userId);
        Assertions.assertEquals(dto.getId(), 1);
        Assertions.assertEquals(dto.getRide().getId(), new CreatedRideDTO(ride).getId());
        Assertions.assertEquals(dto.getReason(), reasonString);
        Assertions.assertEquals(dto.getTime(), currentDateTime);
        Assertions.assertEquals(dto.getVehicleId(), vehicleId);
        Assertions.assertEquals(driver.getBusy(), false);
        Assertions.assertEquals(ride.getStatus(), "PANIC");
        verify(rideRepository).save(ride);
        verify(driverRepository).save(driver);
        verify(template).convertAndSend(any(), any(WebsocketMessage.class));
        verify(messageRepository).save(any());
        verify(vehicleMovementRepository).delete(vehicleMovement);
    }

    @Test
    void panicRideFromDriverTest() {
        int rideId = 666;
        int userId = 6666;
        int driverId = 66666;
        int vehicleId = 666;
        int vehicleMovementId = 6666;
        String reasonString = "testReason";

        Ride ride = new Ride();
        Driver driver = new Driver();
        Vehicle vehicle = new Vehicle();
        Passenger passenger = new Passenger();
        ReasonDTO reason = new ReasonDTO();
        VehicleMovement vehicleMovement = new VehicleMovement();

        vehicle.setId(vehicleId);
        driver.setId(driverId);
        driver.setVehicle(vehicle);
        passenger.setId(userId);
        passenger.setRole(ERole.ROLE_PASSENGER);
        ride.setId(rideId);
        ride.setStatus("ACTIVE");
        ride.setDriver(driver);
        reason.setReason(reasonString);
        vehicleMovement.setRideId(rideId);
        vehicleMovement.setId(vehicleMovementId);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(userRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(vehicleMovementRepository.findByRideId(rideId)).thenReturn(Optional.of(vehicleMovement));

        PanicRideDTO dto = rideService.panic(reason, rideId, driverId);
        Assertions.assertEquals(dto.getId(), 1);
        Assertions.assertEquals(dto.getRide().getId(), new CreatedRideDTO(ride).getId());
        Assertions.assertEquals(dto.getReason(), reasonString);
        Assertions.assertEquals(dto.getTime(), currentDateTime);
        Assertions.assertEquals(dto.getVehicleId(), vehicleId);
        Assertions.assertEquals(driver.getBusy(), false);
        Assertions.assertEquals(ride.getStatus(), "PANIC");
        verify(rideRepository).save(ride);
        verify(driverRepository).save(driver);
        verify(template).convertAndSend(any(), any(WebsocketMessage.class));
        verify(messageRepository).save(any());
        verify(vehicleMovementRepository).delete(vehicleMovement);
    }

    // --------------------------------------------- getPanicList --------------------------------------------------- //

    @Test
    void getPanicListEmptyTest() {
        when(rideRepository.findByStatus("PANIC")).thenReturn(new ArrayList<Ride>());
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.getPanicList();
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        Assertions.assertEquals("404 NOT_FOUND \"No panics\"", exception.getMessage());
    }

    @Test
    void getPanicListValidTest() {
        int panicRideId = 666;
        String rejectionReason = "testReason";

        Ride panicRide = new Ride();
        Rejection rejection = new Rejection();
        List<Ride> panicRides = new ArrayList<Ride>();

        rejection.setReason(rejectionReason);
        panicRide.setId(panicRideId);
        panicRide.setStatus("PANIC");
        panicRide.setPanic(true);
        panicRide.setRejection(rejection);
        panicRides.add(panicRide);

        when(rideRepository.findByStatus("PANIC")).thenReturn(panicRides);
        PanicListDTO dto = rideService.getPanicList();
        Assertions.assertEquals(dto.getTotalCount(), 1);
        Assertions.assertEquals(dto.getResults().get(0).getRide().getId(), panicRideId);
        Assertions.assertEquals(dto.getResults().get(0).getRide().getStatus(), "PANIC");
        Assertions.assertEquals(dto.getResults().get(0).getReason(), rejectionReason);
    }

    // ----------------------------------------- deleteVehicleMovement ---------------------------------------------- //
    @Test
    void deleteVehicleMovementNotFoundTest() {
        int rideId = 666;

        when(vehicleMovementRepository.findByRideId(rideId)).thenReturn(Optional.empty());
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.deleteVehicleMovement(rideId);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        Assertions.assertEquals("404 NOT_FOUND \"Vehicle movement couldnt be found\"", exception.getMessage());
    }

    @Test
    void deleteVehicleMovementValidTest() {
        int rideId = 666;

        VehicleMovement vehicleMovement = new VehicleMovement();

        vehicleMovement.setRideId(rideId);
        when(vehicleMovementRepository.findByRideId(rideId)).thenReturn(Optional.of(vehicleMovement));

        rideService.deleteVehicleMovement(rideId);
        verify(vehicleMovementRepository).delete(vehicleMovement);
    }

    // ------------------------------------------ makeVehicleMovement ----------------------------------------------- //

    @Test
    void makeVehicleMovementBadRequestTest() {
        int rideId = 666;

        Ride ride = new Ride();
        Driver driver = new Driver();
        Vehicle vehicle = new Vehicle();
        Location location = new Location();
        Location vehicleLocation = new Location();
        List<Location> locations = new ArrayList<Location>();

        location.setLatitude(1000);
        location.setLongitude(1000);
        locations.add(location);
        vehicleLocation.setLatitude(1000);
        vehicleLocation.setLongitude(1000);
        vehicle.setCurrentLocation(vehicleLocation);
        driver.setVehicle(vehicle);
        ride.setId(rideId);
        ride.setLocations(locations);
        ride.setDriver(driver);

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.makeVehicleMovement(ride);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        Assertions.assertEquals("400 BAD_REQUEST \"message: OSRM request failed\"", exception.getMessage());
    }

    @Test
    void makeVehicleMovementCloseLocationsTest() {
        int rideId = 666;

        Ride ride = new Ride();
        Driver driver = new Driver();
        Vehicle vehicle = new Vehicle();
        Location location = new Location();
        Location vehicleLocation = new Location();
        List<Location> locations = new ArrayList<Location>();

        location.setLongitude(13.333333);
        location.setLatitude(55.555556);
        locations.add(location);
        vehicleLocation.setLongitude(13.333333);
        vehicleLocation.setLatitude(55.555555);
        vehicle.setCurrentLocation(vehicleLocation);
        driver.setVehicle(vehicle);
        ride.setId(rideId);
        ride.setLocations(locations);
        ride.setDriver(driver);

        VehicleMovement vm = rideService.makeVehicleMovement(ride);

        Assertions.assertEquals(vm.getLocationList().size(), 2);
        Assertions.assertEquals(vm.getCurrent(), 0);
        Assertions.assertEquals(vm.getRideId(), rideId);
        verify(vehicleMovementRepository).save(any());
        verify(locationRepository, times(2)).save(any());
    }

    @Test
    void makeVehicleMovementFarLocationsTest() {
        int rideId = 666;

        Ride ride = new Ride();
        Driver driver = new Driver();
        Vehicle vehicle = new Vehicle();
        Location location = new Location();
        Location vehicleLocation = new Location();
        List<Location> locations = new ArrayList<Location>();

        location.setLongitude(13.433333);
        location.setLatitude(55.655556);
        locations.add(location);
        vehicleLocation.setLongitude(13.333333);
        vehicleLocation.setLatitude(55.555555);
        vehicle.setCurrentLocation(vehicleLocation);
        driver.setVehicle(vehicle);
        ride.setId(rideId);
        ride.setLocations(locations);
        ride.setDriver(driver);

        VehicleMovement vm = rideService.makeVehicleMovement(ride);

        Assertions.assertEquals(vm.getLocationList().size(), 32);
        Assertions.assertEquals(vm.getCurrent(), 0);
        Assertions.assertEquals(vm.getRideId(), rideId);
        verify(vehicleMovementRepository).save(any());
        verify(locationRepository, times(32)).save(any());
    }

    @Test
    void makeVehicleMovementSameLocationsTest() {
        int rideId = 666;

        Ride ride = new Ride();
        Driver driver = new Driver();
        Vehicle vehicle = new Vehicle();
        Location location = new Location();
        Location vehicleLocation = new Location();
        List<Location> locations = new ArrayList<Location>();

        location.setLongitude(13.333333);
        location.setLatitude(55.555555);
        locations.add(location);
        vehicleLocation.setLongitude(13.333333);
        vehicleLocation.setLatitude(55.555555);
        vehicle.setCurrentLocation(vehicleLocation);
        driver.setVehicle(vehicle);
        ride.setId(rideId);
        ride.setLocations(locations);
        ride.setDriver(driver);

        VehicleMovement vm = rideService.makeVehicleMovement(ride);

        Assertions.assertEquals(vm.getLocationList().size(), 2);
        Assertions.assertEquals(vm.getLocationList().get(0).getLatitude(), vm.getLocationList().get(1).getLatitude());
        Assertions.assertEquals(vm.getLocationList().get(0).getLongitude(), vm.getLocationList().get(1).getLongitude());
        Assertions.assertEquals(vm.getCurrent(), 0);
        Assertions.assertEquals(vm.getRideId(), rideId);
        verify(vehicleMovementRepository).save(any());
    }

    // ----------------------------------------- updateVehicleMovement ---------------------------------------------- //

    @Test
    void updateVehicleMovementNotFoundTest() {
        int rideId = 666;

        Ride ride = new Ride();
        ride.setId(rideId);

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.updateVehicleMovement(ride);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        Assertions.assertEquals("404 NOT_FOUND \"Vehicle movement couldnt be found\"", exception.getMessage());
    }

    @Test
    void updateVehicleMovementBadRequestTest() {
        int rideId = 666;

        Ride ride = new Ride();
        Location locationOne = new Location();
        Location locationTwo = new Location();
        VehicleMovement vehicleMovement = new VehicleMovement();
        List<Location> locations = new ArrayList<Location>();

        locationOne.setLatitude(1000);
        locationOne.setLongitude(1000);
        locationTwo.setLatitude(1000);
        locationTwo.setLongitude(1000);
        locations.add(locationOne);
        locations.add(locationTwo);

        ride.setId(rideId);
        ride.setLocations(locations);
        vehicleMovement.setRideId(rideId);

        when(vehicleMovementRepository.findByRideId(rideId)).thenReturn(Optional.of(vehicleMovement));

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideService.updateVehicleMovement(ride);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        Assertions.assertEquals("400 BAD_REQUEST \"message: OSRM request failed\"", exception.getMessage());
    }

    @Test
    void updateVehicleMovementFarLocationsTest() {
        int rideId = 666;
        int vehicleMovementId = 6666;

        Ride ride = new Ride();
        Location locationOne = new Location();
        Location locationTwo = new Location();
        VehicleMovement vehicleMovement = new VehicleMovement();
        List<Location> locations = new ArrayList<Location>();

        locationTwo.setLatitude(55.655556);
        locationTwo.setLongitude(13.433333);
        locationOne.setLongitude(13.333333);
        locationOne.setLatitude(55.555555);
        locations.add(locationOne);
        locations.add(locationTwo);

        ride.setId(rideId);
        ride.setLocations(locations);
        vehicleMovement.setId(vehicleMovementId);
        vehicleMovement.setRideId(rideId);

        when(vehicleMovementRepository.findByRideId(rideId)).thenReturn(Optional.of(vehicleMovement));

        VehicleMovement vm = rideService.updateVehicleMovement(ride);

        Assertions.assertEquals(vm.getId(), vehicleMovement.getId());
        Assertions.assertEquals(vm.getLocationList().size(), 32);
        Assertions.assertEquals(vm.getCurrent(), 0);
        Assertions.assertEquals(vm.getRideId(), rideId);
        verify(vehicleMovementRepository).save(any());
        verify(locationRepository, times(32)).save(any());
    }

    @Test
    void updateVehicleMovementCloseLocationsTest() {
        int rideId = 666;
        int vehicleMovementId = 6666;

        Ride ride = new Ride();
        Location locationOne = new Location();
        Location locationTwo = new Location();
        VehicleMovement vehicleMovement = new VehicleMovement();
        List<Location> locations = new ArrayList<Location>();

        locationTwo.setLatitude(55.555556);
        locationTwo.setLongitude(13.333333);
        locationOne.setLongitude(13.333333);
        locationOne.setLatitude(55.555555);
        locations.add(locationOne);
        locations.add(locationTwo);

        ride.setId(rideId);
        ride.setLocations(locations);
        vehicleMovement.setId(vehicleMovementId);
        vehicleMovement.setRideId(rideId);

        when(vehicleMovementRepository.findByRideId(rideId)).thenReturn(Optional.of(vehicleMovement));

        VehicleMovement vm = rideService.updateVehicleMovement(ride);

        Assertions.assertEquals(vm.getId(), vehicleMovement.getId());
        Assertions.assertEquals(vm.getLocationList().size(), 2);
        Assertions.assertEquals(vm.getCurrent(), 0);
        Assertions.assertEquals(vm.getRideId(), rideId);
        verify(vehicleMovementRepository).save(any());
        verify(locationRepository, times(2)).save(any());
    }

    @Test
    void updateVehicleMovementSameLocation() {
        int rideId = 666;
        int vehicleMovementId = 6666;

        Ride ride = new Ride();
        Location locationOne = new Location();
        Location locationTwo = new Location();
        VehicleMovement vehicleMovement = new VehicleMovement();
        List<Location> locations = new ArrayList<Location>();

        locationTwo.setLatitude(55.555555);
        locationTwo.setLongitude(13.333333);
        locationOne.setLongitude(13.333333);
        locationOne.setLatitude(55.555555);
        locations.add(locationOne);
        locations.add(locationTwo);

        ride.setId(rideId);
        ride.setLocations(locations);
        vehicleMovement.setId(vehicleMovementId);
        vehicleMovement.setRideId(rideId);

        when(vehicleMovementRepository.findByRideId(rideId)).thenReturn(Optional.of(vehicleMovement));

        VehicleMovement vm = rideService.updateVehicleMovement(ride);

        Assertions.assertEquals(vm.getId(), vehicleMovement.getId());
        Assertions.assertEquals(vm.getLocationList().size(), 2);
        Assertions.assertEquals(vm.getLocationList().get(0).getLatitude(), vm.getLocationList().get(1).getLatitude());
        Assertions.assertEquals(vm.getLocationList().get(0).getLongitude(), vm.getLocationList().get(1).getLongitude());
        Assertions.assertEquals(vm.getCurrent(), 0);
        Assertions.assertEquals(vm.getRideId(), rideId);
        verify(vehicleMovementRepository).save(any());
        verify(locationRepository, times(2)).save(any());
    }
}