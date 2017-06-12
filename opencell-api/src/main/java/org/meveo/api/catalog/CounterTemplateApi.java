package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.CounterTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class CounterTemplateApi extends BaseCrudApi<CounterTemplate, CounterTemplateDto> {

    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private CalendarService calendarService;

    public CounterTemplate create(CounterTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getCalendar())) {
            missingParameters.add("calendar");
        }

        handleMissingParametersAndValidate(postData);

        

        if (counterTemplateService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(CounterTemplate.class, postData.getCode());
        }
        Calendar calendar = calendarService.findByCode(postData.getCalendar());
        if (calendar == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
        }

        CounterTemplate counterTemplate = new CounterTemplate();
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
        counterTemplate.setNotificationLevels(postData.getNotificationLevels());

        counterTemplateService.create(counterTemplate);

        return counterTemplate;
    }

    public CounterTemplate update(CounterTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getCalendar())) {
            missingParameters.add("calendar");
        }

        handleMissingParametersAndValidate(postData);

        
        CounterTemplate counterTemplate = counterTemplateService.findByCode(postData.getCode());
        if (counterTemplate == null) {
            throw new EntityDoesNotExistsException(CounterTemplate.class, postData.getCode());
        }
        Calendar calendar = calendarService.findByCode(postData.getCalendar());
        if (calendar == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
        }
        counterTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode())?postData.getCode():postData.getUpdatedCode());
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
        counterTemplate.setNotificationLevels(postData.getNotificationLevels());

        counterTemplate = counterTemplateService.update(counterTemplate);

        return counterTemplate;
    }

    /* (non-Javadoc)
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
    @Override
    public CounterTemplateDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("counterTemplateCode");
            handleMissingParameters();
        }
        CounterTemplate counterTemplate = counterTemplateService.findByCode(code);
        if (counterTemplate == null) {
            throw new EntityDoesNotExistsException(CounterTemplate.class, code);
        }

        return new CounterTemplateDto(counterTemplate);
    }
    
    public void remove(String code) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("counterTemplateCode");
            handleMissingParameters();
        }
        CounterTemplate counterTemplate = counterTemplateService.findByCode(code);
        if (counterTemplate == null) {
            throw new EntityDoesNotExistsException(CounterTemplate.class, code);
        }

        counterTemplateService.remove(counterTemplate);
    }

    @Override
    public CounterTemplate createOrUpdate(CounterTemplateDto postData) throws MeveoApiException, BusinessException {    	 
        if (counterTemplateService.findByCode(postData.getCode()) == null) {
            return create(postData);
        } else {
            return update(postData);
        }
    }
}