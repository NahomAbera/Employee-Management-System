import java.sql.*;
import java.util.Scanner;

/**
 * The SearchEmployee class provides functionality to search for employee details within a database.
 * It supports searching by employee ID, first name, or last name.
 */
public class SearchEmployee {
    private final EmployeeDatabase employeeDatabase;

    /**
     * Constructs a SearchEmployee object with a given EmployeeDatabase instance.
     * 
     * @param employeeDatabase The database handler for employee data.
     */
    public SearchEmployee(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    /**
     * Prompts the user to enter an employee ID, first name, or last name and performs a search based on the input.
     * It handles both numeric ID searches and string name searches.
     * 
     * @param scanner A Scanner instance for reading user input.
     */
    public void searchEmployee(Scanner scanner) {
        System.out.print("Enter employee first name, last name, or Employee ID to search: ");
        String searchInput = scanner.nextLine();
    
        try {
            int empId = Integer.parseInt(searchInput); // Attempt to parse the input as an employee ID
            String query = "SELECT * FROM employees WHERE empid = ?";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setInt(1, empId);
                executeSearch(pstmt);
            }
        } catch (NumberFormatException e) {
            // If input is not an integer, search by first name or last name
            String query = "SELECT * FROM employees WHERE Fname LIKE ? OR Lname LIKE ?";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setString(1, "%" + searchInput + "%");
                pstmt.setString(2, "%" + searchInput + "%");
                executeSearch(pstmt);
            } catch (SQLException ex) {
                System.out.println("Failed to search for employee: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            System.out.println("Failed to search for employee: " + ex.getMessage());
        }
    }

    /**
     * Executes the search query and prints out the employee details.
     * 
     * @param pstmt The PreparedStatement to execute, containing the SQL query.
     * @throws SQLException If there is a problem executing the query or processing the result set.
     */
    void executeSearch(PreparedStatement pstmt) throws SQLException {
        try (ResultSet resultSet = employeeDatabase.executeQuery(pstmt)) {
            boolean found = false;
            while (resultSet.next()) {
                found = true;
                displayEmployeeDetails(resultSet);
            }
            if (!found) {
                System.out.println("No employee found matching the search criteria.");
            }
        }
    }

    /**
     * Displays the details of an employee from the current row of the ResultSet.
     * 
     * @param resultSet The ResultSet from which to retrieve and display employee data.
     * @throws SQLException If there is an error retrieving data from the ResultSet.
     */
    private void displayEmployeeDetails(ResultSet resultSet) throws SQLException {
        int empId = resultSet.getInt("empid");
        String firstName = resultSet.getString("Fname");
        String lastName = resultSet.getString("Lname");
        String email = resultSet.getString("email");
        Date hireDate = resultSet.getDate("HireDate");
        double salary = resultSet.getDouble("Salary");
        String address = resultSet.getString("address");
        Date dob = resultSet.getDate("date_of_birth");

        System.out.println("\nEmployee ID: " + empId);
        System.out.println("First Name: " + firstName);
        System.out.println("Last Name: " + lastName);
        System.out.println("Email: " + email);
        System.out.println("Hire Date: " + hireDate);
        System.out.println("Salary: " + salary);
        System.out.println("Address: " + address);
        System.out.println("Date of Birth: " + dob);
        if (isSSNColumnAvailable()) {
            System.out.println("SSN: " + resultSet.getString("SSN"));
        }
    }

    /**
     * Checks if the SSN column is available in the 'employees' table.
     * 
     * @return true if the SSN column exists, false otherwise.
     */
    boolean isSSNColumnAvailable() {
        try {
            DatabaseMetaData metaData = employeeDatabase.connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "employees", "SSN");
            return columns.next(); // True if the SSN column exists.
        } catch (SQLException e) {
            System.out.println("Failed to check SSN column availability: " + e.getMessage());
            return false;
        }
    }
}
