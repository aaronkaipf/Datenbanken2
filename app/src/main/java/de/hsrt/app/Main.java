package de.hsrt.app;

import de.hsrt.core.service.Service;
import de.hsrt.core.service.ServiceImpl;
import de.hsrt.core.repository.Repository;
import de.hsrt.jdbc.JdbcRepository;
import de.hsrt.core.entity.Entities.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            // Setup database connection
            Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/infection_db",
                "user",
                "123456"
            );
            
            // Setup repository and service
            Repository repository = new JdbcRepository(connection);
            Service service = new ServiceImpl(repository);
            
            System.out.println("=== Contact Tracking System Test ===\n");
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            // Test 1: Create users
            System.out.println("1. Creating users...");
            User alice = service.createUser("Alice Johnson", "123-111-1111", "alice@example.com");
            User bob = service.createUser("Bob Smith", "123-222-2222", "bob@example.com");
            User charlie = service.createUser("Charlie Brown", "123-333-3333", "charlie@example.com");
            User dave = service.createUser("Dave Miller", "123-444-4444", "dave@example.com");
            User eve = service.createUser("Eve Adams", "123-555-5555", "eve@example.com");
            
            System.out.println("Created users:");
            System.out.println("- " + alice.getName() + " (ID: " + alice.getId() + ")");
            System.out.println("- " + bob.getName() + " (ID: " + bob.getId() + ")");
            System.out.println("- " + charlie.getName() + " (ID: " + charlie.getId() + ")");
            System.out.println("- " + dave.getName() + " (ID: " + dave.getId() + ")");
            System.out.println("- " + eve.getName() + " (ID: " + eve.getId() + ")\n");
            
            // Test 2: Create contact events
            System.out.println("2. Creating contact events...");
            
            // Alice's infection report will be on 2024-01-16 08:00:00
            // Time window: 3 days before/after = 2024-01-13 to 2024-01-19
            
            // VALID CONTACTS (within time window):
            String tsAliceBob = "2024-01-15 09:00:00";      // 1 Tag vor Alice-Report (VALID)
            String tsBobCharlie = "2024-01-17 07:00:00";    // 1 Tag nach Alice-Report (VALID)
            
            // INVALID CONTACTS (outside time window):
            String tsAliceDave = "2024-01-10 09:00:00";     // 6 Tage vor Alice-Report (zu früh)
            String tsAliceEve = "2024-01-20 09:00:00";      // 4 Tage nach Alice-Report (zu spät)
            
            ContactEvent event1 = service.createContactEvent(alice, bob, tsAliceBob, 60);
            ContactEvent event2 = service.createContactEvent(bob, charlie, tsBobCharlie, 45);
            ContactEvent event3 = service.createContactEvent(alice, dave, tsAliceDave, 30);
            ContactEvent event4 = service.createContactEvent(alice, eve, tsAliceEve, 30);
            
            System.out.println("Created contact events:");
            System.out.println("- " + alice.getName() + " met " + bob.getName() + " at " + tsAliceBob + " (VALID - within time window)");
            System.out.println("- " + bob.getName() + " met " + charlie.getName() + " at " + tsBobCharlie + " (VALID - within time window)");
            System.out.println("- " + alice.getName() + " met " + dave.getName() + " at " + tsAliceDave + " (INVALID - too early)");
            System.out.println("- " + alice.getName() + " met " + eve.getName() + " at " + tsAliceEve + " (INVALID - too late)\n");
            
            // Test 3: Create infection report for Alice only and build chains automatically
            System.out.println("3. Creating infection report for Alice and building chains automatically...");
            
            // Alice meldet sich als infiziert (3 Tage vor/nach Zeitfenster)
            String reportAlice = LocalDateTime.of(2024, 1, 16, 8, 0, 0).format(formatter);
            InfectionReport irAlice = service.createInfectionReport(alice, "COVID-19", reportAlice, "CONFIRMED");
            System.out.println("- " + alice.getName() + " reported on " + irAlice.getReportedAt());
            
            // Automatisch Infektionsketten für Alice erstellen (3 Tage vor/nach)
            List<InfectionChain> aliceChains = service.buildInfectionChainsForNewReport(irAlice, 3, 3);
            System.out.println("  -> Automatically created " + aliceChains.size() + " infection chains for Alice:");
            for (InfectionChain chain : aliceChains) {
                Optional<User> contactUser = service.getUserById(chain.getDescendantUserId());
                if (contactUser.isPresent()) {
                    System.out.println("    - " + alice.getName() + " -> " + contactUser.get().getName() + " (depth " + chain.getDepth() + ")");
                }
            }
            
            // Test 4: Notification system demonstration
            System.out.println("\n4. Notification system demonstration...");
            
            // Wer soll über Alice's Infektion benachrichtigt werden? (max depth 2 = direkte + indirekte Kontakte)
            List<User> aliceContacts = service.getPotentialContactsForNotification(alice.getId(), 2);
            System.out.println("Users to notify about " + alice.getName() + "'s infection (depth 2):");
            for (User contact : aliceContacts) {
                System.out.println("  - " + contact.getName() + " (" + contact.getEmail() + ")");
            }
            
            // Test 5: Query specific data
            System.out.println("\n5. Querying specific data...");
            Optional<User> foundAlice = service.getUserById(alice.getId());
            List<ContactEvent> aliceEvents = service.getContactEventsByUserId(alice.getId());
            List<InfectionReport> aliceReports = service.getInfectionReportsByUserId(alice.getId());
            
            System.out.println("Found user: " + foundAlice.map(User::getName).orElse("Not found"));
            System.out.println(alice.getName() + " has " + aliceEvents.size() + " contact events");
            System.out.println(alice.getName() + " has " + aliceReports.size() + " infection reports");
            
            connection.close();
            System.out.println("\n=== Test completed successfully! ===");
            
        } catch (Exception e) {
            System.err.println("Error during test: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 