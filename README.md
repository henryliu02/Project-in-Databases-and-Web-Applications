- # General
    - #### Team#: CoolTeam
    
    - #### Names: Henry Liu
    
    - #### URLs: 
                Single version (https): https://coolfablix.com/cs122b-project4/login.html
                Scaled version master instance: http://35.171.149.244:8080/cs122b-project4/login.html
                Scaled version slave instance: http://44.193.188.22:8080/cs122b-project4/login.html
                Acess via AWS Load Balancer: http://3.87.51.210/cs122b-project4/login.html
                Access via GCP Load Balancer: http://34.125.146.64/cs122b-project4/login.html
    
    - #### Project 5 Video Demo Link: https://youtu.be/KDvfqitI3eo

    - #### Instruction of deployment:

    - #### Collaborations and Work Distribution: Henry Liu


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    - Filename configued JDBC Connection Pooling: 
    - 1. META-INF/context.xml


    - Filenames using JDBC Connection Pooling:
    - 1. AddMovieServlet.java
    - 2. AddStarServlet.java
    - 3. FullTextSearchServlet.java
    - 4. GenreListServlet.java
    - 5. GenreServlet.java
    - 6. IndexServlet.java
    - 7. LoginServlet.java
    - 8. MoviesServlet.java
    - 9. PaymentServlet.java
    - 10. SearchServlet.java
    - 11. ShoppingCartServlet.java
    - 12. SingleMovieServlet.java
    - 13. SingleStarServlet.java

    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
    - Connection pooling in the Fabflix code is implemented using the connection pool provided by Tomcat, a widely used web server and servlet container. This connection pool is configured in the context.xml file. 
    - In the context.xml file, two resources are defined, one for read-write operations and another for read-only operations. These resources represent two different connection pools. The parameters for each resource (like maxTotal, maxIdle, maxWaitMillis) control the behavior of the connection pool, such as the maximum number of active connections, the maximum number of idle connections, and the maximum time to wait for an available connection.
    - Then, in the Java servlets code, when a database connection is needed, instead of directly creating a new connection to the database (which is resource-intensive), a connection is borrowed from the connection pool using JNDI lookup (java:comp/env/jdbc/moviedbMaster or java:comp/env/jdbc/moviedbSlave). After the database operation is done, the connection is closed and returned to the pool, making it available for the next request.
    
    - #### Explain how Connection Pooling works with two backend SQL.
    - Connection pooling works with two backend SQL servers by maintaining two separate connection pools, one for each server. This is the case in the Fabflix code, where two connection pools are defined in the context.xml file, one for the master (read-write operations) and one for the slave (read-only operations).
    - When a read-write operation is to be performed, a connection is obtained from the master connection pool, and when a read-only operation is to be performed, a connection is obtained from the slave connection pool. This way, read and write operations are effectively distributed between the master and slave servers, optimizing the utilization of resources.
    - Connection pooling can also work using MySQL Router, which is optional in this project. 
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
    - 1. AddMovieServlet.java
    - 2. AddStarServlet.java
    - 3. FullTextSearchServlet.java
    - 4. GenreListServlet.java
    - 5. GenreServlet.java
    - 6. IndexServlet.java
    - 7. LoginServlet.java
    - 8. MoviesServlet.java
    - 9. PaymentServlet.java
    - 10. SearchServlet.java
    - 11. ShoppingCartServlet.java
    - 12. SingleMovieServlet.java
    - 13. SingleStarServlet.java

    - #### How read/write requests were routed to Master/Slave SQL?
    - The routing of read/write requests to the master or slave SQL server is done explicitly in the application code by manually choosing the appropriate connection pool (master or slave) based on the type of operation (read or write). 
    - If the operation is a read operation (i.e., SELECT statement), the application performs a JNDI lookup for java:comp/env/jdbc/moviedbSlave to get a connection from the slave connection pool. The request is then executed using this connection, effectively routing the read request to the slave SQL server.
    - If the operation is a write operation (i.e., INSERT, UPDATE, DELETE statement), the application performs a JNDI lookup for java:comp/env/jdbc/moviedbMaster to get a connection from the master connection pool. The request is then executed using this connection, effectively routing the write request to the master SQL server.

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
    - code snippet for using 'log_processing.py':
        python3 ur_path/log_processing.py *.txt *.txt ...
        python3 ur_path/log_processing.py *.txt
    - log_processing.py takes arbitrary numbers of arguments(>= 1), calculates the sums up all the Query portion time and Servlet portion time and finally outputs their each individual average. 


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![img/single_HTTP_1_thread.png]   |     121.9537228944496 ms   |  122.55922299090074 ms       | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![img/single_HTTPS_10thread.png]   | 1013.204812487942 ms       |       1013.9072310241172 ms         | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | ![img/single_HTTPS_10thread.png]   | 919.7748291378373 ms       | 920.9626432073362 ms            | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![img/single_http_10thread_no_connection_pooling.png]   | 962.5927430020525 ms | 963.6106511601205 ms  | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![img/scaled_http_1_thread.png]   | 118.17613329046762 ms| 118.94785486600713 ms | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![img/scaled_10thread.png]  | 403.56602453656296 ms| 404.33694855080665 ms| ??   | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![img/scaled_10thread.png]   | 472.0135441696854 ms | 472.6441489188455 ms| ??  | ??           |
