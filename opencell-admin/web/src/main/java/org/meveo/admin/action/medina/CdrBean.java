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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.medina.impl.CDRService;

@Named
@ViewScoped
public class CdrBean extends BaseBean<CDR> {

    private static final long serialVersionUID = 7833532801870480214L;

    @Inject
    private CDRService cdrService;
    
    @Inject
    private CustomFieldTemplateService customFieldTemplateService;
    
    private Set<String> params;
    
    private String writeOffReason;
    
    private Map<String,CustomFieldTemplate> cfs;

    public CdrBean() {
        super(CDR.class);
    }
    
    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @return CDR
     */
    @Produces
    @Named("cdr")
    public CDR init() {
        return initEntity();
    }
    
    @PostConstruct
    public void initParams() {
        cfs = customFieldTemplateService.findByAppliesTo("CDR");
        params = cfs.keySet();  
    }
    
    public boolean isDate(String param) {
        return cfs.get(param).getFieldType().equals(CustomFieldTypeEnum.DATE);
    }
    public String getParamLabel(String param) {
        return cfs.get(param).getDescription();
    }

    @Override
    public String getEditViewName() {
        return "cdrDetail";
    }
    
    public void reprocess(CDR cdr) {
        cdrService.reprocess(Arrays.asList(cdr.getId()));
    }
    
    public void writeOff() {
        getEntity().setStatus(CDRStatusEnum.DISCARDED);
        getEntity().setRejectReason(writeOffReason);
        cdrService.update(getEntity());
        writeOffReason = "";
    }

    public boolean canReprocess(CDR cdr) {
        List<CDRStatusEnum> authorizedStatuses = Arrays.asList(CDRStatusEnum.OPEN, CDRStatusEnum.ERROR);
        if(authorizedStatuses.contains(cdr.getStatus())) {
            return true;
        }
        return false;
    }
    public boolean canWriteOff(CDR cdr) {
        List<CDRStatusEnum> authorizedStatuses = Arrays.asList(CDRStatusEnum.OPEN, CDRStatusEnum.ERROR);
        if(authorizedStatuses.contains(cdr.getStatus())) {
            return true;
        }
        return false;
    }
    public void massReprocessing() {
        if (getSelectedEntities() == null) {
            return;
        }
        List<CDRStatusEnum> authorizedStatuses = Arrays.asList(CDRStatusEnum.OPEN, CDRStatusEnum.ERROR);

        log.debug("Reprocessing {} cdrs", getSelectedEntities().size());

        List<Long> selectedIds = getSelectedEntities().parallelStream()
                                        .filter(cdr -> authorizedStatuses.contains(cdr.getStatus()))
                                        .map(CDR::getId)
                                        .collect(Collectors.toList());
        cdrService.reprocess(selectedIds);
    }
    
    public void massWritingOff() {
        if (getSelectedEntities() == null) {
            return;
        }
        List<CDRStatusEnum> authorizedStatuses = Arrays.asList(CDRStatusEnum.OPEN, CDRStatusEnum.ERROR);

        log.debug("Writing off {}  cdrs", getSelectedEntities().size());

        getSelectedEntities().parallelStream()
                             .filter(cdr -> authorizedStatuses.contains(cdr.getStatus()))
                             .forEach(cdr->{
                                 cdr.setStatus(CDRStatusEnum.DISCARDED);
                                 cdr.setRejectReason(writeOffReason);
                                 cdrService.update(cdr);
                             });
        writeOffReason = "";
    }
    
    @Override
    protected IPersistenceService<CDR> getPersistenceService() {
        return cdrService;
    }
    
    public Set<String> getParams() {
        return params;
    }
    public void setParams(Set<String> params) {
        this.params = params;
    }

    public String getWriteOffReason() {
        return writeOffReason;
    }
    public void setWriteOffReason(String writeOffReason) {
        this.writeOffReason = writeOffReason;
    }
}