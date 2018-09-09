package org.meveo.api.payment;

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
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.service.payments.impl.DDRequestLotOpService;
import org.meveo.service.payments.impl.PaymentGatewayService;

/**
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @since Jul 11, 2016 7:30:19 PM
 **/
@Stateless
public class DDRequestLotOpApi extends BaseApi {

	@Inject
	private DDRequestLotOpService ddrequestLotOpService;
	
	@Inject
	private PaymentGatewayService paymentGatewayService;
	
	public void create(DDRequestLotOpDto dto) throws BusinessException, MissingParameterException, EntityDoesNotExistsException{
		if(StringUtils.isBlank(dto.getPaymentGatewayCode())){
			this.missingParameters.add("paymentGatewayCode");
		}
		if(StringUtils.isBlank(dto.getFromDueDate())){
			this.missingParameters.add("fromDueDate");
		}
		if(StringUtils.isBlank(dto.getToDueDate())){
			this.missingParameters.add("toDueDate");
		}
		this.handleMissingParameters();
		
		//DDRequestBuilder ddRequestBuilder  = paymentGatewayService.findByCode(dto.getPaymentGatewayCode());
		
		//if(paymentGateway == null) {
		  //  throw new EntityDoesNotExistsException(PaymentGateway.class, dto.getPaymentGatewayCode());
		//}
		
		DDRequestLotOp lot=new DDRequestLotOp();
		lot.setFromDueDate(dto.getFromDueDate());
		lot.setToDueDate(dto.getToDueDate());
		//lot.setDdRequestBuilder(ddRequestBuilder);
		if(StringUtils.isBlank(dto.getDdrequestOp())){
			lot.setDdrequestOp(DDRequestOpEnum.CREATE);
		}else{
			lot.setDdrequestOp(dto.getDdrequestOp());
		}
		if(StringUtils.isBlank(dto.getStatus())){
			lot.setStatus(DDRequestOpStatusEnum.WAIT);
		}else{
			lot.setStatus(dto.getStatus());
		}
		lot.setErrorCause(dto.getErrorCause());
		ddrequestLotOpService.create(lot);
	}
	
	public List<DDRequestLotOpDto> listDDRequestLotOps(Date fromDueDate,Date toDueDate,DDRequestOpStatusEnum status){
		List<DDRequestLotOpDto> result=new ArrayList<DDRequestLotOpDto>();
		List<DDRequestLotOp> lots=ddrequestLotOpService.findByDateStatus(fromDueDate,toDueDate,status);
		if(lots!=null&&!lots.isEmpty()){
			for(DDRequestLotOp lot:lots){
				result.add(new DDRequestLotOpDto(lot));
			}
		}
		return result;
	}
	
}

