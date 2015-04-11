package com.lenddo.data.models;

/**
 * Created by joseph on 6/3/14.
 */
public class AndroidAttendee {
    private boolean isOrganizer;
    private long id;
    private String email;
    private String name;
    private String status;
    private String relationship;
    private String type;

    public void setIsOrganizer(boolean isOrganizer) {
        this.isOrganizer = isOrganizer;
    }

    public boolean isOrganizer() {
        return isOrganizer;
    }

    public void setOrganizer(boolean isOrganizer) {
        this.isOrganizer = isOrganizer;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
