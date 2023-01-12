package banking;

public class Account {
    String cardNumber;
    String pin;

    public Account() {
        this.cardNumber = generateCardNumber();
        this.pin = generatePIN();
    }

    public String generateCardNumber() {
        cardNumber = "400000";

        for (int i = 0; i < 9; i++) {
            cardNumber += String.valueOf((int) (Math.random()*10));
        }
        cardNumber += luhnAlgorithm(cardNumber);
        return cardNumber;
    }

    public static String luhnAlgorithm(String strNumber) {
        int[] numberArray = new int[strNumber.length()];
        int sum = 0;

        for (int i = 0; i < strNumber.length(); i++) {
            numberArray[i] = Integer.parseInt(String.valueOf(strNumber.charAt(i)));
        }

        for (int i = 0; i < numberArray.length; i += 2) {
            numberArray[i] *= 2;
        }

        for (int i = 0; i < numberArray.length; i++) {
            if (numberArray[i] > 9) {
                numberArray[i] -= 9;
            }
        }

        for (int i = 0; i < numberArray.length; i++) {
            sum += numberArray[i];
        }

        if (sum % 10 == 0) {
            return "0";
        }

        int result = 10 - (sum % 10);
        return String.valueOf(result);
    }


    public String getCardNumber() {
        return cardNumber;
    }

    public static String generatePIN() {

        String number = "";

        for (int i = 0; i < 4; i++) {
            number += (int) (Math.random()*10);
        }
        return number;
    }

    public String getPin() {
        return pin;
    }
}