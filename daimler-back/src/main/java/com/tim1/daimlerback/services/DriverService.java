package com.tim1.daimlerback.services;


import com.tim1.daimlerback.dtos.common.RegisterDTO;
import com.tim1.daimlerback.dtos.driver.*;
import com.tim1.daimlerback.dtos.passenger.UpdateUserDTO;
import com.tim1.daimlerback.entities.*;
import com.tim1.daimlerback.repositories.IDocumentRepository;
import com.tim1.daimlerback.repositories.IDriverRepository;
import com.tim1.daimlerback.repositories.IRideRepository;
import com.tim1.daimlerback.repositories.IWorkingHourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DriverService {

    @Autowired
    private IDriverRepository driverRepository;
    @Autowired
    private UpdateRequestService requestService;
    @Autowired
    private IRideRepository rideRepository;
    @Autowired
    private IDocumentRepository documentRepository;
    @Autowired
    private IWorkingHourRepository workingHourRepository;

    public DocumentDTO createDocument(Integer driverId, CreateDocumentDTO dto) {
        if (dto.getName() == null || dto.getName().length() > 50) {
            String value = "Bad input data";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        if (driverRepository.findById(driverId).isEmpty()) {
            String value = "Driver does not exist";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        if (dto.getDocumentImage() == null || dto.getName() == null) {
            String value = "Bad input data";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        Document document = new Document();
        document.setDocumentImage(dto.getDocumentImage());
        document.setName(dto.getName());
        document.setDriverId(driverId);
        return new DocumentDTO(documentRepository.save(document));
    }

    public DriversDTO getAllDrivers() {
        List<Driver> driverList = driverRepository.findAll();
        if (driverList.isEmpty()) {
            String value = "No drivers found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        DriversDTO dto = new DriversDTO();
        ArrayList<DriverDTO> drivers = new ArrayList<DriverDTO>();
        for (Driver d : driverList)
            drivers.add(new DriverDTO(d));
        dto.setResults(drivers);
        dto.setTotalCount(drivers.size());
        return dto;
    }

    public void deleteDocument(Integer documentId) {
        Optional<Document> document = documentRepository.findById(documentId);
        if (document.isEmpty()) {
            String value = "Document does not exist";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        Document d = document.get();
        documentRepository.delete(d);
    }

    public List<DocumentDTO> getDocuments(Integer driverId) {
        List<Document> documents = documentRepository.findByDriverId(driverId);
        List<DocumentDTO> dto = new ArrayList<DocumentDTO>();
        for (Document d : documents)
            dto.add(new DocumentDTO(d));
        return dto;
    }

    public Driver register(RegisterDTO registerDTO)  {
        Optional<Driver> driverCheck = driverRepository.findByEmail(registerDTO.getEmail());
        if (!driverCheck.isEmpty()) {
            String value = "poruka: Account with that email already exists";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }

        if (registerDTO.getAddress() == null || registerDTO.getEmail() == null || registerDTO.getName() == null
                || registerDTO.getSurname() == null || registerDTO.getProfilePicture() == null || registerDTO.getTelephoneNumber() == null) {
            String value = "Bad input data";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }

        Driver driver = new Driver();

        driver.setBlocked(false);
        driver.setName(registerDTO.getName());
        driver.setSurname(registerDTO.getSurname());
        driver.setProfilePicture(registerDTO.getProfilePicture());
        driver.setTelephoneNumber(registerDTO.getTelephoneNumber());
        driver.setEmail(registerDTO.getEmail());
        driver.setAddress(registerDTO.getAddress());
        driver.setPassword(registerDTO.getPassword());

        return save(driver);
    }

    public Driver getDriver(Integer id) {
        Optional<Driver> driver = driverRepository.findById(id);
        if(driver.isEmpty()) throw new  ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        else return driver.get();
    }

    public Driver update(Integer id, UpdateUserDTO userDTO) {
        Optional<Driver> driver = driverRepository.findById(id);
        if (driver.isEmpty()) {
            String value = "message: Invalid User";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        String newPassword = "";
        try {
            newPassword = requestService.get(id).getPassword();
            requestService.delete(id);
        } catch(Exception ex) {
            ex.printStackTrace();
        }


        Driver updated = driver.get();
        updated.setName(userDTO.getName());
        updated.setSurname(userDTO.getSurname());
        updated.setAddress(userDTO.getAddress());
        updated.setEmail(userDTO.getEmail());
        updated.setProfilePicture(userDTO.getProfilePicture());
        updated.setTelephoneNumber(userDTO.getTelephoneNumber());
        if(!newPassword.isEmpty()) updated.setPassword(newPassword);

        return save(updated);
    }

    public Driver save(Driver driver) {
        return driverRepository.save(driver);
    }

    public Page<Ride> getRides(Integer id, Integer page, Integer elementsPerPage, String sortBy) {
        if (driverRepository.findById(id).isEmpty()) {
            String value = "Driver not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        PageRequest query = PageRequest.of(page, elementsPerPage, Sort.by(sortBy));
        return rideRepository.findByDriverId(id, query);
    }
    
    public SimpleStatsDTO getSimpleStats(Integer userId, Long from, Long to) {
        SimpleStatsDTO stats = new SimpleStatsDTO();
        List<Ride> finished = rideRepository.findFinishedByDriverId(userId, from, to);
        List<Ride> rejected = rideRepository.findRejectedByDriverId(userId, from, to);
        stats.setAccepted(finished.size());
        stats.setRejected(rejected.size());
        stats.setEarned(finished.stream().mapToInt(Ride::getTotalCost).sum());
        stats.setWorkingHours(ThreadLocalRandom.current().nextInt(1, 10));
        return stats;
    }

    public GraphStatsDTO getGraphStats(Integer userId, Long from, Long to) {
        GraphStatsDTO stats = new GraphStatsDTO();
        Calendar startInterval = Calendar.getInstance();
        startInterval.setTimeInMillis(from);
        startInterval.add(Calendar.HOUR_OF_DAY, startInterval.get(Calendar.HOUR_OF_DAY));
        startInterval.add(Calendar.MINUTE, startInterval.get(Calendar.MINUTE));
        Calendar toInterval = Calendar.getInstance();
        toInterval.setTimeInMillis(startInterval.getTimeInMillis());
        toInterval.add(Calendar.DAY_OF_MONTH, 1);
        Calendar endInterval = Calendar.getInstance();
        endInterval.setTimeInMillis(to);
        endInterval.add(Calendar.DAY_OF_MONTH, 1);
        while (toInterval.getTimeInMillis() < endInterval.getTimeInMillis()) {
            List<Ride> finished = rideRepository.findFinishedByDriverId(userId, startInterval.getTimeInMillis(), toInterval.getTimeInMillis());
            stats.addRideDay(finished.size());
            if (finished.size() > 0) stats.addDistanceDay((int) finished.stream().mapToDouble(Ride::getDistance).sum());
            else stats.addDistanceDay(0);
            startInterval.add(Calendar.DAY_OF_MONTH, 1);
            toInterval.add(Calendar.DAY_OF_MONTH, 1);
        }
        return stats;
    }

    public WorkingHour createWorkingHour(Integer driverId, String start) {
        if (!workingHourRepository.findOngoingWorkingHourByDriverId(driverId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shift already ongoing!");
        }
        if (start == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter start is required!");
        }
        start = start.replace("T", " ").replace("Z", "");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println("Parsing " + start);
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(start, format);

            WorkingHour workingHour = new WorkingHour();
            workingHour.setDriverId(driverId);
            workingHour.setStart(startDateTime);
            workingHour.setEnd(null);

            workingHourRepository.save(workingHour);

            Optional<Driver> driver = driverRepository.findById(driverId);
            if (!driver.isEmpty()) {
                driver.get().setActive(true);
                driverRepository.save(driver.get());
            }

            return workingHour;
        } catch (DateTimeParseException exc) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid start date time!");
        }
    }

    public WorkingHour updateWorkingHour(Integer id, String end, User user) {
        Optional<WorkingHour> workingHourOpt = workingHourRepository.findById(id);
        if (workingHourOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Working hour doesn't exits!");
        }
        WorkingHour workingHour = workingHourOpt.get();
        System.out.println("Driver: " + workingHour.getDriverId() + " wh: " + workingHour.getId() + " user: " + user.getId());
        Integer driverId = workingHour.getDriverId();
        if (workingHourRepository.findOngoingWorkingHourByDriverId(driverId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No shift is ongoing!");
        }
        if (!driverId.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }
        if (workingHour.getEnd() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shift is not ongoing!");
        }
        if (end == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter end is required!");
        }
        end = end.replace("T", " ").replace("Z", "");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println("Parsing " + end);
        try {
            LocalDateTime endDateTime = LocalDateTime.parse(end, format);

            workingHour.setEnd(endDateTime);

            workingHourRepository.save(workingHour);

            Optional<Driver> driver = driverRepository.findById(user.getId());
            if (!driver.isEmpty()) {
                driver.get().setActive(false);
                driverRepository.save(driver.get());
            }

            return workingHour;
        } catch (DateTimeParseException exc) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid end date time!");
        }
    }

    public ArrayList<WorkingHourDTO> getWorkingHours(Integer id, Integer page, Integer size) {
        if (driverRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        }
        PageRequest query = PageRequest.of(page, size);
        return new ArrayList<WorkingHourDTO>(workingHourRepository.findWorkingHoursByDriverId(id, query).stream().map(WorkingHourDTO::new).collect(Collectors.toList()));
    }

    public WorkingHour getWorkingHour(Integer workingHourId) {
        Optional<WorkingHour> workingHourOpt = workingHourRepository.findById(workingHourId);
        if (workingHourOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Working hour doesn't exits!");
        }
        return workingHourOpt.get();
    }

    public double getTodayWorkingHours(Driver driver) {
        System.out.println("Begin calculating working hours");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
        now = now.plusDays(1);
        LocalDateTime to = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
        now = now.minusDays(1);

        List<WorkingHour> workingHours = workingHourRepository.findWorkingHoursInRange(driver.getId(), from, to);
        double total = 0.0;
        for (WorkingHour workingHour : workingHours) {
            LocalDateTime start = workingHour.getStart();
            LocalDateTime end = workingHour.getEnd();
            if (end == null) {
                end = now;
            }
            if (start.compareTo(from) < 0) {
                start = from;
            }
            if (end.compareTo(now) > 0) {
                end = now;
            }
            if (start.compareTo(end) < 0) {
                total += ((double)Duration.between(start, end).toMillis()) / 1000 / 60 / 60;
            }
        }
        System.out.println("End calculating working hours");
        return total;
    }
}
