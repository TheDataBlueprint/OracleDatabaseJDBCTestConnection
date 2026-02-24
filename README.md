🚀 **Oracle Database JDBC Utility (Executable JAR)**

A high-performance Java CLI tool built with JDK 8 for interacting with and benchmarking Oracle Databases. This utility allows developers and DBAs to perform connectivity tests, CRUD operations, and security audits without an IDE.

✨ **Key Features**

* Ready-to-Run: Pre-compiled for Java 8 (JDK 8) compatibility, ensuring stability in legacy and modern environments.

* External Configuration: Decoupled architecture using a config.properties file for SID or Service Name connections.

* Transaction Control: Manual management of Auto-Commit, Commit, and Rollback to test data integrity.

* Full CRUD Suite: Interactive menu for creating tables, inserting/selecting data, and dropping schemas.

* Performance Benchmarking: An Auto Test mode that executes a full lifecycle (Create -> Batch Insert -> Select -> Delete -> Drop) with precise millisecond latency logging.

* Security Auditing: Dedicated module to query current Session Roles, System Privileges, and Object Privileges.

🛠 **Setup & Configuration**
Before running, ensure your config/config.properties file is structured as follows:

```properties
Properties
db.host=10.144.1.2
db.port=1521
db.sid=orcl
db.username=test1
db.password=test2026
db.driver=oracle.jdbc.driver.OracleDriver
```
📋 **Execution Guide**

To run the application, ensure the ojdbc8.jar and your config/ folder are in the same directory as the main JAR:

**General command to run the utility**
```bash
java -cp oraclevastest.jar
```


📊 **Interactive Menu**

The CLI provides 11 options, including Check Connection Validity, Toggle AutoCommit, and Show User Privileges, providing a complete toolkit for Oracle DB troubleshooting.

*Developed by nguyenxuanluu (Updated Feb 2026).*