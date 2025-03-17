package com.organizationmanager;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OrganizationXMLParser {
    private File xmlFile;

    public OrganizationXMLParser(String fileName) {
        this.xmlFile = new File(fileName);
    }

    public TreeSet<Organization> loadOrganizations() {
        TreeSet<Organization> organizations = new TreeSet<>();
        try {
            if (!xmlFile.exists()) {
                System.err.println("Error: The file " + xmlFile.getName() + " does not exist.");
                return organizations;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("organization");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Organization org = parseOrganization(element);
                    if (org != null) {
                        organizations.add(org);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading XML file: " + e.getMessage());
        }
        return organizations;
    }

    private Organization parseOrganization(Element element) {
        try {
            Long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
            String name = element.getElementsByTagName("name").item(0).getTextContent();

            Element coordinatesElement = (Element) element.getElementsByTagName("coordinates").item(0);
            float x = Float.parseFloat(coordinatesElement.getElementsByTagName("x").item(0).getTextContent());
            Integer y = Integer.parseInt(coordinatesElement.getElementsByTagName("y").item(0).getTextContent());
            Coordinates coordinates = new Coordinates(x, y);

            String creationDateStr = element.getElementsByTagName("creationDate").item(0).getTextContent();
            ZonedDateTime creationDate = ZonedDateTime.parse(creationDateStr, DateTimeFormatter.ISO_ZONED_DATE_TIME);

            Integer annualTurnover = Integer.parseInt(element.getElementsByTagName("annualTurnover").item(0).getTextContent());

            OrganizationType type = null;
            Node typeNode = element.getElementsByTagName("type").item(0);
            if (typeNode != null && !typeNode.getTextContent().isEmpty()) {
                type = OrganizationType.valueOf(typeNode.getTextContent());
            }

            Element addressElement = (Element) element.getElementsByTagName("postalAddress").item(0);
            String zipCode = addressElement.getElementsByTagName("zipCode").item(0).getTextContent();
            Address postalAddress = new Address(zipCode);

            return new Organization(id, name, coordinates, annualTurnover, type, postalAddress);
        } catch (Exception e) {
            System.err.println("Error parsing organization: " + e.getMessage());
            return null;
        }
    }
}