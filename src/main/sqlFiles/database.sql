-- Userprofile table
CREATE TABLE UserProfile(
id INTEGER PRIMARY KEY AUTO_INCREMENT,
first_name VARCHAR(10) NOT NULL,
last_name VARCHAR(10) NOT NULL,
e_mail VARCHAR(30) UNIQUE NOT NULL,
phone VARCHAR(8) UNIQUE NOT NULL,
address VARCHAR(40) NOT NULL,
gender VARCHAR(10) NOT NULL,
age INTEGER NOT NULL,
user_password VARCHAR(15),
USERNAME  VARCHAR(30) UNIQUE
);


-- trigger before insert data to database, create username first_name + last_name + two last digits from phone number
 DELIMITER $$
 CREATE TRIGGER before_insert_user BEFORE INSERT ON UserProfile
 FOR EACH ROW
 BEGIN
     IF NEW.username IS NULL THEN
         SET NEW.username = CONCAT(NEW.first_name, NEW.last_name, RIGHT(NEW.phone, 2));
     END IF;
 END$$
 DELIMITER ;


-- trigger before update user information, update username, first_name + last_name + two last digits from phone number
 DELIMITER $$
 CREATE TRIGGER before_update_user
 BEFORE UPDATE ON UserProfile FOR EACH ROW
 BEGIN
     SET NEW.username = CONCAT(NEW.first_name, NEW.last_name, RIGHT(NEW.phone, 2));
 END$$
 DELIMITER ;


-- Message table
CREATE TABLE Messages (
id INT AUTO_INCREMENT PRIMARY KEY,
receiver_id INT NOT NULL,
sender_id INT NOT NULL,
time_sent TIMESTAMP,
message VARCHAR(100),
CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES userprofile(id),
CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES userprofile(id)
);