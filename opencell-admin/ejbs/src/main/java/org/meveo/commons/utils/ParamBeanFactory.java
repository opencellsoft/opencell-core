package org.meveo.commons.utils;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class ParamBeanFactory {

    private static final Logger log = LoggerFactory.getLogger(ParamBeanFactory.class);

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    /**
     * 
     */
    public ParamBeanFactory() {
        super();
        log.info("> ParamBean2 init");
    }

    public ParamBean getInstance() {
        log.info("> ParamBeanFactory > getInstance");
        ParamBean paramBean = null;
        if (currentUser != null && !StringUtils.isBlank(currentUser.getProviderCode())) {
            log.info("> ParamBean2 > getInstance > ByProvider");
            paramBean = ParamBean.getInstanceByProvider(currentUser.getProviderCode());
            return paramBean;
        }
        paramBean = ParamBean.getInstance(null);
        return paramBean;
    }

}
