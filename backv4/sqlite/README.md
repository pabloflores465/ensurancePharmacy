# BackV4 SQLite Migration Scripts

This directory contains SQL migration scripts for the BackV4 (Ensurance) system database.

## Migration Scripts

### 01_initial_schema.sql
- Creates the complete initial database schema
- Includes all tables for users, policies, hospitals, medicines, prescriptions, etc.
- Sets up indexes for performance optimization
- Inserts default system configurations and initial data

### 02_add_missing_tables.sql
- Adds the SUBCATEGORY table if missing
- Updates SYSTEM_CONFIG table structure
- Creates additional indexes
- Inserts default subcategory data

## Usage

To apply migrations to a new database:

```bash
# Navigate to the sqlite directory
cd backv4/sqlite/

# Apply initial schema
sqlite3 USUARIO.sqlite < 01_initial_schema.sql

# Apply additional migrations
sqlite3 USUARIO.sqlite < 02_add_missing_tables.sql
```

## Database Structure

The BackV4 database includes:
- **USERS**: User management and authentication
- **POLICY**: Insurance policies
- **HOSPITALS**: Hospital information
- **MEDICINE**: Medicine catalog
- **PRESCRIPTION**: Medical prescriptions
- **TRANSACTIONS**: Financial transactions
- **APPOINTMENTS**: Medical appointments
- **SYSTEM_CONFIG**: System configuration parameters

## Notes

- All scripts use `CREATE TABLE IF NOT EXISTS` to prevent errors on re-runs
- Foreign key constraints are implied but not explicitly enforced in SQLite
- Indexes are created for commonly queried columns
- Default data is inserted using `INSERT OR IGNORE` to prevent duplicates
