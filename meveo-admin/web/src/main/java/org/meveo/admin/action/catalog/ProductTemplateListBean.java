package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.communication.impl.MeveoInstanceService;

@Named
@ConversationScoped
public class ProductTemplateListBean extends BaseBean<ProductTemplate> {

	private static final long serialVersionUID = -7109673492144846741L;

	@Inject
	private ProductTemplateService productTemplateService;

	@Inject
	private MeveoInstanceService meveoInstanceService;

	private MeveoInstance meveoInstanceToExport = new MeveoInstance();

	private List<MeveoInstance> meveoInstances = new ArrayList<MeveoInstance>();

	private List<ProductTemplate> productTemplates = new ArrayList<ProductTemplate>();

	public ProductTemplateListBean() {
		super(ProductTemplate.class);
	}

	@Override
	protected IPersistenceService<ProductTemplate> getPersistenceService() {
		return productTemplateService;
	}

	public List<MeveoInstance> getMeveoInstances() {
		meveoInstances = meveoInstanceService.list();
		return meveoInstances;
	}

	public void setMeveoInstances(List<MeveoInstance> meveoInstances) {
		this.meveoInstances = meveoInstances;
	}

	public MeveoInstance getMeveoInstanceToExport() {
		return meveoInstanceToExport;
	}

	public void setMeveoInstanceToExport(MeveoInstance meveoInstanceToExport) {
		this.meveoInstanceToExport = meveoInstanceToExport;
	}

	public List<ProductTemplate> getProductTemplates() {
		productTemplates = productTemplateService.list();
		return productTemplates;
	}

	public void setProductTemplates(List<ProductTemplate> productTemplates) {
		this.productTemplates = productTemplates;
	}

}
