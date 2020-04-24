package org.meveo.api.security.config;

import org.meveo.api.security.filter.NullFilter;
import org.meveo.api.security.filter.SecureMethodResultFilter;
import org.meveo.api.security.parameter.CodeParser;

public class SecuredMethodConfig {

    private SecureMethodParameterConfig[]  validate;

    private Class<? extends SecureMethodResultFilter> resultFilter = NullFilter.class;

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
