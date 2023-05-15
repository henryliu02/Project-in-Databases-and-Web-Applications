USE moviedb;

DELIMITER $$

CREATE PROCEDURE insert_star(IN p_name VARCHAR(100), IN p_birthYear INT, OUT p_starId VARCHAR(10))
BEGIN
  DECLARE max_id INT;
  DECLARE new_id INT;
  DECLARE new_id_str CHAR(10);
  
  SELECT MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)) INTO max_id
  FROM stars;

  SET new_id = max_id + 1;
  SET new_id_str = CONCAT('nm', LPAD(new_id, 7, '0'));

  INSERT INTO stars (id, name, birthYear)
  VALUES (new_id_str, p_name, p_birthYear);

  SET p_starId = new_id_str;
END $$

DELIMITER ;
