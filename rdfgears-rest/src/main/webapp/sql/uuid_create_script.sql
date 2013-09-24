-- -
-- #%L
-- RDFGears
-- %%
-- Copyright (C) 2013 WIS group at the TU Delft (http://www.wis.ewi.tudelft.nl/)
-- %%
-- Permission is hereby granted, free of charge, to any person obtaining a copy
-- of this software and associated documentation files (the "Software"), to deal
-- in the Software without restriction, including without limitation the rights
-- to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
-- copies of the Software, and to permit persons to whom the Software is
-- furnished to do so, subject to the following conditions:
-- 
-- The above copyright notice and this permission notice shall be included in
-- all copies or substantial portions of the Software.
-- 
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
-- IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
-- FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
-- AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
-- LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
-- OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
-- THE SOFTWARE.
-- #L%
-- -
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
