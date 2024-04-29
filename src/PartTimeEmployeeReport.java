import java.sql.*;

/**
 * The PartTimeEmployeeReport class extends the EmployeeReport abstract class to provide a specific
 * implementation for generating and displaying reports of part-time employees and their corresponding payroll data.
 */
public class PartTimeEmployeeReport extends EmployeeReport {

    /**
     * Constructs a PartTimeEmployeeReport object with a given EmployeeDatabase instance.
     * 
     * @param employeeDatabase The database handler for employee data.
     */
    public PartTimeEmployeeReport(EmployeeDatabase employeeDatabase) {
        super(employeeDatabase);
    }

    /**
     * Generates and prints a detailed report of all part-time employees including their personal information
     * and detailed payroll data. It queries the database for employees who are in the part_time_payroll table,
     * indicating part-time status, and prints out each individual's job title, email, and payroll details.
     */
    @Override
    public void generateEmployeeReport() {
        StringBuilder output = new StringBuilder("");
        String sqlcommand = "SELECT e.Fname, e.Lname, e.email, jt.job_title, e.empid " +
                "FROM employees e  " +
                "JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
                "JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id  " +
                "WHERE e.empid IN (SELECT empid FROM part_time_payroll) " +
                "ORDER BY e.empid ; ";

        try {
            PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(sqlcommand);
            ResultSet myRS = employeeDatabase.executeQuery(pstmt);
            if (!myRS.next()) {
                System.out.println("No part-time employees found.");
                return;
            }
            do {
                output.append("Name= " + myRS.getString("e.Fname") + " " + myRS.getString("e.Lname") + "\t");
                output.append("Title=" + myRS.getString("jt.job_title") + "     " + myRS.getString("e.email") + "\n");
                System.out.print(output.toString());
                output.setLength(0);
                PartTimePayroll p1 = new PartTimePayroll(employeeDatabase);
                output.append(p1.getPayByMonth(myRS.getInt("e.empid")));
            } while (myRS.next());
        } catch (SQLException e) {
            System.out.println("ERROR " + e.getLocalizedMessage());
        }
    }
}

/**
 * The PartTimePayroll class handles the retrieval and formatting of payroll data for part-time employees.
 */
class PartTimePayroll {
    private final EmployeeDatabase employeeDatabase;

    /**
     * Constructs a PartTimePayroll object with a given EmployeeDatabase instance.
     * 
     * @param employeeDatabase The database handler for employee data.
     */
    public PartTimePayroll(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    /**
     * Retrieves and formats the payroll data for a specific part-time employee by their employee ID.
     * The method queries the database for payroll details specifically for part-time workers and
     * formats them in a tabulated string format.
     * 
     * @param empID The employee ID for which payroll data is to be retrieved.
     * @return A StringBuilder object containing formatted payroll data for the specified part-time employee.
     */
    public StringBuilder getPayByMonth(int empID) {
        StringBuilder output = new StringBuilder("");
        String sqlcommand1 = "SELECT e.empid, p.pay_date, p.hours_worked, p.hourly_wage, " +
                "p.fed_tax,p.fed_med,p.fed_SS,p.state_tax " +
                "FROM employees e " +
                "JOIN part_time_payroll p ON e.empid = p.empid " +
                "WHERE e.empid = ? " +
                "ORDER BY p.pay_date;";
        try {
            PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(sqlcommand1);
            pstmt.setInt(1, empID);
            ResultSet myRS1 = employeeDatabase.executeQuery(pstmt);
            if (!myRS1.next()) {
                return output; // If no payroll data found, return an empty StringBuilder
            }
            output.append("\tEMP ID\tPAY DATE\tHOURS WORKED\tHOURLY WAGE\tFederal\tFedMed\tFedSS\tState\n");
            do {
                output.append("\t" + myRS1.getString("e.empid") + "\t");
                output.append(myRS1.getDate("p.pay_date") + "\t" + myRS1.getDouble("p.hours_worked") + "\t");
                output.append(myRS1.getDouble("p.hourly_wage") + "\t" + myRS1.getDouble("p.fed_tax") + "\t");
                output.append(myRS1.getDouble("p.fed_med") + "\t" + myRS1.getDouble("p.fed_SS") + "\t");
                output.append(myRS1.getDouble("p.state_tax") + "\n");
            } while (myRS1.next());
            System.out.println(output.toString());
            output.setLength(0);
        } catch (SQLException e) {
            System.out.println("ERROR " + e.getLocalizedMessage());
        }
        return output;
    }
}
