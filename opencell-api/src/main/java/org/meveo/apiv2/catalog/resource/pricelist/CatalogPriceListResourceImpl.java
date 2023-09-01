package org.meveo.apiv2.catalog.resource.pricelist;

import java.util.List;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.apiv2.catalog.ImmutablePriceList;
import org.meveo.apiv2.catalog.ImmutablePriceLists;
import org.meveo.apiv2.catalog.PriceLists;
import org.meveo.apiv2.catalog.service.PriceListApiService;
import org.meveo.apiv2.ordering.common.LinkGenerator;

@Interceptors({ WsRestApiInterceptor.class })
public class CatalogPriceListResourceImpl implements CatalogPriceListResource {

	@Inject
    private PriceListApiService priceListApiService;
	
	@Inject
	private GenericPagingAndFilteringUtils genericPagingAndFilteringUtils;

	@Override
	public Response getPriceLists(Long pOffset, Long pLimit, String pSortOrder, String pSortBy, String billingAccountCode, Request pRequest) {
		long lApiLimit = genericPagingAndFilteringUtils.getLimit(pLimit != null ? pLimit.intValue() : null);
		List<org.meveo.apiv2.catalog.PriceList> lPriceListEntities = priceListApiService.getPriceList(pOffset, lApiLimit, pSortOrder, pSortBy, billingAccountCode);
		Long lPriceListCount = priceListApiService.count(pSortOrder, pSortBy, billingAccountCode);
		return buildPriceList(pOffset, pLimit, pRequest, lPriceListEntities, lPriceListCount);
	}

	/**
	 * Build Price List
	 * @param pOffset Offset
	 * @param pLimit Limit
	 * @param pRequest {@link Request}
	 * @param pPriceListEntities A list of {@link org.meveo.apiv2.catalog.PriceList}
	 * @param pPriceListCount PriceList Count
	 * @return {@link Response}
	 */
	private Response buildPriceList(Long pOffset, Long pLimit, Request pRequest, List<org.meveo.apiv2.catalog.PriceList> pPriceListEntities, Long pPriceListCount) {
    	EntityTag lEntityTag = new EntityTag(Integer.toString(pPriceListEntities.hashCode()));
		CacheControl lCacheControl = new CacheControl();
		lCacheControl.setMaxAge(1000);
		Response.ResponseBuilder lBuilder = pRequest.evaluatePreconditions(lEntityTag);
		
		if (lBuilder != null) {
			lBuilder.cacheControl(lCacheControl);
			return lBuilder.build();
		}
		
		ImmutablePriceList[] lPriceList = pPriceListEntities.stream()
				.map(priceList -> toResourcePriceListWithLink(priceList))
				.toArray(ImmutablePriceList[]::new);
		PriceLists lPriceLists = ImmutablePriceLists.builder().addData(lPriceList).offset(pOffset).limit(pLimit)
				.total(pPriceListCount).build().withLinks(new LinkGenerator.PaginationLinkGenerator(PriceListResource.class)
						.offset(pOffset).limit(pLimit).total(pPriceListCount).build());
		return Response.ok().cacheControl(lCacheControl).tag(lEntityTag).entity(lPriceLists).build();
	}

	/**
	 * Build Price List with Link
	 * @param pPriceList {@link org.meveo.apiv2.catalog.PriceList}
	 * @return {@link org.meveo.apiv2.catalog.PriceList}
	 */
    private org.meveo.apiv2.catalog.PriceList toResourcePriceListWithLink(org.meveo.apiv2.catalog.PriceList pPriceList) {
		return ImmutablePriceList.copyOf(pPriceList)
				.withLinks(new LinkGenerator.SelfLinkGenerator(PriceListResource.class).withId(pPriceList.getId())
						.withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction().build());
	}
}