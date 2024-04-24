import java.sql.*;
import java.util.Scanner;
import java.util.Date;

public class EmployeeManagementSystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/employeeData";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {
            EmployeeDatabase employeeDatabase = new EmployeeDatabase(connection);
            EmployeeManagement employeeManagement = new EmployeeManagement(employeeDatabase);
            ReportGenerator reportGenerator = new ReportGenerator(employeeDatabase);
            SchemaUpdater schemaUpdater = new SchemaUpdater(employeeDatabase);

            schemaUpdater.addSSNColumn(); 

            int choice;
            do {
                System.out.println("\nEmployee Management System");
                System.out.println("1. Add Employee");
                System.out.println("2. Search Employee");
                System.out.println("3. Update Employee Data");
                System.out.println("4. Update Employee Salary Range");
                System.out.println("5. Generate Full-Time Employee Report");
                System.out.println("6. Generate Total Pay by Job Title Report");
                System.out.println("7. Generate Total Pay by Division Report");
                System.out.println("8. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        employeeManagement.addEmployee(scanner);
                        break;
                    case 2:
                        employeeManagement.searchEmployee(scanner);
                        break;
                    case 3:
                        employeeManagement.updateEmployeeData(scanner);
                        break;
                    case 4:
                        employeeManagement.updateEmployeeSalaryRange(scanner);
                        break;
                    case 5:
                        reportGenerator.generateFullTimeEmployeeReport();
                        break;
                    case 6:
                        reportGenerator.generateTotalPayByJobTitleReport(scanner);
                        break;
                    case 7:
                        reportGenerator.generateTotalPayByDivisionReport(scanner);
                        break;
                    case 8:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 8.");
                }
            } while (choice != 8);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class EmployeeDatabase {
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
}

class EmployeeManagement {
    private final EmployeeDatabase employeeDatabase;

    EmployeeManagement(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    void addEmployee(Scanner scanner) {
        System.out.println("Enter employee details:");
        System.out.print("Employee ID: ");
        int empId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Hire Date (YYYY-MM-DD): ");
        String hireDate = scanner.nextLine();
        System.out.print("Salary: ");
        double salary = scanner.nextDouble();
        scanner.nextLine(); 
        System.out.print("SSN (no dashes): ");
        String ssn = scanner.nextLine();

        String query = "INSERT INTO employees (empid, Fname, Lname, email, HireDate, Salary, SSN) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
            pstmt.setInt(1, empId);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, email);
            pstmt.setDate(5, Date.valueOf(hireDate));
            pstmt.setDouble(6, salary);
            pstmt.setString(7, ssn);
            employeeDatabase.executeUpdate(pstmt);
            System.out.println("Employee added successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to add employee: " + e.getMessage());
        }
    }

    void searchEmployee(Scanner scanner) {
        System.out.print("Enter employee first name, last name, SSN, or Employee ID to search: ");
        String searchInput = scanner.nextLine();

        try {
            int empId = Integer.parseInt(searchInput);
            String query = "SELECT * FROM employees WHERE empid = ?";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setInt(1, empId);
                executeSearch(pstmt);
            }
        } catch (NumberFormatException e) {
            String query = "SELECT * FROM employees WHERE Fname LIKE ? OR Lname LIKE ? OR SSN LIKE ?";
            try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
                pstmt.setString(1, "%" + searchInput + "%");
                pstmt.setString(2, "%" + searchInput + "%");
                pstmt.setString(3, "%" + searchInput + "%");
                executeSearch(pstmt);
            } catch (SQLException ex) {
                System.out.println("Failed to search for employee: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            System.out.println("Failed to search for employee: " + ex.getMessage());
        }
    }

    void executeSearch(PreparedStatement pstmt) throws SQLException {
        try (ResultSet resultSet = employeeDatabase.executeQuery(pstmt)) {
            while (resultSet.next()) {
                int empId = resultSet.getInt("empid");
                String firstName = resultSet.getString("Fname");
                String lastName = resultSet.getString("Lname");
                String email = resultSet.getString("email");
                Date hireDate = resultSet.getDate("HireDate");
                double salary = resultSet.getDouble("Salary");
                String ssn = resultSet.getString("SSN");

                System.out.println("\nEmployee ID: " + empId);
                System.out.println("First Name: " + firstName);
                System.out.println("Last Name: " + lastName);
                System.out.println("Email: " + email);
                System.out.println("Hire Date: " + hireDate);
                System.out.println("Salary: " + salary);
                System.out.println("SSN: " + ssn);
            }
        }
    }

    void updateEmployeeData(Scanner scanner) {
        System.out.print("Enter Employee ID for update: ");
        int empId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter new email: ");
        String email = scanner.nextLine();
        System.out.print("Enter new salary: ");
        double salary = scanner.nextDouble();
        scanner.nextLine(); 

        String query = "UPDATE employees SET email = ?, Salary = ? WHERE empid = ?";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setDouble(2, salary);
            pstmt.setInt(3, empId);
            employeeDatabase.executeUpdate(pstmt);
            System.out.println("Employee data updated successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to update employee data: " + e.getMessage());
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

class ReportGenerator {
    private final EmployeeDatabase employeeDatabase;

    ReportGenerator(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    void generateFullTimeEmployeeReport() {
        String query = "SELECT * FROM employees WHERE JobType = 'Full-Time'";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query);
             ResultSet resultSet = employeeDatabase.executeQuery(pstmt)) {
            while (resultSet.next()) {
                int empId = resultSet.getInt("empid");
                String firstName = resultSet.getString("Fname");
                String lastName = resultSet.getString("Lname");
                String email = resultSet.getString("email");
                Date hireDate = resultSet.getDate("HireDate");
                double salary = resultSet.getDouble("Salary");

                System.out.println("\nEmployee ID: " + empId);
                System.out.println("First Name: " + firstName);
                System.out.println("Last Name: " + lastName);
                System.out.println("Email: " + email);
                System.out.println("Hire Date: " + hireDate);
                System.out.println("Salary: " + salary);
            }
        } catch (SQLException e) {
            System.out.println("Failed to generate full-time employee report: " + e.getMessage());
        }
    }

    void generateTotalPayByJobTitleReport(Scanner scanner) {
        System.out.print("Enter the month (1-12): ");
        int month = scanner.nextInt();
        scanner.nextLine(); 

        String query = "SELECT JobTitle, SUM(Salary) as TotalSalary FROM employees WHERE MONTH(HireDate) = ? GROUP BY JobTitle";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
            pstmt.setInt(1, month);
            try (ResultSet resultSet = employeeDatabase.executeQuery(pstmt)) {
                while (resultSet.next()) {
                    String jobTitle = resultSet.getString("JobTitle");
                    double totalSalary = resultSet.getDouble("TotalSalary");

                    System.out.println("\nJob Title: " + jobTitle);
                    System.out.println("Total Salary for Month: " + totalSalary);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to generate total pay by job title report: " + e.getMessage());
        }
    }

    void generateTotalPayByDivisionReport(Scanner scanner) {
        System.out.print("Enter the month (1-12): ");
        int month = scanner.nextInt();
        scanner.nextLine();

        String query = "SELECT Division, SUM(Salary) as TotalSalary FROM employees WHERE MONTH(HireDate) = ? GROUP BY Division";
        try (PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(query)) {
            pstmt.setInt(1, month);
            try (ResultSet resultSet = employeeDatabase.executeQuery(pstmt)) {
                while (resultSet.next()) {
                    String division = resultSet.getString("Division");
                    double totalSalary = resultSet.getDouble("TotalSalary");

                    System.out.println("\nDivision: " + division);
                    System.out.println("Total Salary for Month: " + totalSalary);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to generate total pay by division report: " + e.getMessage());
        }
    }
}

class SchemaUpdater {
    private final EmployeeDatabase employeeDatabase;

    SchemaUpdater(EmployeeDatabase employeeDatabase) {
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
