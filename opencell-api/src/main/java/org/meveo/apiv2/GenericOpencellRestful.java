package org.meveo.apiv2;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.apache.commons.collections.map.HashedMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.meveo.apiv2.accounting.resource.impl.AccountingPeriodResourceImpl;
import org.meveo.apiv2.accountreceivable.AccountReceivableResourceImpl;
import org.meveo.apiv2.accounts.impl.AccountsManagementResourceImpl;
import org.meveo.apiv2.article.impl.AccountingArticleResourceImpl;
import org.meveo.apiv2.article.impl.ArticleMappingLineResourceImpl;
import org.meveo.apiv2.article.impl.ArticleMappingResourceImpl;
import org.meveo.apiv2.billing.impl.DiscountPlanInstanceResourceImpl;
import org.meveo.apiv2.billing.impl.InvoiceResourceImpl;
import org.meveo.apiv2.billing.impl.InvoicingResourceImpl;
import org.meveo.apiv2.billing.impl.RatedTransactionResourceImpl;
import org.meveo.apiv2.catalog.resource.DiscountPlanResourceImpl;
import org.meveo.apiv2.document.DocumentResourceImpl;
import org.meveo.apiv2.dunning.dunningAction.DunningActionImpl;
import org.meveo.apiv2.dunning.impl.CollectionPlanStatusResourceImpl;
import org.meveo.apiv2.dunning.impl.DunningCollectionManagementResourceImpl;
import org.meveo.apiv2.dunning.impl.DunningSettingsResourceImpl;
import org.meveo.apiv2.finance.impl.ReportingResourceImpl;
import org.meveo.apiv2.generic.GenericResourceImpl;
import org.meveo.apiv2.generic.NotYetImplementedResource;
import org.meveo.apiv2.generic.VersionImpl;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.apiv2.generic.exception.BadRequestExceptionMapper;
import org.meveo.apiv2.generic.exception.BusinessExceptionMapper;
import org.meveo.apiv2.generic.exception.ConflictExceptionMapper;
import org.meveo.apiv2.generic.exception.EJBTransactionRolledbackExceptionMapper;
import org.meveo.apiv2.generic.exception.EntityDoesNotExistsExceptionMapper;
import org.meveo.apiv2.generic.exception.ForbiddenExceptionMapper;
import org.meveo.apiv2.generic.exception.IllegalArgumentExceptionMapper;
import org.meveo.apiv2.generic.exception.MeveoExceptionMapper;
import org.meveo.apiv2.generic.exception.NotFoundExceptionMapper;
import org.meveo.apiv2.generic.exception.UnprocessableEntityExceptionMapper;
import org.meveo.apiv2.generic.exception.ValidationExceptionMapper;
import org.meveo.apiv2.generic.services.GenericApiLoggingFilter;
import org.meveo.apiv2.ordering.resource.order.OrderResourceImpl;
import org.meveo.apiv2.ordering.resource.orderitem.OrderItemResourceImpl;
import org.meveo.apiv2.ordering.resource.product.ProductResourceImpl;
import org.meveo.apiv2.quote.impl.QuoteOfferResourceImpl;
import org.meveo.apiv2.refund.RefundResourceImpl;
import org.meveo.apiv2.report.query.impl.ReportQueryResourceImpl;
import org.meveo.apiv2.standardReport.impl.StandardReportResourceImpl;
import org.meveo.commons.utils.ParamBeanFactory;
import org.slf4j.Logger;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

@ApplicationPath("/api/rest/v2")
public class GenericOpencellRestful extends Application {
    private static final String GENERIC_API_REQUEST_LOGGING_CONFIG_KEY = "generic.api.request.logging";
    private static final String GENERIC_API_REQUEST_EXTRACT_LIST_CONFIG_KEY = "generic.api.extract.list";
    private static final String API_LIST_DEFAULT_LIMIT_KEY = "api.list.defaultLimit";
    private static String GENERIC_API_REQUEST_LOGGING_CONFIG;
    private static boolean GENERIC_API_REQUEST_EXTRACT_LIST;
    public static List<Map<String, String>> VERSION_INFO = new ArrayList<>();
    public static Map<String, List<String>> ENTITIES_MAP = new HashMap<>();
    public static long API_LIST_DEFAULT_LIMIT;

    @Inject
    protected Logger log;
    @Inject
    private ParamBeanFactory paramBeanFactory;

    @PostConstruct
    public void init() {
        API_LIST_DEFAULT_LIMIT = paramBeanFactory.getInstance().getPropertyAsInteger(API_LIST_DEFAULT_LIMIT_KEY, 100);
        GENERIC_API_REQUEST_LOGGING_CONFIG = paramBeanFactory.getInstance().getProperty(GENERIC_API_REQUEST_LOGGING_CONFIG_KEY, "false");
        GENERIC_API_REQUEST_EXTRACT_LIST = Boolean.parseBoolean(paramBeanFactory.getInstance().getProperty(GENERIC_API_REQUEST_EXTRACT_LIST_CONFIG_KEY, "true"));
        loadVersionInformation();
        loadEntitiesList();
    }

    @Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> resources = Stream.of(VersionImpl.class, GenericResourceImpl.class,
				NotYetImplementedResource.class, NotFoundExceptionMapper.class, BadRequestExceptionMapper.class,
				MeveoExceptionMapper.class, IllegalArgumentExceptionMapper.class,
				EJBTransactionRolledbackExceptionMapper.class, ForbiddenExceptionMapper.class,
                EntityDoesNotExistsExceptionMapper.class,
                OpenApiResource.class, DocumentResourceImpl.class,
				GenericJacksonProvider.class, ProductResourceImpl.class, OrderItemResourceImpl.class,
				OrderResourceImpl.class, AccountingArticleResourceImpl.class, ArticleMappingLineResourceImpl.class, ReportingResourceImpl.class,
				ArticleMappingResourceImpl.class, InvoiceResourceImpl.class, DiscountPlanResourceImpl.class, AccountingPeriodResourceImpl.class,
				DiscountPlanInstanceResourceImpl.class, RatedTransactionResourceImpl.class, RefundResourceImpl.class, ValidationExceptionMapper.class,
				BusinessExceptionMapper.class, InvoicingResourceImpl.class, ReportQueryResourceImpl.class, AccountsManagementResourceImpl.class, DunningSettingsResourceImpl.class, DunningActionImpl.class,
				QuoteOfferResourceImpl.class, ConflictExceptionMapper.class, UnprocessableEntityExceptionMapper.class, AccountReceivableResourceImpl.class, DunningCollectionManagementResourceImpl.class, CollectionPlanStatusResourceImpl.class, StandardReportResourceImpl.class )
		        .collect(Collectors.toSet());
		if (GENERIC_API_REQUEST_LOGGING_CONFIG.equalsIgnoreCase("true")) {
			resources.add(GenericApiLoggingFilter.class);
			log.info(
					"generic api requests logging is enabled, to disable logging for generic api request, put {} to false",
					GENERIC_API_REQUEST_LOGGING_CONFIG_KEY);
		}
		log.info("Opencell OpenAPI definition is accessible in /api/rest/v2/openapi.{type:json|yaml}");
		return resources;
	}

    private void loadVersionInformation() {
        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("version.json");
            JSONParser parser = new JSONParser();
            resources.asIterator().forEachRemaining(url -> {
                try {
                    Object obj = parser.parse(new String(url.openStream().readAllBytes()));
                    JSONObject jsonObject = (JSONObject) obj;

                    Map<String, String> versionInfo = new HashedMap();
                    versionInfo.put("name", (String) jsonObject.get("name"));
                    versionInfo.put("version", (String) jsonObject.get("version"));
                    versionInfo.put("commit", (String) jsonObject.get("commit"));

                    VERSION_INFO.add(versionInfo);
                } catch (ParseException | IOException e) {
                    log.warn(e.toString());
                    log.error("error = {}", e);
                }
            });
        } catch (IOException e) {
            log.warn("There was a problem loading version information");
            log.error("error = {}", e);
        }
    }

    private void loadEntitiesList() {
        List<String> listEntities = new ArrayList<>();
        for (Map.Entry<String, Class> entry : GenericHelper.entitiesByName.entrySet()) {
            listEntities.add(entry.getValue().getSimpleName());
        }
        ENTITIES_MAP.put("entities", listEntities);
    }

    public boolean shouldExtractList() {
        return GENERIC_API_REQUEST_EXTRACT_LIST;
    }
}
