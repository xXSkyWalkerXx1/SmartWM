package de.tud.inf.mmt.wmscrape.springdata;

/**
 * temporarily stores the connection data. the data is used by
 * {@link de.tud.inf.mmt.wmscrape.springdata.DataSourceConfig}
 */
public class SpringIndependentData {

    private static String username; // e.g. test
    private static String password;
    private static String propertyConnectionPath; // e.g. "mysql://localhost/"
    private static String springConnectionPath; // e.g. "jdbc:mysql://localhost/test_wms_db"


    public SpringIndependentData() {}

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        SpringIndependentData.password = password;
    }

    public static String getPropertyConnectionPath() {
        return propertyConnectionPath;
    }

    public static void setPropertyConnectionPath(String propertyConnectionPath) {
        SpringIndependentData.propertyConnectionPath = propertyConnectionPath;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        SpringIndependentData.username = username;
    }

    public static String getSpringConnectionPath() { return springConnectionPath; }

    public static void setSpringConnectionPath(String springConnectionPath) {
        SpringIndependentData.springConnectionPath = springConnectionPath;
    }
}