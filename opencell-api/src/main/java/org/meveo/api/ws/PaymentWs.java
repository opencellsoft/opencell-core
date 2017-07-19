package org.meveo.api.ws;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.CardTokenDto;
import org.meveo.api.dto.payment.CardTokenResponseDto;
import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.dto.payment.DoPaymentRequestDto;
import org.meveo.api.dto.payment.DoPaymentResponseDto;
import org.meveo.api.dto.payment.ListCardTokenResponseDto;
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
	
	/**
	 * create a ddrequestLotOp by dto
	 * @param ddrequestLotOp
	 * @return
	 */
	@WebMethod
	ActionStatus createDDRequestLotOp(@WebParam(name="ddrequestLotOp")DDRequestLotOpDto ddrequestLotOp);
	/**
	 * list ddrequestLotOps by fromDueDate,toDueDate,status
	 * @param fromDueDate
	 * @param toDueDate
	 * @param status
	 * @return
	 */
	@WebMethod
	DDRequestLotOpsResponseDto listDDRequestLotops(@WebParam(name="fromDueDate")Date fromDueDate,@WebParam(name="toDueDate")Date toDueDate,@WebParam(name="status")DDRequestOpStatusEnum status);
	
	@WebMethod
	public CardTokenResponseDto createCardToken(@WebParam(name = "CardTokenRequest")CardTokenDto cardTokenRequestDto);
	
	@WebMethod
	public ActionStatus updateCardToken(@WebParam(name = "CardTokenRequest")CardTokenDto cardTokenRequestDto);
	
	@WebMethod
	public ActionStatus removeCardToken(@WebParam(name = "id")Long id);
	
	@WebMethod
	public ListCardTokenResponseDto listCardToken(@WebParam(name = "customerAccountId")Long customerAccountId,@WebParam(name = "customerAccountCode")String customerAccountCode);
	
	@WebMethod
	public CardTokenResponseDto findCardToken(@WebParam(name = "id")Long id);
	
	@WebMethod
	public DoPaymentResponseDto doPayment(@WebParam(name = "DoPaymentRequest")DoPaymentRequestDto doPaymentRequestDto);

}
