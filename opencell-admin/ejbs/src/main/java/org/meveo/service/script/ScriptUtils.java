/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

/**
 * 
 */
package org.meveo.service.script;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.meveo.commons.utils.StringUtils;

/**
 * @author melyoussoufi
 * @lastModifiedVersion 7.2.0
 *
 */
public abstract class ScriptUtils {

    /**
     * Check that the class implements ScriptInterface interface
     * 
     * @param fullClassName Full class name
     * @return True if class implements ScriptInterface interface
     */
    @SuppressWarnings("rawtypes")
    public static boolean isScriptInterfaceClass(String fullClassName) {
        try {
            Class classDefinition = Class.forName(fullClassName);

            return ScriptInterface.class.isAssignableFrom(classDefinition);

        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Check full class name is existed class path or not.
     * 
     * @param fullClassName Full class name
     * @return True if class is overridden
     */
    public static boolean isOverwritesJavaClass(String fullClassName) {
        try {
            Class.forName(fullClassName);
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    /**
     * Find the package name in a source java text.
     * 
     * @param src Java source code
     * @return Package name
     */
    public static String getPackageName(String src) {
        return StringUtils.patternMacher("package (.*?);", src);
    }

    /**
     * Find the class name in a source java text
     * 
     * @param src Java source code
     * @return Class name
     */
    public static String getClassName(String src) {
        String className = StringUtils.patternMacher("public class (.*) extends", src);
        if (className == null) {
            className = StringUtils.patternMacher("public class (.*) implements", src);
        }
        return className != null ? className.trim() : null;
    }

    /**
     * Gets a full classname of a script by combining a package (if applicable) and a classname
     * 
     * @param script Java source code
     * @return Full classname
     */
    public static String getFullClassname(String script) {
        String packageName = getPackageName(script);
        String className = getClassName(script);
        return (packageName != null ? packageName.trim() + "." : "") + className;
    }

    /**
     * Parse parameters encoded in URL like style param=value&amp;param=value.
     * 
     * @param encodedParameters Parameters encoded in URL like style param=value&amp;param=value
     * @return A map of parameter keys and values
     */
    public static Map<String, Object> parseParameters(String encodedParameters) {
        Map<String, Object> parameters = new HashMap<String, Object>();

        if (!StringUtils.isBlank(encodedParameters)) {
            StringTokenizer tokenizer = new StringTokenizer(encodedParameters, "&");
            while (tokenizer.hasMoreElements()) {
                String paramValue = tokenizer.nextToken();
                String[] paramValueSplit = paramValue.split("=");
                if (paramValueSplit.length == 2) {
                    parameters.put(paramValueSplit[0], paramValueSplit[1]);
                } else {
                    parameters.put(paramValueSplit[0], null);
                }
            }

        }
        return parameters;
    }

    /**
     * Convert Comparison operator to an sql or java operator
     * @param comparison operator
     * @param toSql or toJava
     * @return sql Comparison operator
     */
    public static String buildOperator(String operator, boolean toSql) {
		String operatorExpression;
		switch (operator) {
		case "<":
            operatorExpression = "<";
            break;
		case "≤":
            operatorExpression = "<=";
            break;
		case "=":
            operatorExpression = toSql? "=" : "==";
            break;
		case "≠":
			operatorExpression = toSql? "<>" : "!=";
			break;
		case "≥":
			operatorExpression = ">=";
			break;
		default:
			operatorExpression = ">";
			break;
		}
		return operatorExpression;
	}

}
