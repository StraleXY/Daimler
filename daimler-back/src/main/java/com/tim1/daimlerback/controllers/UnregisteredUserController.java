package com.tim1.daimlerback.controllers;

import com.tim1.daimlerback.dtos.common.AssumptionDTO;
import com.tim1.daimlerback.dtos.common.EstimationDTO;
import com.tim1.daimlerback.services.EstimationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/unregisteredUser/")
public class UnregisteredUserController {
    @Autowired
    private EstimationService estimationService;
    @PostMapping(consumes = "application/json")
    public ResponseEntity<EstimationDTO> getEstimate(@RequestBody AssumptionDTO dto) {
        EstimationDTO ret = estimationService.getEstimate(dto);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }
}
