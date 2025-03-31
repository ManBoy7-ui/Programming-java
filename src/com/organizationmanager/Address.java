package com.organizationmanager;

public class Address {
    private String zipCode; // Can be null

    public Address(String zipCode) {
        this.zipCode = zipCode;
    }

    // Getter
    public String getZipCode() { return zipCode; }
}