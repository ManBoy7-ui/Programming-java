package com.organizationmanager;

import java.util.TreeSet;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Error: Please provide the XML file name as a command-line argument.");
            System.err.println("Usage: java com.organizationmanager.Main <filename.xml>");
            return;
        }

        String fileName = args[0];
        OrganizationXMLParser parser = new OrganizationXMLParser(fileName);
        TreeSet<Organization> organizations = parser.loadOrganizations();

        System.out.println("Debug: Loaded " + organizations.size() + " organizations");

        OrganizationManager manager = new OrganizationManager(organizations);
        manager.startInteractiveMode();
    }
}