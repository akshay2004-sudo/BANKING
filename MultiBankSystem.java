
package BANKING;// We're going to put everything in a single file for this assignment, as requested.
// The code is organized to show a clear thought process:
// 1. A class to represent a single bank account.
// 2. A class to represent an entire bank, managing its accounts and transactions.
// 3. The main class that ties it all together, allowing the user to select a bank.

import java.io.*;
import java.util.*;
import java.util.Random;

/**
 * A class to represent a single bank account.
 * This holds the basic data for an account: its number, password, and balance.
 */
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

/**
 * A class to represent a single bank.
 * This bank manages its own collection of accounts and keeps a log of transactions in a dedicated file.
 * This structure makes it easy to add more banks later on.
 */
class Bank {
    private String bankName;
    private HashMap<String, BankAccount> accounts = new HashMap<>();
    private String transactionFileName;
    private Scanner scanner;
    private Random random = new Random();

    public Bank(String bankName, String transactionFileName, Scanner scanner) {
        this.bankName = bankName;
        this.transactionFileName = transactionFileName;
        this.scanner = scanner;
    }

    // A helper method to create a new account within this specific bank.
    public void createAccount() {
        System.out.println("Creating a new account at " + bankName + "...");
        System.out.print("Enter a new bank account number: ");
        String accountNo = scanner.nextLine();
        
        if (accounts.containsKey(accountNo)) {
            System.out.println("Account number already exists at " + bankName + "! Try a different one.");
            return;
        }
        
        System.out.print("Enter a password for the account: ");
        String password = scanner.nextLine();
        accounts.put(accountNo, new BankAccount(accountNo, password, 0));
        logTransaction(accountNo, "New account created with 0 balance.");
        System.out.println("Account " + accountNo + " created successfully at " + bankName + ".");
    }

    // A method to handle the login process for this bank.
    public String login() {
        System.out.print("Enter your bank account number: ");
        String accountNo = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        BankAccount account = accounts.get(accountNo);
        if (account != null && account.password.equals(password)) {
            System.out.println("Login successful at " + bankName + "!");
            return accountNo;
        } else {
            System.out.println("Invalid credentials. Please try again.");
            return null;
        }
    }

    // Handles the deposit operation for a given account.
    public void deposit(String accountNo) {
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

    // Handles the withdrawal operation.
    public void withdraw(String accountNo) {
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

    // Handles the transfer operation, including the OTP step.
    public void transfer(String accountNo) {
        System.out.print("Enter target bank account number: ");
        String targetAccount = scanner.nextLine();
        
        if (!accounts.containsKey(targetAccount)) {
            System.out.println("Target account does not exist within this bank.");
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
            // A simple OTP generation.
            int otp = 1000 + random.nextInt(9000); 
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

    // Logs the transaction to a file. This ensures persistence of the "past details".
    private void logTransaction(String accountNo, String message) {
        try (FileWriter fw = new FileWriter(transactionFileName, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("Account " + accountNo + ": " + message + ". New balance: " + accounts.get(accountNo).balance + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to transaction file for " + bankName + ": " + e.getMessage());
        }
    }

    // Initial setup for the bank. You can add more accounts here if needed.
    public void setupInitialAccounts() {
        if (bankName.equals("Indian Bank")) {
            accounts.put("1001", new BankAccount("1001", "pass123", 5000));
            accounts.put("1002", new BankAccount("1002", "hello", 10000));
        } else if (bankName.equals("Global Bank")) {
            accounts.put("2001", new BankAccount("2001", "secret", 2500));
            accounts.put("2002", new BankAccount("2002", "world", 7500));
        }
    }

    public String getBankName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBankName'");
    }
}

/**
 * The main program that runs the entire banking system.
 * It's now a hub that lets you choose which bank to use.
 */
public class MultiBankSystem {
    private static HashMap<String, Bank> banks = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Create instances for each bank. This is how we "attach" to another bank.
        // Each bank manages its own data and files.
        banks.put("Indian Bank", new Bank("Indian Bank", "indian_transactions.txt", scanner));
        banks.put("Global Bank", new Bank("Global Bank", "global_transactions.txt", scanner));

        // Let's set up the initial accounts for our banks.
        banks.get("Indian Bank").setupInitialAccounts();
        banks.get("Global Bank").setupInitialAccounts();
        
        while (true) {
            System.out.println("\n--- Welcome to the Multi-Bank System ---");
            System.out.println("Available Banks:");
            System.out.println("1. Indian Bank");
            System.out.println("2. Global Bank");
            System.out.println("3. Exit System");
            System.out.print("Please select a bank (1-2) or exit (3): ");
            String bankChoice = scanner.nextLine();

            Bank selectedBank = null;
            if (bankChoice.equals("1")) {
                selectedBank = banks.get("Indian Bank");
            } else if (bankChoice.equals("2")) {
                selectedBank = banks.get("Global Bank");
            } else if (bankChoice.equals("3")) {
                System.out.println("Exiting the system. Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
                continue;
            }

            // Now, we let the selected bank handle the user's session.
            handleBankSession(selectedBank);
        }
        scanner.close();
    }

    /**
     * This method handles all the operations for a specific bank after it has been selected.
     * @param bank The selected Bank object.
     */
    private static void handleBankSession(Bank bank) {
        while (true) {
            System.out.println("\n--- Welcome to " + bank.getBankName() + " ---");
            System.out.println("1. Create New Account");
            System.out.println("2. Login");
            System.out.println("3. Go back to main menu");
            System.out.print("Enter your choice (1/2/3): ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                bank.createAccount();
            } else if (choice.equals("2")) {
                String accountNo = bank.login();
                if (accountNo != null) {
                    // Once logged in, show the transaction menu.
                    showTransactionMenu(bank, accountNo);
                }
            } else if (choice.equals("3")) {
                System.out.println("Returning to the main menu...");
                return; // Exit this loop and go back to the main menu.
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Displays the options for a logged-in user and handles their actions.
     * @param bank The current Bank object.
     * @param accountNo The account number of the logged-in user.
     */
    private static void showTransactionMenu(Bank bank, String accountNo) {
        while (true) {
            System.out.println("\nOptions for account " + accountNo + ":");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. Logout");
            System.out.print("Enter option (1/2/3/4): ");
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    bank.deposit(accountNo);
                    break;
                case "2":
                    bank.withdraw(accountNo);
                    break;
                case "3":
                    bank.transfer(accountNo);
                    break;
                case "4":
                    System.out.println("Logged out from " + bank.getBankName() + ".");
                    return; // Exit this loop.
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }
}
