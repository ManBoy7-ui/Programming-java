package com.organizationmanager;

/**
 * @param zipCode
 *            Can be null
 */
public record Address(String zipCode) {

    @Override
    public String toString() {
        return zipCode != null ? zipCode : "null";
    }
}