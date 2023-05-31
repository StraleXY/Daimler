package com.tim1.daimlerback.entities;

import com.tim1.daimlerback.entities.enumeration.ERole;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("admin")
public class Admin extends User{
    public Admin() {
        setRole(ERole.ROLE_ADMIN);
    }
}
