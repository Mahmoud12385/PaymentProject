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
    private int employeeId;
    private String name;
    private EmployeeType employeeType;
    private String paymentMethod;
    private String paymentDetails;
    private String taxInformation;

    public Employee(int employeeId, String name, EmployeeType employeeType) {
        this.employeeId = employeeId;
        this.name = name;
        this.employeeType = employeeType;
    }

   
    public int getEmployeeId() {// i donot need setter for id as it generated automataclly
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public String getTaxInformation() {
        return taxInformation;
    }

    // Setters
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public void setTaxInformation(String taxInformation) {
        this.taxInformation = taxInformation;
    }

    // Abstract methods
    abstract double calculatePay();

    abstract String generatePayStub();
}

class SalariedEmployee extends Employee {
    private double salary;

    public SalariedEmployee(int employeeId, String name, double salary) {
        super(employeeId, name, EmployeeType.SALARIED);
        this.salary = salary;
    }

    // Getter
    public double getSalary() {
        return salary;
    }

    // Setter
    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    double calculatePay() {
        return salary;
    }

    @Override
    String generatePayStub() {
        return "Pay Stub - Salaried Employee\n" +
                "Name: " + getName() + "\n" +
                "Employee ID: " + getEmployeeId() + "\n" +
                "Salary: $" + salary + "\n";
    }
}

class HourlyEmployee extends Employee {
    private double hourlyRate;
    private int hoursWorked;

    public HourlyEmployee(int employeeId, String name, double hourlyRate, int hoursWorked) {
        super(employeeId, name, EmployeeType.HOURLY);
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    // Getters
    public double getHourlyRate() {
        return hourlyRate;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    // Setters
    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    @Override
    double calculatePay() {
        return hourlyRate * hoursWorked;
    }

    @Override
    String generatePayStub() {
        return "Pay Stub - Hourly Employee\n" +
                "Name: " + getName() + "\n" +
                "Employee ID: " + getEmployeeId() + "\n" +
                "Hours Worked: " + hoursWorked + "\n" +
                "Hourly Rate: $" + hourlyRate + "\n" +
                "Total Pay: $" + calculatePay() + "\n";
    }
}

class CommissionedEmployee extends Employee {
    private double commissionRate;
    private int totalSales;

    public CommissionedEmployee(int employeeId, String name, double commissionRate, int totalSales) {
        super(employeeId, name, EmployeeType.COMMISSIONED);
        this.commissionRate = commissionRate;
        this.totalSales = totalSales;
    }

    // Getters
    public double getCommissionRate() {
        return commissionRate;
    }

    public int getTotalSales() {
        return totalSales;
    }

    // Setters
    public void setCommissionRate(double commissionRate) {
        this.commissionRate = commissionRate;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    @Override
    double calculatePay() {
        return commissionRate * totalSales;
    }

    @Override
    String generatePayStub() {
        return "Pay Stub - Commissioned Employee\n" +
                "Name: " + getName() + "\n" +
                "Employee ID: " + getEmployeeId() + "\n" +
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
        loadUserCredentials(); // Load user credentials from file at startup
        loadEmployees();
    }

    private void saveEmployees() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("employees.txt"))) {
            for (Employee employee : employees.values()) {
                String employeeData = employee.getEmployeeId() + "," + employee.getName() + "," + employee.getEmployeeType()
                        + "," + getEmployeeDetails(employee);
                writer.write(employeeData);
                writer.newLine();
            }
        }
    }

    private String getEmployeeDetails(Employee employee) {
        if (employee instanceof SalariedEmployee) {
            return String.valueOf(((SalariedEmployee) employee).getSalary());
        } else if (employee instanceof HourlyEmployee) {
            HourlyEmployee hourlyEmployee = (HourlyEmployee) employee;
            return hourlyEmployee.getHourlyRate() + "," + hourlyEmployee.getHoursWorked();
        } else if (employee instanceof CommissionedEmployee) {
            CommissionedEmployee commissionedEmployee = (CommissionedEmployee) employee;
            return commissionedEmployee.getCommissionRate() + "," + commissionedEmployee.getTotalSales();
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

    private void loadUserCredentials() throws IOException {
        File file = new File(credentialsFile);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    userCredentials.put(parts[0], parts[1]);
                }
            }
        }
    }

    private void saveUserCredentials() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(credentialsFile))) {
            for (var entry : userCredentials.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        }
    }

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

    public void start() throws IOException {
        System.out.println("Welcome to the Company Payroll System!");

        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> registerUser();
                case 2 -> {
                    if (loginUser()) {
                        menu();
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
            scanner.nextLine();

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

    public void addEmployee(Employee employee) {
        employees.put(employee.getEmployeeId(), employee);
        System.out.println("Employee added successfully!");
        try {
            saveEmployees();
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
        System.out.print("Enter Employee ID to Update: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Employee employee = employees.get(id);
        if (employee != null) {
            EmployeeType type = employee.getEmployeeType();
            switch (type) {
                case SALARIED -> {
                    SalariedEmployee salariedEmployee = (SalariedEmployee) employee;
                    System.out.printf("1) Name: %s\n2) Salary: %.2f\n", salariedEmployee.getName(), salariedEmployee.getSalary());
                    handleUpdate(employee);
                }
                case HOURLY -> {
                    HourlyEmployee hourlyEmployee = (HourlyEmployee) employee;
                    System.out.printf("1) Name: %s\n2) Hourly Rate: %.2f\n3) Hours Worked: %d\n", hourlyEmployee.getName(), hourlyEmployee.getHourlyRate(), hourlyEmployee.getHoursWorked());
                    handleUpdate(employee);
                }
                case COMMISSIONED -> {
                    CommissionedEmployee commissionedEmployee = (CommissionedEmployee) employee;
                    System.out.printf("1) Name: %s\n2) Commission Rate: %.2f\n3) Total Sales: %d\n", commissionedEmployee.getName(), commissionedEmployee.getCommissionRate(), commissionedEmployee.getTotalSales());
                    handleUpdate(employee);
                }
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

            System.out.println("ID: " + key + ", Name: " + value.getName()); // Accessing Employee properties
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