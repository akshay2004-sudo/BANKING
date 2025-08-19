package BANKING;// A simple banking application with basic functionalities, prepared for a school project.

// A simple banking application with basic functionalities, prepared for a school project.

import java.util.*;

/**
 * This class represents a simple, command-line based banking application.
 * It's designed to demonstrate basic concepts like user input, conditional logic,
 * and simple transaction handling for a class project.
 * It's not production-ready, but it gets the job done for the assignment.
 */
public class SimpleBankingApp {

    // Now, instead of hardcoding a single account, we'll use a Map to store multiple accounts.
    // The keys are account numbers, and the values are their passwords.
    private static Map<String, String> userCredentials = new HashMap<>();
    private static Map<String, Double> userBalances = new HashMap<>();
    
    // Let's pre-populate with an initial account to make testing easier.
    static {
        userCredentials.put("1234567890", "mysecretpassword");
        userBalances.put("1234567890", 1000.00);
    }
    
    private static String loggedInAccountNo; // To keep track of the current user.

    // Let's keep a record of transactions, just to be thorough.
    private static StringBuilder transactionLog = new StringBuilder();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Indian Bank Self-Service Portal!");

        // The initial menu now has a login and a create account option.
        showInitialMenu(scanner);

        scanner.close();
    }

    /**
     * Shows the initial menu for logging in or creating a new account.
     * @param scanner The Scanner object for user input.
     */
    private static void showInitialMenu(Scanner scanner) {
        while (loggedInAccountNo == null) {
            System.out.println("\n-- Choose an option --");
            System.out.println("1) Login");
            System.out.println("2) Create a New Account");
            System.out.println("3) Exit");
            System.out.print("Please enter your choice (1-3): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    verifyCredentials(scanner);
                    if (loggedInAccountNo != null) {
                        System.out.println("\nLogin successful. What would you like to do today?");
                        showMainMenu(scanner);
                    } else {
                        System.out.println("\nInvalid credentials. Please try again.");
                    }
                    break;
                case "2":
                    createNewAccount(scanner);
                    break;
                case "3":
                    System.out.println("Exiting application. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 3.");
            }
        }
    }

    /**
     * Handles the user login process by checking against the stored accounts.
     * @param scanner The Scanner object for user input.
     */
    private static void verifyCredentials(Scanner scanner) {
        System.out.print("Enter your Bank Account Number: ");
        String enteredAccountNo = scanner.nextLine();

        System.out.print("Enter your Password: ");
        String enteredPassword = scanner.nextLine();

        // Check if the entered account number exists and if the password matches.
        if (userCredentials.containsKey(enteredAccountNo) && userCredentials.get(enteredAccountNo).equals(enteredPassword)) {
            loggedInAccountNo = enteredAccountNo; // Set the current user.
        } else {
            loggedInAccountNo = null; // No one is logged in.
        }
    }

    /**
     * Prompts the user to create a new account and stores the details.
     * This is a simple version, just for the assignment.
     * @param scanner The Scanner object for user input.
     */
    private static void createNewAccount(Scanner scanner) {
        System.out.println("\n-- Create New Account --");
        System.out.print("Enter a new Bank Account Number (e.g., a 10-digit number): ");
        String newAccountNo = scanner.nextLine();

        // Let's do a quick check to see if the account number is already taken.
        if (userCredentials.containsKey(newAccountNo)) {
            System.out.println("That account number already exists. Please choose a different one.");
            return;
        }

        System.out.print("Create a password for the new account: ");
        String newPassword = scanner.nextLine();

        // Add the new credentials and initial balance to our maps.
        userCredentials.put(newAccountNo, newPassword);
        userBalances.put(newAccountNo, 0.00); // New accounts start with a zero balance.
        System.out.println("Account created successfully! You can now log in with account number: " + newAccountNo);
    }

    /**
     * Displays the main menu and handles user choices after a successful login.
     * @param scanner The Scanner object for user input.
     */
    private static void showMainMenu(Scanner scanner) {
        // Keep the menu running until the user decides to log out.
        while (loggedInAccountNo != null) {
            System.out.println("\n-- Main Menu --");
            System.out.println("1) Deposit");
            System.out.println("2) Withdraw");
            System.out.println("3) Transfer");
            System.out.println("4) Check Balance"); // Added this for convenience
            System.out.println("5) Logout");

            System.out.print("Please enter your choice (1-5): ");
            String choice = scanner.nextLine();

            // Using a switch statement here to make the code cleaner than a bunch of if-else.
            switch (choice) {
                case "1":
                    handleDeposit(scanner);
                    break;
                case "2":
                    handleWithdrawal(scanner);
                    break;
                case "3":
                    handleTransfer(scanner);
                    break;
                case "4":
                    double currentBalance = userBalances.get(loggedInAccountNo);
                    System.out.printf("Your current balance is: $%.2f%n", currentBalance);
                    break;
                case "5":
                    System.out.println("Thank you for using our service. Logging out...");
                    System.out.println("\n--- Transaction Log ---");
                    System.out.println(transactionLog.toString());
                    loggedInAccountNo = null; // This will break the loop and go back to the initial menu.
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                    break;
            }
        }
    }

    /**
     * Handles a deposit operation.
     * @param scanner The Scanner object.
     */
    private static void handleDeposit(Scanner scanner) {
        System.out.print("Enter amount to deposit: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            if (amount > 0) {
                double currentBalance = userBalances.get(loggedInAccountNo);
                currentBalance += amount;
                userBalances.put(loggedInAccountNo, currentBalance);
                System.out.printf("Successfully deposited $%.2f. New balance is $%.2f.%n", amount, currentBalance);
                transactionLog.append("Deposited $").append(String.format("%.2f", amount)).append("\n");
            } else {
                System.out.println("Deposit amount must be positive. No action taken.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        }
    }

    /**
     * Handles a withdrawal operation, with balance verification.
     * @param scanner The Scanner object.
     */
    private static void handleWithdrawal(Scanner scanner) {
        System.out.print("Enter amount to withdraw: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            double currentBalance = userBalances.get(loggedInAccountNo);
            // Amount verification condition as per the problem statement.
            if (amount > 0 && amount <= currentBalance) {
                currentBalance -= amount;
                userBalances.put(loggedInAccountNo, currentBalance);
                System.out.printf("Successfully withdrew $%.2f. New balance is $%.2f.%n", amount, currentBalance);
                transactionLog.append("Withdrew $").append(String.format("%.2f", amount)).append("\n");
            } else if (amount > currentBalance) {
                System.out.println("Insufficient funds. You cannot withdraw more than your balance.");
            } else {
                System.out.println("Withdrawal amount must be positive. No action taken.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        }
    }

    /**
     * Handles a money transfer operation, including OTP verification.
     * @param scanner The Scanner object.
     */
    private static void handleTransfer(Scanner scanner) {
        System.out.print("Enter recipient's Bank Account Number: ");
        String recipientAccount = scanner.nextLine(); 

        // Let's add a check to make sure the recipient account exists.
        if (!userBalances.containsKey(recipientAccount)) {
            System.out.println("Recipient account not found. Transfer cancelled.");
            return;
        }

        System.out.print("Enter amount to transfer: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            double currentBalance = userBalances.get(loggedInAccountNo);

            if (amount > 0 && amount <= currentBalance) {
                // Now, for the OTP part!
                int generatedOtp = generateOTP();
                System.out.println("An OTP has been generated: " + generatedOtp);
                System.out.print("Please enter the OTP to confirm the transfer: ");
                int enteredOtp = Integer.parseInt(scanner.nextLine());

                // Let's verify the OTP.
                if (enteredOtp == generatedOtp) {
                    currentBalance -= amount;
                    userBalances.put(loggedInAccountNo, currentBalance);
                    
                    // Transfer the money to the recipient
                    double recipientBalance = userBalances.get(recipientAccount);
                    recipientBalance += amount;
                    userBalances.put(recipientAccount, recipientBalance);

                    System.out.printf("Transfer of $%.2f to account %s was successful. New balance is $%.2f.%n", amount, recipientAccount, currentBalance);
                    transactionLog.append("Transferred $").append(String.format("%.2f", amount)).append(" to account ").append(recipientAccount).append("\n");
                } else {
                    System.out.println("Invalid OTP. Transfer cancelled.");
                }
            } else if (amount > currentBalance) {
                System.out.println("Insufficient funds. You cannot transfer more than your balance.");
            } else {
                System.out.println("Transfer amount must be positive. No action taken.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount or OTP. Please enter a valid number.");
        }
    }

    /**
     * Generates a random 4-digit OTP.
     * This is just for demonstration purposes. A real system would have much more secure generation.
     * @return The 4-digit OTP.
     */
    private static int generateOTP() {
        // Human-like thought: Use Random class to generate a number.
        // It's a bit of a classic, simple approach.
        Random random = new Random();
        // Generates a number between 1000 (inclusive) and 10000 (exclusive)
        int otp = 1000 + random.nextInt(9000);
        return otp;
    }
}
