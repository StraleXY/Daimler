package com.tim1.daimlerback.services;

import com.tim1.daimlerback.dtos.common.LocationDTO;
import com.tim1.daimlerback.dtos.vehicle.LatLongDTO;
import com.tim1.daimlerback.dtos.vehicle.VehicleLocationsDTO;
import com.tim1.daimlerback.dtos.vehicle.VehicleRegisterDTO;
import com.tim1.daimlerback.dtos.vehicle.VehicleRegisteredDTO;
import com.tim1.daimlerback.entities.Driver;
import com.tim1.daimlerback.entities.Location;
import com.tim1.daimlerback.entities.Vehicle;
import com.tim1.daimlerback.entities.VehicleMovement;
import com.tim1.daimlerback.repositories.IDriverRepository;
import com.tim1.daimlerback.repositories.ILocationRepository;
import com.tim1.daimlerback.repositories.IVehicleMovementRepository;
import com.tim1.daimlerback.repositories.IVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private IVehicleRepository vehicleRepository;
    @Autowired
    private ILocationRepository locationRepository;
    @Autowired
    private IDriverRepository driverRepository;
    @Autowired
    private IVehicleMovementRepository vehicleMovementRepository;

    public Vehicle updateDriversVehicle(Integer driverId, VehicleRegisterDTO registerDTO) {
        if (registerDTO.getBabyTransport() == null || registerDTO.getPetTransport() == null ||
                registerDTO.getCurrentLocation() == null || registerDTO.getModel() == null || registerDTO.getVehicleType() == null
                || registerDTO.getPassengerSeats() == null || registerDTO.getLicenseNumber() == null) {
            String value = "Bad input data";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }

        Optional<Vehicle> vehicle = vehicleRepository.findByDriver_Id(driverId);
        if (vehicle.isEmpty()) {
            String value = "Vehicle not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        Optional<Driver> driver = driverRepository.findById(driverId);
        if (driver.isEmpty()) {
            String value = "Driver not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        Driver d = driver.get();
        Vehicle v = vehicle.get();
        v.setBabyTransport(registerDTO.getBabyTransport());
        v.setPetTransport(registerDTO.getPetTransport());
        v.setPassengerSeats(registerDTO.getPassengerSeats());
        v.setModel(registerDTO.getModel());
        v.setVehicleType(registerDTO.getVehicleType());
        v.setLicenseNumber(registerDTO.getLicenseNumber());
        Location l = new Location(registerDTO.getCurrentLocation());
        locationRepository.save(l);
        v.setCurrentLocation(l);
        v.setDriver(d);
        return vehicleRepository.save(v);
    }

    public Vehicle register(Integer driverId, VehicleRegisterDTO registerDTO)  {
        Optional<Driver> driverCheck = driverRepository.findById(driverId);
        if (driverCheck.isEmpty()) {
            String value = "poruka: Driver with that id does not exist";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }

        if (registerDTO.getBabyTransport() == null || registerDTO.getPetTransport() == null ||
        registerDTO.getCurrentLocation() == null || registerDTO.getModel() == null || registerDTO.getVehicleType() == null
        || registerDTO.getPassengerSeats() == null || registerDTO.getLicenseNumber() == null) {
            String value = "Bad input data";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }

        Driver driver = driverCheck.get();
        Vehicle vehicle = new Vehicle();

        vehicle.setBabyTransport(registerDTO.getBabyTransport());
        vehicle.setPetTransport(registerDTO.getPetTransport());
        vehicle.setPassengerSeats(registerDTO.getPassengerSeats());
        vehicle.setModel(registerDTO.getModel());
        vehicle.setVehicleType(registerDTO.getVehicleType());
        vehicle.setLicenseNumber(registerDTO.getLicenseNumber());
        Location l = new Location(registerDTO.getCurrentLocation());
        locationRepository.save(l);
        vehicle.setCurrentLocation(l);
        vehicle.setDriver(driver);

        return save(vehicle);
    }

    public VehicleLocationsDTO getLocations() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        ArrayList<LatLongDTO> latLong = new ArrayList<LatLongDTO>();
        for (Vehicle v : vehicles) {
            LatLongDTO dto = new LatLongDTO();
            dto.setVehicleId(v.getId());
            dto.setLatitude(v.getCurrentLocation().getLatitude());
            dto.setLongitude(v.getCurrentLocation().getLongitude());
            if (v.getDriver() != null) dto.setBusy(v.getDriver().getBusy());
            else dto.setBusy(true);
            latLong.add(dto);
        }
        VehicleLocationsDTO ret = new VehicleLocationsDTO();
        ret.setLocations(latLong);
        return ret;
    }

    public Vehicle getDriversVehicle(Integer driverId) {
        Optional<Vehicle> vehicle = vehicleRepository.findByDriver_Id(driverId);
        if (vehicle.isEmpty()) throw new  ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found!");
        else return vehicle.get();
    }

    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public boolean updateLocation(LocationDTO dto, Integer id) {
        if(!checkLatLon(dto.getLatitude(), dto.getLongitude())) {
            String value = "Invalid input";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }

        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isEmpty())
            return false;

        Vehicle v = vehicle.get();
        Location l = new Location(dto);
        locationRepository.save(l);
        v.setCurrentLocation(l);
        vehicleRepository.save(v);
        return true;
    }

    public boolean checkLatLon(Double lat, Double lon) {
        return lat <= 90 && lat >= -90 && lon < 180 && lon >= -180;
    }
}
