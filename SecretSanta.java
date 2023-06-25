import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Employee {
    private String name;
    private String email;

    public Employee(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

class SecretSantaAssignment {
    private Employee giver;
    private Employee receiver;

    public SecretSantaAssignment(Employee giver, Employee receiver) {
        this.giver = giver;
        this.receiver = receiver;
    }

    public Employee getGiver() {
        return giver;
    }

    public Employee getReceiver() {
        return receiver;
    }
}

public class SecretSanta {

    private static List<Employee> parseEmployeeList(String filePath) {
        List<Employee> employeeList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                String name = parts[0].trim();
                String email = parts[1].trim();
                employeeList.add(new Employee(name, email));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return employeeList;
    }

  private static List<SecretSantaAssignment> parsePreviousAssignments(String filePath, List<Employee> employees) {
    List<SecretSantaAssignment> previousAssignments = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        boolean isFirstLine = true;

        while ((line = reader.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine = false;
                continue;
            }

            String[] parts = line.split(",");
            String giverName = parts[0].trim();
        //  String giverEmail = parts[1].trim();
            String receiverName = parts[1].trim();
        //  String receiverEmail = parts[3].trim();

            Employee giver = findEmployeeByName(giverName, employees);
            Employee receiver = findEmployeeByName(receiverName, employees);

            if (giver != null && receiver != null) {
                previousAssignments.add(new SecretSantaAssignment(giver, receiver));
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    return previousAssignments;
}

private static Employee findEmployeeByName(String name, List<Employee> employees) {
    for (Employee employee : employees) {
        if (employee.getName().equals(name)) {
            return employee;
        }
    }
    return null;
}

    private static List<SecretSantaAssignment> generateSecretSantaAssignments(List<Employee> employees,
                                                                             List<SecretSantaAssignment> previousAssignments) {
        List<SecretSantaAssignment> assignments = new ArrayList<>();
        List<Employee> availableEmployees = new ArrayList<>(employees);

        Random random = new Random();

        for (Employee giver : employees) {
            Employee receiver = null;

            do {
                // Select a random receiver from the available employees
                int index = random.nextInt(availableEmployees.size());
                receiver = availableEmployees.get(index);

                // Check if the receiver is not the same as the giver and not assigned in the previous year
            } while (receiver.equals(giver) || isAssignedInPreviousYear(previousAssignments, giver, receiver));

            assignments.add(new SecretSantaAssignment(giver, receiver));
            availableEmployees.remove(receiver);
        }

        return assignments;
    }

    private static boolean isAssignedInPreviousYear(List<SecretSantaAssignment> previousAssignments,
                                                    Employee giver, Employee receiver) {
        for (SecretSantaAssignment assignment : previousAssignments) {
            if (assignment.getGiver().equals(giver) && assignment.getReceiver().equals(receiver)) {
                return true;
            }
        }
        return false;
    }

    private static void writeSecretSantaAssignmentsToFile(List<SecretSantaAssignment> assignments, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Employee_Name,Employee_EmailID,Secret_Child_Name,Secret_Child_EmailID\n");

            for (SecretSantaAssignment assignment : assignments) {
                String giverName = assignment.getGiver().getName();
                String giverEmail = assignment.getGiver().getEmail();
                String receiverName = assignment.getReceiver().getName();
                String receiverEmail = assignment.getReceiver().getEmail();

                writer.write(giverName + "," + giverEmail + "," + receiverName + "," + receiverEmail + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   



    public static void main(String[] args) {
        String employeeListFilePath = "employee_list.csv";
        String previousAssignmentsFilePath = "previous_assignments.csv";
        String outputFilePath = "secret_santa_assignments.csv";

        // Parse employee list from the CSV file
        List<Employee> employees = parseEmployeeList(employeeListFilePath);

        // Parse previous year's assignments from the CSV file
        List<SecretSantaAssignment> previousAssignments = parsePreviousAssignments(previousAssignmentsFilePath, employees);

        // Generate new secret Santa assignments
        List<SecretSantaAssignment> newAssignments = generateSecretSantaAssignments(employees, previousAssignments);

        // Write the new assignments to the output CSV file
        writeSecretSantaAssignmentsToFile(newAssignments, outputFilePath);
    }
}

