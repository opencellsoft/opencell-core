package Utils;

public class Constants {

    // Final strings for defining complex scenario
    public static final String AND_LOGIC = ", ";
    public static final String BASE_COMMAND = "mvn.cmd test -Dcucumber.filter.tags=@";
    public static final String AND_CMD = " && ";

    // System configuration for Keycloak
    public static final String URL_OC = "opencell.url";
    public static final String KCL_SECRET = "opencell.keycloak.secret";
    public static final String KCL_REALM = "opencell.keycloak.realm";
    public static final String KCL_CLIENT_ID = "opencell.keycloak.clientId";
    public static final String KCL_URL = "opencell.keycloak.url";

    // Credentials for user opencell.admin
    public static final String USERNAME_OC_ADMIN = "adminUsername";
    public static final String PASSWORD_OC_ADMIN = "adminPassword";

    // Credentials for user opencell.superadmin
    public static final String USERNAME_OC_SPADMIN = "superUsername";
    public static final String PASSWORD_OC_SPADMIN = "superPassword";

    // prefix of API v2
    public static final String PREFIX_PUT_API = "/opencell/api/rest/v1/accountManagement/sellers/";
    public static final String PREFIX_DELETE_API = "/opencell/api/rest/seller/";
    public static final String PREFIX_POST_ALL_API_V2 = "/opencell/api/rest/seller/";

    // other constants
    public static final String SEPARATOR_SLASH = "/";
    public static final String EMPTY_PAYLOAD_TO_VERIFY_EXISTENCE = "{}";

    // colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


}
