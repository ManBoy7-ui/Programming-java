package com.organizationmanager;

import java.time.ZonedDateTime;

public class Organization implements Comparable<Organization> {
    private Long id; // Automatically generated, unique, > 0
    private String name; // Cannot be null or empty
    private Coordinates coordinates; // Cannot be null
    private ZonedDateTime creationDate; // Automatically generated
    private Integer annualTurnover; // Cannot be null, > 0
    private OrganizationType type; // Can be null
    private Address postalAddress; // Cannot be null

    public Organization(Long id, String name, Coordinates coordinates, Integer annualTurnover, OrganizationType type, Address postalAddress) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = ZonedDateTime.now(); // Automatically generated
        this.annualTurnover = annualTurnover;
        this.type = type;
        this.postalAddress = postalAddress;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public ZonedDateTime getCreationDate() { return creationDate; }
    public Integer getAnnualTurnover() { return annualTurnover; }
    public OrganizationType getType() { return type; }
    public Address getPostalAddress() { return postalAddress; }

    @Override
    public int compareTo(Organization o) {
        return this.id.compareTo(o.id); // Default sorting by id
    }

    @Override
    public String toString() {
        return String.format("Organization[id=%d, name=%s, creationDate=%s]", id, name, creationDate);
    }
}