import java.sql.*; // imports the sql java package containing the JDBC database access

/**
 * The EmployeeDatabase class encapsulates the management of database connections and operations for employee data.
 * It provides methods for executing updates and queries, as well as checking for the existence of specific database columns.
 */
public class EmployeeDatabase {
    /**
     * The connection to the database used for executing SQL statements.
     */
    final Connection connection;

    /**
     * Constructs an EmployeeDatabase object with an existing database connection.
     * 
     * @param connection The connection to the database that will be used for SQL operations.
     */
    EmployeeDatabase(Connection connection) {
        this.connection = connection;
    }

    /**
     * Executes an update operation such as INSERT, UPDATE, or DELETE.
     * 
     * @param pstmt The PreparedStatement object containing the SQL statement to be executed.
     * @throws SQLException If there is a database error during the execution of the statement.
     */
    void executeUpdate(PreparedStatement pstmt) throws SQLException {
        pstmt.executeUpdate();
    }

    /**
     * Executes a query operation and returns the ResultSet.
     * 
     * @param pstmt The PreparedStatement object containing the SQL query to be executed.
     * @return ResultSet containing the data returned by the query.
     * @throws SQLException If there is a database error during the execution of the query.
     */
    ResultSet executeQuery(PreparedStatement pstmt) throws SQLException {
        return pstmt.executeQuery();
    }

    /**
     * Checks if the SSN column is available in the 'employees' table of the database.
     * 
     * @return true if the SSN column exists, otherwise false.
     * @throws SQLException If there is a database error while accessing the metadata.
     */
    boolean isSSNColumnAvailable() throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet columns = metaData.getColumns(null, null, "employees", "SSN")) {
            return columns.next(); // Returns true if the SSN column exists.
        }
    }
}
