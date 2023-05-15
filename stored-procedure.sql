USE moviedb;

DELIMITER //

CREATE PROCEDURE add_movie(IN movie_title VARCHAR(100), IN movie_year INT, IN movie_director VARCHAR(100),
                           IN star_name VARCHAR(100), IN star_birthYear INT, IN genre_name VARCHAR(32),
                           OUT out_movie_id VARCHAR(10), OUT out_star_id VARCHAR(10), OUT out_genre_id INT)
BEGIN
    DECLARE max_movie_id, new_movie_id VARCHAR(10);
    DECLARE max_star_id, new_star_id VARCHAR(10);
    DECLARE max_genre_id, new_genre_id INT;
    DECLARE existing_star_id, existing_genre_id VARCHAR(10);
    DECLARE existing_movie_id VARCHAR(10);
    DECLARE movie_id_prefix_length, star_id_prefix_length INT;

    -- Check if movie already exists
    SELECT id INTO existing_movie_id FROM movies WHERE title = movie_title AND year = movie_year AND director = movie_director;
    IF existing_movie_id IS NOT NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The movie already exists.';
    ELSE
        -- Generate new movie id
        SELECT MAX(id) INTO max_movie_id FROM movies WHERE id LIKE 'tt0%';
        SET movie_id_prefix_length = 2;
        
        SET new_movie_id = CONCAT(
            SUBSTRING(max_movie_id, 1, movie_id_prefix_length),
            LPAD(
                CAST(SUBSTRING(max_movie_id, movie_id_prefix_length + 1) AS UNSIGNED) + 1,
                7,
                '0'
            )
        );

        -- Check if star already exists
        SELECT id INTO existing_star_id FROM stars WHERE name = star_name;
        IF existing_star_id IS NULL THEN
            -- Generate new star id
            SELECT MAX(id) INTO max_star_id FROM stars WHERE id LIKE 'nm%';
            SET star_id_prefix_length = 2;
            
            SET new_star_id = CONCAT(
                SUBSTRING(max_star_id, 1, star_id_prefix_length),
                LPAD(
                    CAST(SUBSTRING(max_star_id, star_id_prefix_length + 1) AS UNSIGNED) + 1,
                    7,
                    '0'
                )
            );

            -- Insert new star
            INSERT INTO stars (id, name, birthYear) VALUES (new_star_id, star_name, star_birthYear);
            SET existing_star_id = new_star_id;
        END IF;

        -- Check if genre already exists
        SELECT id INTO existing_genre_id FROM genres WHERE name = genre_name;
        IF existing_genre_id IS NULL THEN
            -- Generate new genre id
            SELECT MAX(id) INTO max_genre_id FROM genres;
            SET new_genre_id = max_genre_id + 1;

            -- Insert new genre
            INSERT INTO genres (id, name) VALUES (new_genre_id, genre_name);
            SET existing_genre_id = new_genre_id;
        END IF;

        -- Insert new movie
        INSERT INTO movies (id, title, year, director) VALUES (new_movie_id, movie_title, movie_year, movie_director);

        -- Insert records into stars_in_movies and genres_in_movies
        INSERT INTO stars_in_movies (starId, movieId) VALUES (existing_star_id, new_movie_id);
        INSERT INTO genres_in_movies (genreId, movieId) VALUES (existing_genre_id, new_movie_id);

        -- Set the OUT parameters
        SET out_movie_id = new_movie_id;
        SET out_star_id = existing_star_id;
        SET out_genre_id = existing_genre_id;
    END IF;
END //

DELIMITER ;
