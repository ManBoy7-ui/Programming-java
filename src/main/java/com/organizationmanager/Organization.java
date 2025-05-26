package com.organizationmanager;

import java.time.ZonedDateTime;
import java.util.Objects;

public class Organization implements Comparable<Organization> {
    private final Long id;
    private final String name;
    private final Coordinates coordinates;
    private final ZonedDateTime creationDate;
    private final Integer annualTurnover;
    private final OrganizationType type;
    private final Address postalAddress;

    public Organization(Long id, String name, Coordinates coordinates, Integer annualTurnover, OrganizationType type,
            Address postalAddress) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        if (id <= 0)
            throw new IllegalArgumentException("ID must be positive");

        this.name = Objects.requireNonNull(name, "Name cannot be null");
        if (name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");

        this.coordinates = Objects.requireNonNull(coordinates, "Coordinates cannot be null");
        this.annualTurnover = Objects.requireNonNull(annualTurnover, "Annual turnover cannot be null");
        if (annualTurnover <= 0)
            throw new IllegalArgumentException("Annual turnover must be positive");

        this.type = type; // Can be null
        this.postalAddress = Objects.requireNonNull(postalAddress, "Postal address cannot be null");
        this.creationDate = ZonedDateTime.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public Integer getAnnualTurnover() {
        return annualTurnover;
    }

    public OrganizationType getType() {
        return type;
    }

    public Address getPostalAddress() {
        return postalAddress;
    }

    @Override
    public int compareTo(Organization o) {
        return this.id.compareTo(o.id);
    }

    @Override
    public String toString() {
        return String.format(
                "Organization[id=%d, name='%s', coordinates=%s, created=%s, " + "turnover=%d, type=%s, address=%s]", id,
                name, coordinates, creationDate, annualTurnover, type, postalAddress);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Organization that))
            return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}