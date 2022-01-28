package functional.driver.utils;

import java.util.Map;

public class ApiUtils {

    public static String getUrlForGetFromDataTable(String entityName, String baseUrl, Map<String, String> anInstance) {
        StringBuilder completeUrl = new StringBuilder(baseUrl).append("?");

        for (Map.Entry<String, String> column : anInstance.entrySet()) {
            if (entityName.equals("AccessPoint"))
                completeUrl.append(column.getKey()).append("=").append(column.getValue()).append("&");
            else if (entityName.equals("ServiceTemplate"))
                completeUrl.append("serviceTemplateCode").append("=").append(column.getValue());
        }

        return completeUrl.toString();
    }

    public static String getUrlForGetInLine(String entityName, String baseUrl, String entityCode) {
        StringBuilder completeUrl = new StringBuilder(baseUrl).append("?");

        if (entityName.equals("Subscription"))
            completeUrl.append("subscriptionCode").append("=").append(entityCode);
        else if (entityName.equals("ServiceTemplate"))
            completeUrl.append("serviceTemplateCode").append("=").append(entityCode);

        return completeUrl.toString();
    }

}
