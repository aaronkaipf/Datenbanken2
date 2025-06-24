package de.hsrt.core.entity;

public class Entities {
    public static class User {
        private Integer id;
        private String name;
        private String createdAt;
        private String phoneNumber;
        private String email;

        public User() {}

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class InfectionReport {
        private Integer id;
        private User user;
        private String reportedAt;
        private String testType;
        private String status;

        public InfectionReport() {}

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
        public String getReportedAt() { return reportedAt; }
        public void setReportedAt(String reportedAt) { this.reportedAt = reportedAt; }
        public String getTestType() { return testType; }
        public void setTestType(String testType) { this.testType = testType; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class ContactEvent {
        private Integer id;
        private User initiator;
        private User receiver;
        private String timestamp;
        private Integer durationSeconds;

        public ContactEvent() {}

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public User getInitiator() { return initiator; }
        public void setInitiator(User initiator) { this.initiator = initiator; }
        public User getReceiver() { return receiver; }
        public void setReceiver(User receiver) { this.receiver = receiver; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        public Integer getDurationSeconds() { return durationSeconds; }
        public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
    }

    public static class InfectionChain {
        private Integer id;
        private Integer ancestorReportId;
        private Integer descendantUserId;
        private Integer depth;

        public InfectionChain() {}

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public Integer getAncestorReportId() { return ancestorReportId; }
        public void setAncestorReportId(Integer ancestorReportId) { this.ancestorReportId = ancestorReportId; }
        public Integer getDescendantUserId() { return descendantUserId; }
        public void setDescendantUserId(Integer descendantUserId) { this.descendantUserId = descendantUserId; }
        public Integer getDepth() { return depth; }
        public void setDepth(Integer depth) { this.depth = depth; }
    }
} 