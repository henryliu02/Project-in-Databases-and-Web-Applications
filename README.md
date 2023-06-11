- # General
    - #### Team#: CoolTeam
    
    - #### Names: Ziheng Liu
    
    - #### Project 5 Video Demo Link:

    - #### Instruction of deployment:

    - #### Collaborations and Work Distribution:


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
    
    - #### Explain how Connection Pooling works with two backend SQL.
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

    - #### How read/write requests were routed to Master/Slave SQL?
    

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


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
| Case 2: HTTP/10 threads                        | ![img/scaled_10thread.png]  | 403.56602453656296 ms| 404.33694855080665 ms| ??    ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![img/scaled_10thread.png]   | 472.0135441696854 ms | 472.6441489188455 ms| ??  | ??           |
