package banking;

public class Account {

    private String cardNumber;
    private String pin;
    private int balance;

    public Account(String cardNumber, String pin, int balance) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = 0;
    }

    public Account(String cardNumber, String pin) {
        this(cardNumber, pin, 0);
    }

    public int getBalance() {
        return balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPin() {
        return pin;
    }

    public void addToBalance(int addedAmount) {
        this.balance += addedAmount;
    }
}