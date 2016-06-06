package org.meveo.admin.action.catalog;

import org.meveo.admin.action.CustomFieldSearchBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class OfferTemplateCategoryBean extends CustomFieldSearchBean<OfferTemplateCategory> {

	private static final long serialVersionUID = 1L;

	@Inject
	private OfferTemplateCategoryService offerTemplateCategoryService;

	private UploadedFile uploadedFile;

	private List<OfferTemplateCategory> offerTemplateCategories;

	public OfferTemplateCategoryBean() {
		super(OfferTemplateCategory.class);
	}

	@Override
	protected IPersistenceService<OfferTemplateCategory> getPersistenceService() {
		return offerTemplateCategoryService;
	}

	@Override
	protected String getListViewName() {
		return "offerTemplateCategories";
	}

	@Override
    @ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		if (entity.getOfferTemplateCategory() != null && entity.getOfferTemplateCategory().getLevel() == 3) {
			throw new BusinessException("Max level for offer template category.");
		}

		if (!entity.isTransient()) {
			// check if level is changed
			if (entity.getOfferTemplateCategory() != null) {
				entity.setLevel(entity.getOfferTemplateCategory().getLevel() + 1);
			}
		}

		return super.saveOrUpdate(killConversation);
	}

	public void handleFileUpload(FileUploadEvent event) throws BusinessException {
		uploadedFile = event.getFile();

		if (uploadedFile != null) {
			byte[] contents = uploadedFile.getContents();
			try {
				entity.setImage(new SerialBlob(contents));
			} catch (SQLException e) {
				entity.setImage(null);
			}
			entity.setImageContentType(uploadedFile.getContentType());

			saveOrUpdate(entity);

			initEntity();

			FacesMessage message = new FacesMessage("Succesful", uploadedFile.getFileName() + " is uploaded.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public void submitActionListener() throws SerialException, SQLException {
		log.debug("save image");

		if (uploadedFile != null) {
			byte[] contents = uploadedFile.getContents();
			try {
				entity.setImage(new SerialBlob(contents));
			} catch (SQLException e) {
				log.error(e.getMessage());
			}
			entity.setImageContentType(uploadedFile.getContentType());

			FacesMessage message = new FacesMessage("Succesful", uploadedFile.getFileName() + " is uploaded.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public List<OfferTemplateCategory> getParentHierarchy() {
		if (offerTemplateCategories == null) {
			offerTemplateCategories = new ArrayList<OfferTemplateCategory>();

			List<OfferTemplateCategory> result = offerTemplateCategoryService.listAllRootsExceptId(getEntity().getId());

			for (OfferTemplateCategory a : result) {
				offerTemplateCategories.add(a);
				if (a.getChildren() != null && a.getChildren().size() > 0) {
					for (OfferTemplateCategory b : a.getChildren()) {
						offerTemplateCategories.add(b);
						if (b.getChildren() != null && b.getChildren().size() > 0) {
							for (OfferTemplateCategory c : b.getChildren()) {
								offerTemplateCategories.add(c);
							}
						}
					}
				}
			}
		}

		return offerTemplateCategories;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	private ActionListener submitActionListener = new ActionListener() {
		@Override
		public void processAction(ActionEvent event) throws AbortProcessingException {
			log.debug("save image");
		}
	};

	public ActionListener getSubmitActionListener() {
		return submitActionListener;
	}

	public void setSubmitActionListener(ActionListener submitActionListener) {
		this.submitActionListener = submitActionListener;
	}

}
