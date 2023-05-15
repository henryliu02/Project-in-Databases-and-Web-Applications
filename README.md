
DEMO URL LINK: https://youtu.be/ZiNw_DlSpkE

USER WEBSITE LINK: https://coolfablix.com:8443/cs122b-project3/login.html

EMPLOYEE DASHBOARD WEBSITE LINK: https://coolfablix.com:8443/cs122b-project3/_dashboard/login.html

Assigned an Elastic IP

Registered a domain name for Fabflix(coolfablix.com)

Signed the domain of the GoDaddy host with the AWS Certificate Authority

Used two parsing time optimization strategies:
1. Batch Insertion for each table: Instead of performing individual database insertions for each data entry, batch insertion allows multiple insertions to be executed in a single database transaction. This strategy reduces the overhead of establishing multiple database connections and improves efficiency by minimizing the round trips between the application and the database. By grouping multiple insertions together, the parsing process becomes more efficient and faster.
2. In-memory hash tables for both data from current tables and data from parsed file
3. A highly optimized data structure achieves O(1) for each comparison and insertion.

Inconsistency data:


### Features
1. This example application incorporates reCAPTCHA as a security measure, which displays appropriate error messages when the verification fails.
2. The example application utilizes HTTPS, which is functioning properly. The application redirects all HTTP traffic to HTTPS.
3. The passwords stored in the database have been encrypted, and the login process will authenticate user input using the encrypted password.
4. Transformed all queries in the codebase to utilize prepared statements.
5. The example application has implemented the Employee Dashboard. Users can log in to the dashboard using their employee credentials. On the Employee Dashboard, they can add stars or movies, and these additions will be searchable and accessible on the webpage. Moreover, proper success messages and error messages will be displayed.
6. The example application successfully parsed the data from main.xml, actors.xml, and casts.xml. The newly added movies, stars linked to movies, and actors of movies are all searchable and accessible on the webpage.


### Brief Explanation

- The default username is `anteater` and password is `123456` .

- The employee username is `classta@email.edu` and password is `classta`. This account leads to Employee Dashboard.


- [RecaptchaVerifyUtils.java](src/RecaptchaVerifyUtils.java) class provides a utility method to verify the response received from Google reCAPTCHA API.


- [index.html](WebContent/_dashboard/index.html) shows the metadata.


- [index.js](WebContent/_dashboard/index.js) file contains code that performs an AJAX request to retrieve metadata from the server and populates it in the [index.html] page.
   - jQuery AJAX request fetches the metadata from the server `IndexServlet.java`.
   - `handleMetadataResult` function handles the metadata received from the server and a jQuery AJAX request to fetch the data.


- [star.html](WebContent/_dashboard/star.html) is where the employee can add a star.
   - The star requires star name and birth year.


- [star.js](WebContent/_dashboard/star.js) is designed to handle form submission and perform an AJAX request to add a star.
   - `submitSubmit_form` is the event handler for the new star form submission.
   - `handleMetadataResult` function handles the data returned by the server (`AddStarServlet`) after the AJAX request. It checks the value of resultData and displays a corresponding message in the element with the "login_error_message".


- [movie.html](WebContent/_dashboard/movie.html) is where the employee can add a movie.
   - The movie requires movie title, year, director, star name, star birth year and movie genre.


- [movie.js](WebContent/_dashboard/movie.js) is designed to handle form submission and perform an AJAX request to add a movie.
   - `submitSubmit_form` is the event handler for the new movie form submission.
   - `handleMetadataResult` function handles the data returned by the server (`AddMovieServlet`) after the AJAX request. It checks the value of resultData and displays a corresponding message in the element with the "login_error_message".


- [MovieSaxParser.java](main/java/MovieSaxParser.java) is a Java class used for parsing XML files and extracting movie information. The class is based on a SAX parser, which allows it to parse XML files containing movie data and store the movie objects in a list.
   - It retrieves data from `mains243.xml`(see `parseDocument` method).
   - The `startElement` is called when the parser encounters the start tag of an element in the XML file.
   - The `endElement` will extract movie's information, and it checks if the movie has any missing information (id, title, year, or director). If any of these fields are missing, it is considered an inconsistent movie, and the details are written to the `inconsistentMoviesWriter` file. The inconsistent counter is incremented. At the same time, it checks if the movie's ID is a duplicate. If it is considered a duplicate movie, the details are written to the `duplicateMoviesWriter` file. The duplicate counter is incremented.
   - The `processBatch` Processes the remaining items in the batch after parsing.
   - The `runExample` method starts the parsing process
   - The `insertStarsInMovies` method inserts the relationships between stars and movies into the database using a prepared statement
   - The `insertMoviesBatch` inserts the batch of movies into the database using a prepared statement.
   - The `populateGenreIdCache` populate a genre ID cache with data retrieved from a database. The purpose of this method is to fetch genre IDs and names from the genres table in the database and store them in a cache for future reference.


- [StarSaxParser.java](main/java/StarSaxParser.java) is a Java class used for parsing XML files and extracting star information. The class is based on a SAX parser, which allows it to parse XML files containing actor data and store the actor objects in a list.
   - It retrieves data from `actors63.xml`(see `parseDocument` method).
   - The `startElement` is called when the parser encounters the start tag of an element in the XML file.
   - The `endElement` and `isStarDuplicate` methods will extract actor's name and birth year, checking duplicate actors at the same time.
   - The `runExample` method starts the parsing process


- [CastSaxParser.java](main/java/CastSaxParser.java) is a Java class based on a SAX parser, and it extracts movie and star information, identifies movies that are missing or empty in the main XML file, and writes the details to separate files.
   - It retrieves data from `casts124.xml`(see `parseDocument` method).
   - The `startElement` is called when the parser encounters the start tag of an element in the XML file.
   - The `endElement` handles the closing tag of the "filmc" element and extracts movie and star information from the XML attributes and values. It also adds the star to the list of stars and the movie to the set of found movies if inside the "filmc" element.
   - The `runExample` method starts the parsing process.
