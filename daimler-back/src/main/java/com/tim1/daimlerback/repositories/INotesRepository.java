package com.tim1.daimlerback.repositories;

import com.tim1.daimlerback.entities.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface INotesRepository extends JpaRepository<Note, Integer> {

    Page<Note> findAllByUserId(Integer userId, PageRequest pageRequest);
}
