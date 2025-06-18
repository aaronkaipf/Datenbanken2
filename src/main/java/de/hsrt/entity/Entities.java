package de.hsrt.entity;

import java.time.LocalDateTime;
import java.util.List;

public class Entities {
    
    public static class User {
        private Long id;
        private String name;
        private String phoneNumber;
        private String email;
        private String address;

        public User() {}

        public User(Long id, String name, String phoneNumber, String email, String address) {
            this.id = id;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.address = address;
        }

        // Getter und Setter
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }

    public static class InfectionReport {
        private Long id;
        private User user;
        private LocalDateTime reportDate;
        private String testType;
        private String status;

        public InfectionReport() {}

        public InfectionReport(Long id, User user, LocalDateTime reportDate, String testType, String status) {
            this.id = id;
            this.user = user;
            this.reportDate = reportDate;
            this.testType = testType;
            this.status = status;
        }

        // Getter und Setter
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
        public LocalDateTime getReportDate() { return reportDate; }
        public void setReportDate(LocalDateTime reportDate) { this.reportDate = reportDate; }
        public String getTestType() { return testType; }
        public void setTestType(String testType) { this.testType = testType; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class ContactEvent {
        private Long id;
        private User user1;
        private User user2;
        private LocalDateTime timestamp;
        private String location;
        private Integer duration;

        public ContactEvent() {}

        public ContactEvent(Long id, User user1, User user2, LocalDateTime timestamp, String location, Integer duration) {
            this.id = id;
            this.user1 = user1;
            this.user2 = user2;
            this.timestamp = timestamp;
            this.location = location;
            this.duration = duration;
        }

        // Getter und Setter
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public User getUser1() { return user1; }
        public void setUser1(User user1) { this.user1 = user1; }
        public User getUser2() { return user2; }
        public void setUser2(User user2) { this.user2 = user2; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }
    }

    public static class InfectionChain {
        private Long id;
        private User sourceUser;
        private List<ContactEvent> events;
        private String status;

        public InfectionChain() {}

        public InfectionChain(Long id, User sourceUser, List<ContactEvent> events, String status) {
            this.id = id;
            this.sourceUser = sourceUser;
            this.events = events;
            this.status = status;
        }

        // Getter und Setter
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public User getSourceUser() { return sourceUser; }
        public void setSourceUser(User sourceUser) { this.sourceUser = sourceUser; }
        public List<ContactEvent> getEvents() { return events; }
        public void setEvents(List<ContactEvent> events) { this.events = events; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
} 