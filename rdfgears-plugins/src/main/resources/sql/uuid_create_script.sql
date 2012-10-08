CREATE DATABASE IF NOT EXISTS imreal;

USE imreal;

CREATE TABLE IF NOT EXISTS UUID (
	uuid VARCHAR(100) NOT NULL,
	webid VARCHAR(100),
	provider VARCHAR(100),
	CONSTRAINT pk_uuid PRIMARY KEY (uuid, webid, provider)
);