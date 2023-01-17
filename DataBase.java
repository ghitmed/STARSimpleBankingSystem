package banking;

public class DataBase {

    private final String defaultDataBaseName = "DBSystem.db";
    private final String defaultDataBasePath = "jdbc:sqlite:";
    private String dataBaseName;
    private final String BIN = "400000";
    private String pathToDataBase;
    private Integer currentAccountIndex;
    private int optionSelection;

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public String getDefaultDbName() {
        return defaultDataBaseName;
    }

    public String getDefaultDbPathRoot() {
        return defaultDataBasePath;
    }

    public String getPathToDataBase() {
        return pathToDataBase;
    }

    public void setPathToDataBase(String pathToDataBase) {
        this.pathToDataBase = pathToDataBase;
    }

    public String getBIN() {
        return BIN;
    }

    public int getOptionSelection() {
        return optionSelection;
    }

    public void setOptionSelection(int optionSelection) {
        this.optionSelection = optionSelection;
    }

    public Integer getCurrentAccountIndex() {
        return currentAccountIndex;
    }

    public void setCurrentAccountIndex(Integer currentAccountIndex) {
        this.currentAccountIndex = currentAccountIndex;
    }
}
