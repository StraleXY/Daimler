package com.tim1.daimlerback.dtos.driver;

public class CreateDocumentDTO {
    private String name;
    private String documentImage;

    public CreateDocumentDTO() {

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
}
