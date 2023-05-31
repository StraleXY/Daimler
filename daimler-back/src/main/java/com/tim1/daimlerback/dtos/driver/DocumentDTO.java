package com.tim1.daimlerback.dtos.driver;

import com.tim1.daimlerback.entities.Document;

public class DocumentDTO {
    private Integer id;
    private String name;
    private String documentImage;
    private Integer driverId;

    public DocumentDTO(){

    }

    public DocumentDTO(Document document){
        this.id = document.getId();
        this.name = document.getName();
        this.documentImage = document.getDocumentImage();
        this.driverId = document.getDriverId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentImage() {
        return documentImage;
    }

    public void setDocumentImage(String documentImage) {
        this.documentImage = documentImage;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }
}
