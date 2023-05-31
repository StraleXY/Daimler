package com.tim1.daimlerback.repositories;

import com.tim1.daimlerback.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface IMessageRepository extends JpaRepository<Message, Integer> {
    List<Message> getAllByReceiverIdInAndSenderIdInAndTypeAndRideId(Collection<Integer> ids1, Collection<Integer> ids2, String type, Integer rideId);

    @Query(value = "SELECT DISTINCT ON (CASE WHEN (sender_id >= receiver_id) THEN (sender_id, receiver_id, type, ride_id) ELSE (receiver_id, sender_id, type, ride_id) END ) * FROM Message WHERE sender_id = :userId OR receiver_id = :userId ORDER BY timestamp DESC", nativeQuery = true)
    List<Message> getAllLastMessages(Integer userId);
    
    List<Message> findBySenderId(Integer id);
    
    List<Message> findByReceiverId(Integer id);
}