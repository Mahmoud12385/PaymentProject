import java.io.*;
import java.util.HashMap;
import java.util.Scanner;



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
    double totalSales;

    public CommissionedEmployee(int employeeId, String name, double commissionRate, double totalSales) {
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
            System.out.println("3. Calculate Total Payroll");
            System.out.println("4. Generate Payslips");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> addEmployeeInteraction();
                case 2 -> removeEmployeeInteraction();
                case 3 -> calculateTotalPayroll();
                case 4 -> generatePayslips();
                case 5 -> {
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
        System.out.print("Enter Employee ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter Employee Name: ");
        String name = scanner.nextLine();
        System.out.println("Select Employee Type: 1-Salaried, 2-Hourly, 3-Commissioned");
        int typeChoice = scanner.nextInt();

        switch (typeChoice) {
            case 1 -> {
                System.out.print("Enter Salary: ");
                double salary = scanner.nextDouble();
                addEmployee(new SalariedEmployee(id, name, salary));
            }
            case 2 -> {
                System.out.print("Enter Hourly Rate: ");
                double hourlyRate = scanner.nextDouble();
                System.out.print("Enter Hours Worked: ");
                int hoursWorked = scanner.nextInt();
                addEmployee(new HourlyEmployee(id, name, hourlyRate, hoursWorked));
            }
            case 3 -> {
                System.out.print("Enter Commission Rate (e.g., 0.1 for 10%): ");
                double commissionRate = scanner.nextDouble();
                System.out.print("Enter Total Sales: ");
                double totalSales = scanner.nextDouble();
                addEmployee(new CommissionedEmployee(id, name, commissionRate, totalSales));
            }
            default -> System.out.println("Invalid Employee Type!");
        }
    }

    public void removeEmployee(int employeeId) {
        if (employees.remove(employeeId) != null) {
            System.out.println("Employee removed successfully!");
        } else {
            System.out.println("Employee not found!");
        }
    }

    private void removeEmployeeInteraction() {
        System.out.print("Enter Employee ID to Remove: ");
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
}
