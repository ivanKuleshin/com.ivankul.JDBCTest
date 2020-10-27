CREATE OR REPLACE VIEW Sequence_Of_Employee_ID AS
(SELECT EmployeeID FROM employees
ORDER BY EmployeeID DESC LIMIT 1);