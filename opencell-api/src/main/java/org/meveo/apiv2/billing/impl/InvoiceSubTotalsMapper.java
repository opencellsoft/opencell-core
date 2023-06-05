package org.meveo.apiv2.billing.impl;

import static java.util.stream.Collectors.toList;
import static org.meveo.commons.utils.EjbUtils.getServiceInterface;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.billing.ImmutableInvoiceSubTotals;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.billing.InvoiceSubTotals;
import org.meveo.model.billing.InvoiceType;
import org.meveo.service.billing.impl.InvoiceTypeService;

public class InvoiceSubTotalsMapper extends ResourceMapper<org.meveo.apiv2.billing.InvoiceSubTotals, InvoiceSubTotals>{


	private InvoiceTypeService invoiceTypeService = (InvoiceTypeService) getServiceInterface(InvoiceTypeService.class.getSimpleName());
	
	@Override
	protected org.meveo.apiv2.billing.InvoiceSubTotals toResource(InvoiceSubTotals entity) {
		try {
			ImmutableInvoiceSubTotals resource = (ImmutableInvoiceSubTotals) initResource(ImmutableInvoiceSubTotals.class, entity);
			return ImmutableInvoiceSubTotals.builder().from(resource).id(entity.getId())
						.invoiceType(entity.getInvoiceType() != null ? ImmutableResource.builder().id(entity.getInvoiceType().getId()).build() : null)
						.subTotalEl(entity.getSubTotalEl())
						.label(entity.getLabel())
						.code(entity.getCode())
						.amountTax(entity.getAmountWithTax())
						.amountWithoutTax(entity.getAmountWithoutTax())
						.transactionalAmountTax(entity.getTransactionalAmountWithTax())
						.transactionalAmountWithoutTax(entity.getTransactionalAmountWithoutTax())
						.build();
		}catch(Exception e) {
			throw new BusinessException(e);
		}
	}

	public List<org.meveo.apiv2.billing.InvoiceSubTotals> toResources(List<InvoiceSubTotals> invoiceSubTotals) {
		if(CollectionUtils.isEmpty(invoiceSubTotals)) {
			return null;
		}
		return invoiceSubTotals.stream().map(i->toResource(i)).collect(toList());
	}

	@Override
	protected InvoiceSubTotals toEntity(org.meveo.apiv2.billing.InvoiceSubTotals resource) {
		try {
			InvoiceSubTotals entity = initEntity(resource, new InvoiceSubTotals());
			entity.setId(resource.getId());
			entity.setCode(resource.getCode());
			entity.setAmountWithoutTax(resource.getAmountWithoutTax());
			entity.setAmountWithTax(resource.getAmountTax());
			entity.setSubTotalEl(resource.getSubTotalEl());
			entity.setLabel(resource.getLabel());
			if(resource.getInvoiceType() != null) {
				InvoiceType invoiceType = new InvoiceType();
				invoiceType.setId(resource.getInvoiceType().getId());
				entity.setInvoiceType(invoiceType);
			}
		}catch(Exception e) {
			throw new BusinessException(e);
		}
		return null;
	}

}
