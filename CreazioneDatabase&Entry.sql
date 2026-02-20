-- -----------------------------------------------------
-- 1. SETUP DATABASE
-- -----------------------------------------------------
DROP DATABASE IF EXISTS `Biglietteria_SE`;
CREATE SCHEMA IF NOT EXISTS `Biglietteria_SE` DEFAULT CHARACTER SET utf8 ;
USE `Biglietteria_SE` ;

-- -----------------------------------------------------
-- 2. CREAZIONE TABELLE (DDL)
-- -----------------------------------------------------

-- Table TIPO_EVENTO
CREATE TABLE IF NOT EXISTS `Biglietteria_SE`.`TIPO_EVENTO` (
  `idTIPO_EVENTO` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(45) NULL,
  PRIMARY KEY (`idTIPO_EVENTO`))
ENGINE = InnoDB;

-- Table TIPO_ORGANIZZAZIONE
CREATE TABLE IF NOT EXISTS `Biglietteria_SE`.`TIPO_ORGANIZZAZIONE` (
  `idTIPO_ORGANIZZAZIONE` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(45) NULL,
  PRIMARY KEY (`idTIPO_ORGANIZZAZIONE`))
ENGINE = InnoDB;

-- Table ORGANIZZAZIONE
CREATE TABLE IF NOT EXISTS `Biglietteria_SE`.`ORGANIZZAZIONE` (
  `idORGANIZZAZIONE` INT NOT NULL AUTO_INCREMENT,
  `TIPOLOGIA_ORGANIZZAZIONE_idTIPOLOGIA_ORGANIZZAZIONE` INT NOT NULL,
  `nome` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  `password` VARCHAR(45) NULL,
  PRIMARY KEY (`idORGANIZZAZIONE`),
  INDEX `fk_ORGANIZZAZIONE_TIPOLOGIA_ORGANIZZAZIONE1_idx` (`TIPOLOGIA_ORGANIZZAZIONE_idTIPOLOGIA_ORGANIZZAZIONE` ASC) VISIBLE,
  CONSTRAINT `fk_ORGANIZZAZIONE_TIPOLOGIA_ORGANIZZAZIONE1`
    FOREIGN KEY (`TIPOLOGIA_ORGANIZZAZIONE_idTIPOLOGIA_ORGANIZZAZIONE`)
    REFERENCES `Biglietteria_SE`.`TIPO_ORGANIZZAZIONE` (`idTIPO_ORGANIZZAZIONE`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- Table LUOGO_EVENTO
CREATE TABLE IF NOT EXISTS `Biglietteria_SE`.`LUOGO_EVENTO` (
  `idLUOGO_EVENTO` INT NOT NULL AUTO_INCREMENT,
  `luogo` VARCHAR(45) NULL,
  `maxPosti` INT NULL,
  PRIMARY KEY (`idLUOGO_EVENTO`))
ENGINE = InnoDB;

-- Table EVENTI
CREATE TABLE IF NOT EXISTS `Biglietteria_SE`.`EVENTI` (
  `idEvento` INT NOT NULL AUTO_INCREMENT,
  `titolo` VARCHAR(45) NULL,
  `descrizione` VARCHAR(400) NULL,
  `costi` VARCHAR(45) NOT NULL,
  `dataEvento` DATE NOT NULL,
  `orarioInizio` TIME NOT NULL,
  `orarioFine` TIME NOT NULL,
  `tagTematici` VARCHAR(45) NULL,
  `noteOrganizzative` VARCHAR(400) NULL,
  `TIPO_EVENTO_idTIPO_EVENTO` INT NOT NULL,
  `ORGANIZZAZIONE_idORGANIZZAZIONE` INT NOT NULL,
  `LUOGO_EVENTO_idLUOGO_EVENTO` INT NOT NULL,
  PRIMARY KEY (`idEvento`),
  INDEX `fk_EVENTI_TIPO_EVENTO_idx` (`TIPO_EVENTO_idTIPO_EVENTO` ASC) VISIBLE,
  INDEX `fk_EVENTI_ORGANIZZAZIONE1_idx` (`ORGANIZZAZIONE_idORGANIZZAZIONE` ASC) VISIBLE,
  INDEX `fk_EVENTI_LUOGO_EVENTO1_idx` (`LUOGO_EVENTO_idLUOGO_EVENTO` ASC) VISIBLE,
  CONSTRAINT `fk_EVENTI_TIPO_EVENTO`
    FOREIGN KEY (`TIPO_EVENTO_idTIPO_EVENTO`)
    REFERENCES `Biglietteria_SE`.`TIPO_EVENTO` (`idTIPO_EVENTO`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_EVENTI_ORGANIZZAZIONE1`
    FOREIGN KEY (`ORGANIZZAZIONE_idORGANIZZAZIONE`)
    REFERENCES `Biglietteria_SE`.`ORGANIZZAZIONE` (`idORGANIZZAZIONE`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_EVENTI_LUOGO_EVENTO1`
    FOREIGN KEY (`LUOGO_EVENTO_idLUOGO_EVENTO`)
    REFERENCES `Biglietteria_SE`.`LUOGO_EVENTO` (`idLUOGO_EVENTO`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- Table CLIENTE
CREATE TABLE IF NOT EXISTS `Biglietteria_SE`.`CLIENTE` (
  `idCLIENTE` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(45) NOT NULL,
  `cognome` VARCHAR(45) NOT NULL,
  `compleanno` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idCLIENTE`))
ENGINE = InnoDB;

-- Table STATO_PRENOTAZIONE
CREATE TABLE IF NOT EXISTS `Biglietteria_SE`.`STATO_PRENOTAZIONE` (
  `idSTATO_PRENOTAZIONE` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(45) NULL,
  PRIMARY KEY (`idSTATO_PRENOTAZIONE`))
ENGINE = InnoDB;

-- Table PRENOTAZIONE
CREATE TABLE IF NOT EXISTS `Biglietteria_SE`.`PRENOTAZIONE` (
  `idPRENOTAZIONE` INT NOT NULL AUTO_INCREMENT,
  `data` VARCHAR(45) NOT NULL,
  `time` TIME NOT NULL,
  `postiPrenotati` VARCHAR(400) NOT NULL,
  `CLIENTE_idCLIENTE` INT NOT NULL,
  `EVENTI_idEvento` INT NOT NULL,
  `STATO_PRENOTAZIONE_idSTATO_PRENOTAZIONE` INT NOT NULL,
  PRIMARY KEY (`idPRENOTAZIONE`),
  INDEX `fk_PRENOTAZIONE_CLIENTE1_idx` (`CLIENTE_idCLIENTE` ASC) VISIBLE,
  INDEX `fk_PRENOTAZIONE_EVENTI1_idx` (`EVENTI_idEvento` ASC) VISIBLE,
  INDEX `fk_PRENOTAZIONE_STATO_PRENOTAZIONE1_idx` (`STATO_PRENOTAZIONE_idSTATO_PRENOTAZIONE` ASC) VISIBLE,
  CONSTRAINT `fk_PRENOTAZIONE_CLIENTE1`
    FOREIGN KEY (`CLIENTE_idCLIENTE`)
    REFERENCES `Biglietteria_SE`.`CLIENTE` (`idCLIENTE`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PRENOTAZIONE_EVENTI1`
    FOREIGN KEY (`EVENTI_idEvento`)
    REFERENCES `Biglietteria_SE`.`EVENTI` (`idEvento`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PRENOTAZIONE_STATO_PRENOTAZIONE1`
    FOREIGN KEY (`STATO_PRENOTAZIONE_idSTATO_PRENOTAZIONE`)
    REFERENCES `Biglietteria_SE`.`STATO_PRENOTAZIONE` (`idSTATO_PRENOTAZIONE`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- Table POSTI
CREATE TABLE IF NOT EXISTS `Biglietteria_SE`.`POSTI` (
  `idPOSTI` INT NOT NULL,
  `EVENTI_idEvento` INT NOT NULL,
  PRIMARY KEY (`idPOSTI`, `EVENTI_idEvento`),
  INDEX `fk_POSTI_EVENTI1_idx` (`EVENTI_idEvento` ASC) VISIBLE,
  CONSTRAINT `fk_POSTI_EVENTI1`
    FOREIGN KEY (`EVENTI_idEvento`)
    REFERENCES `Biglietteria_SE`.`EVENTI` (`idEvento`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- Table TIPO_COLLABORATORI
CREATE TABLE IF NOT EXISTS `Biglietteria_SE`.`TIPO_COLLABORATORI` (
  `idTIPO_COLLABORATORI` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(45) NULL,
  PRIMARY KEY (`idTIPO_COLLABORATORI`))
ENGINE = InnoDB;

-- Table COLLABORATORI
-- NOTA: Ho aggiunto la colonna `disponibilita` che era presente nell'INSERT ma mancava nella CREATE TABLE
CREATE TABLE IF NOT EXISTS `Biglietteria_SE`.`COLLABORATORI` (
  `idCOLLABORATORI` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(45) NOT NULL,
  `compenso` VARCHAR(45) NOT NULL,
  `disponibilita` TINYINT NULL, 
  `comunicazioniInterne` VARCHAR(400) NOT NULL,
  `TIPO_COLLABORATORI_idTIPO_COLLABORATORI` INT NOT NULL,
  PRIMARY KEY (`idCOLLABORATORI`),
  INDEX `fk_COLLABORATORI_TIPO_COLLABORATORI1_idx` (`TIPO_COLLABORATORI_idTIPO_COLLABORATORI` ASC) VISIBLE,
  CONSTRAINT `fk_COLLABORATORI_TIPO_COLLABORATORI1`
    FOREIGN KEY (`TIPO_COLLABORATORI_idTIPO_COLLABORATORI`)
    REFERENCES `Biglietteria_SE`.`TIPO_COLLABORATORI` (`idTIPO_COLLABORATORI`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- Table EVENTO_COLLABORATORE
CREATE TABLE IF NOT EXISTS `Biglietteria_SE`.`EVENTO_COLLABORATORE` (
  `COLLABORATORI_idCOLLABORATORI` INT NOT NULL,
  `EVENTI_idEvento` INT NOT NULL,
  INDEX `fk_EVENTO_COLLABORATORE_COLLABORATORI1_idx` (`COLLABORATORI_idCOLLABORATORI` ASC) VISIBLE,
  PRIMARY KEY (`EVENTI_idEvento`, `COLLABORATORI_idCOLLABORATORI`),
  CONSTRAINT `fk_EVENTO_COLLABORATORE_COLLABORATORI1`
    FOREIGN KEY (`COLLABORATORI_idCOLLABORATORI`)
    REFERENCES `Biglietteria_SE`.`COLLABORATORI` (`idCOLLABORATORI`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_EVENTO_COLLABORATORE_EVENTI1`
    FOREIGN KEY (`EVENTI_idEvento`)
    REFERENCES `Biglietteria_SE`.`EVENTI` (`idEvento`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- Table Lista_attesa
CREATE TABLE IF NOT EXISTS `Biglietteria_SE`.`Lista_attesa` (
  `data` VARCHAR(45) NULL,
  `ora` VARCHAR(45) NULL,
  `EVENTI_idEvento` INT NOT NULL,
  `CLIENTE_idCLIENTE` INT NOT NULL,
  PRIMARY KEY (`EVENTI_idEvento`, `CLIENTE_idCLIENTE`),
  INDEX `fk_Lista_attesa_EVENTI1_idx` (`EVENTI_idEvento` ASC) VISIBLE,
  INDEX `fk_Lista_attesa_CLIENTE1_idx` (`CLIENTE_idCLIENTE` ASC) VISIBLE,
  CONSTRAINT `fk_Lista_attesa_EVENTI1`
    FOREIGN KEY (`EVENTI_idEvento`)
    REFERENCES `Biglietteria_SE`.`EVENTI` (`idEvento`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Lista_attesa_CLIENTE1`
    FOREIGN KEY (`CLIENTE_idCLIENTE`)
    REFERENCES `Biglietteria_SE`.`CLIENTE` (`idCLIENTE`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;





-- -----------------------------------------------------
-- 3. INSERIMENTO DATI (DML)
-- -----------------------------------------------------



-- --------------------------------------------------
-- Tipi fissi
-- --------------------------------------------------
INSERT IGNORE INTO tipo_collaboratori (nome) VALUES ('RELATORE');
INSERT IGNORE INTO tipo_collaboratori (nome) VALUES ('ARTISTA');
INSERT IGNORE INTO tipo_collaboratori (nome) VALUES ('TECNICO');

INSERT IGNORE INTO stato_prenotazione (nome) VALUES ('COMPLETATA');
INSERT IGNORE INTO stato_prenotazione (nome) VALUES ('ATTESA');
INSERT IGNORE INTO stato_prenotazione (nome) VALUES ('ANNULLATA');

INSERT IGNORE INTO tipo_evento (nome) VALUES ('CONCERTO');
INSERT IGNORE INTO tipo_evento (nome) VALUES ('MOSTRA');
INSERT IGNORE INTO tipo_evento (nome) VALUES ('RASSEGNA CINEMATOGRAFICA');
INSERT IGNORE INTO tipo_evento (nome) VALUES ('SPETTACOLO TEATRALE');
INSERT IGNORE INTO tipo_evento (nome) VALUES ('CONFERENZA');

INSERT IGNORE INTO tipo_organizzazione (nome) VALUES ('COMUNE');
INSERT IGNORE INTO tipo_organizzazione (nome) VALUES ('ASSOCIAZIONE');
INSERT IGNORE INTO tipo_organizzazione (nome) VALUES ('PICCOLA FONDAZIONE');


INSERT IGNORE INTO organizzazione
(idORGANIZZAZIONE, TIPOLOGIA_ORGANIZZAZIONE_idTIPOLOGIA_ORGANIZZAZIONE, nome, email, password)
VALUES (1, 1, 'Organizzazione', 'organizzazione@gmail.com', 'password');


-- --------------------------------------------------
-- Luoghi
-- --------------------------------------------------
INSERT IGNORE INTO luogo_evento (idLUOGO_EVENTO, luogo, maxPosti) VALUES
(1, 'Teatro Comunale', 200),
(2, 'Sala Conferenze', 100),
(3, 'Auditorium', 150),
(4, 'Galleria Mostre', 80);


-- --------------------------------------------------
-- Eventi per l'organizzazione 1
-- --------------------------------------------------
INSERT INTO eventi 
(idEvento, titolo, descrizione, costi, dataEvento, orarioInizio, orarioFine, tagTematici, noteOrganizzative, TIPO_EVENTO_idTIPO_EVENTO, ORGANIZZAZIONE_idORGANIZZAZIONE, LUOGO_EVENTO_idLUOGO_EVENTO)
VALUES
(1, 'Concerto Jazz', 'Concerto di musica jazz', '20', '2025-11-15', '20:00:00', '22:00:00', 'musica,jazz', '', 1, 1, 1),
(2, 'Mostra Arte Moderna', 'Esposizione di artisti contemporanei', '10', '2025-11-20', '10:00:00', '18:00:00', 'arte,moderna', '', 2, 1, 4),
(3, 'Rassegna Film Indie', 'Proiezione film indipendenti', '15', '2025-11-25', '18:00:00', '22:00:00', 'cinema,indie', '', 3, 1, 3),
(4, 'Spettacolo Teatrale Classico', 'Opera teatrale tradizionale', '25', '2025-12-01', '19:00:00', '21:30:00', 'teatro,classico', '', 4, 1, 1),
(5, 'Conferenza Scienza', 'Evento scientifico per studenti', '0', '2025-12-05', '15:00:00', '17:00:00', 'scienza,conferenza', '', 5, 1, 2),
(6, 'Concerto Rock', 'Serata rock con band locali', '30', '2025-12-10', '21:00:00', '23:30:00', 'musica,rock', '', 1, 1, 1),
(7, 'Mostra Fotografia', 'Esposizione fotografica', '12', '2025-12-15', '10:00:00', '18:00:00', 'fotografia,arte', '', 2, 1, 4),
(8, 'Rassegna Documentari', 'Proiezione documentari internazionali', '15', '2025-12-20', '17:00:00', '21:00:00', 'cinema,documentari', '', 3, 1, 3),
(9, 'Spettacolo Comico', 'Serata cabaret e comicità', '18', '2025-12-22', '20:00:00', '22:30:00', 'comico,spettacolo', '', 4, 1, 1),
(10, 'Conferenza Letteraria', 'Discussione e lettura di libri', '5', '2025-12-28', '16:00:00', '18:00:00', 'letteratura,conferenza', '', 5, 1, 2);


-- --------------------------------------------------
-- Collaboratori
-- --------------------------------------------------
INSERT INTO collaboratori (idCOLLABORATORI, nome, compenso, comunicazioniInterne, TIPO_COLLABORATORI_idTIPO_COLLABORATORI)
VALUES
(1, 'Mario Rossi', '100', '', 2),
(2, 'Anna Bianchi', '150', '', 2),
(3, 'Carlo Verdi', '80', '', 1),
(4, 'Laura Neri', '120', '', 3);


-- --------------------------------------------------
-- Associa collaboratori agli eventi
-- --------------------------------------------------
INSERT INTO evento_collaboratore (COLLABORATORI_idCOLLABORATORI, EVENTI_idEvento) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(1, 5),
(2, 6),
(3, 7),
(4, 8),
(1, 9),
(2, 10);


-- Evento 1: Concerto Jazz (18 posti, alcuni "vuoti")
INSERT INTO posti (idPOSTI, EVENTI_idEvento) VALUES
(1,1),(2,1),(3,1),(5,1),(6,1),(8,1),(9,1),(10,1),(12,1),(13,1),(14,1),(15,1),(17,1),(18,1),(19,1),(20,1);

-- Evento 2: Mostra Arte Moderna (12 posti, alcuni "vuoti")
INSERT INTO posti (idPOSTI, EVENTI_idEvento) VALUES
(1,2),(2,2),(3,2),(5,2),(6,2),(7,2),(9,2),(10,2),(11,2),(12,2),(14,2),(15,2);

-- Evento 3: Rassegna Film Indie (15 posti, tutti consecutivi)
INSERT INTO posti (idPOSTI, EVENTI_idEvento) VALUES
(1,3),(2,3),(3,3),(4,3),(5,3),(6,3),(7,3),(8,3),(9,3),(10,3),(11,3),(12,3),(13,3),(14,3),(15,3);

-- Evento 4: Spettacolo Teatrale (20 posti, saltando alcuni)
INSERT INTO posti (idPOSTI, EVENTI_idEvento) VALUES
(1,4),(2,4),(3,4),(5,4),(6,4),(7,4),(9,4),(10,4),(11,4),(12,4),(14,4),(15,4),(16,4),(17,4),(19,4),(20,4);

-- Evento 5: Conferenza Scienza (10 posti, tutti consecutivi)
INSERT INTO posti (idPOSTI, EVENTI_idEvento) VALUES
(1,5),(2,5),(3,5),(4,5),(5,5),(6,5),(7,5),(8,5),(9,5),(10,5);

-- Evento 6: Concerto Pop (25 posti, alcuni vuoti)
INSERT INTO posti (idPOSTI, EVENTI_idEvento) VALUES
(1,6),(2,6),(3,6),(4,6),(6,6),(7,6),(8,6),(10,6),(11,6),(12,6),(13,6),(15,6),(16,6),(17,6),(18,6),(20,6),(21,6),(22,6),(23,6),(25,6);

-- Evento 7: Mostra Fotografia (18 posti, tutti consecutivi)
INSERT INTO posti (idPOSTI, EVENTI_idEvento) VALUES
(1,7),(2,7),(3,7),(4,7),(5,7),(6,7),(7,7),(8,7),(9,7),(10,7),(11,7),(12,7),(13,7),(14,7),(15,7),(16,7),(17,7),(18,7);

-- Evento 8: Festival Corti (22 posti, salti casuali)
INSERT INTO posti (idPOSTI, EVENTI_idEvento) VALUES
(1,8),(2,8),(3,8),(5,8),(6,8),(7,8),(8,8),(10,8),(11,8),(12,8),(13,8),(15,8),(16,8),(17,8),(18,8),(20,8),(21,8),(22,8);

-- Evento 9: Teatro Commedia Musicale (30 posti, alcuni vuoti)
INSERT INTO posti (idPOSTI, EVENTI_idEvento) VALUES
(1,9),(2,9),(3,9),(4,9),(6,9),(7,9),(8,9),(10,9),(11,9),(12,9),(14,9),(15,9),(16,9),(17,9),(19,9),(20,9),(21,9),(23,9),(24,9),(25,9),(27,9),(28,9),(29,9),(30,9);

-- Evento 10: Conferenza Innovazione (12 posti, tutti consecutivi)
INSERT INTO posti (idPOSTI, EVENTI_idEvento) VALUES
(1,10),(2,10),(3,10),(4,10),(5,10),(6,10),(7,10),(8,10),(9,10),(10,10),(11,10),(12,10);
