package org.meveo.service.payments.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.dunning.DunningModeEnum;
import org.meveo.model.dunning.DunningTemplate;
import org.meveo.model.payments.ActionChannelEnum;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.TradingLanguageService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class DunningTemplateService extends BusinessService<DunningTemplate> {

    @Inject
    private TradingLanguageService tradingLanguageService;

    @Override
    public void create(DunningTemplate template) throws BusinessException {
        validate(template);
        super.create(template);
    }

    private void validate(DunningTemplate template) {
        if(isCodeNullOrAlreadyUsed(template)){
            throw new BusinessApiException("code should be not null or already used by another template.");
        }
        if (template.getLanguage() == null) {
            throw new BusinessApiException("Language is mandatory.");
        }else{
            TradingLanguage tradingLanguage = tradingLanguageService.findById(template.getLanguage().getId());
            if(tradingLanguage == null){
                throw new EntityDoesNotExistsException("Language with id"+ template.getLanguage().getId()+" does not exists.");
            }
            template.setLanguage(tradingLanguage);
        }
        if (template.getChannel() == null) {
            throw new BusinessApiException("channel is mandatory.");
        }
        if (ActionChannelEnum.EMAIL.equals(template.getChannel()) && (template.getSubject() == null || template.getSubject().isBlank())) {
            throw new BusinessApiException("subject is mandatory when template channel is of type EMAIL.");
        }
    }

    private boolean isCodeNullOrAlreadyUsed(DunningTemplate template) {
        if (template.getCode() == null) {
            return true;
        }else{
            DunningTemplate foundTemplate = findByCode(template.getCode());
            if(foundTemplate != null && !foundTemplate.getId().equals(template.getId())){
                return true;
            }
        }
        return false;
    }

    public void duplicate(DunningTemplate template) {
        detach(template);
        template.setId(null);
        template.setCode(findDuplicateCode(template));
        template.setActive(false);
        super.create(template);
    }

    @Override
    public DunningTemplate update(DunningTemplate template) throws BusinessException {
        validate(template);
        if(!template.isActive()){
            Query query = getEntityManager().createNamedQuery("DunningTemplate.isDunningTemplatedRelatedToAnActiveDunningLevel");
            query.setParameter("templateId", template.getId());
            List resultList = query.setMaxResults(1).getResultList();
            if(!resultList.isEmpty()){
                throw new BusinessApiException("error de-activating a template related to an active dunning level.");
            }
        }
        return super.update(template);
    }

    /**
     * Update DunningTemplate after creating a new DunningSettings
     * Set true in active field if the DunningTemplates have the same DunningMode as DunningSettings else false in the active field
     * @param dunningModeEnum {@link DunningModeEnum}
     * @throws BusinessException {@link BusinessException}
     */
    public void updateDunningTemplateByDunningMode(DunningModeEnum dunningModeEnum) throws BusinessException {
        final List<DunningTemplate> dunningTemplates = super.list();
        dunningTemplates.forEach((dunningTemplate -> {
            if(dunningTemplate.getTypeDunningMode().equals(dunningModeEnum)) {
                dunningTemplate.setActive(true);
                dunningTemplate.getAuditable().setUpdated(new Date());
                super.update(dunningTemplate);
            } else {
                dunningTemplate.setActive(false);
                dunningTemplate.getAuditable().setUpdated(new Date());
                super.update(dunningTemplate);
            }
        }));
    }
}
