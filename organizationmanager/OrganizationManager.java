package com.organizationmanager;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;

public class OrganizationManager {
    private final Set<Organization> organizations;
    private final Queue<String> commandHistory;
    private final Set<Path> activeScripts;
    private final Path defaultSavePath;
    private long nextId;

    public OrganizationManager(Set<Organization> organizations, String saveFileName) {
        this.organizations = new TreeSet<>(organizations);
        this.commandHistory = new ArrayDeque<>(8);
        this.activeScripts = new HashSet<>();
        this.defaultSavePath = Paths.get(saveFileName).toAbsolutePath();
        this.nextId = organizations.stream()
                .mapToLong(Organization::getId)
                .max().orElse(0) + 1;
    }

    public void startInteractiveMode() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Organization Manager started. Type 'help' for commands.");
            while (true) {
                try {
                    System.out.print("> ");
                    String input = scanner.nextLine().trim();
                    if (input.isEmpty()) continue;

                    processCommand(input, scanner);
                } catch (NoSuchElementException e) {
                    System.out.println("\nExiting...");
                    return;
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }

    private void processCommand(String input, Scanner scanner) {
        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1].trim() : null;

        addToHistory(command);

        switch (command) {
            case "help" -> displayHelp();
            case "info" -> displayInfo();
            case "show" -> showOrganizations();
            case "add" -> addOrganization(scanner);
            case "update" -> updateOrganization(scanner, parseId(argument));
            case "remove_by_id" -> removeById(parseId(argument));
            case "clear" -> clearCollection();
            case "save" -> saveToFile(argument != null ? Paths.get(argument) : defaultSavePath);
            case "execute_script" -> executeScript(requireArgument(argument, "script filename"));
            case "exit" -> System.exit(0);
            case "add_if_min" -> addIfMin(scanner);
            case "remove_greater" -> removeGreater(scanner);
            case "history" -> displayHistory();
            case "min_by_annual_turnover" -> minByAnnualTurnover();
            case "count_by_type" -> countByType(requireArgument(argument, "organization type"));
            case "filter_starts_with_name" -> filterStartsWithName(requireArgument(argument, "name prefix"));
            default -> System.out.println("Unknown command. Type 'help' for list.");
        }
    }

    // Command implementations...
    private void addToHistory(String command) {
        if (commandHistory.size() == 8) commandHistory.poll();
        commandHistory.add(command);
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("  help - Show this help");
        System.out.println("  info - Collection information");
        System.out.println("  show - List all organizations");
        System.out.println("  add - Add new organization");
        System.out.println("  update id - Update organization");
        System.out.println("  remove_by_id id - Remove organization");
        System.out.println("  clear - Clear collection");
        System.out.println("  save [file] - Save to file (default: " + defaultSavePath + ")");
        System.out.println("  execute_script file - Execute script");
        System.out.println("  exit - Exit program");
        System.out.println("  add_if_min - Add if smallest");
        System.out.println("  remove_greater - Remove larger organizations");
        System.out.println("  history - Command history");
        System.out.println("  min_by_annual_turnover - Find minimum turnover");
        System.out.println("  count_by_type type - Count by type");
        System.out.println("  filter_starts_with_name prefix - Filter by name");
        System.out.println("\nOrganization types: " + Arrays.toString(OrganizationType.values()));
    }

    private void displayInfo() {
        System.out.printf("""
            Collection Info:
              Type: %s
              Size: %d
              Next ID: %d
              Save file: %s
              Last init: %s
            """,
                organizations.getClass().getSimpleName(),
                organizations.size(),
                nextId,
                defaultSavePath,
                LocalDateTime.now()
        );
    }

    private void showOrganizations() {
        if (organizations.isEmpty()) {
            System.out.println("Collection is empty.");
            return;
        }
        organizations.forEach(System.out::println);
    }

    private void addOrganization(Scanner scanner) {
        try {
            Organization org = readOrganization(scanner, generateId());
            organizations.add(org);
            System.out.println("Added organization: " + org.getId());
        } catch (Exception e) {
            System.out.println("Failed to add organization: " + e.getMessage());
        }
    }

    private void updateOrganization(Scanner scanner, long id) {
        Organization existing = findById(id);
        if (existing == null) {
            System.out.println("Organization not found: " + id);
            return;
        }

        try {
            System.out.println("Editing organization " + id);
            Organization updated = readOrganization(scanner, id);
            organizations.remove(existing);
            organizations.add(updated);
            System.out.println("Updated organization: " + id);
        } catch (Exception e) {
            System.out.println("Failed to update: " + e.getMessage());
        }
    }

    private void removeById(long id) {
        if (organizations.removeIf(o -> o.getId() == id)) {
            System.out.println("Removed organization: " + id);
        } else {
            System.out.println("Organization not found: " + id);
        }
    }

    private void clearCollection() {
        organizations.clear();
        System.out.println("Collection cleared.");
    }

    private void saveToFile(Path path) {
        try {
            Files.createDirectories(path.getParent());

            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .newDocument();

            Element root = document.createElement("organizations");
            document.appendChild(root);

            for (Organization org : organizations) {
                Element orgElement = document.createElement("organization");

                addTextElement(document, orgElement, "id", org.getId().toString());
                addTextElement(document, orgElement, "name", org.getName());

                Element coords = document.createElement("coordinates");
                addTextElement(document, coords, "x", org.getCoordinates().getX() != null ?
                        org.getCoordinates().getX().toString() : "");
                addTextElement(document, coords, "y", String.valueOf(org.getCoordinates().getY()));
                orgElement.appendChild(coords);

                addTextElement(document, orgElement, "creationDate",
                        org.getCreationDate().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
                addTextElement(document, orgElement, "annualTurnover",
                        org.getAnnualTurnover().toString());

                if (org.getType() != null) {
                    addTextElement(document, orgElement, "type", org.getType().name());
                }

                Element address = document.createElement("postalAddress");
                if (org.getPostalAddress().getZipCode() != null) {
                    addTextElement(document, address, "zipCode", org.getPostalAddress().getZipCode());
                }
                orgElement.appendChild(address);

                root.appendChild(orgElement);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(new DOMSource(document), new StreamResult(path.toFile()));

            System.out.println("Saved to: " + path);
        } catch (Exception e) {
            System.out.println("Failed to save: " + e.getMessage());
        }
    }

    private void executeScript(String filename) {
        Path path = Paths.get(filename).toAbsolutePath();
        if (activeScripts.contains(path)) {
            System.out.println("Error: Recursive script execution detected");
            return;
        }

        try (Scanner fileScanner = new Scanner(path)) {
            activeScripts.add(path);
            System.out.println("Executing script: " + path);

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("//")) continue;

                System.out.println("> " + line);
                processCommand(line, fileScanner);
            }
        } catch (IOException e) {
            System.out.println("Script error: " + e.getMessage());
        } finally {
            activeScripts.remove(path);
        }
    }

    private void addIfMin(Scanner scanner) {
        try {
            Organization candidate = readOrganization(scanner, generateId());
            if (organizations.isEmpty() ||
                    candidate.compareTo(organizations.iterator().next()) < 0) {
                organizations.add(candidate);
                System.out.println("Added organization: " + candidate.getId());
            } else {
                System.out.println("Organization not added - not the smallest");
            }
        } catch (Exception e) {
            System.out.println("Failed to add: " + e.getMessage());
        }
    }

    private void removeGreater(Scanner scanner) {
        try {
            Organization reference = readOrganization(scanner, generateId());
            int count = organizations.size();
            organizations.removeIf(o -> o.compareTo(reference) > 0);
            System.out.println("Removed " + (count - organizations.size()) + " organizations");
        } catch (Exception e) {
            System.out.println("Failed to remove: " + e.getMessage());
        }
    }

    private void displayHistory() {
        System.out.println("Command history:");
        commandHistory.forEach(cmd -> System.out.println("  " + cmd));
    }

    private void minByAnnualTurnover() {
        organizations.stream()
                .min(Comparator.comparing(Organization::getAnnualTurnover))
                .ifPresentOrElse(
                        org -> System.out.println("Minimum turnover: " + org),
                        () -> System.out.println("Collection is empty")
                );
    }

    private void countByType(String typeStr) {
        try {
            OrganizationType type = OrganizationType.fromString(typeStr);
            long count = organizations.stream()
                    .filter(o -> type == null ? o.getType() == null : type.equals(o.getType()))
                    .count();
            System.out.println("Count of " + type + ": " + count);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void filterStartsWithName(String prefix) {
        System.out.println("Organizations starting with '" + prefix + "':");
        organizations.stream()
                .filter(o -> o.getName().startsWith(prefix))
                .forEach(System.out::println);
    }

    // Helper methods...
    private synchronized long generateId() {
        return nextId++;
    }

    private Organization readOrganization(Scanner scanner, long id) {
        System.out.println("Creating organization " + id);

        String name = readInput(scanner, "Name", false);

        System.out.println("Coordinates:");
        Float x = parseFloat(readInput(scanner, "x (≤84)", true));
        int y = parseInt(readInput(scanner, "y (≤239)", false));

        int turnover = parseInt(readInput(scanner, "Annual Turnover (>0)", false));
        if (turnover <= 0) throw new IllegalArgumentException("Turnover must be positive");

        OrganizationType type = null;
        String typeInput = readInput(scanner,
                "Type (" + Arrays.toString(OrganizationType.values()) + ")", true);
        if (!typeInput.isEmpty()) {
            type = OrganizationType.fromString(typeInput);
        }

        String zipCode = readInput(scanner, "Zip Code", true);

        return new Organization(
                id,
                name,
                new Coordinates(x, y),
                turnover,
                type,
                new Address(zipCode.isEmpty() ? null : zipCode)
        );
    }

    private String readInput(Scanner scanner, String prompt, boolean optional) {
        System.out.print(prompt + (optional ? " (optional): " : ": "));
        String input = scanner.nextLine().trim();
        if (!optional && input.isEmpty()) {
            throw new IllegalArgumentException(prompt + " is required");
        }
        return input;
    }

    private Float parseFloat(String input) {
        return input == null || input.isEmpty() ? null : Float.parseFloat(input);
    }

    private int parseInt(String input) {
        return Integer.parseInt(input);
    }

    private long parseId(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("ID is required");
        }
        try {
            long id = Long.parseLong(input);
            if (id <= 0) throw new IllegalArgumentException("ID must be positive");
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format");
        }
    }

    private String requireArgument(String argument, String description) {
        if (argument == null || argument.isEmpty()) {
            throw new IllegalArgumentException(description + " is required");
        }
        return argument;
    }

    private Organization findById(long id) {
        return organizations.stream()
                .filter(o -> o.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private void addTextElement(Document doc, Element parent, String name, String value) {
        Element element = doc.createElement(name);
        element.setTextContent(value);
        parent.appendChild(element);
    }
}