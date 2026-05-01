-- Flyway migration: V5__performance_indexes.sql
-- Adds composite indexes to optimize frequently used search and filter queries

-- Composite index for soft-delete + status filtering (stats, list views)
CREATE INDEX IF NOT EXISTS idx_policies_status_isdeleted ON policies(status, is_deleted);

-- Composite index for policy_name search with is_deleted filter
CREATE INDEX IF NOT EXISTS idx_policies_name_isdeleted ON policies(policy_name, is_deleted);

-- Index for expiry_date queries used by scheduled jobs
CREATE INDEX IF NOT EXISTS idx_policies_expiry ON policies(expiry_date);

-- Composite index for policy_holder search with is_deleted filter
CREATE INDEX IF NOT EXISTS idx_policies_holder_isdeleted ON policies(policy_holder, is_deleted);

-- Composite index for status + expiry_date (optimized for filtering by status and sorting by expiry)
CREATE INDEX IF NOT EXISTS idx_policies_status_expiry ON policies(status, expiry_date);

-- Partial index for non-deleted policies (covers 90% of application queries)
CREATE INDEX IF NOT EXISTS idx_policies_active_not_deleted ON policies(status, expiry_date)
    WHERE is_deleted = false;

