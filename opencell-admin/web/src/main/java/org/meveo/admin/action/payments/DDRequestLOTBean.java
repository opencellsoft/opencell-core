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
package org.meveo.admin.action.payments;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.DDRequestLOTService;
import org.meveo.service.payments.impl.DDRequestLotOpService;
import org.meveo.service.payments.impl.RecordedInvoiceService;

/**
 * Standard backing bean for {@link DDRequestLOT} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * 
 * @author anasseh
 */
@Named
@ViewScoped
public class DDRequestLOTBean extends BaseBean<DDRequestLOT> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link DDRequestLOT} service. Extends {@link PersistenceService} .
     */
    @Inject
    private DDRequestLOTService ddrequestLOTService;

    @Inject
    private DDRequestLotOpService ddrequestLotOpService;

    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    /**
     * startDueDate parameter for ddRequest batch
     */
    private Date startDueDate;
    /**
     * endDueDate parameter for ddRequest batch
     */
    private Date endDueDate;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public DDRequestLOTBean() {
        super(DDRequestLOT.class);
    }

    /**
     * Regenerate file from entity DDRequestLOT
     * 
     * @return NULL
     */
    public String generateFile() {
        try {
            DDRequestLotOp ddrequestLotOp = new DDRequestLotOp();
            ddrequestLotOp.setDdrequestOp(DDRequestOpEnum.FILE);
            ddrequestLotOp.setStatus(DDRequestOpStatusEnum.WAIT);
            ddrequestLotOp.setDdRequestBuilder(entity.getDdRequestBuilder());
            ddrequestLotOp.setDdrequestLOT(entity);
            ddrequestLotOp.setPaymentOrRefundEnum(entity.getPaymentOrRefundEnum());
            ddrequestLotOp.setRecurrent(Boolean.FALSE);
            ddrequestLotOpService.create(ddrequestLotOp);
            messages.info(new BundleKey("messages", "ddrequestLot.generateFileSuccessful"));
        } catch (Exception e) {
            log.error("failed to generate file", e);
            messages.error(new BundleKey("messages", "ddrequestLot.generateFileFailed"));
        }

        return back();
    }

    /**
     * Do payment for each invoice included in DDRequest File
     * 
     * @return NULL
     */
    public String doPayments() {
        try {
            DDRequestLotOp ddrequestLotOp = new DDRequestLotOp();
            ddrequestLotOp.setDdrequestOp(DDRequestOpEnum.PAYMENT);
            ddrequestLotOp.setStatus(DDRequestOpStatusEnum.WAIT);
            ddrequestLotOp.setDdRequestBuilder(entity.getDdRequestBuilder());
            ddrequestLotOp.setDdrequestLOT(entity);
            ddrequestLotOp.setPaymentOrRefundEnum(entity.getPaymentOrRefundEnum());
            ddrequestLotOp.setRecurrent(Boolean.FALSE);
            ddrequestLotOpService.create(ddrequestLotOp);
            messages.info(new BundleKey("messages", "ddrequestLot.doPaymentsSuccessful"));
        } catch (Exception e) {
            log.error("error generated while creating payments ", e);
            messages.info(new BundleKey("messages", "ddrequestLot.doPaymentsFailed"));
        }

        return back();
    }

    /**
     * Launch DDRequestLOT process
     * 
     * @return NULL
     */
    public String launchProcess() {
        try {
            DDRequestLotOp ddrequestLotOp = new DDRequestLotOp();
            ddrequestLotOp.setFromDueDate(getStartDueDate());
            ddrequestLotOp.setToDueDate(getEndDueDate());
            ddrequestLotOp.setStatus(DDRequestOpStatusEnum.WAIT);
            ddrequestLotOp.setDdrequestOp(DDRequestOpEnum.CREATE);
            ddrequestLotOpService.create(ddrequestLotOp);
            messages.info(new BundleKey("messages", "ddrequestLot.launchProcessSuccessful"));
        } catch (Exception e) {
            log.error("failed to launch process ", e);
            messages.info(new BundleKey("messages", "ddrequestLot.launchProcessFailed"));
            messages.info(e.getMessage());
        }
        return back();
    }

    @Override
    public String getNewViewName() {
        return "ddrequestLotDetail";
    }

    @Override
    protected String getListViewName() {
        return "ddrequestLots";
    }

    @Override
    public String getEditViewName() {
        return "ddrequestLotDetail";
    }

    @Override
    protected IPersistenceService<DDRequestLOT> getPersistenceService() {
        return ddrequestLOTService;
    }

    /**
     * @param startDueDate the startDueDate to set
     */
    public void setStartDueDate(Date startDueDate) {
        this.startDueDate = startDueDate;
    }

    /**
     * @return the startDueDate
     */
    public Date getStartDueDate() {
        return startDueDate;
    }

    /**
     * @param endDueDate the endDueDate to set
     */
    public void setEndDueDate(Date endDueDate) {
        this.endDueDate = endDueDate;
    }

    /**
     * @return the endDueDate
     */
    public Date getEndDueDate() {
        return endDueDate;
    }

    @Override
    public String back() {
        return "/pages/payments/ddrequestLot/ddrequestLots.xhtml";
    }

    public PaginationDataModel<RecordedInvoice> getInvoices() {
        PaginationDataModel<RecordedInvoice> invoices = new PaginationDataModel<RecordedInvoice>(recordedInvoiceService);
        Map<String, Object> filters2 = new HashMap<String, Object>();
        filters2.put("ddRequestLOT", entity);
        invoices.addFilters(filters2);
        invoices.addFetchFields(getListFieldsToFetch());
        invoices.forceRefresh();
        return invoices;
    }

    public boolean canGenerateFile() {
        if (entity != null && !StringUtils.isBlank(entity.getFileName())) {
            return true;
        }
        return false;
    }
    
    public boolean canCreatePayments() {
       
            return true;
       
    }
    
    
}