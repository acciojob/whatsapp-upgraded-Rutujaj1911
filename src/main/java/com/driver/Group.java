package com.driver;

import org.springframework.beans.factory.annotation.Autowired;

public class Group {


    public String name;
    public int numberOfParticipants;

    public Group(){

    }
    public Group(String name,int numberOfParticipants) {
        this.name = name;
        this.numberOfParticipants=numberOfParticipants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }
}
