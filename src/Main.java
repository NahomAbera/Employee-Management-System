/**
 * The Main class implements an application that manages employee records in a database.
 * It provides an interface to add, remove, search, and update employee details, manage divisions,
 * add new database columns, and generate various reports based on employment type and job divisions.
 *
 * The application uses JDBC to connect to a MySQL database, with operations encapsulated in various
 * utility classes for specific functionalities. User interactions are handled via console input.
 */
import java.sql.*;
import java.util.Scanner;

public class Main {
    /**
     * Database URL for connecting to the MySQL database.
     */
    private static final String DB_URL = "jdbc:mysql://localhost:3306/employeeData";

    /**
     * Database user for connection authentication.
     */
    private static final String USER = "root";

    /**
     * Database password for connection authentication.
     */
    private static final String PASSWORD = "@Godblessyou001";

    /**
     * The main method serves as the entry point of the application.
     * It establishes a connection to the database and presents a menu to the user
     * to perform various operations related to employee management.
     * 
     * @param args The command line arguments (not used).
     */
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {
            // Initialization of all operation classes related to employee management
            EmployeeDatabase myEmployeeDatabase = new EmployeeDatabase(connection);
            UpdateEmployee myUpdateEmployee = new UpdateEmployee(myEmployeeDatabase);
            ReportGeneratorByJobTitleOrDivision myReportGeneratorByJobTitleOrDivision = new ReportGeneratorByJobTitleOrDivision(myEmployeeDatabase, scanner);
            SearchEmployee mySearchEmployee = new SearchEmployee(myEmployeeDatabase);
            DeleteEmployee myDeleteEmployee = new DeleteEmployee(myEmployeeDatabase);
            AddEmployee myAddEmployee = new AddEmployee(myEmployeeDatabase);
            AddSsnColumnInDatabase myAddSsnColumnInDatabase = new AddSsnColumnInDatabase(myEmployeeDatabase);
            FullTimeEmployeeReport myFullTimeEmployeeReport = new FullTimeEmployeeReport(myEmployeeDatabase);
            PartTimeEmployeeReport myPartTimeEmployeeReport = new PartTimeEmployeeReport(myEmployeeDatabase);
            UpdateEmployeeDivision myUpdateEmployeeDivision = new UpdateEmployeeDivision(myEmployeeDatabase);

            // Main loop for user interaction
            int choice;
            do {
                // Display the main menu and handle user choice
                System.out.println("\nEmployee Management System");
                System.out.println("1. Add Employee");
                System.out.println("2. Remove Employee");
                System.out.println("3. Search Employee");
                System.out.println("4. Update Employee Data");
                System.out.println("5. Update Employee Division");
                System.out.println("6. Update Employee Salary Range");
                System.out.println("7. Add SSN Column to Database");
                System.out.println("8. Full Time Employee Information with past payroll history");
                System.out.println("9. Part Time Employee Information with past payroll history");
                System.out.println("10. Generate Reports by Division or Job Titles");
                System.out.println("11. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); 
<<<<<<< HEAD
                System.out.println("\nPress Enter to clear the screen...\n");
=======
>>>>>>> ff16d24759a930b887778196e452dfba6cf9dab2
                System.out.print("\033[H\033[2J");
                System.out.flush();

                // Perform the selected operation
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
                        myUpdateEmployeeDivision.updateEmployeeDivision(scanner);
                        break;
                    case 6:
                        myUpdateEmployee.updateEmployeeSalaryRange(scanner);
                        break;
                    case 7:
                        myAddSsnColumnInDatabase.addSSNColumn();
                        break;
                    case 8:
                        myFullTimeEmployeeReport.generateEmployeeReport();
                        break;
                    case 9:
                        myPartTimeEmployeeReport.generateEmployeeReport();
                        break;
                    case 10:
                        myReportGeneratorByJobTitleOrDivision.showMenu();
                        break;
                    case 11:
                        System.out.println("\nGood Bye!\n");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                }
            } while (choice != 11);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
