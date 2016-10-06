package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.CounterTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class CounterTemplateApi extends BaseApi {

    @Inject
    private CounterTemplateService<CounterTemplate> counterTemplateService;

    @Inject
    private CalendarService calendarService;

    public void create(CounterTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getCalendar())) {
            missingParameters.add("calendar");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();

        if (counterTemplateService.findByCode(postData.getCode(), provider) != null) {
            throw new EntityAlreadyExistsException(CounterTemplate.class, postData.getCode());
        }
        Calendar calendar = calendarService.findByCode(postData.getCalendar(), provider);
        if (calendar == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
        }

        CounterTemplate counterTemplate = new CounterTemplate();
        counterTemplate.setProvider(provider);
        counterTemplate.setCode(postData.getCode());
        counterTemplate.setDescription(postData.getDescription());
        counterTemplate.setUnityDescription(postData.getUnity());
        if (postData.getType() != null) {
            counterTemplate.setCounterType(postData.getType());
        }
        counterTemplate.setCeiling(postData.getCeiling());
        counterTemplate.setDisabled(postData.isDisabled());
        counterTemplate.setCalendar(calendar);
        if (postData.getCounterLevel() != null) {
            counterTemplate.setCounterLevel(postData.getCounterLevel());
        }
        counterTemplate.setCeilingExpressionEl(postData.getCeilingExpressionEl());

        counterTemplateService.create(counterTemplate, currentUser);
    }

    public void update(CounterTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getCalendar())) {
            missingParameters.add("calendar");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();

        CounterTemplate counterTemplate = counterTemplateService.findByCode(postData.getCode(), provider);
        if (counterTemplate == null) {
            throw new EntityDoesNotExistsException(CounterTemplate.class, postData.getCode());
        }
        Calendar calendar = calendarService.findByCode(postData.getCalendar(), provider);
        if (calendar == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
        }

        counterTemplate.setDescription(postData.getDescription());
        counterTemplate.setUnityDescription(postData.getUnity());
        if (postData.getType() != null) {
            counterTemplate.setCounterType(postData.getType());
        }
        counterTemplate.setCeiling(postData.getCeiling());
        counterTemplate.setDisabled(postData.isDisabled());
        counterTemplate.setCalendar(calendar);
        if (postData.getCounterLevel() != null) {
            counterTemplate.setCounterLevel(postData.getCounterLevel());
        }
        counterTemplate.setCeilingExpressionEl(postData.getCeilingExpressionEl());

        counterTemplateService.update(counterTemplate, currentUser);
    }

    public CounterTemplateDto find(String code, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("counterTemplateCode");
            handleMissingParameters();
        }
        CounterTemplate counterTemplate = counterTemplateService.findByCode(code, provider);
        if (counterTemplate == null) {
            throw new EntityDoesNotExistsException(CounterTemplate.class, code);
        }

        return new CounterTemplateDto(counterTemplate);
    }

    public void remove(String code, User currentUser) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("counterTemplateCode");
            handleMissingParameters();
        }
        CounterTemplate counterTemplate = counterTemplateService.findByCode(code, currentUser.getProvider());
        if (counterTemplate == null) {
            throw new EntityDoesNotExistsException(CounterTemplate.class, code);
        }

        counterTemplateService.remove(counterTemplate, currentUser);
    }

    public void createOrUpdate(CounterTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (counterTemplateService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }
}