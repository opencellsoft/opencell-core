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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
import org.meveo.service.billing.impl.UsageRatingService;

@Named
@ConversationScoped
public class EdrBean extends BaseBean<EDR> {

    private static final long serialVersionUID = 7833532801870480214L;

    @Inject
    private EdrService edrService;
    
    @Inject
    private UsageRatingService usageRatingService;

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
        List<EDR> edrs = getSelectedEntities();
        if (edrs == null || edrs.isEmpty()) {
            return;
        }
        log.debug("Reopening {} rejected edrs", edrs.size());

        List<Long> selectedIds = new ArrayList<>();
        for (EDR edr : edrs) {
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
    
    /**
     * Can reprocess.
     *
     * @param cdr the cdr
     * @return true, if the user has the necessaries roles and the edr status is open or rejected
     */
    public boolean canReprocess(EDR edr) {
        if(currentUser.hasRole("cdrRateManager") && (EDRStatusEnum.OPEN.equals(edr.getStatus()) || EDRStatusEnum.REJECTED.equals(edr.getStatus()))) {
            return true;
        }
        return false;
    }
    
    /**
     * Reprocess the edr
     *
     * @param edr the edr
     */
    public void reprocess(EDR edr) {
        try {
            edrService.updateEdrsToReprocess(Arrays.asList(edr.getId()));
            usageRatingService.ratePostpaidUsage(edr.getId());
        } catch (Exception e) {
            // do nothing
        }       
    }
    
    /**
     * Mass reprocessing of all selected edrs
     */
    public void massReprocessing() {
        List<EDR> edrs = getSelectedEntities();
        if (edrs == null || edrs.isEmpty()) {
            return;
        }
        List<Long> selectedIds = edrs.parallelStream()
                .filter(edr -> (EDRStatusEnum.REJECTED.equals(edr.getStatus()) || EDRStatusEnum.OPEN.equals(edr.getStatus())))
                .map(EDR::getId)
                .collect(Collectors.toList());
        
        if (selectedIds != null && selectedIds.size() > 0) {
            edrService.updateEdrsToReprocess(selectedIds);
            log.debug("Reprocessing {} edrs", selectedIds.size());        
            for (Long id : selectedIds) {
                try {
                    usageRatingService.ratePostpaidUsage(id);
                } catch (Exception e) {
                    // Do nothing
                }                
            }
        } else {
            messages.warn(new BundleKey("messages", "edr.statusCanBeReprocessed"));
        }  
    }
    
}