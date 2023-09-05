package org.meveo.apiv2.electronicInvoicing.resource.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.electronicInvoicing.resource.ElectronicInvoicingResource;
import org.meveo.apiv2.electronicInvoicing.resource.IsoIcd;
import org.meveo.apiv2.electronicInvoicing.resource.UntdidAllowanceCode;
import org.meveo.apiv2.electronicInvoicing.resource.UntdidInvoiceCodeType;
import org.meveo.apiv2.electronicInvoicing.resource.UntdidInvoiceSubjectCode;
import org.meveo.apiv2.electronicInvoicing.resource.UntdidPaymentMeans;
import org.meveo.apiv2.electronicInvoicing.resource.UntdidTaxationCategory;
import org.meveo.apiv2.electronicInvoicing.resource.UntdidVatPaymentOption;
import org.meveo.apiv2.electronicInvoicing.resource.UntdidVatex;
import org.meveo.service.billing.impl.IsoIcdService;
import org.meveo.service.billing.impl.UntdidAllowanceCodeService;
import org.meveo.service.billing.impl.UntdidInvoiceCodeTypeService;
import org.meveo.service.billing.impl.UntdidInvoiceSubjectCodeService;
import org.meveo.service.billing.impl.UntdidPaymentMeansService;
import org.meveo.service.billing.impl.UntdidTaxationCategoryService;
import org.meveo.service.billing.impl.UntdidVatPaymentOptionService;
import org.meveo.service.billing.impl.UntdidVatexService;

@Interceptors({ WsRestApiInterceptor.class })
public class ElectronicInvoicingResourceImpl implements ElectronicInvoicingResource {

    @Inject private IsoIcdService isoIcdService;
    @Inject private UntdidAllowanceCodeService allowanceCodeService;
    @Inject private UntdidInvoiceCodeTypeService untdidInvoiceCodeTypeService;
    @Inject private UntdidInvoiceSubjectCodeService untdidInvoiceSubjectCodeService;
    @Inject private UntdidPaymentMeansService untdidPaymentMeansService;
    @Inject private UntdidTaxationCategoryService untdidTaxationCategoryService;
    @Inject private UntdidVatexService untdidVatexService;
    @Inject private UntdidVatPaymentOptionService untdidVatPaymentOptionService;
    
	@Override
	public Response createIsoIcd(IsoIcd pIsoIcd) {
        org.meveo.model.billing.IsoIcd isoIcd = pIsoIcd.toEntity();
        isoIcdService.create(isoIcd);
        return Response.ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the IsoIcd successfully created\"},\"id\": " + isoIcd.getId() + "} ")
                .build();
	}
	
	@Override
	public Response updateIsoIcd(Long pIsoIcdId, IsoIcd pIsoIcd) {
		org.meveo.model.billing.IsoIcd returnedIsoIcd = isoIcdService.findById(pIsoIcdId);
        
		if(returnedIsoIcd == null) {
            throw new EntityDoesNotExistsException("IsoIcd with id " + pIsoIcdId + " does not exist.");
        }
		
		org.meveo.model.billing.IsoIcd newIsoIcd = pIsoIcd.toEntity();
		
		isoIcdService.update(returnedIsoIcd, newIsoIcd);
        return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\"}}").build();
	}

	@Override
	public Response deleteIsoIcd(Long pIsoIcdId) {
		org.meveo.model.billing.IsoIcd returnedIsoIcd = isoIcdService.findById(pIsoIcdId);
        
		if(returnedIsoIcd == null) {
            throw new EntityDoesNotExistsException("IsoIcd with id " + pIsoIcdId + " does not exist.");
        }
        
		try {
			isoIcdService.remove(returnedIsoIcd);
        } catch (Exception exception) {
        	if (ExceptionUtils.indexOfThrowable(exception, org.hibernate.exception.ConstraintViolationException.class) > -1) {
        		throw new DeleteReferencedEntityException(IsoIcd.class, pIsoIcdId);
        	} else {
        		throw new BusinessApiException(exception);
        	} 
        }
		
        return Response.ok()
        		.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the IsoIcd successfully deleted\"},\"id\": " + pIsoIcdId + "} ")
        		.build();
	}
	
	@Override
	public Response createUntdidAllowanceCode(UntdidAllowanceCode pUntdidAllowanceCode) {
        org.meveo.model.billing.UntdidAllowanceCode untdidAllowanceCode = pUntdidAllowanceCode.toEntity();
        allowanceCodeService.create(untdidAllowanceCode);
        return Response.ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the UntdidAllowanceCode successfully created\"},\"id\": " + untdidAllowanceCode.getId() + "} ")
                .build();
	}
		

	@Override
	public Response updateUntdidAllowanceCode(Long pUntdidAllowanceCodeId, UntdidAllowanceCode pUntdidAllowanceCode) {
		org.meveo.model.billing.UntdidAllowanceCode returnedUntdidAllowanceCode = allowanceCodeService.findById(pUntdidAllowanceCodeId);
        
		if(returnedUntdidAllowanceCode == null) {
            throw new EntityDoesNotExistsException("UntdidAllowanceCode with id " + pUntdidAllowanceCodeId + " does not exist.");
        }
		
		org.meveo.model.billing.UntdidAllowanceCode newUntdidAllowanceCode = pUntdidAllowanceCode.toEntity();
		
		allowanceCodeService.update(returnedUntdidAllowanceCode, newUntdidAllowanceCode);
        return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\"}}").build();
	}
	

	@Override
	public Response deleteUntdidAllowanceCode(Long pUntdidAllowanceCodeId) {
		org.meveo.model.billing.UntdidAllowanceCode returnedUntdidAllowanceCode = allowanceCodeService.findById(pUntdidAllowanceCodeId);
        
		if(returnedUntdidAllowanceCode == null) {
            throw new EntityDoesNotExistsException("UntdidAllowanceCode with id " + pUntdidAllowanceCodeId + " does not exist.");
        }
        
		try {
			allowanceCodeService.remove(returnedUntdidAllowanceCode);
        } catch (Exception exception) {
        	if (ExceptionUtils.indexOfThrowable(exception, org.hibernate.exception.ConstraintViolationException.class) > -1) {
        		throw new DeleteReferencedEntityException(IsoIcd.class, pUntdidAllowanceCodeId);
        	} else {
        		throw new BusinessApiException(exception);
        	} 
        }
		
        return Response.ok()
        		.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the UntdidAllowanceCode successfully deleted\"},\"id\": " + pUntdidAllowanceCodeId + "} ")
        		.build();
	}
	
	@Override
	public Response createUntdidInvoiceCodeType(UntdidInvoiceCodeType pUntdidInvoiceCodeType) {
		org.meveo.model.billing.UntdidInvoiceCodeType untdidInvoiceCodeType = pUntdidInvoiceCodeType.toEntity();
		untdidInvoiceCodeTypeService.create(untdidInvoiceCodeType);
		return Response.ok()
				.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the UntdidInvoiceCodeType successfully created\"},\"id\": " + untdidInvoiceCodeType.getId() + "} ")
				.build();
	}
	

	@Override
	public Response updateUntdidInvoiceCodeType(Long pUntdidInvoiceCodeTypeId, UntdidInvoiceCodeType pUntdidInvoiceCodeType) {
		org.meveo.model.billing.UntdidInvoiceCodeType returnedUntdidInvoiceCodeType = untdidInvoiceCodeTypeService.findById(pUntdidInvoiceCodeTypeId);
		
		if(returnedUntdidInvoiceCodeType == null) {
			throw new EntityDoesNotExistsException("UntdidInvoiceCodeType with id " + pUntdidInvoiceCodeTypeId + " does not exist.");
		}
		
		org.meveo.model.billing.UntdidInvoiceCodeType newUntdidInvoiceCodeType = pUntdidInvoiceCodeType.toEntity();
		
		untdidInvoiceCodeTypeService.update(returnedUntdidInvoiceCodeType, newUntdidInvoiceCodeType);
		return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\"}}").build();
	}
	

	@Override
	public Response deleteUntdidInvoiceCodeType(Long pUntdidInvoiceCodeTypeId) {
		org.meveo.model.billing.UntdidInvoiceCodeType returnedUntdidInvoiceCodeType = untdidInvoiceCodeTypeService.findById(pUntdidInvoiceCodeTypeId);
		
		if(returnedUntdidInvoiceCodeType == null) {
			throw new EntityDoesNotExistsException("UntdidInvoiceCodeType with id " + pUntdidInvoiceCodeTypeId + " does not exist.");
		}
		
		try {
			untdidInvoiceCodeTypeService.remove(returnedUntdidInvoiceCodeType);
		} catch (Exception exception) {
			if (ExceptionUtils.indexOfThrowable(exception, org.hibernate.exception.ConstraintViolationException.class) > -1) {
				throw new DeleteReferencedEntityException(IsoIcd.class, pUntdidInvoiceCodeTypeId);
			} else {
				throw new BusinessApiException(exception);
			} 
		}
		
		return Response.ok()
				.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the UntdidInvoiceCodeType successfully deleted\"},\"id\": " + pUntdidInvoiceCodeTypeId + "} ")
				.build();
	}
	

	@Override
	public Response createUntdidInvoiceSubjectCode(UntdidInvoiceSubjectCode pUntdidInvoiceSubjectCode) {
		org.meveo.model.billing.UntdidInvoiceSubjectCode untdidInvoiceSubjectCode = pUntdidInvoiceSubjectCode.toEntity();
		untdidInvoiceSubjectCodeService.create(untdidInvoiceSubjectCode);
		return Response.ok()
				.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the UntdidInvoiceSubjectCode successfully created\"},\"id\": " + untdidInvoiceSubjectCode.getId() + "} ")
				.build();
	}
	

	@Override
	public Response updateUntdidInvoiceSubjectCode(Long pUntdidInvoiceSubjectCodeId, UntdidInvoiceSubjectCode pUntdidInvoiceSubjectCode) {
		org.meveo.model.billing.UntdidInvoiceSubjectCode returnedUntdidInvoiceSubjectCode = untdidInvoiceSubjectCodeService.findById(pUntdidInvoiceSubjectCodeId);
		
		if(returnedUntdidInvoiceSubjectCode == null) {
			throw new EntityDoesNotExistsException("UntdidInvoiceSubjectCode with id " + pUntdidInvoiceSubjectCodeId + " does not exist.");
		}
		
		org.meveo.model.billing.UntdidInvoiceSubjectCode newUntdidInvoiceSubjectCode = pUntdidInvoiceSubjectCode.toEntity();
		
		untdidInvoiceSubjectCodeService.update(returnedUntdidInvoiceSubjectCode, newUntdidInvoiceSubjectCode);
		return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\"}}").build();
	}
	

	@Override
	public Response deleteUntdidInvoiceSubjectCode(Long pUntdidInvoiceSubjectCodeId) {
		org.meveo.model.billing.UntdidInvoiceSubjectCode returnedUntdidInvoiceSubjectCode = untdidInvoiceSubjectCodeService.findById(pUntdidInvoiceSubjectCodeId);
		
		if(returnedUntdidInvoiceSubjectCode == null) {
			throw new EntityDoesNotExistsException("UntdidInvoiceSubjectCode with id " + pUntdidInvoiceSubjectCodeId + " does not exist.");
		}
		
		try {
			untdidInvoiceSubjectCodeService.remove(returnedUntdidInvoiceSubjectCode);
		} catch (Exception exception) {
			if (ExceptionUtils.indexOfThrowable(exception, org.hibernate.exception.ConstraintViolationException.class) > -1) {
				throw new DeleteReferencedEntityException(IsoIcd.class, pUntdidInvoiceSubjectCodeId);
			} else {
				throw new BusinessApiException(exception);
			} 
		}
		
		return Response.ok()
				.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the UntdidInvoiceSubjectCode successfully deleted\"},\"id\": " + pUntdidInvoiceSubjectCodeId + "} ")
				.build();
	}
	

	@Override
	public Response createUntdidPaymentMeans(UntdidPaymentMeans pUntdidPaymentMeans) {
		org.meveo.model.billing.UntdidPaymentMeans untdidPaymentMeans = pUntdidPaymentMeans.toEntity();
		untdidPaymentMeansService.create(untdidPaymentMeans);
		return Response.ok()
				.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the UntdidPaymentMeans successfully created\"},\"id\": " + untdidPaymentMeans.getId() + "} ")
				.build();
	}
	

	@Override
	public Response updateUntdidPaymentMeans(Long pUntdidPaymentMeansId, UntdidPaymentMeans pUntdidPaymentMeans) {
		org.meveo.model.billing.UntdidPaymentMeans returnedUntdidPaymentMeans = untdidPaymentMeansService.findById(pUntdidPaymentMeansId);
		
		if(returnedUntdidPaymentMeans == null) {
			throw new EntityDoesNotExistsException("UntdidPaymentMeans with id " + pUntdidPaymentMeansId + " does not exist.");
		}
		
		org.meveo.model.billing.UntdidPaymentMeans newUntdidPaymentMeans = pUntdidPaymentMeans.toEntity();
		
		untdidPaymentMeansService.update(returnedUntdidPaymentMeans, newUntdidPaymentMeans);
		return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\"}}").build();
	}
	

	@Override
	public Response deleteUntdidPaymentMeans(Long pUntdidPaymentMeansId) {
		org.meveo.model.billing.UntdidPaymentMeans returnedUntdidPaymentMeans = untdidPaymentMeansService.findById(pUntdidPaymentMeansId);
		
		if(returnedUntdidPaymentMeans == null) {
			throw new EntityDoesNotExistsException("UntdidPaymentMeans with id " + pUntdidPaymentMeansId + " does not exist.");
		}
		
		try {
			untdidPaymentMeansService.remove(returnedUntdidPaymentMeans);
		} catch (Exception exception) {
			if (ExceptionUtils.indexOfThrowable(exception, org.hibernate.exception.ConstraintViolationException.class) > -1) {
				throw new DeleteReferencedEntityException(IsoIcd.class, pUntdidPaymentMeansId);
			} else {
				throw new BusinessApiException(exception);
			} 
		}
		
		return Response.ok()
				.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the UntdidPaymentMeans successfully deleted\"},\"id\": " + pUntdidPaymentMeansId + "} ")
				.build();
	}
	

	@Override
	public Response createUntdidTaxationCategory(UntdidTaxationCategory pUntdidTaxationCategory) {
		org.meveo.model.billing.UntdidTaxationCategory untdidTaxationCategory = pUntdidTaxationCategory.toEntity();
		untdidTaxationCategoryService.create(untdidTaxationCategory);
		return Response.ok()
				.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the UntdidTaxationCategory successfully created\"},\"id\": " + untdidTaxationCategory.getId() + "} ")
				.build();
	}
	

	@Override
	public Response updateUntdidTaxationCategory(Long pUntdidTaxationCategoryId, UntdidTaxationCategory pUntdidTaxationCategory) {
		org.meveo.model.billing.UntdidTaxationCategory returnedUntdidTaxationCategory = untdidTaxationCategoryService.findById(pUntdidTaxationCategoryId);
		
		if(returnedUntdidTaxationCategory == null) {
			throw new EntityDoesNotExistsException("UntdidTaxationCategory with id " + pUntdidTaxationCategoryId + " does not exist.");
		}
		
		org.meveo.model.billing.UntdidTaxationCategory newUntdidTaxationCategory = pUntdidTaxationCategory.toEntity();
		
		untdidTaxationCategoryService.update(returnedUntdidTaxationCategory, newUntdidTaxationCategory);
		return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\"}}").build();
	}
	

	@Override
	public Response deleteUntdidTaxationCategory(Long pUntdidTaxationCategoryId) {
		org.meveo.model.billing.UntdidTaxationCategory returnedUntdidTaxationCategory = untdidTaxationCategoryService.findById(pUntdidTaxationCategoryId);
		
		if(returnedUntdidTaxationCategory == null) {
			throw new EntityDoesNotExistsException("UntdidTaxationCategory with id " + pUntdidTaxationCategoryId + " does not exist.");
		}
		
		try {
			untdidTaxationCategoryService.remove(returnedUntdidTaxationCategory);
		} catch (Exception exception) {
			if (ExceptionUtils.indexOfThrowable(exception, org.hibernate.exception.ConstraintViolationException.class) > -1) {
				throw new DeleteReferencedEntityException(IsoIcd.class, pUntdidTaxationCategoryId);
			} else {
				throw new BusinessApiException(exception);
			} 
		}
		
		return Response.ok()
				.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the UntdidTaxationCategory successfully deleted\"},\"id\": " + pUntdidTaxationCategoryId + "} ")
				.build();
	}
	

	@Override
	public Response createUntdidVatex(UntdidVatex pUntdidVatex) {
		org.meveo.model.billing.UntdidVatex untdidVatex = pUntdidVatex.toEntity();
		untdidVatexService.create(untdidVatex);
		return Response.ok()
				.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the UntdidVatex successfully created\"},\"id\": " + untdidVatex.getId() + "} ")
				.build();
	}
	

	@Override
	public Response updateUntdidVatex(Long pUntdidVatexId, UntdidVatex pUntdidVatex) {
		org.meveo.model.billing.UntdidVatex returnedUntdidVatex = untdidVatexService.findById(pUntdidVatexId);
		
		if(returnedUntdidVatex == null) {
			throw new EntityDoesNotExistsException("UntdidVatex with id " + pUntdidVatexId + " does not exist.");
		}
		
		org.meveo.model.billing.UntdidVatex newUntdidVatex = pUntdidVatex.toEntity();
		
		untdidVatexService.update(returnedUntdidVatex, newUntdidVatex);
		return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\"}}").build();
	}
	

	@Override
	public Response deleteUntdidVatex(Long pUntdidVatexId) {
		org.meveo.model.billing.UntdidVatex returnedUntdidVatex = untdidVatexService.findById(pUntdidVatexId);
		
		if(returnedUntdidVatex == null) {
			throw new EntityDoesNotExistsException("UntdidVatex with id " + pUntdidVatexId + " does not exist.");
		}
		
		try {
			untdidVatexService.remove(returnedUntdidVatex);
		} catch (Exception exception) {
			if (ExceptionUtils.indexOfThrowable(exception, org.hibernate.exception.ConstraintViolationException.class) > -1) {
				throw new DeleteReferencedEntityException(IsoIcd.class, pUntdidVatexId);
			} else {
				throw new BusinessApiException(exception);
			} 
		}
		
		return Response.ok()
				.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the UntdidVatex successfully deleted\"},\"id\": " + pUntdidVatexId + "} ")
				.build();
	}


	@Override
	public Response createUntdidVatPaymentOption(UntdidVatPaymentOption pUntdidVatPaymentOption) {
		org.meveo.model.billing.UntdidVatPaymentOption untdidVatPaymentOption = pUntdidVatPaymentOption.toEntity();
		untdidVatPaymentOptionService.create(untdidVatPaymentOption);
		return Response.ok()
				.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the UntdidVatPaymentOption successfully created\"},\"id\": " + untdidVatPaymentOption.getId() + "} ")
				.build();
	}

	@Override
	public Response updateUntdidVatPaymentOption(Long pUntdidVatPaymentOptionId, UntdidVatPaymentOption pUntdidVatPaymentOption) {
		org.meveo.model.billing.UntdidVatPaymentOption returnedUntdidVatPaymentOption = untdidVatPaymentOptionService.findById(pUntdidVatPaymentOptionId);
		
		if(returnedUntdidVatPaymentOption == null) {
			throw new EntityDoesNotExistsException("UntdidVatPaymentOption with id " + pUntdidVatPaymentOptionId + " does not exist.");
		}
		
		org.meveo.model.billing.UntdidVatPaymentOption newUntdidVatPaymentOption = pUntdidVatPaymentOption.toEntity();
		
		untdidVatPaymentOptionService.update(returnedUntdidVatPaymentOption, newUntdidVatPaymentOption);
		return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\"}}").build();
	}

	@Override
	public Response deleteUntdidVatPaymentOption(Long pUntdidVatPaymentOptionId) {
		org.meveo.model.billing.UntdidVatPaymentOption returnedUntdidVatPaymentOption = untdidVatPaymentOptionService.findById(pUntdidVatPaymentOptionId);
		
		if(returnedUntdidVatPaymentOption == null) {
			throw new EntityDoesNotExistsException("UntdidVatPaymentOption with id " + pUntdidVatPaymentOptionId + " does not exist.");
		}
		
		try {
			untdidVatPaymentOptionService.remove(returnedUntdidVatPaymentOption);
		} catch (Exception exception) {
			if (ExceptionUtils.indexOfThrowable(exception, org.hibernate.exception.ConstraintViolationException.class) > -1) {
				throw new DeleteReferencedEntityException(IsoIcd.class, pUntdidVatPaymentOptionId);
			} else {
				throw new BusinessApiException(exception);
			} 
		}
		
		return Response.ok()
				.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the UntdidVatPaymentOption successfully deleted\"},\"id\": " + pUntdidVatPaymentOptionId + "} ")
				.build();
	}
}
