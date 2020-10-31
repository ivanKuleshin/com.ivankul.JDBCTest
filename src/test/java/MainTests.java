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

    @BeforeClass
    public static void setUp() {
        connectionManager = new DatabaseConnectionManager();
        connection = connectionManager.createConnection();
        employeeDAO = new EmployeeDAO(connection);
    }

    @Test
    public void readFromDb(){
        System.out.println("The name of the random employee is: " + employeeDAO.getRandomEmployeeName());
        assertThat(employeeDAO.getRandomEmployeeName() != null).isTrue();
    }

    @Test
    public void createNewEmployeeInDb(){
        Employee employee = getEmployee();
        employee = employeeDAO.create(employee);
        System.out.println(employee.toString());

        assertThat(employee.getId()).isEqualTo(employeeDAO.getLastEmployeeId());
        assertThat(employee.getFirstName()).isEqualTo("Nikita");
        assertThat(employee.getLastName()).isEqualTo("Sobol");
    }

    @Test
    public void updateEmployeeNameInDb(){
        Employee employee = getEmployeeFromDb();
        employee.setFirstName("Ivan");
        employee.setLastName("Kuleshin");

        employee = employeeDAO.update(employee);

        assertThat(employee.getFirstName()).isEqualTo("Ivan");
        assertThat(employee.getLastName()).isEqualTo("Kuleshin");
    }

    @Test
    public void findById(){
        Employee employee = getEmployeeFromDb();
        assertThat(employee.getId()).isEqualTo(employeeDAO.findById(employee.getId()).getId());
    }

    @AfterClass
    public static void tearDown(){
        connectionManager.closeConnection(connection);
    }

    private Employee getEmployeeFromDb(){
        List<Employee> employeeList = employeeDAO.findAll();
        return employeeList.get(new Random().nextInt(employeeList.size()));
    }

    private Employee getEmployee(){
        return new Employee(employeeDAO.getLastEmployeeId() + 1, "Nikita", "Sobol",
                "QA Engineer", "Kyiv", Date.valueOf("1999-10-09"));
    }
}