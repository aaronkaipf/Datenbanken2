package de.hsrt.core.service;

import de.hsrt.core.entity.Entities.*;
import java.util.List;
import java.util.Optional;

public interface Service {
    // User operations
    User createUser(String name, String phoneNumber, String email);
    Optional<User> getUserById(Integer id);
    
    // Contact Event operations
    ContactEvent createContactEvent(User initiator, User receiver, String timestamp, Integer durationSeconds);
    List<ContactEvent> getContactEventsByUserId(Integer userId);
    
    // Infection Report operations
    InfectionReport createInfectionReport(User user, String testType, String reportedAt, String status);
    List<InfectionReport> getInfectionReportsByUserId(Integer userId);
    
    // Infection Chain operations - AUTOMATIC BUILDING
    List<InfectionChain> buildInfectionChainsForNewReport(InfectionReport newReport, int daysBefore, int daysAfter);
    List<InfectionChain> getInfectionChainsByAncestorReportId(Integer ancestorReportId);
    
    // Business logic operations
    List<User> getPotentialContactsForNotification(Integer infectedUserId, Integer maxDepth);
} 