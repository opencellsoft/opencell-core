package org.meveo.api.security.config;

import org.meveo.api.security.filter.SecureMethodResultFilter;

public class SecuredMethodConfig {

    private SecureMethodParameterConfig[]  validate;

    private Class<? extends SecureMethodResultFilter> resultFilter;

    public SecureMethodParameterConfig[] getValidate() {
        return validate;
    }

    public void setValidate(SecureMethodParameterConfig[] validate) {
        this.validate = validate;
    }

    public Class<? extends SecureMethodResultFilter> getResultFilter() {
        return resultFilter;
    }

    public void setResultFilter(Class<? extends SecureMethodResultFilter> resultFilter) {
        this.resultFilter = resultFilter;
    }
}
