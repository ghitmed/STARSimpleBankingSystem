package banking;

public class AppInterface {

    public void printLoggedOutMenu() {
        System.out.println("\n1. Create an account\n" +
                "2. Log into an account\n" +
                "0. Exit");
    }

    public void printLoggedInMenu() {
        System.out.println("\n1. Balance\n" +
                "2. Add income\n" +
                "3. Do transfer\n" +
                "4. Close account\n" +
                "5. Log out\n" +
                "0. Exit");
    }

    void printAccountDetails(Account account) {
        System.out.println("\nYour card has been created\n" +
                "Your card number:\n" + account.getCardNumber() +
                "\nYour card PIN:\n" + account.getPin());
    }


    void promptCardNumber() {
        System.out.println("\nEnter your card number:");
    }

    void promptTransferCardNumber() {
        System.out.println("Enter card number:");
    }

    void promptPin() {
        System.out.println("Enter your PIN:");
    }

    void printLoginError() {
        System.out.println("\nWrong card number or PIN!");
    }

    void printSuccessfulLogin() {
        System.out.println("\nYou have successfully logged in!");
    }

    void printByeMessage() {
        System.out.println("\nBye!");
    }

    void printWrongOptionSelection() {
        System.out.println("Wrong selection!");
    }

    void printBalance(int balance) {
        System.out.println("\nBalance:\n" + balance);
    }

    void printLoggedOutMessage() {
        System.out.println("\nYou have successfully logged out!");
    }

    public void printClosedAccountMessage() {
        System.out.println("\nThe account has been closed!");
    }

    public void promptAddedIncome() {
        System.out.println("\nEnter income:");
    }

    public void announceTransfer() {
        System.out.println("\nTransfer");
    }

    public void invalidCardNumber() {
        System.out.println("Probably you made a mistake in the card number. Please try again!");
    }

    public void promptTransferAmount() {
        System.out.println("Enter how much money you want to transfer:");
    }

    public void sameAccountTransfer() {
        System.out.println("You can't transfer money to the same account!");
    }

    public void notEnoughMoney() {
        System.out.println("Not enough money!");
    }

    public void noSuchCard() {
        System.out.println("Such a card does not exist.");
    }
}