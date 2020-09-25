package dao;

import entity.Employee;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public class EmployeeDAO implements DAO<Employee> {
    private final Connection connection;
    private static final String CREATE_VIEW = getStringFromFile("createView.sql");
    private static final String GET_LAST_ID = getStringFromFile("getLastId.sql");
    private static final String INSERT = getStringFromFile("insertIntoEmployee.sql");
    private static final String GET_ONE = getStringFromFile("getFromEmployeeById.sql");
    private static final String UPDATE = getStringFromFile("updateEmployee.sql");
    private static final String DELETE = getStringFromFile("deleteFromEmployee.sql");

    public EmployeeDAO(Connection connection) {
        this.connection = connection;
    }

    private static String getStringFromFile(String filePath) {
        filePath = "D:\\IntelliJ IDEA Community Edition 2019.2\\IDEA projects\\com.ivankul.JDBCTest\\src\\main\\resources\\" + filePath;
        String stringFromFile = "";
        try {
            Scanner scanner = new Scanner(Paths.get(filePath));
            while (scanner.hasNextLine()) {
                stringFromFile = stringFromFile.concat(scanner.nextLine()) + " ";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringFromFile;
    }

    public String getLastEmployeeId() {
        String lastId = null;
        try {
            createView();
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_LAST_ID);
            while (resultSet.next()) {
                lastId = resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return lastId;
    }

    private void createView() {
        try {
            Statement statement = this.connection.createStatement();
            statement.execute(CREATE_VIEW);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Employee findById(long id) {
        Employee employee = new Employee();
        try (PreparedStatement statement = this.connection.prepareStatement(GET_ONE)) {
            statement.setLong(1, id);
            ResultSet resultset = statement.executeQuery();
            while (resultset.next()) {
                employee.setId(resultset.getLong("EmployeeID"));
                employee.setLastName(resultset.getString("LastName"));
                employee.setFirstName(resultset.getString("FirstName"));
                employee.setTitle(resultset.getString("Title"));
                employee.setBirthDay(resultset.getDate("BirthDate"));
                employee.setCity(resultset.getString("City"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return employee.getLastName() == null ? null : employee;
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> listOfEmployee = new ArrayList<>();
        try (Statement statement = this.connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * from employees;");
            while (resultSet.next()) {
                Employee employee = new Employee();
                employee.setId(resultSet.getLong("EmployeeID"));
                employee.setLastName(resultSet.getString("LastName"));
                employee.setFirstName(resultSet.getString("FirstName"));
                employee.setTitle(resultSet.getString("Title"));
                employee.setBirthDay(resultSet.getDate("BirthDate"));
                employee.setCity(resultSet.getString("City"));
                listOfEmployee.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return listOfEmployee;
    }

    private List<Map<String, String>> getAllRecordsAsList(String fileName) {
        List<Map<String, String>> listOfAllRecords;
        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(getStringFromFile(fileName));
            listOfAllRecords = getRowsOfQuery(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listOfAllRecords;
    }

    public Map<String, String> getFirstSelectedRecord(String fileName) {
        return getTableRecordByIndex(fileName, 0);
    }

    private Map<String, String> getTableRecordByIndex(String fileName, int index) {
        List<Map<String, String>> queryResults = getAllRecordsAsList(fileName);
        if (queryResults.size() > index) {
            return queryResults.get(index);
        }
        return Collections.emptyMap();
    }

    private List<Map<String, String>> getRowsOfQuery(final ResultSet result) throws SQLException {
        List<Map<String, String>> singleQueryResults = new ArrayList<>();
        List<String> columnNames = getColumnNames(result);

        while (result.next()) {
            Map<String, String> singleQueryResultRow = new HashMap<>();
            for (String columnName : columnNames) {
                try {
                    String finalValue = result.getString(columnName);
                    singleQueryResultRow.put(columnName, finalValue);
                } catch (SQLException e) {
                    if (e.getMessage().contains("can not be represented as java.sql.Date")) {
                        singleQueryResultRow.put(columnName, null);
                    } else {
                        throw new IllegalStateException(
                                String.format("SQL statement was not executed successfully: %s", e.getMessage()), e);
                    }
                }
            }
            singleQueryResults.add(singleQueryResultRow);
        }
        return singleQueryResults;
    }

    private static List<String> getColumnNames(final ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        List<String> columnNames = new ArrayList<>();
        for (int columnPosition = 1; columnPosition <= resultSetMetaData.getColumnCount(); columnPosition++) {
            columnNames.add(resultSetMetaData.getColumnLabel(columnPosition));
        }
        return columnNames;
    }

    @Override
    public Employee update(Employee dto) {
        Employee employee;
        try (PreparedStatement statement = this.connection.prepareStatement(UPDATE)) {
            statement.setString(1, dto.getLastName());
            statement.setString(2, dto.getFirstName());
            statement.setLong(3, dto.getId());
            statement.execute();
            employee = this.findById(dto.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return employee;
    }

    @Override
    public Employee create(Employee dto) {
        try (PreparedStatement statement = this.connection.prepareStatement(INSERT)) {
            statement.setLong(1, dto.getId());
            statement.setString(2, dto.getLastName());
            statement.setString(3, dto.getFirstName());
            statement.setString(4, dto.getTitle());
            statement.setDate(5, dto.getBirthDay());
            statement.setString(6, dto.getCity());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        long id = Long.parseLong(getLastEmployeeId());
        return findById(id);
    }

    @Override
    public Employee delete(long id) {
        try (PreparedStatement statement = this.connection.prepareStatement(DELETE)) {
            statement.setLong(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return findById(id);
    }
}
