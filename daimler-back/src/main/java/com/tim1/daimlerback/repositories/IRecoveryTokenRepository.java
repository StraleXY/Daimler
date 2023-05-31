package com.tim1.daimlerback.repositories;

import com.tim1.daimlerback.utils.RecoveryToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRecoveryTokenRepository extends JpaRepository<RecoveryToken, Integer> {
    Optional<RecoveryToken> findByToken(String token);
}
