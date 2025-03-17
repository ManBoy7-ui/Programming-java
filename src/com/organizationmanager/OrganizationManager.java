package com.organizationmanager;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class OrganizationManager {
    private TreeSet<Organization> organizations;
    private Deque<String> commandHistory = new ArrayDeque<>(8); // Stores the last 8 commands

    public OrganizationManager(TreeSet<Organization> organizations) {
        this.organizations = organizations;
    }

    public void startInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter command: ");
            String command = scanner.nextLine().trim();
            addToHistory(command); // Add the command to history

            String[] parts = command.split(" ", 2);
            String commandName = parts[0];
            String argument = parts.length > 1 ? parts[1] : null;

            switch (commandName) {
                case "help":
                    displayHelp();
                    break;
                case "info":
                    displayInfo();
                    break;
                case "show":
                    showOrganizations();
                    break;
                case "add":
                    addOrganization(scanner);
                    break;
                case "update":
                    if (argument != null) {
                        updateOrganization(scanner, Long.parseLong(argument));
                    } else {
                        System.out.println("Error: Missing ID for update command.");
                    }
                    break;
                case "remove_by_id":
                    if (argument != null) {
                        removeById(Long.parseLong(argument));
                    } else {
                        System.out.println("Error: Missing ID for remove_by_id command.");
                    }
                    break;
                case "clear":
                    clearCollection();
                    break;
                case "save":
                    saveToFile("organizations.xml");
                    break;
                case "execute_script":
                    if (argument != null) {
                        executeScript(argument);
                    } else {
                        System.out.println("Error: Missing file name for execute_script command.");
                    }
                    break;
                case "exit":
                    System.out.println("Exiting the program.");
                    return;
                case "add_if_min":
                    addIfMin(scanner);
                    break;
                case "remove_greater":
                    removeGreater(scanner);
                    break;
                case "history":
                    displayHistory();
                    break;
                case "min_by_annual_turnover":
                    minByAnnualTurnover();
                    break;
                case "count_by_type":
                    if (argument != null) {
                        countByType(argument);
                    } else {
                        System.out.println("Error: Missing type for count_by_type command.");
                    }
                    break;
                case "filter_starts_with_name":
                    if (argument != null) {
                        filterStartsWithName(argument);
                    } else {
                        System.out.println("Error: Missing name for filter_starts_with_name command.");
                    }
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for a list of commands.");
            }
        }
    }

    private void addToHistory(String command) {
        if (commandHistory.size() == 8) {
            commandHistory.removeFirst();
        }
        commandHistory.addLast(command.split(" ", 2)[0]); // Store only the command name
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("  help - Display help on available commands");
        System.out.println("  info - Print collection information");
        System.out.println("  show - Print all elements of the collection");
        System.out.println("  add {element} - Add a new item to the collection");
        System.out.println("  update id {element} - Update the value of the collection element by id");
        System.out.println("  remove_by_id id - Remove an element from the collection by id");
        System.out.println("  clear - Clear the collection");
        System.out.println("  save - Save the collection to a file");
        System.out.println("  execute_script file_name - Read and execute a script from the specified file");
        System.out.println("  exit - Exit the program (without saving)");
        System.out.println("  add_if_min {element} - Add a new element if its value is less than the smallest element");
        System.out.println("  remove_greater {element} - Remove all elements greater than the specified one");
        System.out.println("  history - Print the last 8 commands");
        System.out.println("  min_by_annual_turnover - Output any object with the minimal annualTurnover");
        System.out.println("  count_by_type type - Output the number of elements with the specified type");
        System.out.println("  filter_starts_with_name name - Output elements whose name starts with the given substring");
    }

    private void displayInfo() {
        System.out.println("Collection type: " + organizations.getClass().getSimpleName());
        System.out.println("Initialization date: " + LocalDateTime.now());
        System.out.println("Number of elements: " + organizations.size());
    }

    private void showOrganizations() {
        if (organizations.isEmpty()) {
            System.out.println("The collection is empty.");
        } else {
            for (Organization org : organizations) {
                System.out.println(org);
            }
        }
    }

    private void addOrganization(Scanner scanner) {
        System.out.println("Enter organization details:");
        System.out.print("ID: ");
        Long id = Long.parseLong(scanner.nextLine());
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Coordinates (x): ");
        float x = Float.parseFloat(scanner.nextLine());
        System.out.print("Coordinates (y): ");
        Integer y = Integer.parseInt(scanner.nextLine());
        System.out.print("Annual Turnover: ");
        Integer annualTurnover = Integer.parseInt(scanner.nextLine());
        System.out.print("Type (COMMERCIAL, PUBLIC, GOVERNMENT, TRUST, PRIVATE_LIMITED_COMPANY): ");
        String typeStr = scanner.nextLine();
        OrganizationType type = typeStr.isEmpty() ? null : OrganizationType.valueOf(typeStr);
        System.out.print("Postal Address (zipCode): ");
        String zipCode = scanner.nextLine();

        Coordinates coordinates = new Coordinates(x, y);
        Address postalAddress = new Address(zipCode);
        Organization org = new Organization(id, name, coordinates, annualTurnover, type, postalAddress);
        organizations.add(org);
        System.out.println("Organization added successfully.");
    }

    private void updateOrganization(Scanner scanner, Long id) {
        Organization orgToUpdate = organizations.stream()
                .filter(org -> org.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (orgToUpdate == null) {
            System.out.println("Error: Organization with ID " + id + " not found.");
            return;
        }

        System.out.println("Enter new details for the organization:");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Coordinates (x): ");
        float x = Float.parseFloat(scanner.nextLine());
        System.out.print("Coordinates (y): ");
        Integer y = Integer.parseInt(scanner.nextLine());
        System.out.print("Annual Turnover: ");
        Integer annualTurnover = Integer.parseInt(scanner.nextLine());
        System.out.print("Type (COMMERCIAL, PUBLIC, GOVERNMENT, TRUST, PRIVATE_LIMITED_COMPANY): ");
        String typeStr = scanner.nextLine();
        OrganizationType type = typeStr.isEmpty() ? null : OrganizationType.valueOf(typeStr);
        System.out.print("Postal Address (zipCode): ");
        String zipCode = scanner.nextLine();

        Coordinates coordinates = new Coordinates(x, y);
        Address postalAddress = new Address(zipCode);
        Organization updatedOrg = new Organization(id, name, coordinates, annualTurnover, type, postalAddress);

        organizations.remove(orgToUpdate);
        organizations.add(updatedOrg);
        System.out.println("Organization updated successfully.");
    }

    private void removeById(Long id) {
        Organization orgToRemove = organizations.stream()
                .filter(org -> org.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (orgToRemove == null) {
            System.out.println("Error: Organization with ID " + id + " not found.");
        } else {
            organizations.remove(orgToRemove);
            System.out.println("Organization removed successfully.");
        }
    }

    private void clearCollection() {
        organizations.clear();
        System.out.println("Collection cleared successfully.");
    }

    private void saveToFile(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("<organizations>\n");
            for (Organization org : organizations) {
                writer.write("  <organization>\n");
                writer.write("    <id>" + org.getId() + "</id>\n");
                writer.write("    <name>" + org.getName() + "</name>\n");
                writer.write("    <coordinates>\n");
                writer.write("      <x>" + org.getCoordinates().getX() + "</x>\n");
                writer.write("      <y>" + org.getCoordinates().getY() + "</y>\n");
                writer.write("    </coordinates>\n");
                writer.write("    <creationDate>" + org.getCreationDate() + "</creationDate>\n");
                writer.write("    <annualTurnover>" + org.getAnnualTurnover() + "</annualTurnover>\n");
                writer.write("    <type>" + (org.getType() != null ? org.getType() : "") + "</type>\n");
                writer.write("    <postalAddress>\n");
                writer.write("      <zipCode>" + org.getPostalAddress().getZipCode() + "</zipCode>\n");
                writer.write("    </postalAddress>\n");
                writer.write("  </organization>\n");
            }
            writer.write("</organizations>\n");
            System.out.println("Collection saved to " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving to file: " + e.getMessage());
        }
    }

    private void executeScript(String fileName) {
        System.out.println("Executing script from file: " + fileName);
        // Implementation for script execution (optional)
    }

    private void addIfMin(Scanner scanner) {
        System.out.println("Enter organization details to add if minimal:");
        System.out.print("ID: ");
        Long id = Long.parseLong(scanner.nextLine());
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Coordinates (x): ");
        float x = Float.parseFloat(scanner.nextLine());
        System.out.print("Coordinates (y): ");
        Integer y = Integer.parseInt(scanner.nextLine());
        System.out.print("Annual Turnover: ");
        Integer annualTurnover = Integer.parseInt(scanner.nextLine());
        System.out.print("Type (COMMERCIAL, PUBLIC, GOVERNMENT, TRUST, PRIVATE_LIMITED_COMPANY): ");
        String typeStr = scanner.nextLine();
        OrganizationType type = typeStr.isEmpty() ? null : OrganizationType.valueOf(typeStr);
        System.out.print("Postal Address (zipCode): ");
        String zipCode = scanner.nextLine();

        Coordinates coordinates = new Coordinates(x, y);
        Address postalAddress = new Address(zipCode);
        Organization newOrg = new Organization(id, name, coordinates, annualTurnover, type, postalAddress);

        if (organizations.isEmpty() || newOrg.compareTo(organizations.first()) < 0) {
            organizations.add(newOrg);
            System.out.println("Organization added successfully.");
        } else {
            System.out.println("Organization not added: it is not the smallest.");
        }
    }

    private void removeGreater(Scanner scanner) {
        System.out.println("Enter organization details to remove greater elements:");
        System.out.print("ID: ");
        Long id = Long.parseLong(scanner.nextLine());
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Coordinates (x): ");
        float x = Float.parseFloat(scanner.nextLine());
        System.out.print("Coordinates (y): ");
        Integer y = Integer.parseInt(scanner.nextLine());
        System.out.print("Annual Turnover: ");
        Integer annualTurnover = Integer.parseInt(scanner.nextLine());
        System.out.print("Type (COMMERCIAL, PUBLIC, GOVERNMENT, TRUST, PRIVATE_LIMITED_COMPANY): ");
        String typeStr = scanner.nextLine();
        OrganizationType type = typeStr.isEmpty() ? null : OrganizationType.valueOf(typeStr);
        System.out.print("Postal Address (zipCode): ");
        String zipCode = scanner.nextLine();

        Coordinates coordinates = new Coordinates(x, y);
        Address postalAddress = new Address(zipCode);
        Organization referenceOrg = new Organization(id, name, coordinates, annualTurnover, type, postalAddress);

        organizations.removeIf(org -> org.compareTo(referenceOrg) > 0);
        System.out.println("Elements greater than the specified one removed successfully.");
    }

    private void displayHistory() {
        System.out.println("Last 8 commands:");
        for (String cmd : commandHistory) {
            System.out.println("  " + cmd);
        }
    }

    private void minByAnnualTurnover() {
        Organization minOrg = organizations.stream()
                .min(Comparator.comparing(Organization::getAnnualTurnover))
                .orElse(null);

        if (minOrg == null) {
            System.out.println("The collection is empty.");
        } else {
            System.out.println("Organization with minimal annual turnover: " + minOrg);
        }
    }

    private void countByType(String typeStr) {
        OrganizationType type = OrganizationType.valueOf(typeStr);
        long count = organizations.stream()
                .filter(org -> org.getType() == type)
                .count();
        System.out.println("Number of organizations with type " + type + ": " + count);
    }

    private void filterStartsWithName(String prefix) {
        System.out.println("Organizations whose names start with '" + prefix + "':");
        organizations.stream()
                .filter(org -> org.getName().startsWith(prefix))
                .forEach(System.out::println);
    }
}