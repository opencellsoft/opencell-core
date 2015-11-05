package org.meveo.commons.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class MessagesUtils {

    public static String getMessage(String msgKey, Locale locale, Object... params) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        String msgValue = bundle.getString(msgKey);
        if (params.length == 0) {
            return msgValue;
        }
        MessageFormat messageFormat = new MessageFormat(msgValue);
        return messageFormat.format(params);
    }
}
