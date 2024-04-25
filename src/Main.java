import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/employeeData";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {
            EmployeeDatabase myEmployeeDatabase = new EmployeeDatabase(connection);
            UpdateEmployee myUpdateEmployee = new UpdateEmployee(myEmployeeDatabase);
            ReportGeneratorByJobTitleOrDivision myReportGeneratorByJobTitleOrDivision = new ReportGeneratorByJobTitleOrDivision(myEmployeeDatabase, scanner);
            SearchEmployee mySearchEmployee = new SearchEmployee(myEmployeeDatabase);
            DeleteEmployee myDeleteEmployee = new DeleteEmployee(myEmployeeDatabase);
            AddEmployee myAddEmployee = new AddEmployee(myEmployeeDatabase);
            AddSsnColumnInDatabase myAddSsnColumnInDatabase = new AddSsnColumnInDatabase(myEmployeeDatabase);
            FullTimeEmployeeReport myFullTimeEmployeeReport = new FullTimeEmployeeReport(myEmployeeDatabase);

            int choice;
            do {
                System.out.println("\nEmployee Management System");
                System.out.println("1. Add Employee");
                System.out.println("2. Remove Employee");
                System.out.println("3. Search Employee");
                System.out.println("4. Update Employee Data");
                System.out.println("5. Update Employee Salary Range");
                System.out.println("6. Add SSN Column to Database");
                System.out.println("7. Full Time Employee Information with past payroll history");
                System.out.println("8. Generate Reports by Division or Job Titles");
                System.out.println("9. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        myAddEmployee.addEmployee(scanner);                        
                        break;
                    case 2:
                        myDeleteEmployee.deleteEmployee(scanner);
                        break;
                    case 3:
                        mySearchEmployee.searchEmployee(scanner);
                        break;
                    case 4:
                        myUpdateEmployee.updateEmployeeData(scanner);
                        break;
                    case 5:
                        myUpdateEmployee.updateEmployeeSalaryRange(scanner);
                        break;
                    case 6:
                        myAddSsnColumnInDatabase.addSSNColumn();
                        break;
                        case 7:
                        myFullTimeEmployeeReport.generateFullTimeEmployeeReport();
                        break;
                    case 8:
                        myReportGeneratorByJobTitleOrDivision.showMenu();
                        break;
                    case 9:
                        System.out.println("\nGood Bye!\n");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                }
            } while (choice != 9);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
