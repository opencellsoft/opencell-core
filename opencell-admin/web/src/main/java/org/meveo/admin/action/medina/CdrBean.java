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

package org.meveo.admin.action.medina;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.medina.impl.CDRService;

@Named
@ConversationScoped
public class CdrBean extends BaseBean<CDR> {

    private static final long serialVersionUID = 7833532801870480214L;

    @Inject
    private CDRService cdrService;
    
    private List<String> params;

    public CdrBean() {
        super(CDR.class);
        params = new ArrayList<>();
        ResourceBundle.getBundle("params").getKeys().asIterator().forEachRemaining(entry-> params.add(entry));
    }

    @Override
    public String getEditViewName() {
        return "cdrDetail";
    }

    public void massUpdate() {
        if (getSelectedEntities() == null) {
            return;
        }
        log.debug("Reopening {} rejected cdrs", getSelectedEntities().size());

        List<Long> selectedIds = new ArrayList<>();
        for (CDR cdr : getSelectedEntities()) {
            if (CDRStatusEnum.ERROR.equals(cdr.getStatus())) {
                selectedIds.add(cdr.getId());
            }
        }

//        if (selectedIds.size() > 0) {
//            cdrService.(selectedIds);
//        } else {
//            messages.warn(new BundleKey("messages", "edr.onlyRejectedCanBeUpdated"));
//        }
    }

    @Override
    protected IPersistenceService<CDR> getPersistenceService() {
        return cdrService;
    }
    
    public List<String> getParams() {
        return params;
    }
    public void setParams(List<String> params) {
        this.params = params;
    }
}