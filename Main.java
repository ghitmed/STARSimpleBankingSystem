package banking;

public class Main {

    public static void main(String[] args) {
        args = new String[]{"-fileName", "card.s3db"};
        DAOAccount DAOAccount = new DAOAccount();
        DAOAccount.work(args);
    }
}