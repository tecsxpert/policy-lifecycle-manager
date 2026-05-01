-- Flyway migration: V2__audit.sql
-- Creates the audit_log table for tracking policy changes

CREATE TABLE IF NOT EXISTS audit_log (
    id BIGSERIAL PRIMARY KEY,
    entity_name VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL,
    changed_by VARCHAR(255) NOT NULL,
    change_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    old_value JSON,
    new_value JSON
);

-- Performance index for efficiently querying audit logs by entity
CREATE INDEX IF NOT EXISTS idx_audit_log_entity ON audit_log(entity_name, entity_id);

