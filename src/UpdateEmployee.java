import java.util.Scanner;
import java.sql.*;


/**
 * This class provides methods to update various details of an employee in the database.
 */
class UpdateEmployee {
    private final EmployeeDatabase employeeDatabase;

    /**
     * Constructs an UpdateEmployee object with a reference to an EmployeeDatabase.
     * @param employeeDatabase The EmployeeDatabase object to interact with the database.
     */
    UpdateEmployee(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    /**
     * Updates the data of an employee based on user input. Users can update email, salary, job title, address, 
     * date of birth, and SSN if applicable.
     * @param scanner The scanner object to receive input from the user.
     * @throws SQLException If an SQL error occurs during the update process.
     */
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
        if (employeeDatabase.isSSNColumnAvailable()) {
            System.out.println("6. SSN");
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

    /**
     * Updates the email of a specific employee.
     * @param empId The employee ID for which to update the email.
     * @param scanner The scanner object to receive the new email from the user.
     */
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

    /**
     * Updates the salary of a specific employee.
     * @param empId The employee ID for which to update the salary.
     * @param scanner The scanner object to receive the new salary from the user.
     */
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

    /**
     * Updates the job title of a specific employee.
     * @param empId The employee ID for which to update the job title.
     * @param scanner The scanner object to receive the new job title from the user.
     */
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

    /**
     * Updates the Social Security Number (SSN) of a specific employee, if the SSN column is available.
     * @param empId The employee ID for which to update the SSN.
     * @param scanner The scanner object to receive the new SSN from the user.
     */
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

    /**
     * Updates the salaries of all employees within a specified salary range by a given percentage.
     * @param scanner The scanner object to receive the salary range and increase percentage from the user.
     */
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

    /**
     * Updates the address of a specific employee.
     * @param empId The employee ID for which to update the address.
     * @param scanner The scanner object to receive the new address from the user.
     */
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

    /**
     * Updates the date of birth of a specific employee.
     * @param empId The employee ID for which to update the date of birth.
     * @param scanner The scanner object to receive the new date of birth from the user.
     */
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

    /**
     * Checks if an employee exists in the database.
     * @param empId The employee ID to check.
     * @return true if the employee exists, false otherwise.
     */
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