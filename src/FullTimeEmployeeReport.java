import java.sql.*;

public class FullTimeEmployeeReport {
    private final EmployeeDatabase employeeDatabase;

    public FullTimeEmployeeReport(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    public void generateFullTimeEmployeeReport() {
        StringBuilder output = new StringBuilder("");
        String sqlcommand = "SELECT e.Fname, e.Lname, e.email, jt.job_title, e.empid " +
                "FROM employees e  " +
                "JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
                "JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id  " +
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

class Payroll {
    private final EmployeeDatabase employeeDatabase;

    public Payroll(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    public StringBuilder getPayByMonth(int empID) {
        StringBuilder output = new StringBuilder("");
        String sqlcommand1 = "SELECT e.empid, p.pay_date, p.earnings, p.fed_tax, " +
                "p.fed_med,p.fed_SS,p.state_tax,p.retire_401k,p.health_care  " +
                "FROM employees e " +
                "JOIN payroll p ON e.empid = p.empid " +
                "WHERE e.empid = ? " +
                "ORDER BY p.pay_date;";
        try {
            PreparedStatement pstmt = employeeDatabase.connection.prepareStatement(sqlcommand1);
            pstmt.setInt(1, empID);
            ResultSet myRS1 = employeeDatabase.executeQuery(pstmt);
            output.append("\tEMP ID\tPAY DATE\tGROSS\tFederal\tFedMed\tFedSS\tState\t401K\tHealthCare\n");
            while (myRS1.next()) {
                output.append("\t" + myRS1.getString("e.empid") + "\t");
                output.append(myRS1.getDate("p.pay_date") + "\t" + myRS1.getDouble("p.earnings") + "\t");
                output.append(myRS1.getDouble("p.fed_tax") + "\t" + myRS1.getDouble("p.fed_med") + "\t");
                output.append(myRS1.getDouble("p.fed_SS") + "\t" + myRS1.getDouble("p.state_tax") + "\t");
                output.append(myRS1.getDouble("p.retire_401K") + "\t" + myRS1.getDouble("p.health_care")+"\n" );
            }
            System.out.println(output.toString());
            output.setLength(0);
        } catch (SQLException e) {
            System.out.println("ERROR " + e.getLocalizedMessage());
        }
        return output;
    }
}
