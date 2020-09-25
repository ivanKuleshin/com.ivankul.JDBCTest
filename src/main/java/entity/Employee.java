package entity;

import java.sql.*;

public class Employee {
    private long employeeId;
    private String firstName;
    private String lastName;
    private String title;
    private String city;
    private Date birthDay;

    public Employee() {
    }

    public Employee(long employeeId, String firstName, String lastName, String title, String city, Date birthDay) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.city = city;
        this.birthDay = birthDay;
    }

    public String getRandomEmployeeName(Connection connection) {
        String employeeName = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement.executeQuery("SELECT * FROM employees ORDER BY RAND() LIMIT 1");
            while (resultset.next()) {
                employeeName = resultset.getString("FirstName") + " " + resultset.getString("LastName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (null == employeeName) {
            throw new RuntimeException();
        }
        return employeeName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTitle() {
        return title;
    }

    public String getCity() {
        return city;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public void setId(long employeeId) {
        this.employeeId = employeeId;
    }

    public long getId() {
        return employeeId;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", title='" + title + '\'' +
                ", city='" + city + '\'' +
                ", birthDay=" + birthDay +
                '}';
    }
}
