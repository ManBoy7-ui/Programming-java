package com.organizationmanager;

import java.util.Arrays;

public enum OrganizationType {
    COMMERCIAL,
    PUBLIC,
    GOVERNMENT,
    TRUST,
    PRIVATE_LIMITED_COMPANY;

    public static OrganizationType fromString(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid organization type. Valid types are: " +
                            Arrays.toString(values())
            );
        }
    }
}