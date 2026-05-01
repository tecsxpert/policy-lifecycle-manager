-- Flyway migration: V1__init.sql
-- Creates the policies table with indexes for performance optimization

CREATE TABLE IF NOT EXISTS policies (
    id BIGSERIAL PRIMARY KEY,
    policy_name VARCHAR(255) NOT NULL,
    policy_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    policy_holder VARCHAR(255) NOT NULL,
    expiry_date DATE,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Performance indexes for frequently queried columns
CREATE INDEX IF NOT EXISTS idx_policies_status ON policies(status);
CREATE INDEX IF NOT EXISTS idx_policies_policy_holder ON policies(policy_holder);
CREATE INDEX IF NOT EXISTS idx_policies_is_deleted ON policies(is_deleted);

