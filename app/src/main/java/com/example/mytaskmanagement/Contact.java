package com.example.mytaskmanagement;

public class Contact {

    String username;
    String emailContact;
    String descriptionContact;
    String telephoneContact;
    String img_url;
    String owner;
    Boolean isFavoris;

    public Contact() {
    }

    public Contact(String username, String emailContact, String descriptionContact, String telephoneContact, String img_url, String owner, Boolean isFavoris) {
        this.username = username;
        this.emailContact = emailContact;
        this.descriptionContact = descriptionContact;
        this.telephoneContact = telephoneContact;
        this.img_url = img_url;
        this.owner = owner;
        this.isFavoris = isFavoris;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailContact() {
        return emailContact;
    }

    public void setEmailContact(String emailContact) {
        this.emailContact = emailContact;
    }

    public String getDescriptionContact() {
        return descriptionContact;
    }

    public void setDescriptionContact(String descriptionContact) {
        this.descriptionContact = descriptionContact;
    }

    public String getTelephoneContact() {
        return telephoneContact;
    }

    public void setTelephoneContact(String telephoneContact) {
        this.telephoneContact = telephoneContact;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Boolean getFavoris() {
        return isFavoris;
    }

    public void setFavoris(Boolean favoris) {
        isFavoris = favoris;
    }
}
