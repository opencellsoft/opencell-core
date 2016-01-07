package org.meveo.admin.util;

import java.io.Serializable;
import java.text.MessageFormat;

public class ResourceBundle implements Serializable {

    private static final long serialVersionUID = -5269169718061449505L;

    private transient java.util.ResourceBundle proxiedBundle;

    public ResourceBundle(java.util.ResourceBundle proxiedBundle) {
        this.proxiedBundle = proxiedBundle;
    }

    public String getString(String key, Object... params) {
        if (!proxiedBundle.containsKey(key)) {
            return key;
        }
        String msgValue = proxiedBundle.getString(key);
        if (params.length == 0) {
            return msgValue;
        }

        MessageFormat messageFormat = new MessageFormat(msgValue);
        return messageFormat.format(params);
    }
}