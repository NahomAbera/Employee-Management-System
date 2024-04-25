import java.sql.*;

public class AddSsnColumnInDatabase {
    private final EmployeeDatabase employeeDatabase;

    AddSsnColumnInDatabase(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    void addSSNColumn() throws SQLException {
        if (employeeDatabase.isSSNColumnAvailable()){
            System.out.println("SSN Column Already Exists in the Database.");
        }
        else{
            try {
                DatabaseMetaData dbmd = employeeDatabase.connection.getMetaData();
                ResultSet rs = dbmd.getColumns(null, null, "employees", "SSN");
                if (!rs.next()) {
                    String query = "ALTER TABLE employees ADD COLUMN SSN VARCHAR(9)";
                    try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                        employeeDatabase.executeUpdate(pstmt);
                        System.out.println("SSN column added successfully. \nSelect \"4. Update Employee Data\" on Main Window to add the employees SSN. ");
                    }
                } 
            } catch (SQLException e) {
                System.out.println("Failed to add SSN column: " + e.getMessage());
            }
        }
    }
}

