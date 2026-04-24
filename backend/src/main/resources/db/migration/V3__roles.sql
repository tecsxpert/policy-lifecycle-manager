-- Flyway migration: V3__roles.sql
-- Creates the roles table and seeds the three required RBAC roles

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO roles (name) VALUES
    ('ADMIN'),
    ('MANAGER'),
    ('VIEWER')
ON CONFLICT (name) DO NOTHING;

