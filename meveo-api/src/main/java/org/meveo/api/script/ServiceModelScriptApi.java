package org.meveo.api.script;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.ScriptInstanceApi;
import org.meveo.api.dto.script.ServiceModelScriptDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.model.scripts.ServiceModelScript;
import org.meveo.service.script.ServiceModelScriptService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class ServiceModelScriptApi extends BaseApi {

    @Inject
    private ServiceModelScriptService serviceModelScriptService;

    @Inject
    private ScriptInstanceApi scriptInstanceApi;

    public void create(ServiceModelScriptDto postData, User currentUser) throws MeveoApiException, BusinessException {

        scriptInstanceApi.checkDtoAndUpdateCode(postData);

        if (serviceModelScriptService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(ServiceModelScript.class, postData.getCode());
        }

        ServiceModelScript serviceModelScript = new ServiceModelScript();
        serviceModelScript.setCode(postData.getCode());
        serviceModelScript.setDescription(postData.getDescription());

        if (postData.getType() != null) {
            serviceModelScript.setSourceTypeEnum(postData.getType());
        } else {
            serviceModelScript.setSourceTypeEnum(ScriptSourceTypeEnum.JAVA);
        }
        serviceModelScript.setScript(postData.getScript());

        serviceModelScriptService.create(serviceModelScript, currentUser);
    }

    public void update(ServiceModelScriptDto postData, User currentUser) throws MeveoApiException, BusinessException {

        scriptInstanceApi.checkDtoAndUpdateCode(postData);

        ServiceModelScript serviceModelScript = serviceModelScriptService.findByCode(postData.getCode(), currentUser.getProvider());
        if (serviceModelScript == null) {
            throw new EntityDoesNotExistsException(ServiceModelScript.class, postData.getCode());
        }

        serviceModelScript.setDescription(postData.getDescription());
        if (postData.getType() != null) {
            serviceModelScript.setSourceTypeEnum(postData.getType());
        } else {
            serviceModelScript.setSourceTypeEnum(ScriptSourceTypeEnum.JAVA);
        }
        serviceModelScript.setScript(postData.getScript());

        serviceModelScriptService.update(serviceModelScript, currentUser);
    }

    public void createOrUpdate(ServiceModelScriptDto postData, User currentUser) throws MeveoApiException, BusinessException {

        scriptInstanceApi.checkDtoAndUpdateCode(postData);

        ServiceModelScript ServiceModelScript = serviceModelScriptService.findByCode(postData.getCode(), currentUser.getProvider());
        if (ServiceModelScript == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }

    public void delete(String code, User currentUser) throws EntityDoesNotExistsException {
        ServiceModelScript ServiceModelScript = serviceModelScriptService.findByCode(code, currentUser.getProvider());
        if (ServiceModelScript == null) {
            throw new EntityDoesNotExistsException(ServiceModelScript.class, code);
        }

        serviceModelScriptService.remove(ServiceModelScript);
    }

    public ServiceModelScriptDto get(String code, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        ServiceModelScript ServiceModelScript = serviceModelScriptService.findByCode(code, provider);
        if (ServiceModelScript == null) {
            throw new EntityDoesNotExistsException(ServiceModelScript.class, code);
        }

        return new ServiceModelScriptDto(ServiceModelScript);
    }
}