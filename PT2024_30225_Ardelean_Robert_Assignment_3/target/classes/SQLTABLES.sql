-- Ștergerea bazei de date dacă există
DROP DATABASE IF EXISTS `warehouse_db`;

-- Crearea bazei de date dacă nu există
CREATE DATABASE IF NOT EXISTS `warehouse_db` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `warehouse_db`;

-- Setările inițiale pentru caracterul și timpul de execuție
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Crearea tabelului Client
CREATE TABLE `Client` (
                          `client_id` INT(11) NOT NULL AUTO_INCREMENT,
                          `name` VARCHAR(100) NOT NULL,
                          `email` VARCHAR(100) NOT NULL,
                          `phone` VARCHAR(15),
                          `address` VARCHAR(255),
                          `age` INT(11) NOT NULL,
                          PRIMARY KEY (`client_id`),
                          UNIQUE KEY (`email`),
                          CONSTRAINT chk_email CHECK (`email` LIKE '%_@__%.__%'),
                          CONSTRAINT chk_age CHECK (`age` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Crearea tabelului Product
CREATE TABLE `Product` (
                           `product_id` INT(11) NOT NULL AUTO_INCREMENT,
                           `name` VARCHAR(100) NOT NULL,
                           `price` DECIMAL(10, 2) NOT NULL,
                           `stock` INT(11) NOT NULL,
                           PRIMARY KEY (`product_id`),
                           CONSTRAINT chk_price CHECK (`price` >= 0),
                           CONSTRAINT chk_stock CHECK (`stock` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Crearea tabelului Order
CREATE TABLE `Order` (
                         `order_id` INT(11) NOT NULL AUTO_INCREMENT,
                         `client_id` INT(11) NOT NULL,
                         `product_id` INT(11) NOT NULL,
                         `quantity` INT(11) NOT NULL,
                         `order_date` DATETIME NOT NULL,
                         PRIMARY KEY (`order_id`),
                         FOREIGN KEY (`client_id`) REFERENCES `Client`(`client_id`),
                         FOREIGN KEY (`product_id`) REFERENCES `Product`(`product_id`),
                         CONSTRAINT chk_quantity CHECK (`quantity` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Crearea tabelului Bill
CREATE TABLE `Bill` (
                        `bill_id` INT AUTO_INCREMENT PRIMARY KEY,
                        `order_id` INT,
                        `client_id` INT NOT NULL,
                        `product_id` INT NOT NULL,
                        `quantity` INT NOT NULL,
                        `total_price` DECIMAL(10, 2) NOT NULL,
                        `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (`order_id`) REFERENCES `Order`(`order_id`) ON DELETE SET NULL,
                        FOREIGN KEY (`client_id`) REFERENCES `Client`(`client_id`) ON DELETE CASCADE,
                        FOREIGN KEY (`product_id`) REFERENCES `Product`(`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Popularea tabelului Client
INSERT INTO Client (name, email, phone, address, age) VALUES
                                                          ('Liam Brown', 'liam.brown@example.com', '1234567891', '101 First St', 30),
                                                          ('Olivia Wilson', 'olivia.wilson@example.com', '1234567892', '202 Second St', 25),
                                                          ('Noah Johnson', 'noah.johnson@example.com', '1234567893', '303 Third St', 28),
                                                          ('Emma Smith', 'emma.smith@example.com', '1234567894', '404 Fourth St', 22),
                                                          ('James Williams', 'james.williams@example.com', '1234567895', '505 Fifth St', 18),
                                                          ('Ava Jones', 'ava.jones@example.com', '1234567896', '606 Sixth St', 27),
                                                          ('Sophia Miller', 'sophia.miller@example.com', '1234567897', '707 Seventh St', 26);

-- Popularea tabelului Product
INSERT INTO Product (name, price, stock) VALUES
                                             ('Laptop', 1200.50, 30),
                                             ('Smartphone', 800.75, 45),
                                             ('Tablet', 400.99, 60),
                                             ('Monitor', 200.49, 25),
                                             ('Keyboard', 50.99, 100);

-- Popularea tabelului Order cu date diferite
INSERT INTO `Order` (client_id, product_id, quantity, order_date) VALUES
                                                                      (1, 1, 1, '2023-05-01 10:30:00'),  -- Comanda pentru Liam Brown pe 1 mai 2023
                                                                      (1, 3, 2, '2023-05-01 10:30:00'),  -- Comanda pentru Liam Brown pe 1 mai 2023
                                                                      (2, 2, 1, '2023-05-02 14:45:00'),  -- Comanda pentru Olivia Wilson pe 2 mai 2023
                                                                      (2, 4, 1, '2023-05-02 14:45:00'),  -- Comanda pentru Olivia Wilson pe 2 mai 2023
                                                                      (3, 3, 1, '2023-05-03 09:15:00'),  -- Comanda pentru Noah Johnson pe 3 mai 2023
                                                                      (3, 5, 4, '2023-05-03 09:15:00'),  -- Comanda pentru Noah Johnson pe 3 mai 2023
                                                                      (4, 1, 2, '2023-05-04 16:00:00'),  -- Comanda pentru Emma Smith pe 4 mai 2023
                                                                      (4, 2, 2, '2023-05-04 16:00:00'),  -- Comanda pentru Emma Smith pe 4 mai 2023
                                                                      (5, 4, 2, '2023-05-05 11:20:00'),  -- Comanda pentru James Williams pe 5 mai 2023
                                                                      (5, 5, 3, '2023-05-05 11:20:00');  -- Comanda pentru James Williams pe 5 mai 2023

-- Verificarea datelor
SELECT * FROM Client;
SELECT * FROM Product;
SELECT * FROM `Order`;

-- Resetarea setărilor inițiale
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
