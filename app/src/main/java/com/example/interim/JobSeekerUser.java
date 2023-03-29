package com.example.interim;

public class JobSeekerUser {
    private String name;
    private String firstName;
    private String nationality;
    private String phoneNumber;
    private String birthdate;
    private String city;

    public JobSeekerUser() {
        // Default constructor required for Firestore
    }

    public JobSeekerUser(String name, String firstName, String nationality, String phoneNumber, String birthdate, String city) {
        this.name = name;
        this.firstName = firstName;
        this.nationality = nationality;
        this.phoneNumber = phoneNumber;
        this.birthdate = birthdate;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
