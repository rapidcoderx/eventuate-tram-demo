-- Create Eventuate schema for PostgreSQL
-- Run this SQL against both balance_db and transaction_db

-- Create schema
CREATE SCHEMA IF NOT EXISTS eventuate;

-- Create message table
CREATE TABLE IF NOT EXISTS eventuate.message (
                                                 id VARCHAR(1000) PRIMARY KEY,
                                                 destination VARCHAR(1000) NOT NULL,
                                                 headers VARCHAR(1000) NOT NULL,
                                                 payload VARCHAR(1000) NOT NULL,
                                                 published SMALLINT DEFAULT 0,
                                                 creation_time BIGINT,
                                                 message_partition VARCHAR(1000) -- Add this line
);
-- Create received messages table
CREATE TABLE IF NOT EXISTS eventuate.received_messages (
                                                           consumer_id VARCHAR(1000),
                                                           message_id VARCHAR(1000),
                                                           creation_time BIGINT,
                                                           PRIMARY KEY(consumer_id, message_id)
);

-- Create events table
CREATE TABLE IF NOT EXISTS eventuate.events (
                                                event_id VARCHAR(1000) PRIMARY KEY,
                                                event_type VARCHAR(1000),
                                                event_data VARCHAR(1000) NOT NULL,
                                                entity_type VARCHAR(1000) NOT NULL,
                                                entity_id VARCHAR(1000) NOT NULL,
                                                triggering_event VARCHAR(1000),
                                                metadata VARCHAR(1000),
                                                published SMALLINT DEFAULT 0
);

-- Create entities table
CREATE TABLE IF NOT EXISTS eventuate.entities (
                                                  entity_type VARCHAR(1000),
                                                  entity_id VARCHAR(1000),
                                                  entity_version VARCHAR(1000) NOT NULL,
                                                  PRIMARY KEY(entity_type, entity_id)
);

-- Create snapshots table
CREATE TABLE IF NOT EXISTS eventuate.snapshots (
                                                   entity_type VARCHAR(1000),
                                                   entity_id VARCHAR(1000),
                                                   entity_version VARCHAR(1000),
                                                   snapshot_type VARCHAR(1000) NOT NULL,
                                                   snapshot_json VARCHAR(1000) NOT NULL,
                                                   triggering_events VARCHAR(1000),
                                                   PRIMARY KEY(entity_type, entity_id, entity_version)
);