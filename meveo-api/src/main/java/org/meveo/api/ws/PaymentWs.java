package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.dto.response.payment.DDRequestLotOpsResponseDto;
import org.meveo.model.payments.DDRequestOpStatusEnum;

@WebService
public interface PaymentWs extends IBaseWs {

	@WebMethod
	public ActionStatus create(@WebParam(name = "PaymentDto")PaymentDto postData);

	@WebMethod
	public CustomerPaymentsResponse list(@WebParam(name = "customerAccountCode")String customerAccountCode);
	
	@WebMethod
	ActionStatus createDDRequestLotOp(@WebParam(name="ddrequestLotOp")DDRequestLotOpDto ddrequestLotOp);
	
	@WebMethod
	DDRequestLotOpsResponseDto listDDRequestLotops(@WebParam(name="status")DDRequestOpStatusEnum status);

}
