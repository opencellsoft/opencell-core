package org.meveo.api.wf;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.WFActionDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.wf.WFAction;
import org.meveo.model.wf.WFTransition;
import org.meveo.service.wf.WFActionService;

@Stateless
public class WFActionApi extends BaseApi {

    @Inject
    private WFActionService wfActionService;

    /**
     * 
     * @param wfActionDto
     * 
     * @throws MissingParameterException Missing one or more parameters
     * @throws EntityDoesNotExistsException Reference to an entity was not found
     * @throws EntityAlreadyExistsException Entity can not be created as it already exists
     * @throws BusinessException General business exception
     */
    public void create(WFTransition wfTransition, WFActionDto wfActionDto)
            throws MissingParameterException, BusinessException {
        validateDto(wfActionDto, false);
        WFAction wfAction = fromDTO(wfActionDto, null);
        wfAction.setWfTransition(wfTransition);
        wfActionService.create(wfAction);
    }

    /**
     * 
     * @param wfActionDto
     * 
     * @throws MissingParameterException Missing one or more parameters
     * @throws EntityDoesNotExistsException Reference to an entity was not found
     * @throws BusinessException General business exception
     * @throws BusinessApiException General business exception
     */
    public void update(WFTransition wfTransition, WFActionDto wfActionDto) throws MissingParameterException, EntityDoesNotExistsException, BusinessException, BusinessApiException {
        validateDto(wfActionDto, true);

        WFAction wfAction = wfActionService.findWFActionByUUID(wfActionDto.getUuid());

        if (wfAction == null) {
            throw new EntityDoesNotExistsException(WFAction.class.getName() + "with uuid=" + wfActionDto.getUuid());
        }
        if (!wfTransition.equals(wfAction.getWfTransition())) {
            throw new BusinessApiException("Workflow transition does not match");
        }

        wfAction = fromDTO(wfActionDto, wfAction);
        wfAction.setWfTransition(wfTransition);
        wfActionService.update(wfAction);
    }

    /**
     * 
     * @param wfActionDto
     * 
     * @throws MissingParameterException Missing one or more parameters
     * @throws EntityDoesNotExistsException Reference to an entity was not found
     * @throws EntityAlreadyExistsException Entity can not be created as it already exists
     * @throws BusinessException General business exception
     * @throws BusinessApiException General business exception
     */
    public void createOrUpdate(WFTransition wfTransition, WFActionDto wfActionDto)
            throws MissingParameterException, EntityDoesNotExistsException, BusinessException, BusinessApiException {
        WFAction wfAction = wfActionService.findWFActionByUUID(wfActionDto.getUuid());
        if (wfAction == null) {
            create(wfTransition, wfActionDto);
        } else {
            update(wfTransition, wfActionDto);
        }
    }

    /**
     * 
     * @param wfActionDto
     * @throws MissingParameterException Missing one or more parameters
     */
    public void validateDto(WFActionDto wfActionDto, boolean isUpdate) throws MissingParameterException {
        if (isUpdate && StringUtils.isBlank(wfActionDto.getUuid())) {
            missingParameters.add("uuid");
        }
        if (StringUtils.isBlank(wfActionDto.getActionEl())) {
            missingParameters.add("actionEl");
        }

        handleMissingParameters();
    }

    protected WFAction fromDTO(WFActionDto dto, WFAction wfActionToUpdate) {
        WFAction wfAction = new WFAction();
        if (wfActionToUpdate != null) {
            wfAction = wfActionToUpdate;
        }

        wfAction.setActionEl(dto.getActionEl());
        wfAction.setPriority(dto.getPriority());
        wfAction.setConditionEl(dto.getConditionEl());
        return wfAction;
    }

}
