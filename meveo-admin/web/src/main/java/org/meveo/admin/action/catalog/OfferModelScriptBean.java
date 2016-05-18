package org.meveo.admin.action.catalog;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.scripts.CustomScript;
import org.meveo.model.scripts.OfferModelScript;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.script.GenericScriptService;
import org.meveo.service.script.OfferModelScriptService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class OfferModelScriptBean extends BaseBean<OfferModelScript> {

    private static final long serialVersionUID = -4034706786961656074L;

    @Inject
    private OfferModelScriptService offerModelScriptService;
    
    @Inject
    private GenericScriptService genericScriptService;

    @Inject
    private BusinessOfferModelService businessOfferModelService;

    private Long bomId;

    public OfferModelScriptBean() {
        super(OfferModelScript.class);
    }

    @Override
    public OfferModelScript initEntity() {
        if (bomId != null) {
            return super.initEntity();
        } else {
            return null;
        }
    }

    @Override
    public OfferModelScript initEntity(Long id) {
        super.initEntity(id);

        if (entity.isError()) {
            offerModelScriptService.compileScript(entity, true);
        }

        return entity;
    }

    @Override
    protected IPersistenceService<OfferModelScript> getPersistenceService() {
        return offerModelScriptService;
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("provider");
    }

    public void testCompilation() {
        offerModelScriptService.compileScript(entity, true);
        if (!entity.isError()) {
            messages.info(new BundleKey("messages", "scriptInstance.compilationSuccessfull"));
        }
    }

    @SuppressWarnings("static-access")
	@Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        entity.setCode(offerModelScriptService.getFullClassname(entity.getScript()));

        CustomScript scriptDuplicate =  genericScriptService.findByCode(entity.getCode(), getCurrentProvider());
        if (scriptDuplicate != null && !scriptDuplicate.getId().equals(entity.getId())) {
            messages.error(new BundleKey("messages", "scriptInstance.scriptAlreadyExists"), entity.getCode());
            return null;
        }

        try {
            String result = super.saveOrUpdate(killConversation);

            // find bom
            BusinessOfferModel businessOfferModel = businessOfferModelService.findById(bomId);
            businessOfferModel.setScript(entity);
            businessOfferModelService.update(businessOfferModel, getCurrentUser());

            if (entity.isError()) {
                return null;
            }

            return result;
        } catch (Exception e) {
            messages.error(e.getMessage());
            return null;
        }
    }

    public Long getBomId() {
        return bomId;
    }

    public void setBomId(Long bomId) {
        this.bomId = bomId;
    }

}