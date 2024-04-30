import java.sql.*;

/**
 * The FullTimeEmployeeReport class extends the EmployeeReport abstract class to provide a specific
 * implementation for generating and displaying reports of full-time employees and their corresponding payroll data.
 */
public class FullTimeEmployeeReport extends EmployeeReport {

    /**
     * Constructs a FullTimeEmployeeReport object with a given EmployeeDatabase instance.
     * 
     * @param employeeDatabase The database handler for employee data.
     */
    public FullTimeEmployeeReport(EmployeeDatabase employeeDatabase) {
        super(employeeDatabase);
    }

    /**
     * Generates and prints a detailed report of all full-time employees including their personal information
     * and detailed monthly payroll data. It queries the database for employees who have payroll entries,
     * indicating full-time status, and prints out each individual's job title, email, and payroll details.
     */
    @Override
    public void generateEmployeeReport() {
        StringBuilder output = new StringBuilder("");
        String sqlcommand = "SELECT e.Fname, e.Lname, e.email, jt.job_title, e.empid " +
                "FROM employees e  " +
                "JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
                "JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id  " +
                "WHERE e.empid IN (SELECT empid FROM payroll) " +
                "ORDER BY e.empid ; ";

        try {
            PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(sqlcommand);
            ResultSet myRS = employeeDatabase.executeQuery(pstmt);
            if (!myRS.next()) {
                System.out.println("No full-time employees found.");
                return;
            }
            do {
                output.append("Name= " + myRS.getString("e.Fname") + " " + myRS.getString("e.Lname") + "\t");
                output.append("Title=" + myRS.getString("jt.job_title") + "     " + myRS.getString("e.email") + "\n");
                System.out.print(output.toString());
                output.setLength(0);
                Payroll p1 = new Payroll(employeeDatabase);
                output.append(p1.getPayByMonth(myRS.getInt("e.empid")));
            } while (myRS.next());
        } catch (SQLException e) {
            System.out.println("ERROR " + e.getLocalizedMessage());
        }
    }
}

/**
 * The Payroll class handles the retrieval and formatting of payroll data for individual employees.
 */
class Payroll {
    private final EmployeeDatabase employeeDatabase;

    /**
     * Constructs a Payroll object with a given EmployeeDatabase instance.
     * 
     * @param employeeDatabase The database handler for employee data.
     */
    public Payroll(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    /**
     * Retrieves and formats the payroll data for a specific employee by their employee ID.
     * The method queries the database for payroll details and formats them in a tabulated string format.
     * 
     * @param empID The employee ID for which payroll data is to be retrieved.
     * @return A StringBuilder object containing formatted payroll data.
     */
    public StringBuilder getPayByMonth(int empID) {
        StringBuilder output = new StringBuilder("");
        String sqlcommand1 = "SELECT e.empid, p.pay_date, p.earnings, p.fed_tax, " +
        "p.fed_med, p.fed_SS, p.state_tax, p.retire_401k, p.health_care, p.health_cost " + 
        "FROM employees e " +
        "JOIN payroll p ON e.empid = p.empid " +
        "WHERE e.empid = ? " +
        "ORDER BY p.pay_date;";

        try {
            PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(sqlcommand1);
            pstmt.setInt(1, empID);
            ResultSet myRS1 = employeeDatabase.executeQuery(pstmt);
            if (!myRS1.next()) {
                return output; // If no payroll data found, return an empty StringBuilder
            }
            output.append("\tEMP ID\tPAY DATE\tGROSS\tFederal\tFedMed\tFedSS\tState\t401K\tHealthCare\tHealthCost\n");
            do {
                output.append("\t" + myRS1.getString("e.empid") + "\t");
                output.append(myRS1.getDate("p.pay_date") + "\t" + myRS1.getDouble("p.earnings") + "\t");
                output.append(myRS1.getDouble("p.fed_tax") + "\t" + myRS1.getDouble("p.fed_med") + "\t");
                output.append(myRS1.getDouble("p.fed_SS") + "\t" + myRS1.getDouble("p.state_tax") + "\t");
                output.append(myRS1.getDouble("p.retire_401K") + "\t" + myRS1.getDouble("p.health_care")+"\t\t" );
                output.append(myRS1.getDouble("p.health_cost") + "\n");
            } while (myRS1.next());
            System.out.println(output.toString());
            output.setLength(0);
        } catch (SQLException e) {
            System.out.println("ERROR " + e.getLocalizedMessage());
        }
        return output;
    }
}
