import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
enum EmployeeType {
    SALARIED,
    HOURLY,
    COMMISSIONED
}
class EmployeeIdGenerator {
    private static int currentId = 100;

    public static int generateId() {
        return currentId++;
    }
}


abstract class Employee {
    int employeeId;
    String name;
    EmployeeType employeeType;
    String paymentMethod;
    String paymentDetails;
    String taxInformation;

    public Employee(int employeeId, String name, EmployeeType employeeType) {
        this.employeeId = employeeId;
        this.name = name;
        this.employeeType = employeeType;
    }

    abstract double calculatePay();

    abstract String generatePayStub();
}


class SalariedEmployee extends Employee {
    double salary;

    public SalariedEmployee(int employeeId, String name, double salary) {
        super(employeeId, name, EmployeeType.SALARIED);
        this.salary = salary;
    }

    @Override
    double calculatePay() {
        return salary;
    }

    @Override
    String generatePayStub() {
        return "Pay Stub - Salaried Employee\n" +
                "Name: " + name + "\n" +
                "Employee ID: " + employeeId + "\n" +
                "Salary: $" + salary + "\n";
    }
}


class HourlyEmployee extends Employee {
    double hourlyRate;
    int hoursWorked;

    public HourlyEmployee(int employeeId, String name, double hourlyRate, int hoursWorked) {
        super(employeeId, name, EmployeeType.HOURLY);
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    @Override
    double calculatePay() {
        return hourlyRate * hoursWorked;
    }

    @Override
    String generatePayStub() {
        return "Pay Stub - Hourly Employee\n" +
                "Name: " + name + "\n" +
                "Employee ID: " + employeeId + "\n" +
                "Hours Worked: " + hoursWorked + "\n" +
                "Hourly Rate: $" + hourlyRate + "\n" +
                "Total Pay: $" + calculatePay() + "\n";
    }
}

// CommissionedEmployee Class
class CommissionedEmployee extends Employee {
    double commissionRate;
    int totalSales;

    public CommissionedEmployee(int employeeId, String name, double commissionRate, int totalSales) {
        super(employeeId, name, EmployeeType.COMMISSIONED);
        this.commissionRate = commissionRate;
        this.totalSales = totalSales;
    }

    @Override
    double calculatePay() {
        return commissionRate * totalSales;
    }

    @Override
    String generatePayStub() {
        return "Pay Stub - Commissioned Employee\n" +
                "Name: " + name + "\n" +
                "Employee ID: " + employeeId + "\n" +
                "Total Sales: $" + totalSales + "\n" +
                "Commission Rate: " + (commissionRate * 100) + "%\n" +
                "Total Pay: $" + calculatePay() + "\n";
    }
}



class PayrollConsoleApp {
    public static void main(String[] args) throws IOException {
        PayrollSystem p = new PayrollSystem();
        p.start();
        System.out.println("hello guys!");
    }
}


class PayrollSystem {
    private HashMap<Integer, Employee> employees = new HashMap<>();
    private HashMap<String, String> userCredentials = new HashMap<>();
    private Scanner scanner = new Scanner(System.in);
    private final String credentialsFile = "user_credentials.txt";

    public PayrollSystem() throws IOException {
        loadUserCredentials(); // Load user credentials from file at startup
    }

    // Load user credentials from file
    private void loadUserCredentials() throws IOException {
        File file = new File(credentialsFile);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    userCredentials.put(parts[0], parts[1]); // username, password
                }
            }
        }
    }

    // Save user credentials to file
    private void saveUserCredentials() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(credentialsFile))) {
            for (var entry : userCredentials.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        }
    }

    // User registration
    private void registerUser() throws IOException {
        System.out.print("Enter a username: ");
        String username = scanner.nextLine();
        if (userCredentials.containsKey(username)) {
            System.out.println("Username already exists. Please try again.");
            return;
        }

        System.out.print("Enter a password: ");
        String password = scanner.nextLine();
        System.out.print("Confirm your password: ");
        String confirmPassword = scanner.nextLine();

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Registration failed.");
            return;
        }

        userCredentials.put(username, password);
        saveUserCredentials();
        System.out.println("Registration successful!");
    }

    // User login
    private boolean loginUser() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
            System.out.println("Login successful! Welcome, " + username + "!");
            return true;
        } else {
            System.out.println("Invalid username or password. Please try again.");
            return false;
        }
    }

    // start app from here (main menu)
    public void start() throws IOException {
        System.out.println("Welcome to the Company Payroll System!");

        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> registerUser();
                case 2 -> {
                    if (loginUser()) {
                        menu(); // Proceed to the payroll system menu after successful login
                    }
                }
                case 3 -> {
                    System.out.println("Exiting the system. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Payroll system menu (after login)
    private void menu() {
        while (true) {
            System.out.println("\nPayroll System Menu:");
            System.out.println("1. Add Employee");
            System.out.println("2. Remove Employee");
            System.out.println("3. Update Employee");
            System.out.println("4. Calculate Total Payroll");
            System.out.println("5. Generate Payslips");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> addEmployeeInteraction();
                case 2 -> removeEmployeeInteraction();
                case 3 -> updateEmployeeInteraction();
                case 4 -> calculateTotalPayroll();
                case 5 -> generatePayslips();
                case 6 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    // Employee management and payroll functionalities remain the same
    public void addEmployee(Employee employee) {
        employees.put(employee.employeeId, employee);
        System.out.println("Employee added successfully!");
    }

    private void addEmployeeInteraction() {
        System.out.print("Enter Employee Name: ");
        String name = scanner.nextLine();
        System.out.println("Select Employee Type: 1-Salaried, 2-Hourly, 3-Commissioned");
        int typeChoice = scanner.nextInt();

        switch (typeChoice) {
            case 1 -> {
                System.out.print("Enter Salary: ");
                double salary = scanner.nextDouble();
                addEmployee(new SalariedEmployee(EmployeeIdGenerator.generateId(), name, salary));
            }
            case 2 -> {
                System.out.print("Enter Hourly Rate: ");
                double hourlyRate = scanner.nextDouble();
                System.out.print("Enter Hours Worked: ");
                int hoursWorked = scanner.nextInt();
                addEmployee(new HourlyEmployee(EmployeeIdGenerator.generateId(), name, hourlyRate, hoursWorked));
            }
            case 3 -> {
                System.out.print("Enter Commission Rate (e.g., 0.1 for 10%): ");
                double commissionRate = scanner.nextDouble();
                System.out.print("Enter Total Sales: ");
                int totalSales = scanner.nextInt();
                addEmployee(new CommissionedEmployee(EmployeeIdGenerator.generateId(), name, commissionRate, totalSales));
            }
            default -> System.out.println("Invalid Employee Type!");
        }
    }
    private void updateEmployeeInteraction() {
if(employees.isEmpty()) {
    System.out.println("No Employees To Update");
return;
}
        System.out.print("Enter Employee ID to Update: " + "\n");

        for (Map.Entry<Integer, Employee> entry : employees.entrySet()) {
            Integer key = entry.getKey();
            Employee value = entry.getValue();

            System.out.println("ID: " + key + ", Name: " + value.name); // Accessing Employee properties
        }

        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Employee employee = employees.get(id);
        if (employee != null) {
            EmployeeType type=employee.employeeType;
            switch (type){
                case type.SALARIED :
                    SalariedEmployee salariedEmployee=(SalariedEmployee) employee;
                    System.out.printf("1) Name: %s\n2)Salary:%.2f",salariedEmployee.name,salariedEmployee.salary);
                    System.out.print("\nWhat Trait to Edit");
                    int attributeChoice = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new value: ");
                    String input = scanner.next();
                    Object newValue = changedValue(input);


                    switch (attributeChoice){
                        case 1 :
                            updateDetails(employee,"name",newValue);
                            break;
                            case 2 :
                            updateDetails(employee,"salary",newValue);
break;
                        default:
                            System.out.println("Invalid Option");
                            return;



                    }
                    break;
                case type.COMMISSIONED:
                    CommissionedEmployee commissionedEmployee=(CommissionedEmployee) employee;
                    System.out.printf("1)Name: %s\n 2)Commission Rate:%f\n3)Total Sales:%d\n",commissionedEmployee.name,commissionedEmployee.commissionRate,commissionedEmployee.totalSales);
                    System.out.print("\nWhat Trait to Edit\n");
                     attributeChoice = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new value: ");
                     input = scanner.next();
                     newValue = changedValue(input);


                    switch (attributeChoice){
                        case 1 :
                            updateDetails(employee,"name",newValue);
                            break;
                        case 2 :
                            updateDetails(employee,"commissionRate",newValue);
                            break;
                            case 3 :
                            updateDetails(employee,"totalSales",newValue);
                            break;
                        default:
                            System.out.println("Invalid Option");
                            return;




                    }
                    break;
                case type.HOURLY :
                    HourlyEmployee hourlyEmployee=(HourlyEmployee) employee;
                    System.out.printf("1)Name: %s\n 2)Hourly Rate:%.2f\n 3)Worked Hours:%d",hourlyEmployee.name,hourlyEmployee.hourlyRate,hourlyEmployee.hoursWorked);
                    System.out.print("\nWhat Trait to Edit");
                     attributeChoice = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new value: ");
                     input = scanner.next();
                     newValue = changedValue(input);


                    switch (attributeChoice){
                        case 1 :
                            updateDetails(employee,"name",newValue);
                            break;
                        case 2 :
                            updateDetails(employee,"hourlyRate",newValue);
                            break;
                            case 3 :
                            updateDetails(employee,"hoursWorked",newValue);
                            break;
                        default:
                            System.out.println("Invalid Option");




                    }
                    break;

            }




//            Object newValue = scanner.nextLine();

//            updateEmployeeDetails(employee, attribute, newValue);

        } else {
            System.out.println("Employee not found!");
        }
    }
    public  Object changedValue(String input){

Object parsedValue;
        if (input.matches("-?\\d+")) {
            parsedValue = Integer.parseInt(input); // Integer
        } else if (input.matches("-?\\d*\\.\\d+")) {
            parsedValue = Double.parseDouble(input); // Double
        } else if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
            parsedValue = Boolean.parseBoolean(input); // Boolean
        } else {
            parsedValue = input; // String
        }
return parsedValue;


    }

    public void updateDetails(Employee employee,String attribute, Object newValue) {

        switch (attribute) {
            case "name":
                employee.name = (String) newValue;
                break;
            case "salary":
                if (newValue instanceof Double) {
                    if (employee instanceof SalariedEmployee) {
                        ((SalariedEmployee) employee).salary = (Double) newValue;
                    }
                } else {
                    System.out.println("Invalid value for salary.");
                }
                break;
            case "hoursWorked":
                ((HourlyEmployee) employee).hoursWorked = (int) newValue;
break;
            case "totalSales":
                ((CommissionedEmployee) employee).totalSales = (int) newValue;
break;
            case "hourlyRate":
                if (newValue instanceof Double) {
                        ((HourlyEmployee) employee).hourlyRate = (Double) newValue;
                } else {
                    System.out.println("Invalid value for salary.");
                }
break;

            case "commissionRate":
                if (newValue instanceof Double) {
                        ((CommissionedEmployee) employee).commissionRate = (Double) newValue;
                } else {
                    System.out.println("Invalid value for salary.");
                }
                break;
            default:
                System.out.println("Attribute not found or not updatable.");
        }
    }
    public void updateEmployeeDetails(Employee employee, String attribute, Object newValue) {

            updateDetails(employee,attribute, newValue);
            System.out.println("Employee details updated successfully!");

    }


    public void removeEmployee(int employeeId) {
        if (employees.remove(employeeId) != null) {
            System.out.println("Employee removed successfully!");
        } else {
            System.out.println("Employee not found!");
        }
    }

    private void removeEmployeeInteraction() {
        System.out.print("Enter Employee ID to Remove: " + "\n");
        for (Map.Entry<Integer, Employee> entry : employees.entrySet()) {
            Integer key = entry.getKey();
            Employee value = entry.getValue();

            System.out.println("ID: " + key + ", Name: " + value.name); // Accessing Employee properties
        }

        int id = scanner.nextInt();
        removeEmployee(id);
    }

    public void calculateTotalPayroll() {
        double totalPayroll = employees.values().stream()
                .mapToDouble(Employee::calculatePay)
                .sum();
        System.out.println("Total Payroll: $" + totalPayroll);
    }

    public void generatePayslips() {
        if (employees.isEmpty()) {
            System.out.println("No employees in the system!");
            return;
        }
        employees.values().forEach(employee -> {
            System.out.println(employee.generatePayStub());
        });
    }
    private static Object parseInput(String input) {
        // Try to parse as Integer
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ignored) {}

        // Try to parse as Double
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException ignored) {}

        // Try to parse as Boolean
        if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(input);
        }

        // If all parsing fails, return as String
        return input;
    }
}
