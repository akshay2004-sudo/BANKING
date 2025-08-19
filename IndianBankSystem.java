package A;

import java.io.*;
import java.util.*;
import java.util.Random;

// Class to represent a bank account
class BankAccount {
    String accountNo;
    String password;
    double balance;

    public BankAccount(String accountNo, String password, double balance) {
        this.accountNo = accountNo;
        this.password = password;
        this.balance = balance;
    }
}

// Main Banking System class
public class IndianBankSystem {
    // Hardcoded accounts (HashMap for storing accounts)
    private static HashMap<String, BankAccount> accounts = new HashMap<>();
    private static final String TRANSACTION_FILE = "transactions.txt";
    private static Scanner scanner = new Scanner(System.in);
    private static Random random = new Random();

    // Initialize some sample accounts
    static {
        accounts.put("1001", new BankAccount("1001", "pass123", 5000));
        accounts.put("1002", new BankAccount("1002", "hello", 10000));
    }

    // Function to create a new account
    public static void createAccount() {
        System.out.println("Creating a new account...");
        System.out.print("Enter a new bank account number: ");
        String accountNo = scanner.nextLine();
        
        if (accounts.containsKey(accountNo)) {
            System.out.println("Account number already exists! Try a different one.");
            return;
        }
        
        System.out.print("Enter a password for the account: ");
        String password = scanner.nextLine();
        accounts.put(accountNo, new BankAccount(accountNo, password, 0));
        System.out.println("Account " + accountNo + " created successfully with balance 0.");
    }

    // Function to verify credentials
    public static String login() {
        System.out.print("Enter your bank account number: ");
        String accountNo = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        BankAccount account = accounts.get(accountNo);
        if (account != null && account.password.equals(password)) {
            System.out.println("Login successful!");
            return accountNo;
        } else {
            System.out.println("Invalid credentials. Please try again.");
            return null;
        }
    }

    // Function to deposit money
    public static void deposit(String accountNo) {
        System.out.print("Enter amount to deposit: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
            return;
        }

        if (amount > 0) {
            BankAccount account = accounts.get(accountNo);
            account.balance += amount;
            logTransaction(accountNo, "Deposited " + amount);
            System.out.println("Deposited " + amount + ". New balance: " + account.balance);
        } else {
            System.out.println("Amount must be positive.");
        }
    }

    // Function to withdraw money
    public static void withdraw(String accountNo) {
        System.out.print("Enter amount to withdraw: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
            return;
        }

        BankAccount account = accounts.get(accountNo);
        if (amount > 0) {
            if (amount <= account.balance) {
                account.balance -= amount;
                logTransaction(accountNo, "Withdrew " + amount);
                System.out.println("Withdrew " + amount + ". New balance: " + account.balance);
            } else {
                System.out.println("Insufficient balance.");
            }
        } else {
            System.out.println("Amount must be positive.");
        }
    }

    // Function to transfer money
    public static void transfer(String accountNo) {
        System.out.print("Enter target bank account number: ");
        String targetAccount = scanner.nextLine();
        
        if (!accounts.containsKey(targetAccount)) {
            System.out.println("Target account does not exist.");
            return;
        }
        if (targetAccount.equals(accountNo)) {
            System.out.println("Cannot transfer to your own account.");
            return;
        }

        System.out.print("Enter amount to transfer: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
            return;
        }

        BankAccount sender = accounts.get(accountNo);
        if (amount > 0 && amount <= sender.balance) {
            // Generate OTP
            int otp = 1000 + random.nextInt(9000); // 4-digit OTP
            System.out.println("OTP generated: " + otp + " (In real system, this would be sent via SMS)");
            System.out.print("Enter the OTP to verify: ");
            int userOtp;
            try {
                userOtp = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid OTP format.");
                return;
            }

            if (userOtp == otp) {
                sender.balance -= amount;
                accounts.get(targetAccount).balance += amount;
                logTransaction(accountNo, "Transferred " + amount + " to " + targetAccount);
                System.out.println("Transferred " + amount + " to " + targetAccount + ". Your new balance: " + sender.balance);
            } else {
                System.out.println("Invalid OTP. Transfer cancelled.");
            }
        } else {
            System.out.println("Invalid amount or insufficient balance.");
        }
    }

    // Function to log transactions
    public static void logTransaction(String accountNo, String message) {
        try (FileWriter fw = new FileWriter(TRANSACTION_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("Account " + accountNo + ": " + message + ". Balance: " + accounts.get(accountNo).balance);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to transaction file: " + e.getMessage());
        }
    }

    // Main program
    public static void main(String[] args) {
        while (true) {
            System.out.println("\nWelcome to Indian Bank Banking System");
            System.out.println("1. Create New Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice (1/2/3): ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                createAccount();
            } else if (choice.equals("2")) {
                String accountNo = login();
                if (accountNo != null) {
                    while (true) {
                        System.out.println("\nOptions:");
                        System.out.println("1. Deposit");
                        System.out.println("2. Withdraw");
                        System.out.println("3. Transfer");
                        System.out.println("4. Logout");
                        System.out.print("Enter option (1/2/3/4): ");
                        String option = scanner.nextLine();

                        if (option.equals("1")) {
                            deposit(accountNo);
                        } else if (option.equals("2")) {
                            withdraw(accountNo);
                        } else if (option.equals("3")) {
                            transfer(accountNo);
                        } else if (option.equals("4")) {
                            System.out.println("Logged out.");
                            break;
                        } else {
                            System.out.println("Invalid option.");
                        }
                    }
                }
            } else if (choice.equals("3")) {
                System.out.println("Exiting the system. Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
        scanner.close();
    }
}