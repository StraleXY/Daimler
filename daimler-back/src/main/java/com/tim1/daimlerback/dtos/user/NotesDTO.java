package com.tim1.daimlerback.dtos.user;

import java.util.List;

public class NotesDTO {
    private Integer totalCount;
    private List<NoteDTO> results;
    public NotesDTO() {

    }

    public NotesDTO(List<NoteDTO> noteDTOS) {
        this.totalCount = noteDTOS.size();
        this.results = noteDTOS;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public List<NoteDTO> getResults() {
        return results;
    }

    public void setResults(List<NoteDTO> results) {
        this.results = results;
    }
}
