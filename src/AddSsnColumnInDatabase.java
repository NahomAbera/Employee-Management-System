import java.sql.*;

/**
 * The AddSsnColumnInDatabase class is responsible for adding a new column for Social Security Numbers (SSNs)
 * to an existing employees table in the database. It checks if the SSN column already exists and adds it if it does not.
 */
public class AddSsnColumnInDatabase {
    /**
     * An instance of EmployeeDatabase to manage database connections and operations.
     */
    private final EmployeeDatabase employeeDatabase;

    /**
     * Constructs an AddSsnColumnInDatabase object with a given EmployeeDatabase instance.
     * 
     * @param employeeDatabase The database handler for employee data.
     */
    AddSsnColumnInDatabase(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    /**
     * Adds an SSN column to the employees table in the database if it does not already exist.
     * Provides feedback about the success or failure of adding the column.
     * 
     * @throws SQLException If a database access error occurs or this method is called on a closed connection.
     */
    void addSSNColumn() throws SQLException {
        if (employeeDatabase.isSSNColumnAvailable()){
            System.out.println("SSN Column Already Exists in the Database.");
        }
        else{
            DatabaseMetaData dbmd = employeeDatabase.connection.getMetaData();
            try (ResultSet rs = dbmd.getColumns(null, null, "employees", "SSN")) {
                if (!rs.next()) {
                    String query = "ALTER TABLE employees ADD COLUMN SSN VARCHAR(9)";
                    try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                        employeeDatabase.executeUpdate(pstmt);
                        System.out.println("SSN column added successfully. \nSelect \"4. Update Employee Data\" on Main Window to add the employees SSN.");
                    }
                } 
            } catch (SQLException e) {
                System.out.println("Failed to add SSN column: " + e.getMessage());
            }
        }
    }
}
