package org.meveo.api.rest.cpq.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.meveo.api.cpq.ContractApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.ContractDto;
import org.meveo.api.dto.cpq.ContractItemDto;
import org.meveo.api.dto.cpq.ContractListResponsDto;
import org.meveo.api.dto.cpq.TradingContractItemDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.GetContractDtoResponse;
import org.meveo.api.dto.response.cpq.GetContractLineDtoResponse;
import org.meveo.api.dto.response.cpq.GetListContractDtoResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.cpq.ContractRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.cpq.enums.ContractAccountLevel;

@Interceptors({ WsRestApiInterceptor.class })
public class ContractRsImpl  extends BaseRs implements ContractRs {

	@Inject
	private ContractApi contractApi;
	
	@Override
	public Response createContract(ContractDto contractDto) {
		try {
			Long id = contractApi.createContract(contractDto);
			return Response.ok(Collections.singletonMap("id", id)).build();
		} catch (MeveoApiException e) {
			return errorResponse(e);
		}
	}

	@Override
	public Response updateContract(ContractDto contractDto) {
		ActionStatus result = new ActionStatus();
		 try {
			 contractApi.updateContract(contractDto);
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
	public Response findByCode(ContractAccountLevel contractAccountLevel, String accountCode) {
		GetListContractDtoResponse result = new GetListContractDtoResponse();
		 try {
		    	result.setContracts(contractApi.findContractAccountLevel(contractAccountLevel, accountCode));
		        return Response.ok(result).build();
		    } catch (MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
		    }
	}

	@Override
	public Response updateStatus(String contractCode, String status) {
		ActionStatus result = new ActionStatus();
		try {
			contractApi.updateStatus(contractCode, status);
			return Response.ok(result).build();
		} catch (MeveoApiException e) {
			return errorResponse(e, result);
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

	@Override
	public Response createContractLine(ContractItemDto contractItemDto) {
		 try {
	            Long id = contractApi.createContractLine(contractItemDto);
	            return Response.ok(Collections.singletonMap("id", id)).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e);
	        }
	}

	@Override
	public Response updateContractLine(ContractItemDto contractItemDto) {
		ActionStatus result = new ActionStatus();
		try {
			contractApi.updateContractLine(contractItemDto);
			return Response.ok(result).build();
		} catch (MeveoApiException e) {
			return errorResponse(e, result);
		}
	}

	@Override
	public Response deleteContractLine(String contractItemCode) {
		ActionStatus result = new ActionStatus();
		 try {
			 contractApi.deleteContractLine(contractItemCode);
	            return Response.ok(result).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, result);
	        }
	}

	@Override
	public Response getContractLine(String contractItemCode) {
		GetContractLineDtoResponse result = new GetContractLineDtoResponse();
		 try {
		    	result.setContractItem(contractApi.getContractLines(contractItemCode));
		        return Response.ok(result).build();
		    } catch (MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
		    }
	}

	@Override
	public Response duplicateContract(String contractCode) {
		try {
			Long id = contractApi.duplicateContract(contractCode);
			return Response.ok(Collections.singletonMap("id", id)).build();
		} catch (MeveoApiException e) {
			return errorResponse(e);
		}
	}

	@Override
	public Response createTradingContractItem(TradingContractItemDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			result.setEntityId(contractApi.createTradingContractItem(postData).getId());
		} catch (Exception e) {
			processException(e, result);
		}

		return Response.ok(result).build();
	}

	@Override
	public Response updateTradingContractItem(Long tradingContractItemId, TradingContractItemDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			result.setEntityId(contractApi.updateTradingContractItem(tradingContractItemId, postData).getId());
		} catch (Exception e) {
			processException(e, result);
		}

		return Response.ok(result).build();
	}

	@Override
	public Response deleteTradingContractItem(Long tradingContractItemId) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			contractApi.deleteTradingContractItem(tradingContractItemId);
		} catch (Exception e) {
			processException(e, result);
		}

		return Response.ok(result).build();
	}

}
