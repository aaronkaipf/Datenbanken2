package de.hsrt.core.repository;

import de.hsrt.core.entity.Entities.*;
import java.util.List;
import java.util.Optional;

public interface Repository {
    // User operations
    User saveUser(User user);
    Optional<User> findUserById(Integer id);
    List<User> findAllUsers();
    void deleteUser(Integer id);
    
    // Contact Event operations
    ContactEvent saveContactEvent(ContactEvent event);
    List<ContactEvent> findContactEventsByUserId(Integer userId);
    List<ContactEvent> findAllContactEvents();

    // Infection Report operations
    InfectionReport saveInfectionReport(InfectionReport report);
    List<InfectionReport> findInfectionReportsByUserId(Integer userId);
    List<InfectionReport> findAllInfectionReports();

    // Infection Chain operations
    InfectionChain saveInfectionChain(InfectionChain chain);
    List<InfectionChain> findInfectionChainsByAncestorReportId(Integer ancestorReportId);
    List<InfectionChain> findAllInfectionChains();
}