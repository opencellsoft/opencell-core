package Utils;

import API.driver.ApiInfo;
import functional.driver.utils.Constants;
import org.meveo.util.Inflector;

public class URLFormat {
    public static String formatUpdateURL(ApiInfo apiInfo) {
        return Constants.SEPARATOR_SLASH + apiInfo.getApiVer() + Constants.SEPARATOR_SLASH
                + apiInfo.getBusinessDomainPath() + Constants.SEPARATOR_SLASH
                + Inflector.getInstance().pluralize(apiInfo.getEntity())
                + Constants.SEPARATOR_SLASH + apiInfo.getCodeOrId();
    }

    public static String formatCreateURL(ApiInfo apiInfo) {
        return Constants.SEPARATOR_SLASH + apiInfo.getApiVer() + Constants.SEPARATOR_SLASH
                + apiInfo.getBusinessDomainPath() + Constants.SEPARATOR_SLASH
                + Inflector.getInstance().pluralize(apiInfo.getEntity());
    }
}
