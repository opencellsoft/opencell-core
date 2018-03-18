package org.meveo.commons.utils;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;

/**
 * 
 * @author Wassim Drira
 *
 */
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
            log.trace("> ParamBeanFactory > getInstance > ByProvider > {}", currentUser.getProviderCode());
            paramBean = ParamBean.getInstanceByProvider(currentUser.getProviderCode());
            return paramBean;
        }
        log.trace("> ParamBeanFactory > getInstance > *No* Provider > ");
        paramBean = ParamBean.getInstanceByProvider("");
        return paramBean;
    }

    /**
     * Return the chroot folder path of the current provider without passing current provider as a parameter
     * 
     * @return path
     */
    public String getChrootDir() {
        ParamBean paramBean = getInstance();
        if (currentUser != null) {
            return paramBean.getChrootDir(currentUser.getProviderCode());
        } else {
            return paramBean.getChrootDir("");
        }
    }

    /**
     * Return the application scope parameter bean
     * 
     * @return paramBean
     */
    public static ParamBean getAppScopeInstance() {
        return ParamBean.getInstance();
    }
}
