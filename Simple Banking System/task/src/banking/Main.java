package banking;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Scanner;

public class Main {
    static String url = "jdbc:sqlite:card.s3db";
    static SQLiteDataSource dataSource = new SQLiteDataSource();
    static Scanner sc = new Scanner(System.in);
    static String cardNumber = "";
    static private boolean isLogIn = false;

    public static void main(String[] args) {

        int input;
        createTable();
        do {
            printAccMenu();
            input = sc.nextInt();
            System.out.println();

            if (isLogIn) {
                switch (input) {
                    case 0:
                        //end program
                        break;
                    case 1:
                        System.out.println("Balance: " + checkAccBalance() + "\n");
                        break;
                    case 2:
                        addAccIncome();
                        break;
                    case 3:
                        doAccTransfer();
                        break;
                    case 4:
                        closeAccount();
                        break;
                    case 5:
                        logOutAcc();
                        break;
                    default:
                        System.out.println("Invalid option");
                        break;
                }

            } else {
                switch (input) {
                    case 0:
                        //end program
                        break;
                    case 1:
                        createNewAccount();
                        break;
                    case 2:
                        logInAcc();
                        break;
                    default:
                        System.out.println("Invalid option");
                        break;
                }
            }
        } while (input != 0);
        System.out.println("Bye!");
        isLogIn = false;
    }
    public static void createTable() {
        dataSource.setUrl(url);

        try (Connection con = dataSource.getConnection()) {
            String createTable = "CREATE TABLE IF NOT EXISTS card(" +
                    "id INTEGER PRIMARY KEY," +
                    "number TEXT," +
                    "pin TEXT," +
                    "balance INTEGER DEFAULT 0)";

            try (PreparedStatement ps = con.prepareStatement(createTable)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void printAccMenu() {
        if (isLogIn) {
            System.out.println("1. Balance\n" +
                    "2. Add income\n" +
                    "3. Do transfer\n" +
                    "4. Close account\n" +
                    "5. Log out\n" +
                    "0. Exit\n");
        } else {
            System.out.println("1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit\n");
        }
    }
    public static void createNewAccount() {
        Account newAccount = new Account();
        String number = newAccount.getCardNumber();
        String pin = newAccount.getPin();

        try (Connection con = dataSource.getConnection()) {
            con.setAutoCommit(false);
            String insert = "INSERT INTO card (number, pin) VALUES (?, ?)";

            Savepoint savepoint = con.setSavepoint();
            try (PreparedStatement ps = con.prepareStatement(insert)) {
                ps.setString(1, number);
                ps.setString(2, pin);
                ps.executeUpdate();

                con.commit();
            } catch (SQLException e) {
                con.rollback(savepoint);
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(number);
        System.out.println("Your card PIN:");
        System.out.println(pin);
        System.out.println();
    }
    public static void logInAcc() {
        String expectedPin = "";

        System.out.println("Enter your card number:");
        String enteredCardNumber = sc.next();
        System.out.println("Enter your PIN:");
        String enteredPin = sc.next();
        System.out.println();

        try (Connection con = dataSource.getConnection()) {
            String select = "SELECT pin FROM card WHERE number = ?";

            try (PreparedStatement preparedStatement = con.prepareStatement(select)) {
                preparedStatement.setString(1, enteredCardNumber);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    expectedPin = resultSet.getString("pin");

                    if (expectedPin.equals(enteredPin)) {
                        System.out.println("You have successfully logged in!\n");
                        cardNumber = enteredCardNumber;
                        isLogIn = true;
                        return;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Wrong card number or PIN!\n");
        isLogIn = false;
    }
    public static void logOutAcc() {
        System.out.println("You have successfully logged out!");
        cardNumber = "";
        isLogIn = false;
    }
    public static int checkAccBalance() {
        return checkAccBalance(cardNumber);
    }
    public static int checkAccBalance(String numberOfAccount) {

        if (!numberOfAccount.isEmpty()) {
            String select = "SELECT balance FROM card WHERE number = ?";
            try (Connection con = dataSource.getConnection();
                 PreparedStatement preparedStatement = con.prepareStatement(select)) {

                preparedStatement.setString(1, numberOfAccount);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int accountBalance = resultSet.getInt("accountBalance");
                    return accountBalance;
                }
                return 0;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static void addAccIncome() {

        if (isLogIn) {
            System.out.println("Enter income:");
            int income = sc.nextInt();
            System.out.println();

            int balance = checkAccBalance();
            String update = "UPDATE card SET balance = ? WHERE number LIKE ?";
            try (Connection con = dataSource.getConnection()) {
                con.setAutoCommit(false);
                try (PreparedStatement preparedStatement = con.prepareStatement(update)) {
                    preparedStatement.setInt(1, balance + income);
                    preparedStatement.setString(2, cardNumber);
                    preparedStatement.executeUpdate();

                    con.commit();
                    System.out.println("Income was added!\n");

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("You are not logged into an account");
        }
    }
    public static void doAccTransfer() {
        String update = "UPDATE card SET balance = ? WHERE number LIKE ?";
        String transfer = "UPDATE card SET balance = ? WHERE number LIKE ?";

        System.out.println("Enter card number:");
        String receiver = sc.next();

        String tempNum = receiver.substring(0, receiver.length() - 1);
        String i = Account.luhnAlgorithm(tempNum);

        char charAt = i.charAt(0);

        if (charAt != receiver.charAt(receiver.length() - 1)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!\n");
            return;
        }

        if (!accountExists(receiver)) {
            System.out.println("Such a card does not exist.\n");
            return;
        }

        if (cardNumber.equals(receiver)) {
            System.out.println("You can't transfer money to the same account!\n");
            return;
        }

        System.out.println("Enter how much money you want to transfer:");
        int amountToTransfer = sc.nextInt();
        System.out.println();

        int balanceOfSender = checkAccBalance();
        int balanceOfReceiver = checkAccBalance(receiver);

        if (amountToTransfer > balanceOfSender) {
            System.out.println("Not enough money!");
            return;
        }

        try (Connection con = dataSource.getConnection()) {
            con.setAutoCommit(false);
            Savepoint savepoint = con.setSavepoint();

            try (PreparedStatement withdrawMoney = con.prepareStatement(update)) {
                // withdraw from sender
                withdrawMoney.setInt(1, balanceOfSender - amountToTransfer);
                withdrawMoney.setString(2, cardNumber);
                withdrawMoney.executeUpdate();
                // transfer to receiver
                PreparedStatement transferMoney = con.prepareStatement(transfer);
                transferMoney.setInt(1, balanceOfReceiver + amountToTransfer);
                transferMoney.setString(2, receiver);
                transferMoney.executeUpdate();

                con.commit();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static boolean accountExists(String number) {
        try (Connection con = dataSource.getConnection()) {

            String select = "SELECT * FROM card WHERE number = ?";
            try (PreparedStatement preparedStatement = con.prepareStatement(select)) {
                preparedStatement.setString(1, number);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void closeAccount() {
        String delete = "DELETE FROM card WHERE number LIKE ?";

        if (isLogIn) {

            try (Connection con = dataSource.getConnection();
                 PreparedStatement preparedStatement = con.prepareStatement(delete)) {

                preparedStatement.setString(1, cardNumber);
                preparedStatement.executeUpdate();

                System.out.println("The account has been closed!\n");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("You are not logged into an account.\n");
        }
    }
}