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
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.tools.ant.util.DateUtils;
import org.meveo.model.rating.CDR;
import org.meveo.service.medina.impl.CDRService;
import org.meveo.util.view.LazyDataModelWSize;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@Named
@ConversationScoped
public class CdrListBean extends CdrBean {

    private static final long serialVersionUID = -6872704581103186463L;

    protected LazyDataModel<CDR> cdrFileNames;
    
    private boolean loadCdrFileName = false;
        

    @Inject
    private CDRService cdrService;

    @SuppressWarnings("rawtypes")
    public LazyDataModel<CDR> getFilteredLazyDataModel() {
        if (cdrFileNames == null) {
            cdrFileNames = new LazyDataModelWSize<CDR>() {

                private static final long serialVersionUID = 1L;

                @Override
                public List<CDR> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map mapfilters) {
                    Date fromRange = filters.get("fromRange created") != null ? (Date) filters.get("fromRange created") : null;
                    Date toRange = filters.get("toRange created") != null ? (Date) filters.get("toRange created") : null;
                    String originBatch = filters.get("originBatch") != null ? (String) filters.get("originBatch") : null;
                    List<CDR> entities = null;
                    if(filters != null && !filters.isEmpty() && 
                            (fromRange != null || toRange != null || originBatch != null)) {
                        String fromCreationDate = fromRange != null ? DateUtils.format(fromRange, "yyyy-MM-dd") : null;
                        String toCreationDate = toRange != null ? DateUtils.format(toRange, "yyyy-MM-dd") : null;
                        entities = cdrService.getCDRFileNames(originBatch, fromCreationDate, toCreationDate);
                    } else {
                        entities = cdrService.getCDRFileNames();
                    }
                    setRowCount(entities.size());
                    if (getRowCount() > 0) {
                        return entities.subList(first, (first + pageSize) > entities.size() ? entities.size() : (first + pageSize));
                    } else {
                        return new ArrayList<CDR>();
                    }
                }
            };
        } 
        return cdrFileNames; 
    }

    public boolean isLoadCdrFileName() {
        return loadCdrFileName;
    }

    public void setLoadCdrFileName(boolean loadCdrFileName) {
        this.loadCdrFileName = loadCdrFileName;
    }        

}