package com.tim1.daimlerback.repositories;


import com.tim1.daimlerback.entities.Document;
import com.tim1.daimlerback.entities.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IDocumentRepository extends JpaRepository<Document, Integer> {
    List<Document> findByDriverId(Integer id);
}
