package org.meveo.api.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.order.OrderTypeDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.GetListOrderTypeResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.cpq.commercial.OrderType;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.cpq.order.OrderTypeService;
import org.primefaces.model.SortOrder;


/**
 * @author Tarik FA.
 * @version 11.0
 * @dateCreation 13-01-2021
 *
 */
@Stateless
public class OrderTypeApi extends BaseApi {

	@Inject private OrderTypeService orderTypeService;
	@Inject private CommercialOrderService commercialOrderService;
	
	public OrderTypeDto create(OrderTypeDto orderTypeDto) {
		if(Strings.isEmpty(orderTypeDto.getCode()))
			missingParameters.add("code");
		handleMissingParameters();
		if(orderTypeService.findByCode(orderTypeDto.getCode()) != null)
			throw new EntityAlreadyExistsException(OrderType.class, orderTypeDto.getCode());
		final OrderType orderType = new OrderType();
		orderType.setCode(orderTypeDto.getCode());
		orderType.setDescription(orderTypeDto.getDescription());
		
		orderTypeService.create(orderType);
		
		return new OrderTypeDto(orderType);
	}
	
	public OrderTypeDto update(OrderTypeDto orderTypeDto) {
		if(Strings.isEmpty(orderTypeDto.getCode()))
			missingParameters.add("code");
		handleMissingParameters();
		final OrderType orderType = orderTypeService.findByCode(orderTypeDto.getCode());
		if(orderType == null)
			throw new EntityDoesNotExistsException(OrderType.class, orderTypeDto.getCode());
		
		if(!Strings.isEmpty(orderTypeDto.getDescription())) {
			orderType.setDescription(orderTypeDto.getDescription());
		}
		orderTypeService.update(orderType);
		return new OrderTypeDto(orderType);
		
	}
	
	public void delete(String orderTypeCode) {
		if(Strings.isEmpty(orderTypeCode))
			missingParameters.add("code");
		final OrderType orderType = orderTypeService.findByCode(orderTypeCode);
		if(orderType == null)
			throw new EntityDoesNotExistsException(OrderType.class, orderTypeCode);
		if(!commercialOrderService.findByOrderType(orderTypeCode).isEmpty())
			throw new MeveoApiException("Current Order type is already attached to order");
		
		orderTypeService.remove(orderType);
		
	}

	public OrderTypeDto findByCode(String orderTypeCode) {
		if(Strings.isEmpty(orderTypeCode))
			missingParameters.add("code");
		final OrderType orderType = orderTypeService.findByCode(orderTypeCode);
		if(orderType == null)
			throw new EntityDoesNotExistsException(OrderType.class, orderTypeCode);
		return new OrderTypeDto(orderType);
	}
	

	 public GetListOrderTypeResponseDto list (PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
		 if (pagingAndFiltering == null) {
			 pagingAndFiltering = new PagingAndFiltering();
		 }
		 String sortBy = DEFAULT_SORT_ORDER_ID;
		 if (!StringUtils.isBlank(pagingAndFiltering.getSortBy())) {
			 sortBy = pagingAndFiltering.getSortBy();
		 }
		 PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, SortOrder.ASCENDING, null, pagingAndFiltering, OrderType.class);
		 Long totalCount = orderTypeService.count(paginationConfiguration);
		 GetListOrderTypeResponseDto result = new GetListOrderTypeResponseDto();
		 result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
		 result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

		 if(totalCount > 0) {
			 orderTypeService.list(paginationConfiguration).stream().forEach(p -> {
				 result.getOrderTypes().add(new OrderTypeDto(p));
			 });
		 }
		 return result;
	 }
	 
}
