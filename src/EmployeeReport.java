/**
 * The EmployeeReport class serves as an abstract base for creating reports about employees.
 * It holds common functionality and structures needed by specific types of employee reports.
 */
public abstract class EmployeeReport {
    /**
     * An instance of EmployeeDatabase to manage database connections and operations.
     */
    protected final EmployeeDatabase employeeDatabase;

    /**
     * Constructs an EmployeeReport object with a given EmployeeDatabase instance.
     * This constructor is used by subclasses to ensure that the database connection
     * is available for executing any necessary SQL operations in the reports.
     * 
     * @param employeeDatabase The database handler for employee data, used to access and manipulate employee information.
     */
    public EmployeeReport(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    /**
     * Generates a report for employees. This method is intended to be overridden
     * by subclasses to provide specific reporting logic based on different criteria.
     * The default implementation is empty because the specific details and implementation
     * will depend on the subclass.
     */
    public void generateEmployeeReport() {
        // Implementation to be provided by subclasses
    }
}
