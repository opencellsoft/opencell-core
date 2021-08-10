package Utils;

public class SystemProperties {

    private static String username = "opencell.admin";
    private static String password = "opencell.admin";
    private static String env;
    private static String keycloakURL;
    private static String keycloakSecret = "afe07e5a-68cb-4fb0-8b75-5b6053b07dc3";
    private static String keycloakRealm = "opencell";
    private static String keycloakClientId = "opencell-web";

    public static String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static String getENV() {
        return env;
    }

    public static void setENV(String anEnv) {
        env = anEnv;
        setKeycloakURL( env );
    }

    public static String getKeycloakURL() {
        return keycloakURL;
    }

    public static void setKeycloakURL(String URL) {
        keycloakURL = URL + "/auth";
    }

    public static String getKeycloakSecret() {
        return keycloakSecret;
    }

    public static String getKeycloakRealm() {
        return keycloakRealm;
    }

    public static String getKeycloakClientId() {
        return keycloakClientId;
    }

}
