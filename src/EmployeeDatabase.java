import java.sql.*;

public class EmployeeDatabase {
    final Connection connection;

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
