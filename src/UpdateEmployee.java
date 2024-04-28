import java.util.Scanner;
import java.sql.*;

class UpdateEmployee {
    private final EmployeeDatabase employeeDatabase;

    UpdateEmployee(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    void updateEmployeeData(Scanner scanner) throws SQLException {
        System.out.print("Enter Employee ID for update: ");
        int empId = scanner.nextInt();
        scanner.nextLine();

        //check if employee with the provided ID exists
        if (!employeeExists(empId)) {
            System.out.println("Employee with ID " + empId + " does not exist.");
            return;
        }

        System.out.println("Select the field you want to update:");
        System.out.println("1. Email");
        System.out.println("2. Salary");
        System.out.println("3. Job Title");
        System.out.println("4. Address");
        System.out.println("5. Date of Birth");
        System.out.println("6. Division");
        if (employeeDatabase.isSSNColumnAvailable()) {
            System.out.println("7. SSN");
        }
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                updateEmail(empId, scanner);
                break;
            case 2:
                updateSalary(empId, scanner);
                break;
            case 3:
                updateJobTitle(empId, scanner);
                break;
            case 4:
                updateAddress(empId, scanner);
                break;
            case 5:
                updateDateOfBirth(empId, scanner);
                break;
            case 6:
                updateDivision(empId, scanner);
                break;
            case 7:
                if (employeeDatabase.isSSNColumnAvailable()) {
                    updateSSN(empId, scanner);
                } else {
                    System.out.println("SSN column is not available in the employees table.");
                }
                break;
            
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void updateEmail(int empId, Scanner scanner) {
        System.out.print("Enter new email: ");
        String email = scanner.nextLine();
        String query = "UPDATE employees SET email = ? WHERE empid = ?";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, empId);
            employeeDatabase.executeUpdate(pstmt);
            System.out.println("Employee email updated successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to update employee email: " + e.getMessage());
        }
    }

    private void updateSalary(int empId, Scanner scanner) {
        System.out.print("Enter new salary: ");
        double salary = scanner.nextDouble();
        scanner.nextLine();

        String query = "UPDATE employees SET Salary = ? WHERE empid = ?";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
            pstmt.setDouble(1, salary);
            pstmt.setInt(2, empId);
            employeeDatabase.executeUpdate(pstmt);
            System.out.println("Employee salary updated successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to update employee salary: " + e.getMessage());
        }
    }

    private void updateJobTitle(int empId, Scanner scanner) {
        System.out.print("Enter new job title: ");
        String jobTitle = scanner.nextLine();
        String query = "UPDATE job_titles jt " +
                "JOIN employee_job_titles ejt ON jt.job_title_id = ejt.job_title_id " +
                "SET jt.job_title = ? " +
                "WHERE ejt.empid = ?";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
            pstmt.setString(1, jobTitle);
            pstmt.setInt(2, empId);
            employeeDatabase.executeUpdate(pstmt);
            System.out.println("Employee job title updated successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to update employee job title: " + e.getMessage());
        }
    }

    private void updateSSN(int empId, Scanner scanner) {
        System.out.print("Enter new SSN: ");
        String ssn = scanner.nextLine();
        String query = "UPDATE employees SET SSN = ? WHERE empid = ?";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
            pstmt.setString(1, ssn);
            pstmt.setInt(2, empId);
            employeeDatabase.executeUpdate(pstmt);
            System.out.println("Employee SSN updated successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to update employee SSN: " + e.getMessage());
        }
    }
    void updateEmployeeSalaryRange(Scanner scanner) {
        System.out.print("Enter minimum salary: ");
        double minSalary = scanner.nextDouble();
        System.out.print("Enter maximum salary: ");
        double maxSalary = scanner.nextDouble();
        System.out.print("Enter salary increase percentage (e.g., 3.2): ");
        double increase = scanner.nextDouble();
        scanner.nextLine(); 

        String query = "UPDATE employees SET Salary = Salary * (1 + ? / 100) WHERE Salary >= ? AND Salary < ?";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
            pstmt.setDouble(1, increase);
            pstmt.setDouble(2, minSalary);
            pstmt.setDouble(3, maxSalary);
            employeeDatabase.executeUpdate(pstmt);
            System.out.println("Employee salaries updated successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to update employee salaries: " + e.getMessage());
        }
    }

    private void updateAddress(int empId, Scanner scanner) {
        System.out.print("Enter new address: ");
        String address = scanner.nextLine();
        String query = "UPDATE employees SET address = ? WHERE empid = ?";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
            pstmt.setString(1, address);
            pstmt.setInt(2, empId);
            employeeDatabase.executeUpdate(pstmt);
            System.out.println("Employee address updated successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to update employee address: " + e.getMessage());
        }
    }

    private void updateDateOfBirth(int empId, Scanner scanner) {
        System.out.print("Enter new date of birth (YYYY-MM-DD): ");
        String dob = scanner.nextLine();
        String query = "UPDATE employees SET date_of_birth = ? WHERE empid = ?";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(dob));
            pstmt.setInt(2, empId);
            employeeDatabase.executeUpdate(pstmt);
            System.out.println("Employee date of birth updated successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to update employee date of birth: " + e.getMessage());
        }
    }

    private void updateDivision(int empId, Scanner scanner) {
        // List all available divisions
        listAvailableDivisions();
    
        System.out.print("Enter Division ID to assign to employee: ");
        int divisionId = scanner.nextInt();
        scanner.nextLine();
    
        // Check if the provided division ID exists
        if (!divisionExists(divisionId)) {
            System.out.println("Division with ID " + divisionId + " does not exist.");
            return;
        }
    
        // Check if the employee already has a division
        int currentDivisionId = getCurrentDivision(empId);
        if (currentDivisionId != -1) {
            // Update employee's division
            if(updateEmployeeDivision(empId, divisionId)) {
                System.out.println("Employee's division updated successfully.");
            } else {
                System.out.println("Failed to update the employee's division.");
            }
        } else {
            // Assign a new division to the employee
            if(assignNewDivision(empId, divisionId)) {
                System.out.println("Employee's new division assigned successfully.");
            } else {
                System.out.println("Failed to assign a new division to the employee.");
            }
        }
    }
    
    private void listAvailableDivisions() {
        String query = "SELECT ID, Name FROM division";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query);
             ResultSet resultSet = pstmt.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String name = resultSet.getString("Name");
                System.out.printf("%d - %s%n", id, name);
            }
        } catch (SQLException e) {
            System.out.println("Error listing divisions: " + e.getMessage());
        }
    }

    private boolean divisionExists(int divisionId) {
        try {
            String query = "SELECT COUNT(*) AS count FROM division WHERE ID = ?";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setInt(1, divisionId);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt("count");
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to check division existence" );
        }
        return false;
    }

    private int getCurrentDivision(int empId) {
        try {
            String query = "SELECT div_ID FROM employee_division WHERE empid = ?";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setInt(1, empId);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("div_ID");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to get current division for employee");
        }
        return -1;
    }

    private boolean updateEmployeeDivision(int empId, int divisionId) {
        boolean success = false;
        try {
            employeeDatabase.connection.setAutoCommit(false);
            // Disable foreign key checks
            try (Statement stmt = employeeDatabase.connection.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS=0");
            }
            // Update employee's division
            String query = "UPDATE employee_division SET div_ID = ? WHERE empid = ?";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setInt(1, divisionId);
                pstmt.setInt(2, empId);
                int affectedRows = pstmt.executeUpdate();
                success = affectedRows > 0;
            }
            // Enable foreign key checks
            try (Statement stmt = employeeDatabase.connection.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS=1");
            }
            employeeDatabase.connection.commit();
        } catch (SQLException e) {
            System.out.println("Failed to update employee division: " + e.getMessage());
            try {
                employeeDatabase.connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Failed to rollback: " + ex.getMessage());
            }
        } finally {
            try {
                employeeDatabase.connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Failed to reset auto-commit: " + ex.getMessage());
            }
        }
        return success;
    }
    
    
    private boolean assignNewDivision(int empId, int divisionId) {
        boolean success = false;
        try {
            String query = "INSERT INTO employee_division (empid, div_ID) VALUES (?, ?)";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setInt(1, empId);
                pstmt.setInt(2, divisionId);
                int affectedRows = pstmt.executeUpdate();
                success = affectedRows > 0;
            }
        } catch (SQLException e) {
            System.out.println("Failed to assign new division to employee: " + e.getMessage());
        }
        return success;
    }

    private boolean employeeExists(int empId) {
        try {
            String query = "SELECT COUNT(*) AS count FROM employees WHERE empid = ?";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setInt(1, empId);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt("count");
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to check employee existence: " + e.getMessage());
        }
        return false;
    }
}


