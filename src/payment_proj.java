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
    private static int currentId;

    static {
        currentId = loadLastEmployeeId(); // Initialize currentId with the last used ID from the file
    }

    public static int generateId() {
        return currentId++;
    }

    private static int loadLastEmployeeId() {
        // Read from employees.txt to find the last employee ID
        File file = new File("employees.txt");
        int maxId = 100; // Default ID if no employees are found

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length > 0) {
                        int id = Integer.parseInt(parts[0]); // Assuming ID is the first field
                        if (id > maxId) {
                            maxId = id; // Update maxId if a larger ID is found
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading employee file: " + e.getMessage());
            }
        }

        return maxId + 1; // Start generating IDs from the next value after the highest ID found
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
    }
}


class PayrollSystem {
    private HashMap<Integer, Employee> employees = new HashMap<>();
    private HashMap<String, String> userCredentials = new HashMap<>();
    private Scanner scanner = new Scanner(System.in);
    private final String credentialsFile = "user_credentials.txt";

    public PayrollSystem() throws IOException {
        loadUserCredentials();// Load user credentials from file at startup
        loadEmployees();

    }
    private void saveEmployees() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("employees.txt"))) {
            for (Employee employee : employees.values()) {
                // Format employee data into a CSV-like structure (ID, name, type, salary, etc.)
                String employeeData = employee.employeeId + "," + employee.name + "," + employee.employeeType
                        + "," + getEmployeeDetails(employee); // Use a method to get employee-specific data
                writer.write(employeeData);
                writer.newLine();
            }
        }
    }
    private String getEmployeeDetails(Employee employee) {
        if (employee instanceof SalariedEmployee) {
            return String.valueOf(((SalariedEmployee) employee).salary);
        } else if (employee instanceof HourlyEmployee) {
            HourlyEmployee hourlyEmployee = (HourlyEmployee) employee;
            return hourlyEmployee.hourlyRate + "," + hourlyEmployee.hoursWorked;
        } else if (employee instanceof CommissionedEmployee) {
            CommissionedEmployee commissionedEmployee = (CommissionedEmployee) employee;
            return commissionedEmployee.commissionRate + "," + commissionedEmployee.totalSales;
        }
        return "";
    }
    private void loadEmployees() throws IOException {
        File file = new File("employees.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 2) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    EmployeeType type = EmployeeType.valueOf(parts[2]);
                    Employee employee = null;

                    // Create employee objects based on their type
                    switch (type) {
                        case SALARIED:
                            employee = new SalariedEmployee(id, name, Double.parseDouble(parts[3]));
                            break;
                        case HOURLY:
                            employee = new HourlyEmployee(id, name, Double.parseDouble(parts[3]), Integer.parseInt(parts[4]));
                            break;
                        case COMMISSIONED:
                            employee = new CommissionedEmployee(id, name, Double.parseDouble(parts[3]), Integer.parseInt(parts[4]));
                            break;
                    }
                    if (employee != null) {
                        employees.put(id, employee);
                    }
                }
            }
        }
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
        try {
            saveEmployees(); // Save changes after adding an employee
        } catch (IOException e) {
            System.out.println("Error saving employee data.");
        }
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
                case SALARIED :
                    SalariedEmployee salariedEmployee=(SalariedEmployee) employee;
                    System.out.printf("1) Name: %s\n2)Salary:%.2f",salariedEmployee.name,salariedEmployee.salary);
                    System.out.print("\nWhat Trait to Edit ");
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
                case COMMISSIONED:
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
                case HOURLY :
                    HourlyEmployee hourlyEmployee=(HourlyEmployee) employee;
                    System.out.printf("1)Name: %s\n2)Hourly Rate:%.2f\n3)Worked Hours:%d",hourlyEmployee.name,hourlyEmployee.hourlyRate,hourlyEmployee.hoursWorked);
                    System.out.print("\nWhat Trait to Edit ");
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

                            return;


                            
                    }

            }




//            Object newValue = scanner.nextLine();

//            updateEmployeeDetails(employee, attribute, newValue);

            try {
                saveEmployees(); // Save employees to file after updating details
            } catch (IOException e) {
                System.out.println("Error saving employees to file: " + e.getMessage());
            }

            System.out.println("Employee details updated successfully!");
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

    public boolean updateDetails(Employee employee, String attribute, Object newValue) {
        if (newValue == null) {
            System.out.println("Invalid value: null is not allowed.");
            return false;
        }
    
        switch (attribute) {
            case "name":
                if (newValue instanceof String) {
                    employee.name = (String) newValue;
                } else {
                    System.out.println("Invalid value for name.");
                    return false;
                }
                break;
    
            case "salary":
                if (newValue instanceof Number && employee instanceof SalariedEmployee) {
                    ((SalariedEmployee) employee).salary = ((Number) newValue).doubleValue();
                } else {
                    System.out.println("Invalid value for salary.");
                    return false;
                }
                break;
    
            case "hoursWorked":
                if (newValue instanceof Number && employee instanceof HourlyEmployee) {
                    ((HourlyEmployee) employee).hoursWorked = ((Number) newValue).intValue();
                } else {
                    System.out.println("Invalid value or employee type for hoursWorked.");
                    return false;
                }
                break;
    
            case "totalSales":
                if (newValue instanceof Number && employee instanceof CommissionedEmployee) {
                    ((CommissionedEmployee) employee).totalSales = ((Number) newValue).intValue();
                } else {
                    System.out.println("Invalid value or employee type for totalSales.");
                    return false;
                }
                break;
    
            case "hourlyRate":
                if (newValue instanceof Number && employee instanceof HourlyEmployee) {
                    ((HourlyEmployee) employee).hourlyRate = ((Number) newValue).doubleValue();
                } else {
                    System.out.println("Invalid value or employee type for hourlyRate.");
                    return false;
                }
                break;
    
            case "commissionRate":
                if (newValue instanceof Number && employee instanceof CommissionedEmployee) {
                    ((CommissionedEmployee) employee).commissionRate = ((Number) newValue).doubleValue();
                } else {
                    System.out.println("Invalid value or employee type for commissionRate.");
                    return false;
                }
                break;
    
            default:
                System.out.println("Attribute not found or not updatable.");
                return false;
        }
        return true;
    }
    
    public void updateEmployeeDetails(Employee employee, String attribute, Object newValue) {

     boolean IsThereUpdate =   updateDetails(employee,attribute, newValue);
     if(!IsThereUpdate)
            return;
        try {
            saveEmployees(); // Save changes after updating an employee
        } catch (IOException e) {
            System.out.println("Error saving employee data.");
        }
        System.out.println("Employee details updated successfully!");

    }


    public void removeEmployee(int employeeId) {
        if (employees.remove(employeeId) != null) {
            System.out.println("Employee removed successfully!");
            try {
                saveEmployees(); // Save changes after removing an employee
            } catch (IOException e) {
                System.out.println("Error saving employee data.");
            }
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