import java.sql.*;
import java.util.Scanner;
import java.math.BigDecimal;

public class ReportGeneratorByJobTitleOrDivision {
    private final Scanner scanner;
    private EmployeeDatabase employeeDatabase;

    public ReportGeneratorByJobTitleOrDivision(EmployeeDatabase employeeDatabase, Scanner scanner) {
        this.employeeDatabase = employeeDatabase;
        this.scanner = scanner;
    }

    public void showMenu() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println("\nSelect Report Type:");
            System.out.println("1. Total Pay by Job Title Report");
            System.out.println("2. Total Pay by Division Report");
            System.out.println("3. Back To Main Menu");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    generateTotalPayByJobTitleReport();
                    break;
                case 2:
                    generateTotalPayByDivisionReport();
                    break;
                case 3:
                    exit = true;
                    System.out.println("Directing Back To The Main Menu");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public void generateTotalPayByJobTitleReport() {
        System.out.println("\nAvailable Job Titles:");
        listJobTitles();  // List all job titles for user selection
    
        System.out.print("Enter Job Title ID to generate report: ");
        int jobTitleId = scanner.nextInt();
        scanner.nextLine(); 
    
        String query = "SELECT jt.job_title, e.empid, e.Fname, e.Lname, e.email, e.HireDate, e.Salary, SUM(p.earnings) AS TotalPay " +
                       "FROM payroll p " +
                       "JOIN employee_job_titles ejt ON p.empid = ejt.empid " +
                       "JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id " +
                       "JOIN employees e ON p.empid = e.empid " +
                       "WHERE jt.job_title_id = ? " +
                       "GROUP BY jt.job_title, e.empid";
    
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
            pstmt.setInt(1, jobTitleId);
            ResultSet resultSet = pstmt.executeQuery();
            
            // Print header
            System.out.println("\n----------------------------------------------------------------------------------------------");
            System.out.printf("%s\t%s\t%s\t%s\t%s\t\t%s\t%s\t\t%s%n", "Job Title", "Employee ID", "First Name", "Last Name", "Email", "Hire Date", "Salary", "Total Pay");
            System.out.println("----------------------------------------------------------------------------------------------");
            
            // Print rows
            while (resultSet.next()) {
                String jobTitle = resultSet.getString("job_title");
                int empId = resultSet.getInt("empid");
                String firstName = resultSet.getString("Fname");
                String lastName = resultSet.getString("Lname");
                String email = resultSet.getString("email");
                Date hireDate = resultSet.getDate("HireDate");
                double salary = resultSet.getDouble("Salary");
                BigDecimal totalPay = resultSet.getBigDecimal("TotalPay");
                
                System.out.printf("%s\t%d\t%-15s%s\t%s\t%s\t%.2f\t%.2f%n", jobTitle, empId, firstName, lastName, email, hireDate.toString(), salary, totalPay);
                Payroll payrollProcessor = new Payroll(employeeDatabase);
                System.out.println(payrollProcessor.getPayByMonth(empId));
            }
            
            System.out.println("----------------------------------------------------------------------------------------------");
        } catch (SQLException e) {
            System.out.println("Failed to generate total pay by job title report: " + e.getMessage());
        }
    }
    
    

    public void generateTotalPayByDivisionReport() {
        System.out.println("\nAvailable Divisions:");
        listDivisions();  // List all divisions for user selection
    
        System.out.print("Enter Division ID to generate report: ");
        int divisionId = scanner.nextInt();
        scanner.nextLine();
    
        String divisionQuery = "SELECT * FROM division WHERE ID = ?";
        try (PreparedStatement divisionPstmt = employeeDatabase.connection.prepareStatement(divisionQuery)) {
            divisionPstmt.setInt(1, divisionId);
            ResultSet divisionResultSet = divisionPstmt.executeQuery();
    
            // Print division information
            if (divisionResultSet.next()) {
                String divisionName = divisionResultSet.getString("Name");
                String city = divisionResultSet.getString("city");
                String addressLine1 = divisionResultSet.getString("addressLine1");
                String addressLine2 = divisionResultSet.getString("addressLine2");
                String state = divisionResultSet.getString("state");
                String country = divisionResultSet.getString("country");
                String postalCode = divisionResultSet.getString("postalCode");
    
                System.out.println("\nDivision Information:");
                System.out.println("Division Name: " + divisionName);
                System.out.println("City: " + city);
                System.out.println("Address Line 1: " + addressLine1);
                System.out.println("Address Line 2: " + addressLine2);
                System.out.println("State: " + state);
                System.out.println("Country: " + country);
                System.out.println("Postal Code: " + postalCode);
            } else {
                System.out.println("No division found with the provided ID.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch division information: " + e.getMessage());
            return;
        }
    
        String query = "SELECT e.empid, e.Fname, e.Lname, e.email, e.HireDate, e.Salary " +
                       "FROM employees e " +
                       "JOIN employee_division ed ON e.empid = ed.empid " +
                       "WHERE ed.div_ID = ?";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
            pstmt.setInt(1, divisionId);
            ResultSet resultSet = pstmt.executeQuery();
    
            // Check if there are employees under this division
            if (!resultSet.next()) {
                System.out.println("No employees in this division.");
                return;
            }
    
            // Print header for employee information
            System.out.println("\n------------------------------------------------------------------------------");
            System.out.printf("%s\t%s\t%s\t%s\t%s\t%s\n",
                    "Emp ID", "First Name", "Last Name", "Email", "Hire Date", "Salary");
            System.out.println("------------------------------------------------------------------------------");
    
            // Print employee information if available
            do {
                int empId = resultSet.getInt("empid");
                String firstName = resultSet.getString("Fname");
                String lastName = resultSet.getString("Lname");
                String email = resultSet.getString("email");
                Date hireDate = resultSet.getDate("HireDate");
                double salary = resultSet.getDouble("Salary");
    
                System.out.printf("%d\t%s\t%s\t%s\t%s\t%.2f\n",empId, firstName, lastName, email, hireDate.toString(), salary);
                Payroll payrollProcessor = new Payroll(employeeDatabase);
                System.out.println(payrollProcessor.getPayByMonth(empId));
            } while (resultSet.next());
        } catch (SQLException e) {
            System.out.println("Failed to generate total pay by division report: " + e.getMessage());
        }
    }

    private void listJobTitles() {
        String query = "SELECT job_title_id, job_title FROM job_titles";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query);
             ResultSet resultSet = pstmt.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("job_title_id");
                String title = resultSet.getString("job_title");
                System.out.printf("%d - %s%n", id, title);
            }
        } catch (SQLException e) {
            System.out.println("Error listing job titles: " + e.getMessage());
        }
    }

    private void listDivisions() {
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
}