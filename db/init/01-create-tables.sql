-- Datenbank erstellen
CREATE DATABASE infection_db;
\c infection_db;

-- Users Tabelle
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    email VARCHAR(255),
    address TEXT
);

-- Infection Reports Tabelle
CREATE TABLE infection_reports (
    id SERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    report_date TIMESTAMP NOT NULL,
    test_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL
);

-- Contact Events Tabelle
CREATE TABLE contact_events (
    id SERIAL PRIMARY KEY,
    user1_id BIGINT REFERENCES users(id),
    user2_id BIGINT REFERENCES users(id),
    timestamp TIMESTAMP NOT NULL,
    location TEXT,
    duration INTEGER
);

-- Infection Chains Tabelle
CREATE TABLE infection_chains (
    id SERIAL PRIMARY KEY,
    source_user_id BIGINT REFERENCES users(id),
    status VARCHAR(50) NOT NULL
);

-- Verbindungstabelle für Infection Chains und Contact Events
CREATE TABLE infection_chain_events (
    chain_id BIGINT REFERENCES infection_chains(id),
    event_id BIGINT REFERENCES contact_events(id),
    PRIMARY KEY (chain_id, event_id)
); 