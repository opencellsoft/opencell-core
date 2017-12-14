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

		try {
			byte[] chunk = new byte[4096];
			int bytesRead;
			InputStream stream = toDownload.openStream();

			while ((bytesRead = stream.read(chunk)) > 0) {
				outputStream.write(chunk, 0, bytesRead);
			}

		} catch (IOException e) {
			e.printStackTrace();
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