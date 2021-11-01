package org.meveo.service.payments.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.dunning.DunningTemplate;
import org.meveo.model.payments.ActionChannelEnum;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.TradingLanguageService;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class DunningTemplateService extends BusinessService<DunningTemplate> {

    @Inject
    private TradingLanguageService tradingLanguageService;

    @Override
    public void create(DunningTemplate template) throws BusinessException {
        if (template.getCode() == null || findByCode(template.getCode()) != null) {
            throw new BusinessApiException("code should be not null or already used by another template.");
        }
        if (template.getLanguage() == null) {
            throw new BusinessApiException("Language is mandatory.");
        }else{
            TradingLanguage tradingLanguage = tradingLanguageService.findById(template.getLanguage().getId());
            if(tradingLanguage == null){
                throw new EntityDoesNotExistsException("Language with id"+template.getLanguage().getId()+" does not exists.");
            }
            template.setLanguage(tradingLanguage);
        }
        if (template.getChannel() == null) {
            throw new BusinessApiException("channel is mandatory.");
        }
        if (ActionChannelEnum.EMAIL.equals(template.getChannel()) && (template.getSubject() == null || template.getSubject().isBlank())) {
            throw new BusinessApiException("subject is mandatory when template channel is of type EMAIL.");
        }
        super.create(template);
    }

    public void duplicate(DunningTemplate template) {
        detach(template);
        template.setId(null);
        template.setCode(template.getCode() + " - copy");
        super.create(template);
    }
}
