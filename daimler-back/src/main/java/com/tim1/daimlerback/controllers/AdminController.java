package com.tim1.daimlerback.controllers;

import com.tim1.daimlerback.dtos.admin.AdminDTO;
import com.tim1.daimlerback.dtos.admin.UpdateAdminDTO;
import com.tim1.daimlerback.dtos.driver.DriverDTO;
import com.tim1.daimlerback.dtos.passenger.UpdateUserDTO;
import com.tim1.daimlerback.entities.Admin;
import com.tim1.daimlerback.entities.Driver;
import com.tim1.daimlerback.services.AdminService;
import com.tim1.daimlerback.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.*;

@RestController
@RequestMapping(value = "api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private DriverService driverService;

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AdminDTO> getPassenger(@PathVariable Integer id) {
        return new ResponseEntity<>(new AdminDTO(adminService.get(id)), HttpStatus.OK);
    }

    @PutMapping(value = "{id}", consumes = "application/json")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AdminDTO> updatePassenger(@PathVariable Integer id, @RequestBody UpdateAdminDTO adminDTO) {
        Admin admin = adminService.update(id, adminDTO);
        return new ResponseEntity<>(new AdminDTO(admin), HttpStatus.OK);
    }

    @PutMapping(value = "request/{id}", consumes = "application/json")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DriverDTO> approveUpdateDriver(@PathVariable Integer id, @RequestBody UpdateUserDTO driverDTO) {
        Driver driver = driverService.update(id, driverDTO);
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

}
