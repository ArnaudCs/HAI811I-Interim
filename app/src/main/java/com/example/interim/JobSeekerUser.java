package com.example.interim;

public class JobSeekerUser {
    private String name;
    private String firstName;
    private String nationality;
    private String email;
    private String phoneNumber;
    private String birthdate;
    private String city;
    private String password;

    public JobSeekerUser() {
        // Default constructor required for Firestore
    }

    public JobSeekerUser(String name, String firstName, String nationality, String email, String phoneNumber, String birthdate, String city, String password) {
        this.name = name;
        this.firstName = firstName;
        this.nationality = nationality;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthdate = birthdate;
        this.city = city;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
