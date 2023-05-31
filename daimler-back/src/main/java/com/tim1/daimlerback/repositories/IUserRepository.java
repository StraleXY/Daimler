package com.tim1.daimlerback.repositories;

import com.tim1.daimlerback.entities.User;
import com.tim1.daimlerback.entities.enumeration.ERole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {
    public Optional<User> findByEmail(String email);
    public Optional<User> findById(Integer id);
    public Page<User> findAllByRoleIn(Collection<ERole> roles, PageRequest pageRequest);
}