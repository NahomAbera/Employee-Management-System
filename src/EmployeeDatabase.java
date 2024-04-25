import java.sql.*; // imports the sql java package containing the JDBC database access

public class EmployeeDatabase {
    final Connection connection;  // connection to the database

    EmployeeDatabase(Connection connection) {
        this.connection = connection;
    }

    void executeUpdate(PreparedStatement pstmt) throws SQLException {
        pstmt.executeUpdate();
    }

    ResultSet executeQuery(PreparedStatement pstmt) throws SQLException {
        return pstmt.executeQuery();
    }

    boolean isSSNColumnAvailable() throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet columns = metaData.getColumns(null, null, "employees", "SSN");
        return columns.next(); // If next() returns true column exists, otherwise it doesn't.
    }
}
