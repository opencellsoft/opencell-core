package org.meveo.api.payment;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.dto.payment.DDRequestLotOpsDto;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.service.payments.impl.DDRequestLotOpService;

/**
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jul 11, 2016 7:30:19 PM
 **/
@Stateless
public class DDRequestLotOpApi extends BaseApi {

	@Inject
	private DDRequestLotOpService ddrequestLotOpService;
	
	public void create(DDRequestLotOpDto dto,User currentUser) throws BusinessException, MissingParameterException{
		if(StringUtils.isBlank(dto.getFileFormat())){
			this.missingParameters.add("fileFormat");
		}
		if(StringUtils.isBlank(dto.getFromDueDate())){
			this.missingParameters.add("fromDueDate");
		}
		if(StringUtils.isBlank(dto.getToDueDate())){
			this.missingParameters.add("toDueDate");
		}
		this.handleMissingParameters();
		
		DDRequestLotOp lot=new DDRequestLotOp();
		lot.setFromDueDate(dto.getFromDueDate());
		lot.setToDueDate(dto.getToDueDate());
		lot.setFileFormat(dto.getFileFormat());
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
		ddrequestLotOpService.create(lot, currentUser);
	}
	
	public DDRequestLotOpsDto listDDRequestLotOps(Date fromDueDate,Date toDueDate,DDRequestOpStatusEnum status,Provider currentProvider){
		DDRequestLotOpsDto result=new DDRequestLotOpsDto();
		List<DDRequestLotOp> lots=ddrequestLotOpService.findByDateStatus(fromDueDate,toDueDate,status, currentProvider);
		if(lots!=null&&!lots.isEmpty()){
			for(DDRequestLotOp lot:lots){
				result.getDdrequestLotOps().add(new DDRequestLotOpDto(lot));
			}
		}
		return result;
	}
	
}

