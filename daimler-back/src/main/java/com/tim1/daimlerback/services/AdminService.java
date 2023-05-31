package com.tim1.daimlerback.services;

import com.tim1.daimlerback.dtos.admin.UpdateAdminDTO;
import com.tim1.daimlerback.entities.Admin;
import com.tim1.daimlerback.repositories.IAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private IAdminRepository adminRepository;

    public Admin get(int id) {
        Optional<Admin> admin = adminRepository.findById(id);
        if (admin.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        else return admin.get();
    }

    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }

    public Admin update(Integer id, UpdateAdminDTO adminDTO) {
        Optional<Admin> admin = adminRepository.findById(id);
        if (admin.isEmpty()) {
            String value = "poruka: Invalid User";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }

        Admin updated = admin.get();
        updated.setName(adminDTO.getName());
        updated.setSurname(adminDTO.getSurname());
        updated.setEmail(adminDTO.getEmail());
        if(!adminDTO.getPassword().isEmpty()) updated.setPassword(adminDTO.getPassword());

        return save(updated);
    }
}
