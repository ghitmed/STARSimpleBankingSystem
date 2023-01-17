package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class DAOAccount {

    private AppInterface AppInterface;
    private DataBase DataBase;

    public DAOAccount() {
        this.DataBase = new DataBase();
        this.AppInterface = new AppInterface();
    }

    public void work(String[] args) {
        DataBase.setDataBaseName(DataBase.getDefaultDbName());
        for (int i = 0; i < args.length; i++) {
            try {
                if (args[i].equals("-fileName")) {
                    DataBase.setDataBaseName(args[i+1]);
                    i++;
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Wrong arguments passed");
            }
        }
        DataBase.setPathToDataBase(DataBase.getDefaultDbPathRoot() + DataBase.getDataBaseName());
        this.createDatabase();
        do {
            this.promptSelection();
            this.operateBasedOnSelection();
        }
        while (DataBase.getOptionSelection() != 0);
    }

    private void createDatabase() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(DataBase.getPathToDataBase());
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                String queryText = "CREATE TABLE IF NOT EXISTS card (id INTEGER PRIMARY KEY, number TEXT, pin TEXT, balance INTEGER DEFAULT 0);";
                statement.executeUpdate(queryText);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void promptSelection() {
        if (DataBase.getCurrentAccountIndex() == null) {
            AppInterface.printLoggedOutMenu();
        } else {
            AppInterface.printLoggedInMenu();
        }
        Scanner scanner = new Scanner(System.in);
        DataBase.setOptionSelection(scanner.nextInt());
    }

    private void operateBasedOnSelection() {
        if (DataBase.getCurrentAccountIndex() == null) {
            switch (DataBase.getOptionSelection()) {
                case 1:
                    Account newAccount = this.generateNewAccount();
                    AppInterface.printAccountDetails(newAccount);
                    this.addAccount(newAccount);
                    break;
                case 2:
                    this.logIn();
                    break;
                case 0:
                    AppInterface.printByeMessage();
                    break;
                default:
                    AppInterface.printWrongOptionSelection();
            }
        }
        else {
            switch (DataBase.getOptionSelection()) {
                case 1:
                    AppInterface.printBalance(this.getBalanceById(DataBase.getCurrentAccountIndex()));
                    break;
                case 2:
                    this.addIncome();
                    break;
                case 3:
                    this.transfer();
                    break;
                case 4:
                    this.closeAccount();
                    break;
                case 5:
                    this.logOut();
                    break;
                case 0:
                    AppInterface.printByeMessage();
                    break;
                default:
                    AppInterface.printWrongOptionSelection();
            }
        }
    }

    private Account generateNewAccount() {
        Random random = new Random();
        String accountId = this.appendZeroes(String.valueOf(random.nextInt(1_000_000_000)), 9);
        String accountPin = this.appendZeroes(String.valueOf(random.nextInt(10000)), 4);
        String checkSum = this.getCheckSum(DataBase.getBIN() + accountId);
        String accountCardNumber = DataBase.getBIN() + accountId + checkSum;
        return new Account(accountCardNumber, accountPin);
    }

    private void addAccount(Account account) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(DataBase.getPathToDataBase());

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                String queryText = "INSERT INTO card(number, pin) VALUES('"+account.getCardNumber() + "', '" + account.getPin() + "')";
                statement.executeUpdate(queryText);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private String appendZeroes(String string, int length) {
        StringBuilder stringBuilder = new StringBuilder(string);
        while (stringBuilder.length() != length) {
            stringBuilder.insert(0, "0");
        }
        return stringBuilder.toString();
    }

    private String getCheckSum(String cardNumber) {
        int digitsSum = 0;
        String checkSum;
        int currentNumber;

        for (int i = 0; i < cardNumber.length(); i++) {
            currentNumber = Character.getNumericValue(cardNumber.charAt(i));
            currentNumber *= i % 2 == 0 ? 2 : 1;
            currentNumber -= currentNumber > 9 ? 9 : 0;
            digitsSum += currentNumber;
        }
        checkSum = String.valueOf((10 - (digitsSum % 10)) % 10);
        return checkSum;
    }

    private void logIn() {
        Scanner scanner = new Scanner(System.in);
        AppInterface.promptCardNumber();
        String inputCardNumber = scanner.nextLine();
        AppInterface.promptPin();
        String inputPin = scanner.nextLine();

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(DataBase.getPathToDataBase());
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                String queryText = "SELECT id, number, pin from card;";
                try (ResultSet credentials = statement.executeQuery(queryText)) {
                    while (credentials.next()) {
                        Integer id = credentials.getInt("id");
                        String cardNumber = credentials.getString("number");
                        String pin = credentials.getString("pin");

                        if (inputCardNumber.equals(cardNumber) && inputPin.equals(pin)) {
                            DataBase.setCurrentAccountIndex(id);
                            AppInterface.printSuccessfulLogin();
                            return;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        AppInterface.printLoginError();
    }

    private void logOut() {
        DataBase.setCurrentAccountIndex(null);
        AppInterface.printLoggedOutMessage();
    }

    private void closeAccount() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(DataBase.getPathToDataBase());
        try (Connection connection = dataSource.getConnection()) {
            String deleteAccount = "DELETE FROM card WHERE id = " + DataBase.getCurrentAccountIndex();
            try (Statement deleteStatement = connection.createStatement()) {
                deleteStatement.executeUpdate(deleteAccount);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DataBase.setCurrentAccountIndex(null);
        AppInterface.printClosedAccountMessage();
    }

    private void addIncome() {
        Scanner scanner = new Scanner(System.in);
        AppInterface.promptAddedIncome();
        int addedIncome = scanner.nextInt();
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(DataBase.getPathToDataBase());
        try (Connection connection = dataSource.getConnection()) {
            String updateBalance = "UPDATE card set balance = balance + " + addedIncome + " where id = " + DataBase.getCurrentAccountIndex();
            try (Statement addIncomeStatement = connection.createStatement()) {
                addIncomeStatement.executeUpdate(updateBalance);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void transfer() {
        AppInterface.announceTransfer();
        AppInterface.promptTransferCardNumber();
        Scanner scanner = new Scanner(System.in);
        String numberToSendTo = scanner.nextLine();
        if (!validateCardNumber(numberToSendTo)) {
            AppInterface.invalidCardNumber();
            return;
        }
        if (numberToSendTo.equals(this.getCardNumberById(DataBase.getCurrentAccountIndex()))) {
            AppInterface.sameAccountTransfer();
            return;
        }

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(DataBase.getPathToDataBase());
        try (Connection connection = dataSource.getConnection()) {
            String selectCard = "SELECT * FROM card where number = ?";
            try (PreparedStatement selectCardStatement = connection.prepareStatement(selectCard)) {
                selectCardStatement.setString(1, numberToSendTo);
                ResultSet resultSet = selectCardStatement.executeQuery();
                if (!resultSet.next()) {
                    AppInterface.noSuchCard();
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        AppInterface.promptTransferAmount();
        int transferAmount = scanner.nextInt();
        if (this.getBalanceById(DataBase.getCurrentAccountIndex()) < transferAmount) {
            AppInterface.notEnoughMoney();
            return;
        }

        try (Connection connection = dataSource.getConnection()) {
            String updateBalance = "UPDATE card set balance = balance + ? where id = ?";
            try (PreparedStatement updateBalanceStatement = connection.prepareStatement(updateBalance)) {
                updateBalanceStatement.setInt(1, transferAmount * -1);
                updateBalanceStatement.setInt(2, DataBase.getCurrentAccountIndex());
                updateBalanceStatement.executeUpdate();

                updateBalanceStatement.setInt(1, transferAmount);
                updateBalanceStatement.setInt(2, this.getIdByCardNumber(numberToSendTo));
                updateBalanceStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean validateCardNumber(String cardNumber) {
        return String.valueOf(cardNumber.charAt(cardNumber.length() - 1)).equals(this.getCheckSum(cardNumber.substring(0, cardNumber.length()-1)));
    }

    private Integer getIdByCardNumber(String cardNumber) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(DataBase.getPathToDataBase());
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                String dbQuery = "SELECT id FROM card WHERE number = " + cardNumber;
                try (ResultSet resultSet = statement.executeQuery(dbQuery)) {
                    return resultSet.getInt(1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getCardNumberById(Integer id) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(DataBase.getPathToDataBase());
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                String queryText = "SELECT number FROM card WHERE id = " + id;
                try (ResultSet resultSet = statement.executeQuery(queryText)) {
                    return resultSet.getString(1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getBalanceById(Integer id) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(DataBase.getPathToDataBase());
        try (Connection connection = dataSource.getConnection()) {
            String selectBalance = "SELECT balance from card where id = " + id;
            try (Statement selectBalanceStatement = connection.createStatement()) {
                try (ResultSet balance = selectBalanceStatement.executeQuery(selectBalance)) {
                    return balance.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}