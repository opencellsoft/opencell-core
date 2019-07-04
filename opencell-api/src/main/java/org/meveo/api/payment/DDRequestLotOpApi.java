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
import org.meveo.model.admin.Seller;
import org.meveo.model.filter.Filter;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.model.payments.PaymentOrRefundEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.payments.impl.DDRequestBuilderService;
import org.meveo.service.payments.impl.DDRequestLotOpService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * The Class DDRequestLotOpApi.
 *
 * @author anasseh
 * @author Tyshan Shi(tyshan@manaty.net)
 * @author Said Ramli
 * @lastModifiedVersion 5.3
 */
@Stateless
public class DDRequestLotOpApi extends BaseApi {

    /** The ddrequest lot op service. */
    @Inject
    private DDRequestLotOpService ddrequestLotOpService;

    /** The dd request builder service. */
    @Inject
    private DDRequestBuilderService ddRequestBuilderService;

    /** The filter service. */
    @Inject
    private FilterService filterService;
    
    /** The seller service. */
    @Inject
    private SellerService sellerService;    
    
    
    /** The script instance service. */
    @Inject
    private ScriptInstanceService scriptInstanceService;

    /**
     * Creates the.
     *
     * @param dto the dto
     * @throws BusinessException the business exception
     * @throws MissingParameterException the missing parameter exception
     * @throws EntityDoesNotExistsException the entity does not exists exception
     */
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
        if(dto.getPaymentOrRefundEnum() == null) {
            dto.setPaymentOrRefundEnum(PaymentOrRefundEnum.PAYMENT); 
        }

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
        
        Seller seller = null;
        if (!StringUtils.isBlank(dto.getSellerCode())) {
            seller = sellerService.findByCode(dto.getSellerCode());
            if (seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, dto.getSellerCode());
            }
        }

        DDRequestLotOp ddRequestLotOp = new DDRequestLotOp();
        
        
        if (isNotEmpty(dueDateRageScriptCode)) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(dto.getDueDateRageScriptCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, dueDateRageScriptCode);
            }
            ddRequestLotOp.setScriptInstance(scriptInstance);
        }
        ddRequestLotOp.setRecurrent(dto.getRecurrent());        
        ddRequestLotOp.setFromDueDate(dto.getFromDueDate());
        ddRequestLotOp.setToDueDate(dto.getToDueDate());
        ddRequestLotOp.setDdRequestBuilder(ddRequestBuilder);
        ddRequestLotOp.setPaymentOrRefundEnum(dto.getPaymentOrRefundEnum());
        ddRequestLotOp.setFilter(filter);
        ddRequestLotOp.setSeller(seller);
        if (StringUtils.isBlank(dto.getDdrequestOp())) {
            ddRequestLotOp.setDdrequestOp(DDRequestOpEnum.CREATE);
        } else {
            ddRequestLotOp.setDdrequestOp(dto.getDdrequestOp());
        }
        if (StringUtils.isBlank(dto.getStatus())) {
            ddRequestLotOp.setStatus(DDRequestOpStatusEnum.WAIT);
        } else {
            ddRequestLotOp.setStatus(dto.getStatus());
        }
        ddRequestLotOp.setErrorCause(dto.getErrorCause());
        ddrequestLotOpService.create(ddRequestLotOp);
    }

    /**
     * List DD request lot ops.
     *
     * @param fromDueDate the from due date
     * @param toDueDate the to due date
     * @param status the status
     * @return the list
     */
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
