package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.WalletTemplateService;
import org.meveo.service.catalog.impl.ChannelService;
import org.meveo.service.catalog.impl.DigitalResourceService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.ProductChargeTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.crm.impl.BusinessAccountModelService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.DualListModel;

/**
 * @author Edward P. Legaspi
 */
@Named
@ViewScoped
public class ProductTemplateBean extends CustomFieldBean<ProductTemplate> {

	private static final long serialVersionUID = -7002455215420815747L;

	@Inject
	protected ProductTemplateService productTemplateService;

	@Inject
	private OfferTemplateCategoryService offerTemplateCategoryService;

	@Inject
	private DigitalResourceService digitalResourceService;

	@Inject
	private WalletTemplateService walletTemplateService;

	@Inject
	private BusinessAccountModelService businessAccountModelService;

	@Inject
	private ChannelService channelService;
	
	@Inject
	private ProductChargeTemplateService productChargeTemplateService;

	private DualListModel<OfferTemplateCategory> offerTemplateCategoriesDM;
	private DualListModel<DigitalResource> attachmentsDM;
	private DualListModel<WalletTemplate> walletTemplatesDM;
	private DualListModel<BusinessAccountModel> bamDM;
	private DualListModel<Channel> channelDM;

	private String editMode;

	@Produces
	@Named
	private ProductChargeTemplate productChargeTemplate = new ProductChargeTemplate();
	
	
	public ProductTemplateBean() {
		super(ProductTemplate.class);
	}

	@Override
	public ProductTemplate initEntity() {
		ProductTemplate result = super.initEntity();

		return result;
	}	

	@Override
	protected IPersistenceService<ProductTemplate> getPersistenceService() {
		return productTemplateService;
	}

	@ActionMethod
	public void duplicate() {
		if (entity != null && entity.getId() != null) {
			try {
				productTemplateService.duplicate(entity, getCurrentUser());
				messages.info(new BundleKey("messages", "save.successful"));
			} catch (BusinessException e) {
				log.error("Error encountered persisting product template entity: {}: {}", entity.getCode(), e);
				messages.error(new BundleKey("messages", "save.unsuccessful"));
			}
		}
	}

	public String activateProduct() throws BusinessException {
		if (entity.getValidFrom().before(entity.getValidTo())) {
			entity.setActive(true);
			return saveOrUpdate(false);
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Valid From cannot be greater than Valid To.", "Valid From cannot be greater than Valid To."));
		}
		return "";
	}

	public String saveAsDraft() throws BusinessException {
		if (entity.getValidFrom().before(entity.getValidTo())) {
			entity.setActive(false);
			return saveOrUpdate(false);
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Valid From cannot be greater than Valid To.", "Valid From cannot be greater than Valid To."));
		}
		return "";
	}

	public String discardChanges() {
		return "mm_productTemplates";
	}

	@Override
	@ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		if(!entity.isTransient()){
			productTemplateService.refreshOrRetrieve(entity);
		}		
		if (offerTemplateCategoriesDM != null && (offerTemplateCategoriesDM.getSource() != null || offerTemplateCategoriesDM.getTarget() != null)) {
			entity.getOfferTemplateCategories().clear();
			entity.getOfferTemplateCategories().addAll(offerTemplateCategoryService.refreshOrRetrieve(offerTemplateCategoriesDM.getTarget()));
		}

		if (attachmentsDM != null && (attachmentsDM.getSource() != null || attachmentsDM.getTarget() != null)) {
			entity.getAttachments().clear();
			entity.getAttachments().addAll(digitalResourceService.refreshOrRetrieve(attachmentsDM.getTarget()));
		}

		if (walletTemplatesDM != null && (walletTemplatesDM.getSource() != null || walletTemplatesDM.getTarget() != null)) {
			entity.getWalletTemplates().clear();
			entity.getWalletTemplates().addAll(walletTemplateService.refreshOrRetrieve(walletTemplatesDM.getTarget()));
		}

		if (bamDM != null && (bamDM.getSource() != null || bamDM.getTarget() != null)) {
			entity.getBusinessAccountModels().clear();
			entity.getBusinessAccountModels().addAll(businessAccountModelService.refreshOrRetrieve(bamDM.getTarget()));
		}

		if (channelDM != null && (channelDM.getSource() != null || channelDM.getTarget() != null)) {
			entity.getChannels().clear();
			entity.getChannels().addAll(channelService.refreshOrRetrieve(channelDM.getTarget()));
		}

		String outcome = super.saveOrUpdate(killConversation);

		if (editMode != null && editMode.length() > 0) {
			outcome = "mm_productTemplates";
		}

		return outcome;
	}

	public DualListModel<OfferTemplateCategory> getOfferTemplateCategoriesDM() {
		if (offerTemplateCategoriesDM == null) {
			List<OfferTemplateCategory> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = offerTemplateCategoryService.list(entity.getProvider(), true);
			} else {
				perksSource = offerTemplateCategoryService.list(currentUser.getProvider(), true);
			}

			List<OfferTemplateCategory> perksTarget = new ArrayList<OfferTemplateCategory>();
			if (entity.getOfferTemplateCategories() != null) {
				perksTarget.addAll(entity.getOfferTemplateCategories());
			}
			perksSource.removeAll(perksTarget);

			offerTemplateCategoriesDM = new DualListModel<OfferTemplateCategory>(perksSource, perksTarget);
		}

		return offerTemplateCategoriesDM;
	}

	public void setOfferTemplateCategoriesDM(DualListModel<OfferTemplateCategory> offerTemplateCategoriesDM) {
		this.offerTemplateCategoriesDM = offerTemplateCategoriesDM;
	}

	public DualListModel<DigitalResource> getAttachmentsDM() {
		if (attachmentsDM == null) {
			List<DigitalResource> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = digitalResourceService.list(entity.getProvider(), true);
			} else {
				perksSource = digitalResourceService.list(currentUser.getProvider(), true);
			}

			List<DigitalResource> perksTarget = new ArrayList<DigitalResource>();
			if (entity.getAttachments() != null) {
				perksTarget.addAll(entity.getAttachments());
			}
			perksSource.removeAll(perksTarget);

			attachmentsDM = new DualListModel<DigitalResource>(perksSource, perksTarget);
		}

		return attachmentsDM;
	}

	public void setAttachmentsDM(DualListModel<DigitalResource> attachmentsDM) {
		this.attachmentsDM = attachmentsDM;
	}

	public DualListModel<WalletTemplate> getWalletTemplatesDM() {
		if (walletTemplatesDM == null) {
			List<WalletTemplate> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = walletTemplateService.list(entity.getProvider(), true);
			} else {
				perksSource = walletTemplateService.list(currentUser.getProvider(), true);
			}

			List<WalletTemplate> perksTarget = new ArrayList<WalletTemplate>();
			if (entity.getWalletTemplates() != null) {
				perksTarget.addAll(entity.getWalletTemplates());
			}
			perksSource.removeAll(perksTarget);

			walletTemplatesDM = new DualListModel<WalletTemplate>(perksSource, perksTarget);
		}

		return walletTemplatesDM;
	}

	public void setWalletTemplatesDM(DualListModel<WalletTemplate> walletTemplatesDM) {
		this.walletTemplatesDM = walletTemplatesDM;
	}

	public DualListModel<BusinessAccountModel> getBamDM() {
		if (bamDM == null) {
			List<BusinessAccountModel> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = businessAccountModelService.list(entity.getProvider(), true);
			} else {
				perksSource = businessAccountModelService.list(currentUser.getProvider(), true);
			}

			List<BusinessAccountModel> perksTarget = new ArrayList<BusinessAccountModel>();
			if (entity.getBusinessAccountModels() != null) {
				perksTarget.addAll(entity.getBusinessAccountModels());
			}
			perksSource.removeAll(perksTarget);

			bamDM = new DualListModel<BusinessAccountModel>(perksSource, perksTarget);
		}

		return bamDM;
	}

	public void setBamDM(DualListModel<BusinessAccountModel> bamDM) {
		this.bamDM = bamDM;
	}

	public DualListModel<Channel> getChannelDM() {
		if (channelDM == null) {
			List<Channel> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = channelService.list(entity.getProvider(), true);
			} else {
				perksSource = channelService.list(currentUser.getProvider(), true);
			}

			List<Channel> perksTarget = new ArrayList<Channel>();
			if (entity.getBusinessAccountModels() != null) {
				perksTarget.addAll(entity.getChannels());
			}
			perksSource.removeAll(perksTarget);

			channelDM = new DualListModel<Channel>(perksSource, perksTarget);
		}

		return channelDM;
	}

	public void onNameChange() {
		if (StringUtils.isBlank(entity.getCode())) {
			entity.setCode(entity.getName());
		}
	}

	public void setChannelDM(DualListModel<Channel> channelDM) {
		this.channelDM = channelDM;
	}

	public boolean isBundleTemplate(ProductTemplate pt) {
		return pt instanceof BundleTemplate;
	}

	public String getEditMode() {
		return editMode;
	}

	public void setEditMode(String editMode) {
		this.editMode = editMode;
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}
	
	public boolean displayStatus(ProductTemplate e) {
		Date now = new Date();

		if ((Arrays.asList(LifeCycleStatusEnum.ACTIVE, LifeCycleStatusEnum.LAUNCHED, LifeCycleStatusEnum.IN_TEST).contains(e.getLifeCycleStatus()))) {
			if (e.getValidFrom() == null && e.getValidTo() == null) {
				return true;
			} else if (e.getValidFrom() != null && e.getValidTo() != null && (now.compareTo(e.getValidFrom()) >= 0 && now.compareTo(e.getValidTo()) <= 0)) {
				return true;
			} else if ((e.getValidFrom() != null && e.getValidTo() == null) && now.compareTo(e.getValidFrom()) > 0) {
				return true;
			} else if ((e.getValidFrom() == null && e.getValidTo() != null) && now.compareTo(e.getValidTo()) < 0) {
				return true;
			}
		}

		return false;
	}

	public void newProductChargeTemplate(){
		productChargeTemplate=new ProductChargeTemplate();
		productChargeTemplate.setProvider(getCurrentProvider());
	}
	
	public void editProductChargeTemplate(ProductChargeTemplate productChargeTemplate) {
		this.productChargeTemplate = productChargeTemplate;
	}
	
	public void deleteProductChargeTemplate(Long id) throws BusinessException {
		ProductChargeTemplate productCharge=productChargeTemplateService.findById(id);
		entity.getProductChargeTemplates().remove(productCharge);
		productCharge.getProductTemplates().remove(entity);
		entity=getPersistenceService().update(entity, getCurrentUser());
		messages.info(new BundleKey("messages", "delete.successful"));
	}
	
	public void saveProductChargeTemplate(){
		log.info("saveProductChargeTemplate getObjectId=" + getObjectId());

		try {
			if(productChargeTemplate==null){
				return;
			}
			entity = productTemplateService.refreshOrRetrieve(entity); // TODO this line might cause an issue when after update of charge template service template can not be saved
            productChargeTemplate = productChargeTemplateService.refreshOrRetrieve(productChargeTemplate);
        	if(!productChargeTemplate.getProductTemplates().contains(entity)){
        		productChargeTemplate.getProductTemplates().add(entity);
        	}
			entity.getProductChargeTemplates().add(productChargeTemplate);
			productChargeTemplateService.update(productChargeTemplate, getCurrentUser());
            messages.info(new BundleKey("messages", "save.successful"));
            newProductChargeTemplate();
		} catch (Exception e){
			log.error("error when saving productCharge",e);
            messages.error("error when creating product charge:"+e.getMessage());
		}
	}

	public ProductChargeTemplate getProductChargeTemplate() {
		return productChargeTemplate;
	}

	public void setProductChargeTemplate(ProductChargeTemplate productChargeTemplate) {
		this.productChargeTemplate = productChargeTemplate;
	}
	
	
}
