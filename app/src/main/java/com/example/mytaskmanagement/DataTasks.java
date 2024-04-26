package com.example.mytaskmanagement;

public class DataTasks {

    private String dataTitle;
    private String dataDesc;
    private String dataDeadline;
    private String dataImage;
    private String owner;

    public DataTasks(String dataTitle, String dataDesc, String dataDeadline, String dataImage,String owner) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataDeadline = dataDeadline;
        this.dataImage = dataImage;
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public void setDataTitle(String dataTitle) {
        this.dataTitle = dataTitle;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public void setDataDesc(String dataDesc) {
        this.dataDesc = dataDesc;
    }

    public String getDataDeadline() {
        return dataDeadline;
    }

    public void setDataDeadline(String dataDeadline) {
        this.dataDeadline = dataDeadline;
    }

    public String getDataImage() {
        return dataImage;
    }

    public void setDataImage(String dataImage) {
        this.dataImage = dataImage;
    }
}
