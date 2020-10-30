import dao.EmployeeDAO;
import entity.Employee;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.DatabaseConnectionManager;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.Date;

public class MainTests {
    private Connection connection;
    private DatabaseConnectionManager connectionManager;

    @Before
    public void setUp() {
        connectionManager = new DatabaseConnectionManager();
        connection = connectionManager.createConnection();
    }

    @Test
    public void readFromDb(){
        System.out.println("The name of the random employee is: " + new Employee().getRandomEmployeeName(connection));
        assertThat(new Employee().getRandomEmployeeName(connection) != null).isTrue();
    }

    @Test
    public void createNewEmployee(){
        Date birthDay = Date.valueOf("1999-10-09");
        EmployeeDAO employeeDAO = new EmployeeDAO(connection);
        long employeeIdToTest = Long.parseLong(employeeDAO.getLastEmployeeId()) + 1;

        Employee employee = new Employee(employeeIdToTest, "Nikita", "Sobol", "QA Engineer", "Kyiv", birthDay);
        employee = employeeDAO.create(employee);
        System.out.println(employee.toString());

        assertThat(employee.getId()).isEqualTo(employeeIdToTest);
        assertThat(employee.getFirstName()).isEqualTo("Nikita");
        assertThat(employee.getLastName()).isEqualTo("Sobol");
    }

    @Test
    public void failedTest(){
        assertThat(true).isFalse();
    }

    @After
    public void tearDown(){
        connectionManager.closeConnection(connection);
    }
}
