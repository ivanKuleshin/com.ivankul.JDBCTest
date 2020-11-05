SELECT * FROM employees em
WHERE em.EmployeeID NOT IN (SELECT EmployeeID FROM orders)
order by rand()
limit 1;