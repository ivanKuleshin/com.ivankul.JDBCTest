//import util.DatabaseConnectionManager;
//
//import java.util.Locale;
//import java.util.Scanner;
//
//public class Main {
//    public static DatabaseConnectionManager connectionManager;
//    public static void main(String... args) {
//        Scanner in = new Scanner(System.in);
//        in.useLocale(Locale.ENGLISH);
//
//        while (true) {
//            System.out.println("Select options and press the Enter key.");
//            System.out.println("1. Create employee");
//            System.out.println("2. Select random employee from the DB.");
//            System.out.println("3. Select employee from the DB by id.");
//            System.out.println("4. Update employee's first name.");
//            System.out.println("5. Update employee's last name");
//            System.out.println("6. Delete employee from the DB by id.");
//            System.out.println("Input: ");
//            String answer = in.next();
//            if (answer.equalsIgnoreCase("1")) {
//                createEmployee();
//            } else if (answer.equalsIgnoreCase("2")) {
//                selectRandomEmployee();
//            } else if (answer.equalsIgnoreCase("3")) {
//                selectEmployeeById();
//            } else if (answer.equalsIgnoreCase("4")) {
//                updateEmployeeFirstName();
//            } else if (answer.equalsIgnoreCase("5")) {
//                updateEmployeeLastName();
//            } else if (answer.equalsIgnoreCase("6")) {
//                deleteEmployeeById();
//            } else {
//                break;
//            }
//        }
//    }
//}
