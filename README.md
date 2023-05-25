
DEMO URL LINK: https://youtu.be/qRmmTP04Oos

USER WEBSITE LINK: https://coolfablix.com/cs122b-project3/login.html

EMPLOYEE DASHBOARD WEBSITE LINK: https://coolfablix.com/cs122b-project3/_dashboard/login.html

<br>
<br>
<b>Disclaimer :</b>
1. We've assigned an Elastic IP to our AWS Instance
2. We've signed the purcurcased domain(coolfablix.com) from GoDaddy with the Let's Encrypt certification. 
3. We tried numerous efforts to get the SSL working in server.xml from the Tomcat server(to recognize our cert) and am continuously working on it. 
<br>
<br>

<b>Used three parsing time optimization strategies:</b>
1. Batch Insertion for each table: Instead of performing individual database insertions for each data entry, batch insertion allows multiple insertions to be executed in a single database transaction. This strategy reduces the overhead of establishing multiple database connections and improves efficiency by minimizing the round trips between the application and the database. By grouping multiple insertions together, the parsing process becomes more efficient and faster. In particularly, we used btach insertion for all sql insertions from the parsed files. 
3. In-memory hash tables for both data from current tables and data from parsed file. In particularly, we used hash and in-memory data structure  to store data loaded from mainxx.xml and pass to CastSaxparser as parameter, as well as stored data loaded from actorxx.xml to MovieSaxParser as parameter. 
4. Achieved O(1) for each comparison and insertion. In particularly, we cached sql data from stars table to hashmap and genres table to hashmap, which achieved O(1) constant time comparsion to check duplicate insertions(also used IGNORE keyword)
<br>
  <br>
  
<b>Inconsistency data:</b>
 1. MovieEmpty.txt  -- Defined as movies that exist in mainxx.xml but not in castxx.xml
 2. MoviesDuplicate.txt   -- Defined as movies that duplicate during parsing/insertion
 3. StarsNotFound.txt -- Defined as Stars exist in castxx.xml but not in actorsxx.xml
 4.  MovieNotFound.txt  -- Defined as movies that exist in castxx.xml but not in mainxx.xml
 5.  MoviesInconsistent.txt  -- Defined as movies that missing primiary key(id) or missing title or missing director or missing year (consistent with the employee insertion feature)
 6.  StarsDuplicate.txt   -- Defined as stars that duplicate during parsing/insertion
 <br>
 <b> If needing to see these text files and code for SaxParser, please go to our github branch saxparser to view:<br> https://github.com/UCI-Chenli-teaching/s23-122b-cool_team/tree/saxparser</b>
 
 
 <br>
  <br>
<b>File Names with prepared statement:</b>
  
1. AddMovieServlet.java
2. AddStarServlet.java
3. GenreServlet.java
4. LoginServlet.java
5. PaymentServlet.java
6. SearchServlet.java
7. SingleMovieServlet.java
8. SingleStarServlet.java

<br>
<br>
<b>Stored procedure</b>
  1. star_stored-procedure.sql - helper stored procedure for adding new stars.<br>
  2. stored-procedure.sql - stored procedure required for adding new movies. 

