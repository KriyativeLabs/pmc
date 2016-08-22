ALTER TABLE companies ADD COLUMN mso_type VARCHAR(32) NOT NULL DEFAULT 'UNKNOWN';
ALTER TABLE companies ADD COLUMN cred1 VARCHAR(128);
ALTER TABLE companies ADD COLUMN cred2 VARCHAR(128);
ALTER TABLE companies ADD COLUMN cred3 VARCHAR(128);
ALTER TABLE companies ADD COLUMN cred4 VARCHAR(128);

ALTER TABLE connections ADD COLUMN mso_status VARCHAR(32) NOT NULL DEFAULT 'UNKNOWN';

ALTER TABLE connections ADD COLUMN is_archived BOOLEAN NOT NULL DEFAULT false;
