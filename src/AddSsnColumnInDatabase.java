import java.sql.*;

public class AddSsnColumnInDatabase {
    private final EmployeeDatabase employeeDatabase;

    AddSsnColumnInDatabase(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    void addSSNColumn() {
        try {
            DatabaseMetaData dbmd = employeeDatabase.connection.getMetaData();
            ResultSet rs = dbmd.getColumns(null, null, "employees", "SSN");
            if (!rs.next()) {
                String query = "ALTER TABLE employees ADD COLUMN SSN VARCHAR(9)";
                try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                    employeeDatabase.executeUpdate(pstmt);
                    System.out.println("SSN column added successfully.");
                }
            } else {
                System.out.println("SSN column already exists.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to add SSN column: " + e.getMessage());
        }
    }
}
