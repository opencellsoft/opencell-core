package org.meveo.api.rest.cpq.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.api.cpq.ContractApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.ContractDto;
import org.meveo.api.dto.cpq.ContractListResponsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.GetContractDtoResponse;
import org.meveo.api.dto.response.cpq.GetListContractDtoResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.rest.cpq.ContractRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.cpq.enums.ContractAccountLevel;

public class ContractRsImpl  extends BaseRs implements ContractRs {

	@Inject
	private ContractApi contractApi;
	
	@Override
	public Response createContract(ContractDto contractDto) {
		 try {
	            Long id = contractApi.CreateContract(contractDto);
	            return Response.ok(Collections.singletonMap("id", id)).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e);
	        }
	}

	@Override
	public Response updateContract(ContractDto contractDto) {
		ActionStatus result = new ActionStatus();
		 try {
			 contractApi.updateContract(contractDto);;
	            return Response.ok(result).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, result);
	        }
	}

	@Override
	public Response deleteContract(String contractCode) {
		ActionStatus result = new ActionStatus();
		 try {
			 contractApi.deleteContract(contractCode);
	            return Response.ok(result).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, result);
	        }
	}

	@Override
	public Response findByCode(String contractCode) {
		GetContractDtoResponse result = new GetContractDtoResponse();
		 try {
		    	result.setContract(contractApi.findContract(contractCode));
		        return Response.ok(result).build();
		    } catch (MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
		    }
	}

	@Override
	public Response findByCode(ContractAccountLevel contractAccountLevel, String contractCode) {
		GetListContractDtoResponse result = new GetListContractDtoResponse();
		 try {
		    	result.setContracts(contractApi.findContract(contractAccountLevel, contractCode));
		        return Response.ok(result).build();
		    } catch (MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
		    }
	}

	@Override
	public Response ListPost(PagingAndFiltering pagingAndFiltering) {
		ActionStatus status = new ActionStatus();
		ContractListResponsDto result = new ContractListResponsDto();
		 try {
		    	result.setActionStatus(status);
		    	result = contractApi.list(pagingAndFiltering);
		        return Response.ok(result).build();
		    } catch (MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
		    }
	}

}
