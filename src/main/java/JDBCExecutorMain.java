import util.DatabaseConnectionManager;
import dao.EmployeeDAO;
import entity.Employee;

import java.sql.Connection;
import java.sql.Date;
import java.util.Map;
import java.util.Random;

class JDBCExecutorMain {
    public static void main(String... args) {
//      Initialize connection
        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager();
        Connection connection = connectionManager.createConnection();

//      Initialize values
        Date birthDay = Date.valueOf("1999-10-09");
        EmployeeDAO employeeDAO = new EmployeeDAO(connection);
        long employeeIdToTest = employeeDAO.getLastEmployeeId() + 1;

//      Read info from DB
        System.out.println("The name of the random employee is: " + new EmployeeDAO(connection).getRandomEmployeeName());

//      Create an employee in the DB
        Employee employee = new Employee(employeeIdToTest, "Nikita", "Sobol", "QA Engineer", "Kyiv", birthDay);
        employee = employeeDAO.create(employee);
        System.out.println("New entity.Employee was created: " + employee.getFirstName() + " " + employee.getLastName() + " " + employee.getId());

//      Read info from DB about employee by ID
        employee = employeeDAO.findById(employee.getId());
        System.out.println("The search completed!\nentity.Employee with id " + employee.getId() + " was found!\n" + "First name: " +
                employee.getFirstName() + "\nLast name: " + employee.getLastName());

//      Update the firstName and lastName in the DB
        System.out.println("entity.Employee info was updated!");
        System.out.println("Old info: " + employee.getFirstName() + " " + employee.getLastName() + " " + employee.getId());
        employee.setFirstName("Fegal");
        employee.setLastName("Fegal");
        employee = employeeDAO.update(employee);
        System.out.println("New info: " + employee.getFirstName() + " " + employee.getLastName() + " " + employee.getId());

//      Delete employee from the DB
        if (employeeDAO.delete(employee.getId()).getId() == 0) {
            System.out.println("Employee was deleted successfully!");
        } else {
            System.err.print("ERROR! The employee was NOT deleted!");
        }

//      Print all records from DB
        System.out.println("SELECT * FROM Employees:");
        for (Employee e : employeeDAO.findAll()) {
            System.out.println(e.toString());
        }

//     Print random table line as String
        System.out.println("Random table line:");
        Map<String, String> firstSelectedRecord = employeeDAO.getTableRecordByIndex("sql/selectAllRecordsFromEmployee.sql",
                new Random().nextInt(employeeDAO.findAll().size()));
        System.out.println(firstSelectedRecord);

//      Close connection
        connectionManager.closeConnection(connection);
    }
}