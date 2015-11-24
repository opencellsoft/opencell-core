package org.meveo.api.rest.invoice.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.GenerateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetPdfInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetXmlInvoiceResponseDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.response.CustomerInvoicesResponse;
import org.meveo.api.dto.response.InvoiceCreationResponse;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.invoice.InvoiceApi;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.invoice.InvoiceRs;
import org.meveo.model.billing.InvoiceTypeEnum;

@RequestScoped
@Interceptors({ LoggingInterceptor.class })
@Api(value = "/invoice", tags = "invoice")
public class InvoiceRsImpl extends BaseRs implements InvoiceRs {

	@Inject
	private InvoiceApi invoiceApi;

	@Override
	@ApiOperation(value = "Creates an invoice", response = InvoiceCreationResponse.class, notes = "invoice number depends on invoice type")
	public InvoiceCreationResponse create(
			@ApiParam(value = "contains fields required for creating an invoice") InvoiceDto invoiceDto) {
		InvoiceCreationResponse result = new InvoiceCreationResponse();

		try {
			String invoiceNumber = invoiceApi.create(invoiceDto, getCurrentUser());
			result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
			result.setInvoiceNumber(invoiceNumber);
		} catch (MeveoApiException e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "Finds an invoice", response = CustomerInvoicesResponse.class, responseContainer = "List", notes = "returns all the invoices of a billing account")
	public CustomerInvoicesResponse find(
			@ApiParam(value = "customer account code") @QueryParam("customerAccountCode") String customerAccountCode) {
		CustomerInvoicesResponse result = new CustomerInvoicesResponse();

		try {
			result.setCustomerInvoiceDtoList(invoiceApi.list(customerAccountCode, getCurrentUser().getProvider()));
		} catch (MeveoApiException e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "This operation generates rated transaction given a billing account and invoicing date, updates billing account amounts and generates aggregates and invoice.", response = GenerateInvoiceResponseDto.class)
	public GenerateInvoiceResponseDto generateInvoice(
			@ApiParam(value = "contains the code of the billing account, invoicing and last transaction date") GenerateInvoiceRequestDto generateInvoiceRequestDto) {
		GenerateInvoiceResponseDto result = new GenerateInvoiceResponseDto();
		try {

			result.setGenerateInvoiceResultDto(invoiceApi.generateInvoice(generateInvoiceRequestDto, getCurrentUser()));
			result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		} catch (MissingParameterException mpe) {
			result.getActionStatus().setErrorCode(mpe.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(mpe.getMessage());
		} catch (EntityDoesNotExistsException ednep) {
			result.getActionStatus().setErrorCode(ednep.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(ednep.getMessage());
		} catch (BusinessApiException bae) {
			result.getActionStatus().setErrorCode(bae.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(bae.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}
		log.info("generateInvoice Response={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "Finds an invoice and return it as xml string", response = GetXmlInvoiceResponseDto.class)
	public GetXmlInvoiceResponseDto findXMLInvoice(@ApiParam(value = "invoice number") String invoiceNumber) {
		return findXMLInvoiceWithType(invoiceNumber, InvoiceTypeEnum.COMMERCIAL.name());
	}

	@Override
	@ApiOperation(value = "Finds an invoice and return it as xml string", response = GetXmlInvoiceResponseDto.class)
	public GetXmlInvoiceResponseDto findXMLInvoiceWithType(@ApiParam(value = "invoice number") String invoiceNumber,
			@ApiParam(value = "invoice type") String invoiceType) {
		GetXmlInvoiceResponseDto result = new GetXmlInvoiceResponseDto();
		try {

			result.setXmlContent(invoiceApi.getXMLInvoice(invoiceNumber, invoiceType, getCurrentUser()));
			result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		} catch (MissingParameterException mpe) {
			result.getActionStatus().setErrorCode(mpe.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(mpe.getMessage());
		} catch (EntityDoesNotExistsException ednep) {
			result.getActionStatus().setErrorCode(ednep.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(ednep.getMessage());
		} catch (BusinessException bae) {
			result.getActionStatus().setErrorCode("BusinessException");
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(bae.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}
		log.info("getXMLInvoice Response={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "Finds an invoice and return it as pdf as byte []", response = GetPdfInvoiceResponseDto.class, notes = "invoice is not recreated, instead invoice stored as pdf in database is returned")
	public GetPdfInvoiceResponseDto findPdfInvoice(@ApiParam(value = "invoice number") String InvoiceNumber) {
		return findPdfInvoiceWithType(InvoiceNumber, InvoiceTypeEnum.COMMERCIAL.name());
	}

	@Override
	@ApiOperation(value = "Finds an invoice and return it as pdf as byte []", response = GetPdfInvoiceResponseDto.class, notes = "invoice is not recreated, instead invoice stored as pdf in database is returned")
	public GetPdfInvoiceResponseDto findPdfInvoiceWithType(@ApiParam(value = "invoice number") String InvoiceNumber,
			@ApiParam(value = "invoice type") String invoiceType) {
		GetPdfInvoiceResponseDto result = new GetPdfInvoiceResponseDto();
		try {

			result.setPdfContent(invoiceApi.getPdfInvoince(InvoiceNumber, invoiceType, getCurrentUser()));
			result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		} catch (MissingParameterException mpe) {
			result.getActionStatus().setErrorCode(mpe.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(mpe.getMessage());
		} catch (EntityDoesNotExistsException ednep) {
			result.getActionStatus().setErrorCode(ednep.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(ednep.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}
		log.info("getPdfInvoice Response={}", result);
		return result;
	}

}
