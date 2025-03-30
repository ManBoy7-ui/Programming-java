package com.organizationmanager;

public class Address {
    private final String zipCode; // Can be null

    public Address(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getZipCode() { return zipCode; }

    @Override
    public String toString() {
        return zipCode != null ? zipCode : "null";
    }
}