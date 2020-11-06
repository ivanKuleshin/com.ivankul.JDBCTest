import dao.EmployeeDAO;
import entity.Employee;
import org.junit.*;
import util.DatabaseConnectionManager;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.Random;

public class MainTests {
    static private Connection connection;
    static private DatabaseConnectionManager connectionManager;
    static private EmployeeDAO employeeDAO;
    static private final String IVAN = "Ivan";
    static private final String NIKITA = "Nikita";
    static private final String KUL = "Kuleshin";
    static private final String SOB = "Sobol";

    @BeforeClass
    public static void setUp() {
        connectionManager = new DatabaseConnectionManager();
        connection = connectionManager.createConnection();
        employeeDAO = new EmployeeDAO(connection);
    }

    @Test
    public void readFromDb() {
        System.out.println("The name of the random employee is: " + employeeDAO.getRandomEmployeeName());
        assertThat(employeeDAO.getRandomEmployeeName() != null).isTrue();
    }

    @Test
    public void createNewEmployeeInDb() {
        Employee employee = getEmployee();
        employee = employeeDAO.create(employee);
        System.out.println(employee.toString());

        assertThat(employee.getId()).isEqualTo(employeeDAO.getLastEmployeeId());
        assertThat(employee.getFirstName()).isEqualTo(NIKITA);
        assertThat(employee.getLastName()).isEqualTo(SOB);
    }

    @Test
    public void updateEmployeeNameInDb() {
        Employee employee = getEmployeeFromDb();
        employee.setFirstName(IVAN);
        employee.setLastName(KUL);

        employee = employeeDAO.update(employee);

        assertThat(employee.getFirstName()).isEqualTo(IVAN);
        assertThat(employee.getLastName()).isEqualTo(KUL);
    }

    @Test
    public void findById() {
        Employee employee = employeeDAO.findById(getEmployeeFromDb().getId());
        assertThat(employee.isEmpty()).isFalse();
        assertThat(employee.getId()).isEqualTo(employee.getId());
    }

    @Test
    public void deleteTheEmployee() {
        long employeeId = getUnnecessaryEmployee().getId();
        employeeDAO.delete(employeeId);
        assertThat(employeeDAO.findById(employeeId).isEmpty()).isTrue();
    }

    @AfterClass
    public static void tearDown() {
        connectionManager.closeConnection(connection);
    }

    private Employee getEmployeeFromDb() {
        List<Employee> employeeList = employeeDAO.findAll();
        return employeeList.get(new Random().nextInt(employeeList.size()));
    }

    private Employee getEmployee() {
        return new Employee(employeeDAO.getLastEmployeeId() + 1, NIKITA, SOB,
                "QA Engineer", "Kyiv", Date.valueOf("1999-10-09"));
    }

    private Employee getUnnecessaryEmployee(){
        return employeeDAO.getUnnecessaryEmployee();
    }

    // test
    // test
}