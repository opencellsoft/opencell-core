package org.meveo.commons.utils;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;

@Stateless
public class ParamBeanFactory {

    @Inject
    private Logger log;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    /**
     * Return an instance of current user provider ParamBean
     * 
     * @return ParamBean Instance
     */
    public ParamBean getInstance() {
        ParamBean paramBean = null;
        if (currentUser != null && !StringUtils.isBlank(currentUser.getProviderCode())) {
            log.trace("Get ParamBean by provider");
            paramBean = ParamBean.getInstanceByProvider(currentUser.getProviderCode());
            return paramBean;
        }
        paramBean = ParamBean.getInstance();
        return paramBean;
    }

    public String getChrootDir() {
        ParamBean paramBean = getInstance();
        if (currentUser != null) {
            return paramBean.getChrootDir(currentUser.getProviderCode());
        } else {
            return paramBean.getChrootDir("");
        }
    }
}
