package org.meveo.commons.utils;

import javax.annotation.PostConstruct;
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
     * Constructor
     * 
     */
    public ParamBeanFactory() {
        super();
        log.info("> ParamBeanFactory init");
    }

    @PostConstruct
    void init() {
        log.info("> ParamBeanFactory > init > currentUser > {}", currentUser != null ? "Provider: " + currentUser.getProviderCode() : null);
    }

    /**
     * Return an instance of current user provider ParamBean
     * 
     * @return ParamBean Instance
     */
    public ParamBean getInstance() {
        log.info("> ParamBeanFactory > getInstance");
        ParamBean paramBean = null;
        if (currentUser != null && !StringUtils.isBlank(currentUser.getProviderCode())) {
            log.info("> ParamBeanFactory > getInstance > ByProvider > {}", currentUser.getProviderCode());
            paramBean = ParamBean.getInstanceByProvider(currentUser.getProviderCode());
            return paramBean;
        }
        log.info("> ParamBeanFactory > getInstance > *No* Provider > ");
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
