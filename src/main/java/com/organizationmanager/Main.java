package com.organizationmanager;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        String resourcePath = "/organizations.xml";
        String saveFile = "output.xml";

        Set<Organization> organizations;

        if (args.length > 0) {
            // External file provided
            OrganizationXMLParser parser = new OrganizationXMLParser(args[0]);
            organizations = parser.loadOrganizations();
            saveFile = args.length > 1 ? args[1] : args[0];
        } else {
            // Fallback to internal resource
            OrganizationXMLParser parser = new OrganizationXMLParser(resourcePath);
            organizations = parser.loadOrganizations();
        }

        OrganizationManager manager = new OrganizationManager(organizations, saveFile);
        manager.startInteractiveMode();
    }
}
