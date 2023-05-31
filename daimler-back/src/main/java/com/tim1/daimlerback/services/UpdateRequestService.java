package com.tim1.daimlerback.services;

import com.tim1.daimlerback.dtos.passenger.UpdateUserDTO;
import com.tim1.daimlerback.entities.DriverUpdateRequest;
import com.tim1.daimlerback.repositories.IDriverUpdateRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UpdateRequestService {

    @Autowired
    private IDriverUpdateRequestRepository requestRepository;

    public List<DriverUpdateRequest> get() {
        return requestRepository.findAll();
    }

    public DriverUpdateRequest get(Integer driverId) {
        Optional<DriverUpdateRequest> request = requestRepository.findByDriverId(driverId);
        if(request.isEmpty()) return null;
        return request.get();
    }

    public void insert(Integer driverId, UpdateUserDTO driverDTO) {
        Optional<DriverUpdateRequest> request = requestRepository.findByDriverId(driverId);
        DriverUpdateRequest upd = new DriverUpdateRequest(driverId, driverDTO);
        if(!request.isEmpty()) upd.setId(request.get().getId());
        requestRepository.save(upd);
    }

    public void delete(Integer driverId) {
        Optional<DriverUpdateRequest> request = requestRepository.findByDriverId(driverId);
        if(request.isEmpty()) {
            String value = "poruka: Invalid Request";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        requestRepository.delete(request.get());
    }
}
