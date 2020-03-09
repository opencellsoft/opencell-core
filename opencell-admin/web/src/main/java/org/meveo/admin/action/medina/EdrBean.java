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

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.EdrService;

@Named
@ConversationScoped
public class EdrBean extends BaseBean<EDR> {

    private static final long serialVersionUID = 7833532801870480214L;

    @Inject
    private EdrService edrService;

    public EdrBean() {
        super(EDR.class);
    }

    @Override
    public String getEditViewName() {
        return "edrDetail";
    }

    public void updateStatus(EDR selectedEdr) throws BusinessException {
        if (EDRStatusEnum.REJECTED.equals(selectedEdr.getStatus())) {
            List<Long> ids = new ArrayList<>();
            ids.add(selectedEdr.getId());
            edrService.reopenRejectedEDRS(ids);

        } else {
            messages.warn(new BundleKey("messages", "edr.onlyRejectedCanBeUpdated"));
        }
    }

    public void massUpdate() {
        if (getSelectedEntities() == null) {
            return;
        }
        log.debug("Reopening {} rejected edrs", getSelectedEntities().size());

        List<Long> selectedIds = new ArrayList<>();
        for (EDR edr : getSelectedEntities()) {
            if (EDRStatusEnum.REJECTED.equals(edr.getStatus())) {
                selectedIds.add(edr.getId());
            }
        }

        if (selectedIds.size() > 0) {
            edrService.reopenRejectedEDRS(selectedIds);
        } else {
            messages.warn(new BundleKey("messages", "edr.onlyRejectedCanBeUpdated"));
        }
    }

    @Override
    protected IPersistenceService<EDR> getPersistenceService() {
        return edrService;
    }
}