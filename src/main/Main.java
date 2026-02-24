package main;
import java.sql.*;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    private static Connection conn;
    private static Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static void main(String[] args) {
        log("APPLICATION START");

        try {
            connect();

            while (true) {
                printMenu();
                System.out.print("Choose option: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1": testConnection(); break;
                    case "2": createTable(); break;
                    case "3": insertData(); break;
                    case "4": selectData(); break;
                    case "5": deleteData(); break;
                    case "6": dropTable(); break;
                    case "7": toggleAutoCommit(); break;
                    case "8": commit(); break;
                    case "9": rollback(); break;
                    case "10": autoTest(); break;
                    case "11": showUserInfo(); break;
                    case "0": closeConnection(); System.exit(0);
                    default: log("INVALID OPTION");
                }
            }

        } catch (Exception e) {
            printSqlException(e);
        }
    }

    /* ================= LOGGING ================= */

    private static void log(String message) {
        System.out.println("[" + LocalDateTime.now().format(fmt) + "] " + message);
    }

    /* ================= CONNECTION ================= */

    private static void connect() throws Exception {

        log("Loading config.properties");

        Properties props = new Properties();
        try (InputStream input = new java.io.FileInputStream("config/config.properties")
        		) {
        	props.load(input);
        }

        String host = props.getProperty("db.host");
        String port = props.getProperty("db.port");
        String sid = props.getProperty("db.sid");
        String service = props.getProperty("db.service");
        String username = props.getProperty("db.username");
        String password = props.getProperty("db.password");
        String driver = props.getProperty("db.driver");

        log("Loading driver: " + driver);
        Class.forName(driver);

        String url;
        if (service != null && !service.isEmpty()) {
            url = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + service;
        } else {
            url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;
        }

        log("Connecting to: " + url);

        long start = System.currentTimeMillis();
        conn = DriverManager.getConnection(url, username, password);
        long end = System.currentTimeMillis();

        log("CONNECTED (elapsed " + (end - start) + " ms)");
        log("autoCommit=" + conn.getAutoCommit());
    }

    /* ================= MENU ================= */

    private static void printMenu() {
        System.out.println("\n===== Oracle Database JDBC test connect =====");
        System.out.println("===== DBA: linkedin/nguyenxuanluu 2026-02-24 =====\n");
        System.out.println("1. Test connection");
        System.out.println("2. Create table");
        System.out.println("3. Insert data");
        System.out.println("4. Select data");
        System.out.println("5. Delete data");
        System.out.println("6. Drop table");
        System.out.println("7. Toggle autoCommit");
        System.out.println("8. Commit");
        System.out.println("9. Rollback");
        System.out.println("10. AUTO TEST (full flow)");
        System.out.println("11. Show user info (roles & privileges)");
        System.out.println("0. Exit");
    }

    /* ================= OPERATIONS ================= */

    private static void testConnection() throws SQLException {
        log("Checking connection validity");
        log("isValid=" + conn.isValid(5));
        log("DB Product=" + conn.getMetaData().getDatabaseProductName());
        log("DB Version=" + conn.getMetaData().getDatabaseProductVersion());
    }

    private static void createTable() {
        executeUpdate("CREATE TABLE TEST_TABLE (" +
                "ID NUMBER PRIMARY KEY, NAME VARCHAR2(50))");
    }

    private static void insertData() {
        try {
            System.out.print("Enter ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter NAME: ");
            String name = scanner.nextLine();

            String sql = "INSERT INTO TEST_TABLE VALUES (?, ?)";

            log("Preparing SQL: " + sql);
            log("Parameters: ID=" + id + ", NAME=" + name);

            long start = System.currentTimeMillis();

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setString(2, name);

            int rows = ps.executeUpdate();

            long end = System.currentTimeMillis();

            log("Rows affected=" + rows);
            log("Elapsed=" + (end - start) + " ms");

        } catch (Exception e) {
            printSqlException(e);
        }
    }

    private static void selectData() {
        String sql = "SELECT * FROM TEST_TABLE";
        log("Executing query: " + sql);

        try (Statement stmt = conn.createStatement()) {

            long start = System.currentTimeMillis();
            ResultSet rs = stmt.executeQuery(sql);
            long end = System.currentTimeMillis();

            log("Query executed in " + (end - start) + " ms");
            log("Fetching results...");

            while (rs.next()) {
                log("ROW => ID=" + rs.getInt("ID") +
                        ", NAME=" + rs.getString("NAME"));
            }

        } catch (Exception e) {
            printSqlException(e);
        }
    }

    private static void deleteData() {
        try {
            System.out.print("Enter ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine());

            String sql = "DELETE FROM TEST_TABLE WHERE ID = ?";

            log("Preparing SQL: " + sql);
            log("Parameter: ID=" + id);

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            log("Rows deleted=" + rows);

        } catch (Exception e) {
            printSqlException(e);
        }
    }

    private static void dropTable() {
        executeUpdate("DROP TABLE TEST_TABLE");
    }

    private static void toggleAutoCommit() throws SQLException {
        boolean current = conn.getAutoCommit();
        conn.setAutoCommit(!current);
        log("autoCommit changed to " + conn.getAutoCommit());
    }

    private static void commit() {
        try {
            log("COMMIT START");
            conn.commit();
            log("COMMIT SUCCESS");
        } catch (Exception e) {
            printSqlException(e);
        }
    }

    private static void rollback() {
        try {
            log("ROLLBACK START");
            conn.rollback();
            log("ROLLBACK SUCCESS");
        } catch (Exception e) {
            printSqlException(e);
        }
    }

    private static void executeUpdate(String sql) {
        try (Statement stmt = conn.createStatement()) {

            log("Executing SQL: " + sql);

            long start = System.currentTimeMillis();
            int rows = stmt.executeUpdate(sql);
            long end = System.currentTimeMillis();

            log("Rows affected=" + rows);
            log("Elapsed=" + (end - start) + " ms");

        } catch (Exception e) {
            printSqlException(e);
        }
    }

    /* ================= ERROR HANDLING ================= */

    private static void printSqlException(Exception e) {
        if (e instanceof SQLException) {
            SQLException se = (SQLException) e;
            log("SQL ERROR:");
            log("Message=" + se.getMessage());
            log("SQLState=" + se.getSQLState());
            log("ErrorCode=" + se.getErrorCode());
        } else {
            log("ERROR: " + e.getMessage());
        }
    }

    private static void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            log("Closing connection");
            conn.close();
            log("Connection closed");
        }
    }
    
    private static void autoTest() {

        log("========== AUTO TEST START ==========");

        try {

            /* 1. Test connection */
            testConnection();

            /* 2. Drop table nếu tồn tại */
            try {
                log("Attempting DROP TABLE if exists");
                executeUpdate("DROP TABLE TEST_TABLE");
            } catch (Exception ignored) {}

            /* 3. Create table */
            createTable();

            /* 4. Insert 5 rows */
            String sql = "INSERT INTO TEST_TABLE VALUES (?, ?)";
            log("Preparing batch insert: " + sql);

            long startInsert = System.currentTimeMillis();

            PreparedStatement ps = conn.prepareStatement(sql);

            for (int i = 1; i <= 5; i++) {
                ps.setInt(1, i);
                ps.setString(2, "NAME_" + i);
                ps.executeUpdate();
                log("Inserted row: ID=" + i + ", NAME=NAME_" + i);
            }

            long endInsert = System.currentTimeMillis();
            log("Inserted 5 rows in " + (endInsert - startInsert) + " ms");

            /* 5. Select */
            selectData();

            /* 6. Delete all */
            log("Deleting all rows");
            long startDelete = System.currentTimeMillis();
            int deleted = conn.createStatement()
                    .executeUpdate("DELETE FROM TEST_TABLE");
            long endDelete = System.currentTimeMillis();
            log("Deleted rows=" + deleted);
            log("Delete elapsed=" + (endDelete - startDelete) + " ms");

            /* 7. Drop table */
            dropTable();

            log("========== AUTO TEST SUCCESS ==========");

        } catch (Exception e) {
            printSqlException(e);
            log("========== AUTO TEST FAILED ==========");
        }
    }
    private static void showUserInfo() {

        log("===== USER INFO START =====");

        try (Statement stmt = conn.createStatement()) {

            /* 1. Current user */
            log("Query: select user from dual");

            ResultSet rsUser = stmt.executeQuery("select user from dual");
            if (rsUser.next()) {
                log("CURRENT USER = " + rsUser.getString(1));
            }

            /* 2. Session user */
            log("Query: select sys_context('USERENV','SESSION_USER') from dual");

            ResultSet rsSession = stmt.executeQuery(
                    "select sys_context('USERENV','SESSION_USER') from dual");
            if (rsSession.next()) {
                log("SESSION USER = " + rsSession.getString(1));
            }

            /* 3. Enabled Roles */
            log("Query: select * from session_roles");

            ResultSet rsRoles = stmt.executeQuery(
                    "select role from session_roles");

            log("ROLES:");
            while (rsRoles.next()) {
                log(" - " + rsRoles.getString("ROLE"));
            }

            /* 4. System Privileges */
            log("Query: select privilege from user_sys_privs");

            ResultSet rsSysPriv = stmt.executeQuery(
                    "select privilege from user_sys_privs");

            log("SYSTEM PRIVILEGES:");
            while (rsSysPriv.next()) {
                log(" - " + rsSysPriv.getString("PRIVILEGE"));
            }

            /* 5. Object Privileges */
            log("Query: select * from user_tab_privs");

            ResultSet rsObjPriv = stmt.executeQuery(
                    "select privilege, table_name from user_tab_privs");

            log("OBJECT PRIVILEGES:");
            while (rsObjPriv.next()) {
                log(" - " + rsObjPriv.getString("PRIVILEGE")
                        + " ON "
                        + rsObjPriv.getString("TABLE_NAME"));
            }

            log("===== USER INFO END =====");

        } catch (Exception e) {
            printSqlException(e);
        }
    }
}