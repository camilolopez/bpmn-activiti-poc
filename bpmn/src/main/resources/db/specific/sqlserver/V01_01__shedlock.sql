CREATE TABLE shedlock(
	name VARCHAR(64) NOT NULL, 
	lock_until datetime2 NOT NULL,
    locked_at datetime2 NOT NULL, 
    locked_by VARCHAR(255) NOT NULL, 
    PRIMARY KEY (name));