-- Datenbank erstellen
\c infection_db;

-- Users Tabelle
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       phone_number VARCHAR(50),
                       email VARCHAR(255)
);

-- Infection Reports Tabelle
CREATE TABLE infection_reports (
                                   id SERIAL PRIMARY KEY,
                                   user_id INTEGER REFERENCES users(id),
                                   reported_at TIMESTAMP NOT NULL,
                                   test_type VARCHAR(50) NOT NULL,
                                   status VARCHAR(50) NOT NULL
);

-- Contact Events Tabelle
CREATE TABLE contact_events (
                                id SERIAL PRIMARY KEY,
                                initiator_id INTEGER REFERENCES users(id),
                                receiver_id INTEGER REFERENCES users(id),
                                timestamp TIMESTAMP NOT NULL,
                                duration_seconds INTEGER
);

-- Infection Chain Tabelle
CREATE TABLE infection_chain (
                                 id SERIAL PRIMARY KEY,
                                 ancestor_report_id INTEGER REFERENCES infection_reports(id),
                                 descendant_report_id INTEGER REFERENCES infection_reports(id),
                                 descendant_user_id INTEGER REFERENCES users(id),
                                 depth INTEGER
); 