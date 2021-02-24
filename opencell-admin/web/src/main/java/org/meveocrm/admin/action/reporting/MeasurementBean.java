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
package org.meveocrm.admin.action.reporting;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.model.dwh.MeasurableQuantity;
import org.meveo.model.dwh.MeasuredValue;
import org.meveo.service.base.local.IPersistenceService;
import org.meveocrm.services.dwh.MeasurableQuantityService;
import org.meveocrm.services.dwh.MeasuredValueService;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 */
@Named
@ViewScoped
public class MeasurementBean extends BaseBean<MeasuredValue> {

    private static final long serialVersionUID = 883901110961710869L;

    @Inject
    private MeasuredValueService measuredValueService;

    @Inject
    private MeasurableQuantityService mqService;

    private MeasurableQuantity measurableQuantity;

    public MeasurementBean() {
        super(MeasuredValue.class);
        showDeprecatedWarning();
    }

    /**
     * Retrieves the associated measurable quantity given the selected code in the search form
     * @return the measurable quantity object
     */
    public MeasurableQuantity getMeasurableQuantity() {
        String code = (String) filters.get("code");
        if (code != null) {
            this.measurableQuantity = mqService.findByCode(code);
        } else {
            this.measurableQuantity = null;
        }
        return measurableQuantity;
    }

    /**
     * Retrieves list of measurable quantity codes.
     * @return a map of measurable quantity codes.
     */
    public Map<String, String> getMeasurableQuantityCodes() {
        List<MeasurableQuantity> mqList = mqService.list();
        return mqList.stream()
                .collect(Collectors.toMap(MeasurableQuantity::getCode, MeasurableQuantity::getCode));

    }

    /**
     * When a cell in the data table is changed, this method is called to persist the value as needed
     * @param event primefaces event object containing the updated values when a cell is edited.
     */
    public void onCellEdit(CellEditEvent event) {
        MeasuredValue entity = (MeasuredValue) (((DataTable) event.getComponent()).getRowData());
        if (entity != null && !entity.isTransient()) {
            try {
                boolean result = measuredValueService.updateCellEdit(entity);
                if (result) {
                    messages.info(new BundleKey("messages", "update.successful"));
                }
            } catch (EntityExistsException e) {
                log.error("Fail to update Price plan {}. Reason {}", entity.getCode(), (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
                messages.info(new BundleKey("messages", "pricePlanMatrix.codeExistedFail"), entity.getCode());
            } catch (Exception e) {
                log.error("Fail to update Price plan {}. Reason {}", entity.getCode(), (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
                messages.info(new BundleKey("messages", "pricePlanMatrix.updateCellFail"), entity.getCode(), (e.getMessage() != null ? e.getClass().getSimpleName() : e.getMessage()));
            }
        }
    }

    @Override
    protected IPersistenceService<MeasuredValue> getPersistenceService() {
        return measuredValueService;
    }

    @Override
    protected String getListViewName() {
        return "measuredValueDetail";
    }
}