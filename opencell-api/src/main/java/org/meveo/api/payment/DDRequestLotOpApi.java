package org.meveo.api.payment;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.filter.Filter;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.filter.FilterService;
import org.meveo.service.payments.impl.DDRequestBuilderService;
import org.meveo.service.payments.impl.DDRequestLotOpService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Jul 11, 2016 7:30:19 PM
 * @author Said Ramli
 * @lastModifiedVersion 5.2
 **/
@Stateless
public class DDRequestLotOpApi extends BaseApi {

    @Inject
    private DDRequestLotOpService ddrequestLotOpService;

    @Inject
    private DDRequestBuilderService ddRequestBuilderService;

    @Inject
    private FilterService filterService;
    
    @Inject
    private ScriptInstanceService scriptInstanceService;

    public void create(DDRequestLotOpDto dto) throws BusinessException, MissingParameterException, EntityDoesNotExistsException {
        if (StringUtils.isBlank(dto.getDdRequestBuilderCode())) {
            this.missingParameters.add("ddRequestBuilderCode");
        }
        String dueDateRageScriptCode = dto.getDueDateRageScriptCode();
        if (StringUtils.isBlank(dto.getFilterCode()) && StringUtils.isBlank(dueDateRageScriptCode)) {
            if (StringUtils.isBlank(dto.getFromDueDate())) {
                this.missingParameters.add("fromDueDate or filterCode");
            }
            if (StringUtils.isBlank(dto.getToDueDate())) {
                this.missingParameters.add("toDueDate or filterCode");
            }
        }
        this.handleMissingParameters();

        DDRequestBuilder ddRequestBuilder = ddRequestBuilderService.findByCode(dto.getDdRequestBuilderCode());

        if (ddRequestBuilder == null) {
            throw new EntityDoesNotExistsException(DDRequestBuilder.class, dto.getDdRequestBuilderCode());
        }

        Filter filter = null;
        if (!StringUtils.isBlank(dto.getFilterCode())) {
            filter = filterService.findByCode(dto.getFilterCode());
            if (filter == null) {
                throw new EntityDoesNotExistsException(Filter.class, dto.getFilterCode());
            }
        }

        DDRequestLotOp lotOp = new DDRequestLotOp();
        
        
        if (isNotEmpty(dueDateRageScriptCode)) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(dto.getDueDateRageScriptCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, dueDateRageScriptCode);
            }
            lotOp.setScriptInstance(scriptInstance);
        }
        lotOp.setRecurrent(dto.getRecurrent());        
        lotOp.setFromDueDate(dto.getFromDueDate());
        lotOp.setToDueDate(dto.getToDueDate());
        lotOp.setDdRequestBuilder(ddRequestBuilder);
        lotOp.setFilter(filter);
        if (StringUtils.isBlank(dto.getDdrequestOp())) {
            lotOp.setDdrequestOp(DDRequestOpEnum.CREATE);
        } else {
            lotOp.setDdrequestOp(dto.getDdrequestOp());
        }
        if (StringUtils.isBlank(dto.getStatus())) {
            lotOp.setStatus(DDRequestOpStatusEnum.WAIT);
        } else {
            lotOp.setStatus(dto.getStatus());
        }
        lotOp.setErrorCause(dto.getErrorCause());
        ddrequestLotOpService.create(lotOp);
    }

    public List<DDRequestLotOpDto> listDDRequestLotOps(Date fromDueDate, Date toDueDate, DDRequestOpStatusEnum status) {
        List<DDRequestLotOpDto> result = new ArrayList<DDRequestLotOpDto>();
        List<DDRequestLotOp> lots = ddrequestLotOpService.findByDateStatus(fromDueDate, toDueDate, status);
        if (lots != null && !lots.isEmpty()) {
            for (DDRequestLotOp lot : lots) {
                result.add(new DDRequestLotOpDto(lot));
            }
        }
        return result;
    }

}
