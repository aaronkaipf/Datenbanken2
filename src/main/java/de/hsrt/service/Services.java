package de.hsrt.service;

import de.hsrt.entity.Entities.*;
import de.hsrt.repository.Repositories.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Services {
    
    public static class UserService {
        private final UserRepository repository;

        public UserService() {
            this.repository = new UserRepository();
        }

        public User createUser(String name, String phoneNumber, String email, String address) {
            User user = new User();
            user.setName(name);
            user.setPhoneNumber(phoneNumber);
            user.setEmail(email);
            user.setAddress(address);
            try {
                return repository.save(user);
            } catch (SQLException e) {
                throw new RuntimeException("Fehler beim Speichern des Benutzers", e);
            }
        }
    }

    public static class InfectionService {
        private final InfectionReportRepository repository;

        public InfectionService() {
            this.repository = new InfectionReportRepository();
        }

        public InfectionReport createInfectionReport(User user, String testType, String status) {
            InfectionReport report = new InfectionReport();
            report.setUser(user);
            report.setReportDate(LocalDateTime.now());
            report.setTestType(testType);
            report.setStatus(status);
            try {
                return repository.save(report);
            } catch (SQLException e) {
                throw new RuntimeException("Fehler beim Speichern des Infektionsberichts", e);
            }
        }
    }

    public static class ContactTrackingService {
        private final ContactEventRepository repository;

        public ContactTrackingService() {
            this.repository = new ContactEventRepository();
        }

        public ContactEvent createContactEvent(User user1, User user2, String location, Integer duration) {
            ContactEvent event = new ContactEvent();
            event.setUser1(user1);
            event.setUser2(user2);
            event.setTimestamp(LocalDateTime.now());
            event.setLocation(location);
            event.setDuration(duration);
            try {
                return repository.save(event);
            } catch (SQLException e) {
                throw new RuntimeException("Fehler beim Speichern des Kontaktereignisses", e);
            }
        }

        public List<ContactEvent> findContactsByUser(Long userId) {
            try {
                return repository.findByUserId(userId);
            } catch (SQLException e) {
                throw new RuntimeException("Fehler beim Laden der Kontaktereignisse", e);
            }
        }
    }

    public static class InfectionChainService {
        private final InfectionChainRepository repository;

        public InfectionChainService() {
            this.repository = new InfectionChainRepository();
        }

        public InfectionChain createInfectionChain(User sourceUser, List<ContactEvent> events) {
            InfectionChain chain = new InfectionChain();
            chain.setSourceUser(sourceUser);
            chain.setEvents(events);
            chain.setStatus("ACTIVE");
            try {
                return repository.save(chain);
            } catch (SQLException e) {
                throw new RuntimeException("Fehler beim Speichern der Infektionskette", e);
            }
        }

        public List<InfectionChain> findChainsBySourceUser(Long userId) {
            try {
                return repository.findBySourceUserId(userId);
            } catch (SQLException e) {
                throw new RuntimeException("Fehler beim Laden der Infektionsketten", e);
            }
        }
    }
} 