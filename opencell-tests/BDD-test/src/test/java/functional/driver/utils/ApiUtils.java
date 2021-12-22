package functional.driver.utils;

import java.util.Map;

public class ApiUtils {

    public static String getUrlForGet(String entityName, String baseUrl, Map<String, String> anInstance) {
        StringBuilder completeUrl = new StringBuilder(baseUrl).append("?");

        for (Map.Entry<String, String> column : anInstance.entrySet()) {
            if (entityName.equals("AccessPoint"))
                completeUrl.append(column.getKey()).append("=").append(column.getValue()).append("&");
        }

        return completeUrl.toString();
    }

}
