package com.organizationmanager;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OrganizationXMLParser {
    private final File xmlFile;

    public OrganizationXMLParser(String fileName) {
        this.xmlFile = new File(fileName);
    }

    public Set<Organization> loadOrganizations() {
        Set<Organization> organizations = new TreeSet<>();
        if (!xmlFile.exists() || !xmlFile.canRead()) {
            System.err.println("Error: Cannot access file " + xmlFile.getName());
            return organizations;
        }

        try {
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList nodes = document.getElementsByTagName("organization");
            for (int i = 0; i < nodes.getLength(); i++) {
                try {
                    Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Organization org = parseOrganization((Element) node);
                        if (org != null) organizations.add(org);
                    }
                } catch (Exception e) {
                    System.err.println("Skipping invalid organization: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading XML: " + e.getMessage());
        }
        return organizations;
    }

    private Organization parseOrganization(Element element) throws IllegalArgumentException {
        Long id = parseLong(element, "id");
        String name = parseString(element, "name");

        Element coords = getChildElement(element, "coordinates");
        Float x = parseFloat(coords, "x");
        int y = parseInt(coords, "y");

        int turnover = parseInt(element, "annualTurnover");
        if (turnover <= 0) throw new IllegalArgumentException("Annual turnover must be positive");

        OrganizationType type = null;
        Node typeNode = element.getElementsByTagName("type").item(0);
        if (typeNode != null && !typeNode.getTextContent().isEmpty()) {
            type = OrganizationType.fromString(typeNode.getTextContent());
        }

        String zipCode = null;
        Node addressNode = element.getElementsByTagName("postalAddress").item(0);
        if (addressNode != null) {
            Node zipNode = ((Element)addressNode).getElementsByTagName("zipCode").item(0);
            if (zipNode != null) zipCode = zipNode.getTextContent();
        }

        return new Organization(
                id,
                name,
                new Coordinates(x, y),
                turnover,
                type,
                new Address(zipCode)
        );
    }

    // Helper parsing methods...
    private Element getChildElement(Element parent, String tagName) {
        Node node = parent.getElementsByTagName(tagName).item(0);
        if (node == null) throw new IllegalArgumentException("Missing " + tagName + " element");
        return (Element) node;
    }

    private String parseString(Element element, String tagName) {
        String value = element.getElementsByTagName(tagName).item(0).getTextContent().trim();
        if (value.isEmpty()) throw new IllegalArgumentException(tagName + " cannot be empty");
        return value;
    }

    private Long parseLong(Element element, String tagName) {
        return Long.parseLong(parseString(element, tagName));
    }

    private Float parseFloat(Element element, String tagName) {
        return Float.parseFloat(parseString(element, tagName));
    }

    private int parseInt(Element element, String tagName) {
        return Integer.parseInt(parseString(element, tagName));
    }
}