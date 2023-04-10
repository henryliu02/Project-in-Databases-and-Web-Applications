CREATE TABLE if not exists movies(
       	    id VARCHAR(10) DEFAULT '',
       	    title VARCHAR(100) DEFAULT '',
       	    year INTEGER NOT NULL,
       	    director VARCHAR(100) DEFAULT '',
       	    PRIMARY KEY (id)
       );


CREATE TABLE IF NOT EXISTS stars(
               id varchar(10) primary key,
               name varchar(100) not null,
               birthYear integer
           );

CREATE TABLE IF NOT EXISTS stars_in_movies(
       	    starId VARCHAR(10) DEFAULT '',
       	    movieId VARCHAR(10) DEFAULT '',
       	    FOREIGN KEY (starId) REFERENCES stars(id),
       	    FOREIGN KEY (movieId) REFERENCES movies(id)
       );

CREATE TABLE IF NOT EXISTS genres (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres_in_movies (
  genreId INTEGER NOT NULL,
  movieId VARCHAR(10) NOT NULL,
  FOREIGN KEY (genreId) REFERENCES genres(id),
  FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE creditcards (
  id VARCHAR(20) PRIMARY KEY,
  firstName VARCHAR(50) NOT NULL,
  lastName VARCHAR(50) NOT NULL,
  expiration DATE NOT NULL
);

CREATE TABLE customers (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  firstName VARCHAR(50) NOT NULL,
  lastName VARCHAR(50) NOT NULL,
  ccId VARCHAR(20) NOT NULL,
  address VARCHAR(200) NOT NULL,
  email VARCHAR(50) NOT NULL,
  password VARCHAR(20) NOT NULL,
  FOREIGN KEY (ccId) REFERENCES creditcards(id)
);

CREATE TABLE sales (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  customerId INTEGER NOT NULL,
  movieId VARCHAR(10) NOT NULL,
  saleDate DATE NOT NULL,
  FOREIGN KEY (customerId) REFERENCES customers(id),
  FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE ratings (
  movieId VARCHAR(10) NOT NULL,
  rating FLOAT NOT NULL,
  numVotes INTEGER NOT NULL,
  PRIMARY KEY (movieId),
  FOREIGN KEY (movieId) REFERENCES movies(id)
);
