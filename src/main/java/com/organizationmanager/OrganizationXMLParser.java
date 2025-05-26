package com.organizationmanager;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class OrganizationXMLParser {

    private final String source;

    public OrganizationXMLParser(String source) {
        this.source = source;
    }

    public Set<Organization> loadOrganizations() {
        Set<Organization> organizations = new TreeSet<>();
        InputStream is = null;

        try {
            // Try as file first
            File file = new File(source);
            if (file.exists()) {
                is = new FileInputStream(file);
            } else {
                // Fallback to classpath resource
                is = getClass().getResourceAsStream(source);
            }

            if (is == null) {
                logError("Cannot find XML source: " + source);
                return organizations;
            }

            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(is));
            document.getDocumentElement().normalize();

            NodeList nodes = document.getElementsByTagName("organization");

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    try {
                        Organization org = parseOrganization((Element) node);
                        if (org != null) {
                            organizations.add(org);
                        }
                    } catch (Exception e) {
                        // Suppress error or log it silently
                        logError("Skipping invalid organization: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            logError("Error loading XML: " + e.getMessage());
        } finally {
            closeQuietly(is);
        }

        return organizations;
    }

    private Organization parseOrganization(Element element) {
        try {
            Long id = safeParseLong(getElementText(element, "id"));
            String name = getElementText(element, "name");
            Element coords = getChildElement(element, "coordinates");

            Float x = safeParseFloat(getElementText(coords, "x"));
            Integer y = safeParseInt(getElementText(coords, "y"));

            // Null check for y
            if (y == null) {
                logError("Skipping invalid organization: 'y' is missing or invalid");
                return null;
            }

            Integer turnover = safeParseInt(getElementText(element, "annualTurnover"));
            if (turnover == null || turnover <= 0)
                throw new IllegalArgumentException("Invalid annualTurnover");

            OrganizationType type = null;
            String typeStr = getElementText(element, "type");
            if (typeStr != null && !typeStr.trim().isEmpty()) {
                try {
                    type = OrganizationType.fromString(typeStr);
                } catch (IllegalArgumentException ignored) {
                    // Invalid type → skip
                }
            }

            String zipCode = null;
            Element addressElement = getChildElement(element, "postalAddress");
            if (addressElement != null) {
                zipCode = getElementText(addressElement, "zipCode");
            }

            return new Organization(id, name, new Coordinates(x, y), turnover, type, new Address(zipCode));

        } catch (Exception e) {
            logError("Error parsing organization: " + e.getMessage());
            return null; // Skip this one
        }
    }

    // Helper methods

    private String getElementText(Element parent, String tagName) {
        try {
            NodeList list = parent.getElementsByTagName(tagName);
            if (list.getLength() == 0)
                return null;
            String text = list.item(0).getTextContent().trim();
            return text.isEmpty() ? null : text;
        } catch (Exception e) {
            return null;
        }
    }

    private Element getChildElement(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() == 0)
            return null;
        return (Element) list.item(0);
    }

    private Long safeParseLong(String s) {
        try {
            return s == null ? null : Long.valueOf(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private Integer safeParseInt(String s) {
        try {
            return s == null ? null : Integer.valueOf(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private Float safeParseFloat(String s) {
        try {
            return s == null ? null : Float.valueOf(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private void closeQuietly(Closeable c) {
        try {
            if (c != null)
                c.close();
        } catch (IOException ignored) {
        }
    }

    private void logError(String message) {
        // You can comment out this line to fully suppress errors
        System.err.println("[WARNING] " + message);
    }
}