package com.tim1.daimlerback.dtos.user;

import com.tim1.daimlerback.entities.Note;

public class NoteDTO {
    private Integer id;
    private String date;
    private String message;

    public NoteDTO() {
    }

    public NoteDTO(Note note) {
        this.id = note.getId();
        this.date = note.getDate();
        this.message = note.getMessage();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
