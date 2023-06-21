package org.meveo.model.shared;

public class RegexUtils {

    private static final String REGEX_CODE = "^[a-zA-Z0-9_-]+$";

    /**
     * Check if code is authorized
     * @param pCode Entity Code
     * @return True or False
     */
    public static boolean checkCode(String pCode) {
        return pCode.matches(REGEX_CODE);
    }
}
