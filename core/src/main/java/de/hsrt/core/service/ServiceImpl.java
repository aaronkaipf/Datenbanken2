package de.hsrt.core.service;

import de.hsrt.core.repository.Repository;
import de.hsrt.core.entity.Entities.*;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServiceImpl implements Service {
    private final Repository repository;
    
    public ServiceImpl(Repository repository) {
        this.repository = repository;
    }
    
    // User operations
    @Override
    public User createUser(String name, String phoneNumber, String email) {
        User user = new User();
        user.setName(name);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        return repository.saveUser(user);
    }
    
    @Override
    public Optional<User> getUserById(Integer id) {
        return repository.findUserById(id);
    }
    
    // Contact Event operations
    @Override
    public ContactEvent createContactEvent(User initiator, User receiver, String timestamp, Integer durationSeconds) {
        ContactEvent event = new ContactEvent();
        event.setInitiator(initiator);
        event.setReceiver(receiver);
        event.setTimestamp(timestamp);
        event.setDurationSeconds(durationSeconds);
        return repository.saveContactEvent(event);
    }
    
    @Override
    public List<ContactEvent> getContactEventsByUserId(Integer userId) {
        return repository.findContactEventsByUserId(userId);
    }
    
    // Infection Report operations
    @Override
    public InfectionReport createInfectionReport(User user, String testType, String reportedAt, String status) {
        InfectionReport report = new InfectionReport();
        report.setUser(user);
        report.setTestType(testType);
        report.setReportedAt(reportedAt);
        report.setStatus(status);
        return repository.saveInfectionReport(report);
    }
    
    @Override
    public List<InfectionReport> getInfectionReportsByUserId(Integer userId) {
        return repository.findInfectionReportsByUserId(userId);
    }
    
    // Infection Chain operations - AUTOMATIC BUILDING
    @Override
    public List<InfectionChain> buildInfectionChainsForNewReport(InfectionReport newReport, int daysBefore, int daysAfter) {
        List<InfectionChain> createdChains = new ArrayList<>();
        
        // Parse the report date flexibly
        LocalDateTime reportDate = parseDateTimeFlexibly(newReport.getReportedAt());
        LocalDateTime startDate = reportDate.minusDays(daysBefore);
        LocalDateTime endDate = reportDate.plusDays(daysAfter);
        
        // Get all contact events for the infected user
        Integer infectedUserId = newReport.getUser().getId();
        List<ContactEvent> userEvents = repository.findContactEventsByUserId(infectedUserId);
        
        // Check each contact event in the time window
        for (ContactEvent event : userEvents) {
            LocalDateTime eventDate = parseDateTimeFlexibly(event.getTimestamp());
            
            // Check if event is in the time window
            if (eventDate.isAfter(startDate) && eventDate.isBefore(endDate)) {
                
                // Determine the other user in the contact
                Integer otherUserId;
                if (event.getInitiator().getId().equals(infectedUserId)) {
                    otherUserId = event.getReceiver().getId();
                } else {
                    otherUserId = event.getInitiator().getId();
                }
                
                // Create infection chain with depth 1 (direct contact)
                InfectionChain chain = new InfectionChain();
                chain.setAncestorReportId(newReport.getId());
                chain.setDescendantUserId(otherUserId);
                chain.setDepth(1);
                
                InfectionChain savedChain = repository.saveInfectionChain(chain);
                if (savedChain != null) {
                    createdChains.add(savedChain);
                }
                
                // Now check for indirect contacts (depth 2)
                // Get all contact events for the direct contact (otherUserId)
                List<ContactEvent> indirectEvents = repository.findContactEventsByUserId(otherUserId);
                
                for (ContactEvent indirectEvent : indirectEvents) {
                    LocalDateTime indirectEventDate = parseDateTimeFlexibly(indirectEvent.getTimestamp());
                    
                    // Check if indirect event is in the time window
                    if (indirectEventDate.isAfter(startDate) && indirectEventDate.isBefore(endDate)) {
                        
                        // Determine the third user in the indirect contact
                        Integer thirdUserId;
                        if (indirectEvent.getInitiator().getId().equals(otherUserId)) {
                            thirdUserId = indirectEvent.getReceiver().getId();
                        } else {
                            thirdUserId = indirectEvent.getInitiator().getId();
                        }
                        
                        // Don't create chain if third user is the original infected user
                        if (!thirdUserId.equals(infectedUserId)) {
                            
                            // Check if we already have a direct chain to this user
                            boolean alreadyHasDirectChain = false;
                            for (InfectionChain existingChain : createdChains) {
                                if (existingChain.getDescendantUserId().equals(thirdUserId) && existingChain.getDepth() == 1) {
                                    alreadyHasDirectChain = true;
                                    break;
                                }
                            }
                            
                            // Only create depth 2 chain if there's no direct chain
                            if (!alreadyHasDirectChain) {
                                InfectionChain indirectChain = new InfectionChain();
                                indirectChain.setAncestorReportId(newReport.getId());
                                indirectChain.setDescendantUserId(thirdUserId);
                                indirectChain.setDepth(2);
                                
                                InfectionChain savedIndirectChain = repository.saveInfectionChain(indirectChain);
                                if (savedIndirectChain != null) {
                                    createdChains.add(savedIndirectChain);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return createdChains;
    }
    
    // Helper method to parse dates flexibly
    private LocalDateTime parseDateTimeFlexibly(String dateTimeString) {
        try {
            // Try with milliseconds first
            DateTimeFormatter formatterWithMs = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            return LocalDateTime.parse(dateTimeString, formatterWithMs);
        } catch (Exception e1) {
            try {
                // Try without milliseconds
                DateTimeFormatter formatterWithoutMs = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(dateTimeString, formatterWithoutMs);
            } catch (Exception e2) {
                throw new RuntimeException("Could not parse date: " + dateTimeString, e2);
            }
        }
    }
    
    @Override
    public List<InfectionChain> getInfectionChainsByAncestorReportId(Integer ancestorReportId) {
        return repository.findInfectionChainsByAncestorReportId(ancestorReportId);
    }
    
    // Business logic operations
    @Override
    public List<User> getPotentialContactsForNotification(Integer infectedUserId, Integer maxDepth) {
        List<User> potentialContacts = new ArrayList<>();
        
        // Get all infection chains where this user is the ancestor
        List<InfectionReport> userReports = repository.findInfectionReportsByUserId(infectedUserId);
        
        for (InfectionReport report : userReports) {
            List<InfectionChain> chains = repository.findInfectionChainsByAncestorReportId(report.getId());
            
            for (InfectionChain chain : chains) {
                if (chain.getDepth() <= maxDepth) {
                    // Get the user to notify
                    Optional<User> userToNotify = repository.findUserById(chain.getDescendantUserId());
                    if (userToNotify.isPresent() && !potentialContacts.contains(userToNotify.get())) {
                        potentialContacts.add(userToNotify.get());
                    }
                }
            }
        }
        
        return potentialContacts;
    }
} 