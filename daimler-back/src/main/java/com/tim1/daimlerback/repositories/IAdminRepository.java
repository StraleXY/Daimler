package com.tim1.daimlerback.repositories;


import com.tim1.daimlerback.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAdminRepository extends JpaRepository<Admin, Integer> {

}
