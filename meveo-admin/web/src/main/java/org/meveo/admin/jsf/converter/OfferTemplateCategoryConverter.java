package org.meveo.admin.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.meveo.admin.action.admin.CurrentProvider;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;

@FacesConverter("offerTemplateCategoryConverter")
public class OfferTemplateCategoryConverter implements Converter {

	@Inject
	private OfferTemplateCategoryService offerTemplateCategoryService;
	
	@Inject
    @CurrentProvider
    protected Provider provider;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if(StringUtils.isBlank(value)){
			return null;
		}
		OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(value, provider);
		return offerTemplateCategory;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value instanceof OfferTemplateCategory){
			return ((OfferTemplateCategory) value).getCode();
		}
		return null;
	}
}
