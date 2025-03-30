package com.organizationmanager;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java Main <inputFile.xml> [saveFile.xml]");
            System.err.println("If saveFile is omitted, defaults to inputFile");
            return;
        }

        String inputFile = args[0];
        String saveFile = args.length > 1 ? args[1] : inputFile;

        OrganizationXMLParser parser = new OrganizationXMLParser(inputFile);
        Set<Organization> organizations = parser.loadOrganizations();

        OrganizationManager manager = new OrganizationManager(organizations, saveFile);
        manager.startInteractiveMode();
    }
}