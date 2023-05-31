package com.tim1.daimlerback.repositories;

import com.tim1.daimlerback.entities.Passenger;
import com.tim1.daimlerback.utils.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITokenRepository extends JpaRepository<VerificationToken, Integer> {
    Optional<VerificationToken> findByToken(String token);
}