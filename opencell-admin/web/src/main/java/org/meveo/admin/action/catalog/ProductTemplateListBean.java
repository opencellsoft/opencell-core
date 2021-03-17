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

package org.meveo.admin.action.catalog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.primefaces.model.LazyDataModel;

@Named
@ConversationScoped
public class ProductTemplateListBean extends ProductTemplateBean {

	private static final long serialVersionUID = -7109673492144846741L;

	private MeveoInstance meveoInstanceToExport = new MeveoInstance();

	private List<ProductTemplate> ptToExport = new ArrayList<ProductTemplate>();

	private long activeCount = 0;

	private long inactiveCount = 0;

	private long almostExpiredCount = 0;

	@Override
	public void preRenderView() {
		activeCount = productTemplateService.countProductTemplateActive(true);
		inactiveCount = productTemplateService.countProductTemplateActive(false);
		almostExpiredCount = productTemplateService.countProductTemplateExpiring();
		super.preRenderView();
	}

	@Override
	protected IPersistenceService<ProductTemplate> getPersistenceService() {
		return productTemplateService;
	}

	public String newProductTemplate() {
		return "mm_productTemplateDetail";
	}

	public void updateProductTemplateStatus(ProductTemplate pt) throws BusinessException {
		productTemplateService.update(pt);
	}

	public void addProductTemplateToExport(ProductTemplate pt) {
		if (!ptToExport.contains(pt)) {
			ptToExport.add(pt);
		}
	}
	
	public void deleteForExport(ProductTemplate pt) {
		if (ptToExport.contains(pt)) {
			ptToExport.remove(pt);
		}
	}

	protected byte[] downloadUrl(URL toDownload) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] chunk = new byte[4096];
        int bytesRead;
        try (InputStream stream = toDownload.openStream();) {
            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }
        } catch (IOException e) {
            log.error("error = {}", e);
            return null;
        }

		return outputStream.toByteArray();
	}
	

	public MeveoInstance getMeveoInstanceToExport() {
		return meveoInstanceToExport;
	}

	public void setMeveoInstanceToExport(MeveoInstance meveoInstanceToExport) {
		this.meveoInstanceToExport = meveoInstanceToExport;
	}

    public long getActiveCount() {
        return activeCount;
    }
    
    public long getInactiveCount() {
        return inactiveCount;
    }
    
    public long getAlmostExpiredCount() {
        return almostExpiredCount;
    }

	public List<ProductTemplate> getPtToExport() {
		return ptToExport;
	}

	public void setPtToExport(List<ProductTemplate> ptToExport) {
		this.ptToExport = ptToExport;
	}

	@Override
    protected Map<String, Object> supplementSearchCriteria(Map<String, Object> searchCriteria) {

        // Show product templates only, omitting Bundle templates as they are subclasses of Product template
        if (!searchCriteria.containsKey(PersistenceService.SEARCH_ATTR_TYPE_CLASS)) {
            searchCriteria.put(PersistenceService.SEARCH_ATTR_TYPE_CLASS, ProductTemplate.class);
        }
        return super.supplementSearchCriteria(searchCriteria);
    }
	
	public LazyDataModel<ProductTemplate> getLazyDataModelNoBPM() {
        filters.put("businessProductModel", PersistenceService.SEARCH_IS_NULL);
        return getLazyDataModel(filters, listFiltered);
    }
}