package de.hsrt.jdbc;

import de.hsrt.core.repository.Repository;
import de.hsrt.core.entity.Entities.*;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class JdbcRepository implements Repository {
    private final Connection connection;
    
    public JdbcRepository(Connection connection) {
        this.connection = connection;
    }
    
    // User operations
    @Override
    public User saveUser(User user) {
        String sql = "INSERT INTO users (name, phone_number, email) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPhoneNumber());
            stmt.setString(3, user.getEmail());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving user", e);
        }
    }
    
    @Override
    public Optional<User> findUserById(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setEmail(rs.getString("email"));
                user.setCreatedAt(rs.getTimestamp("created_at").toString());
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user", e);
        }
    }
    
    @Override
    public List<User> findAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setEmail(rs.getString("email"));
                user.setCreatedAt(rs.getTimestamp("created_at").toString());
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all users", e);
        }
        return users;
    }
    
    @Override
    public void deleteUser(Integer id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }
    
    // Contact Event operations
    @Override
    public ContactEvent saveContactEvent(ContactEvent event) {
        String sql = "INSERT INTO contact_events (initiator_id, receiver_id, timestamp, duration_seconds) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, event.getInitiator().getId());
            stmt.setInt(2, event.getReceiver().getId());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(event.getTimestamp()));
            stmt.setInt(4, event.getDurationSeconds() != null ? event.getDurationSeconds() : 0);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                event.setId(rs.getInt(1));
            }
            return event;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving contact event", e);
        }
    }
    
    @Override
    public List<ContactEvent> findContactEventsByUserId(Integer userId) {
        String sql = "SELECT * FROM contact_events WHERE initiator_id = ? OR receiver_id = ?";
        List<ContactEvent> events = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ContactEvent event = new ContactEvent();
                event.setId(rs.getInt("id"));
                User initiator = new User();
                initiator.setId(rs.getInt("initiator_id"));
                event.setInitiator(initiator);
                User receiver = new User();
                receiver.setId(rs.getInt("receiver_id"));
                event.setReceiver(receiver);
                event.setTimestamp(rs.getTimestamp("timestamp").toString());
                event.setDurationSeconds(rs.getInt("duration_seconds"));
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding contact events", e);
        }
        return events;
    }
    
    @Override
    public List<ContactEvent> findAllContactEvents() {
        String sql = "SELECT * FROM contact_events";
        List<ContactEvent> events = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ContactEvent event = new ContactEvent();
                event.setId(rs.getInt("id"));
                User initiator = new User();
                initiator.setId(rs.getInt("initiator_id"));
                event.setInitiator(initiator);
                User receiver = new User();
                receiver.setId(rs.getInt("receiver_id"));
                event.setReceiver(receiver);
                event.setTimestamp(rs.getTimestamp("timestamp").toString());
                event.setDurationSeconds(rs.getInt("duration_seconds"));
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all contact events", e);
        }
        return events;
    }

    
    // Infection Report operations
    @Override
    public InfectionReport saveInfectionReport(InfectionReport report) {
        String sql = "INSERT INTO infection_reports (user_id, reported_at, test_type, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, report.getUser().getId());
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(report.getReportedAt()));
            stmt.setString(3, report.getTestType());
            stmt.setString(4, report.getStatus());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                report.setId(rs.getInt(1));
            }
            return report;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving infection report", e);
        }
    }
    
    @Override
    public List<InfectionReport> findInfectionReportsByUserId(Integer userId) {
        String sql = "SELECT * FROM infection_reports WHERE user_id = ?";
        List<InfectionReport> reports = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                InfectionReport report = new InfectionReport();
                report.setId(rs.getInt("id"));
                User user = new User();
                user.setId(rs.getInt("user_id"));
                report.setUser(user);
                report.setReportedAt(rs.getTimestamp("reported_at").toString());
                report.setTestType(rs.getString("test_type"));
                report.setStatus(rs.getString("status"));
                reports.add(report);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding infection reports", e);
        }
        return reports;
    }
    
    @Override
    public List<InfectionReport> findAllInfectionReports() {
        String sql = "SELECT * FROM infection_reports";
        List<InfectionReport> reports = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                InfectionReport report = new InfectionReport();
                report.setId(rs.getInt("id"));
                User user = new User();
                user.setId(rs.getInt("user_id"));
                report.setUser(user);
                report.setReportedAt(rs.getTimestamp("reported_at").toString());
                report.setTestType(rs.getString("test_type"));
                report.setStatus(rs.getString("status"));
                reports.add(report);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all infection reports", e);
        }
        return reports;
    }
    

    
    // Infection Chain operations
    @Override
    public InfectionChain saveInfectionChain(InfectionChain chain) {
        String sql = "INSERT INTO infection_chain (ancestor_report_id, descendant_user_id, depth) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, chain.getAncestorReportId());
            stmt.setInt(2, chain.getDescendantUserId());
            stmt.setInt(3, chain.getDepth() != null ? chain.getDepth() : 1);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                chain.setId(rs.getInt(1));
            }
            return chain;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving infection chain", e);
        }
    }
    
    @Override
    public List<InfectionChain> findInfectionChainsByAncestorReportId(Integer ancestorReportId) {
        String sql = "SELECT * FROM infection_chain WHERE ancestor_report_id = ?";
        List<InfectionChain> chains = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ancestorReportId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                InfectionChain chain = new InfectionChain();
                chain.setId(rs.getInt("id"));
                chain.setAncestorReportId(rs.getInt("ancestor_report_id"));
                chain.setDescendantUserId(rs.getInt("descendant_user_id"));
                chain.setDepth(rs.getInt("depth"));
                chains.add(chain);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding infection chains by ancestor report id", e);
        }
        return chains;
    }
    
    @Override
    public List<InfectionChain> findAllInfectionChains() {
        String sql = "SELECT * FROM infection_chain";
        List<InfectionChain> chains = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                InfectionChain chain = new InfectionChain();
                chain.setId(rs.getInt("id"));
                chain.setAncestorReportId(rs.getInt("ancestor_report_id"));
                chain.setDescendantUserId(rs.getInt("descendant_user_id"));
                chain.setDepth(rs.getInt("depth"));
                chains.add(chain);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all infection chains", e);
        }
        return chains;
    }

} 