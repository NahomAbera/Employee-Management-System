import java.util.Scanner;
import java.sql.*;

public class UpdateEmployee {
    private final EmployeeDatabase employeeDatabase;

    UpdateEmployee(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    void updateEmployeeData(Scanner scanner) throws SQLException {
        System.out.print("Enter Employee ID for update: ");
        int empId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Select the field you want to update:");
        System.out.println("1. Email");
        System.out.println("2. Salary");
        System.out.println("3. Job Title");
        try {
            if (employeeDatabase.isSSNColumnAvailable()) {
                System.out.println("4. SSN");
            }
        } catch (SQLException e) {
            System.out.println("Failed to check SSN column availability: " + e.getMessage());
            return;
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
}
