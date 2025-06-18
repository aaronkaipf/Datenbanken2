package de.hsrt;

import de.hsrt.entity.Entities.*;
import de.hsrt.service.Services.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Services initialisieren
        UserService userService = new UserService();
        ContactTrackingService contactService = new ContactTrackingService();
        InfectionService infectionService = new InfectionService();
        InfectionChainService chainService = new InfectionChainService();

        // Benutzer anlegen: Alice, Bob, Laura
        System.out.println("=== Benutzer anlegen ===");
        User alice = userService.createUser("Alice", "+49-123-456789", "alice@demo", "Reutlingen");
        System.out.println("→ Alice = " + alice.getId());
        User bob = userService.createUser("Bob", "+49-987-654321", "bob@demo", "Reutlingen");
        System.out.println("→ Bob = " + bob.getId());
        User laura = userService.createUser("Laura", "+49-555-111222", "laura@demo", "Reutlingen");
        System.out.println("→ Laura = " + laura.getId());

        // ContactEvents simulieren
        System.out.println("\n=== Kontakte simulieren ===");
        ContactEvent aliceBobMeeting = contactService.createContactEvent(
            alice, bob, "HS Reutlingen", 30
        );
        System.out.println("→ Alice traf Bob");

        ContactEvent bobLauraMeeting = contactService.createContactEvent(
            bob, laura, "Mensa", 15
        );
        System.out.println("→ Bob traf Laura");

        // Alice positiv melden
        System.out.println("\n=== Report erstellen: Alice ===");
        InfectionReport aliceReport = infectionService.createInfectionReport(
            alice, "PCR", "POSITIVE"
        );
        System.out.println("→ Report Alice = " + aliceReport.getId());

        // Infektionskette für Alice aufbauen
        List<ContactEvent> aliceContacts = new ArrayList<>();
        aliceContacts.add(aliceBobMeeting);
        InfectionChain aliceChain = chainService.createInfectionChain(alice, aliceContacts);
        System.out.println("→ Chain Alice angelegt");

        // Infektionsketten für Alice ausgeben
        System.out.println("\n=== Infektionsketten ausgehend von Alice ===");
        List<InfectionChain> aliceChains = chainService.findChainsBySourceUser(alice.getId());
        for (InfectionChain chain : aliceChains) {
            System.out.println("Kette von " + chain.getSourceUser().getName() + ":");
            for (ContactEvent event : chain.getEvents()) {
                System.out.println("  - Kontakt zwischen " + event.getUser1().getName() +
                                 " und " + event.getUser2().getName() +
                                 " at " + event.getLocation());
            }
        }

        System.out.println("\n=== Demo fertig! ===");
    }
} 