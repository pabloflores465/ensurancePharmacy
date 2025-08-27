-- ============================================================================
-- BACKV4 - Add Missing Tables Migration Script
-- ============================================================================
-- This script adds tables that may be missing from the initial schema
-- Version: 1.1
-- Date: 2025-08-27
-- ============================================================================

-- Add SUBCATEGORY table if it doesn't exist
CREATE TABLE IF NOT EXISTS SUBCATEGORY (
    ID_SUBCATEGORY INTEGER PRIMARY KEY AUTOINCREMENT,
    ENABLED INTEGER NOT NULL DEFAULT 1,
    NAME VARCHAR(100) NOT NULL,
    ID_CATEGORY BIGINT NOT NULL,
    FOREIGN KEY (ID_CATEGORY) REFERENCES CATEGORY(ID_CATEGORY)
);

-- Update SYSTEM_CONFIG table to match current structure
-- Add missing LAST_UPDATED column if it doesn't exist
ALTER TABLE SYSTEM_CONFIG ADD COLUMN LAST_UPDATED TIMESTAMP;

-- Drop old timestamp columns if they exist and rename
-- Note: SQLite doesn't support DROP COLUMN, so we'll work with what we have

-- Create indexes for new tables
CREATE INDEX IF NOT EXISTS idx_subcategory_category ON SUBCATEGORY(ID_CATEGORY);
CREATE INDEX IF NOT EXISTS idx_subcategory_name ON SUBCATEGORY(NAME);

-- Insert default subcategories if they don't exist
INSERT OR IGNORE INTO SUBCATEGORY (NAME, ID_CATEGORY, ENABLED) VALUES
('Consulta General', 2, 1),
('Consulta Especializada', 2, 1),
('Examen de Sangre', 3, 1),
('Radiografía', 3, 1),
('Urgencias Menores', 4, 1),
('Urgencias Mayores', 4, 1);

-- ============================================================================
-- End of migration script
-- ============================================================================
