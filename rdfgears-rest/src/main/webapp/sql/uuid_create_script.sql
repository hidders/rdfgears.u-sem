/* The following is not needed since a user is implicitly created if not exist by the GRANT operation */
/* CREATE USER 'imreal'@'localhost' IDENTIFIED BY 'imreal'; */

GRANT USAGE ON *.* TO 'imreal'@'localhost' IDENTIFIED BY 'imreal' WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0 ;

CREATE DATABASE IF NOT EXISTS `imreal` ;

GRANT ALL PRIVILEGES ON `imreal`.* TO 'imreal'@'localhost';

USE imreal;

CREATE TABLE IF NOT EXISTS uuid (
	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
	uuid VARCHAR(100) NOT NULL UNIQUE,
	email VARCHAR(100)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS uuid_webid (
	uuid_id int NOT NULL,
	webid VARCHAR(100),
	provider VARCHAR(100),
	PRIMARY KEY (uuid_id, webid, provider),
	FOREIGN KEY (uuid_id) REFERENCES uuid (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS userProfile (
	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
	uuid_id int NOT NULL,
	topic VARCHAR(100),
	dvalue TEXT,
	FOREIGN KEY (uuid_id) REFERENCES uuid (id),
	UNIQUE KEY (uuid_id, topic)
) ENGINE=InnoDB;