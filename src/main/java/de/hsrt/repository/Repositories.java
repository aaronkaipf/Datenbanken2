package de.hsrt.repository;

import de.hsrt.db.DatabaseConnection;
import de.hsrt.entity.Entities.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Repositories {
    
    public static class UserRepository {
        private final Connection connection;

        public UserRepository() {
            this.connection = DatabaseConnection.getInstance().getConnection();
        }

        public User save(User user) throws SQLException {
            String sql = "INSERT INTO users (name, phone_number, email, address) VALUES (?, ?, ?, ?) RETURNING id";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getPhoneNumber());
                stmt.setString(3, user.getEmail());
                stmt.setString(4, user.getAddress());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    user.setId(rs.getLong("id"));
                }
            }
            return user;
        }

        public Optional<User> findById(Long id) throws SQLException {
            String sql = "SELECT * FROM users WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
            return Optional.empty();
        }

        private User mapResultSetToUser(ResultSet rs) throws SQLException {
            return new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("phone_number"),
                rs.getString("email"),
                rs.getString("address")
            );
        }
    }

    public static class InfectionReportRepository {
        private final Connection connection;
        private final UserRepository userRepository;

        public InfectionReportRepository() {
            this.connection = DatabaseConnection.getInstance().getConnection();
            this.userRepository = new UserRepository();
        }

        public InfectionReport save(InfectionReport report) throws SQLException {
            String sql = "INSERT INTO infection_reports (user_id, report_date, test_type, status) VALUES (?, ?, ?, ?) RETURNING id";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, report.getUser().getId());
                stmt.setTimestamp(2, Timestamp.valueOf(report.getReportDate()));
                stmt.setString(3, report.getTestType());
                stmt.setString(4, report.getStatus());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    report.setId(rs.getLong("id"));
                }
            }
            return report;
        }

        public List<InfectionReport> findByUserId(Long userId) throws SQLException {
            String sql = "SELECT * FROM infection_reports WHERE user_id = ?";
            List<InfectionReport> reports = new ArrayList<>();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    reports.add(mapResultSetToInfectionReport(rs));
                }
            }
            return reports;
        }

        private InfectionReport mapResultSetToInfectionReport(ResultSet rs) throws SQLException {
            User user = userRepository.findById(rs.getLong("user_id")).orElseThrow();
            return new InfectionReport(
                rs.getLong("id"),
                user,
                rs.getTimestamp("report_date").toLocalDateTime(),
                rs.getString("test_type"),
                rs.getString("status")
            );
        }
    }

    public static class ContactEventRepository {
        private final Connection connection;
        private final UserRepository userRepository;

        public ContactEventRepository() {
            this.connection = DatabaseConnection.getInstance().getConnection();
            this.userRepository = new UserRepository();
        }

        public ContactEvent save(ContactEvent event) throws SQLException {
            String sql = "INSERT INTO contact_events (user1_id, user2_id, timestamp, location, duration) VALUES (?, ?, ?, ?, ?) RETURNING id";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, event.getUser1().getId());
                stmt.setLong(2, event.getUser2().getId());
                stmt.setTimestamp(3, Timestamp.valueOf(event.getTimestamp()));
                stmt.setString(4, event.getLocation());
                stmt.setInt(5, event.getDuration());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    event.setId(rs.getLong("id"));
                }
            }
            return event;
        }

        public List<ContactEvent> findByUserId(Long userId) throws SQLException {
            String sql = "SELECT * FROM contact_events WHERE user1_id = ? OR user2_id = ?";
            List<ContactEvent> events = new ArrayList<>();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, userId);
                stmt.setLong(2, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    events.add(mapResultSetToContactEvent(rs));
                }
            }
            return events;
        }

        private ContactEvent mapResultSetToContactEvent(ResultSet rs) throws SQLException {
            User user1 = userRepository.findById(rs.getLong("user1_id")).orElseThrow();
            User user2 = userRepository.findById(rs.getLong("user2_id")).orElseThrow();
            return new ContactEvent(
                rs.getLong("id"),
                user1,
                user2,
                rs.getTimestamp("timestamp").toLocalDateTime(),
                rs.getString("location"),
                rs.getInt("duration")
            );
        }
    }

    public static class InfectionChainRepository {
        private final Connection connection;
        private final UserRepository userRepository;
        private final ContactEventRepository contactEventRepository;

        public InfectionChainRepository() {
            this.connection = DatabaseConnection.getInstance().getConnection();
            this.userRepository = new UserRepository();
            this.contactEventRepository = new ContactEventRepository();
        }

        public InfectionChain save(InfectionChain chain) throws SQLException {
            String sql = "INSERT INTO infection_chains (source_user_id, status) VALUES (?, ?) RETURNING id";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, chain.getSourceUser().getId());
                stmt.setString(2, chain.getStatus());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    chain.setId(rs.getLong("id"));
                    saveChainEvents(chain);
                }
            }
            return chain;
        }

        private void saveChainEvents(InfectionChain chain) throws SQLException {
            String sql = "INSERT INTO infection_chain_events (chain_id, event_id) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                for (ContactEvent event : chain.getEvents()) {
                    stmt.setLong(1, chain.getId());
                    stmt.setLong(2, event.getId());
                    stmt.executeUpdate();
                }
            }
        }

        public List<InfectionChain> findBySourceUserId(Long userId) throws SQLException {
            String sql = "SELECT * FROM infection_chains WHERE source_user_id = ?";
            List<InfectionChain> chains = new ArrayList<>();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    chains.add(mapResultSetToInfectionChain(rs));
                }
            }
            return chains;
        }

        private InfectionChain mapResultSetToInfectionChain(ResultSet rs) throws SQLException {
            User sourceUser = userRepository.findById(rs.getLong("source_user_id")).orElseThrow();
            Long chainId = rs.getLong("id");
            List<ContactEvent> events = loadChainEvents(chainId);
            return new InfectionChain(
                chainId,
                sourceUser,
                events,
                rs.getString("status")
            );
        }

        private List<ContactEvent> loadChainEvents(Long chainId) throws SQLException {
            String sql = "SELECT event_id FROM infection_chain_events WHERE chain_id = ?";
            List<ContactEvent> events = new ArrayList<>();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, chainId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Long eventId = rs.getLong("event_id");
                    // Hier müsste noch die Methode findById im ContactEventRepository implementiert werden
                    // events.add(contactEventRepository.findById(eventId).orElseThrow());
                }
            }
            return events;
        }
    }
} 