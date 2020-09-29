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

package org.meveo.commons.utils;

import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * 
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
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
     * Return the system provider root directory in despite of the provider code
     *
     * @return path
     */
    public String getDefaultChrootDir(){
        return getInstance().getChrootDir("");
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
