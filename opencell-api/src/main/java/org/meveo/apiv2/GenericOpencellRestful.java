package org.meveo.apiv2;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
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
import org.meveo.apiv2.accounting.resource.impl.AccountingResourceImpl;
import org.meveo.apiv2.accountreceivable.accountOperation.AccountReceivableResourceImpl;
import org.meveo.apiv2.accountreceivable.deferralPayments.AccountReceivableDeferralPaymentsResourceImpl;
import org.meveo.apiv2.accounts.impl.AccountsManagementResourceImpl;
import org.meveo.apiv2.accounts.impl.UserAccountsResourceImpl;
import org.meveo.apiv2.admin.impl.SellerResourceImpl;
import org.meveo.apiv2.admin.providers.ProviderResourceImpl;
import org.meveo.apiv2.article.impl.AccountingArticleResourceImpl;
import org.meveo.apiv2.article.impl.ArticleMappingLineResourceImpl;
import org.meveo.apiv2.article.impl.ArticleMappingResourceImpl;
import org.meveo.apiv2.audit.impl.AuditDataConfigurationResourceImpl;
import org.meveo.apiv2.audit.impl.AuditDataLogResourceImpl;
import org.meveo.apiv2.billing.impl.DiscountPlanInstanceResourceImpl;
import org.meveo.apiv2.billing.impl.InvoiceLinesResourceImpl;
import org.meveo.apiv2.billing.impl.InvoiceResourceImpl;
import org.meveo.apiv2.billing.impl.InvoiceValidationRulesResourceImpl;
import org.meveo.apiv2.billing.impl.InvoicingResourceImpl;
import org.meveo.apiv2.billing.impl.MediationResourceImpl;
import org.meveo.apiv2.billing.impl.RatedTransactionResourceImpl;
import org.meveo.apiv2.billing.service.RollbackOnErrorExceptionMapper;
import org.meveo.apiv2.catalog.resource.DiscountPlanResourceImpl;
import org.meveo.apiv2.catalog.resource.PricePlanMatrixResourceImpl;
import org.meveo.apiv2.catalog.resource.PricePlanResourceImpl;
import org.meveo.apiv2.catalog.resource.pricelist.CatalogPriceListResourceImpl;
import org.meveo.apiv2.catalog.resource.pricelist.PriceListLineResourceImpl;
import org.meveo.apiv2.catalog.resource.pricelist.PriceListResourceImpl;
import org.meveo.apiv2.communication.impl.InternationalSettingsResourceImpl;
import org.meveo.apiv2.cpq.impl.CommercialOrderResourceImpl;
import org.meveo.apiv2.cpq.impl.CpqContractResourceImpl;
import org.meveo.apiv2.cpq.impl.CpqQuoteResourceImpl;
import org.meveo.apiv2.crm.impl.ContactCategoryResourceImpl;
import org.meveo.apiv2.customtable.CustomTableResourceImpl;
import org.meveo.apiv2.document.DocumentResourceImpl;
import org.meveo.apiv2.documentCategory.impl.DocumentCategoryResourceImpl;
import org.meveo.apiv2.dunning.action.DunningActionImpl;
import org.meveo.apiv2.dunning.impl.CollectionPlanStatusResourceImpl;
import org.meveo.apiv2.dunning.impl.CustomerBalanceResourceImpl;
import org.meveo.apiv2.dunning.impl.DunningAgentResourceImpl;
import org.meveo.apiv2.dunning.impl.DunningCollectionPlanResourceImpl;
import org.meveo.apiv2.dunning.impl.DunningLevelResourceImpl;
import org.meveo.apiv2.dunning.impl.DunningPauseReasonsResourceImpl;
import org.meveo.apiv2.dunning.impl.DunningPaymentRetryResourceImpl;
import org.meveo.apiv2.dunning.impl.DunningPolicyResourceImpl;
import org.meveo.apiv2.dunning.impl.DunningSettingsResourceImpl;
import org.meveo.apiv2.dunning.impl.DunningStopReasonsResourceImpl;
import org.meveo.apiv2.dunning.template.DunningTemplateResourceImpl;
import org.meveo.apiv2.electronicInvoicing.resource.impl.ElectronicInvoicingResourceImpl;
import org.meveo.apiv2.esignature.impl.SignatureRequestResourceImpl;
import org.meveo.apiv2.export.ImportExportResourceImpl;
import org.meveo.apiv2.fileType.impl.FileTypeResourceImpl;
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
import org.meveo.apiv2.media.file.upload.FileUploadResourceImpl;
import org.meveo.apiv2.mediation.impl.MediationSettingResourceImpl;
import org.meveo.apiv2.ordering.resource.ooq.OpenOrderQuoteResourceImpl;
import org.meveo.apiv2.ordering.resource.openOrderTemplate.OpenOrderTemplateResourceImpl;
import org.meveo.apiv2.ordering.resource.openorder.OpenOrderResourceImpl;
import org.meveo.apiv2.ordering.resource.order.OrderResourceImpl;
import org.meveo.apiv2.ordering.resource.orderitem.OrderItemResourceImpl;
import org.meveo.apiv2.ordering.resource.product.ProductResourceImpl;
import org.meveo.apiv2.payments.resource.PaymentPlanResourceImpl;
import org.meveo.apiv2.payments.resource.PaymentResourceImpl;
import org.meveo.apiv2.quote.impl.QuoteOfferResourceImpl;
import org.meveo.apiv2.rating.impl.WalletOperationResourceImpl;
import org.meveo.apiv2.refund.RefundResourceImpl;
import org.meveo.apiv2.report.query.impl.ReportQueryResourceImpl;
import org.meveo.apiv2.securityDeposit.financeSettings.impl.FinanceSettingsResourceImpl;
import org.meveo.apiv2.securityDeposit.impl.SecurityDepositResourceImpl;
import org.meveo.apiv2.securityDeposit.securityDepositTemplate.impl.SecurityDepositTemplateResourceImpl;
import org.meveo.apiv2.settings.globalSettings.impl.GlobalSettingsResourceImpl;
import org.meveo.apiv2.settings.openOrderSetting.impl.OpenOrderSettingResourceImpl;
import org.meveo.apiv2.standardReport.impl.StandardReportResourceImpl;
import org.meveo.commons.utils.ParamBeanFactory;
import org.slf4j.Logger;

@ApplicationPath("/api/rest/v2")
public class GenericOpencellRestful extends Application {
    private static final String GENERIC_API_REQUEST_LOGGING_CONFIG_KEY = "generic.api.request.logging";
    private static final String GENERIC_API_REQUEST_EXTRACT_LIST_CONFIG_KEY = "generic.api.extract.list";
    private static final String API_LIST_DEFAULT_LIMIT_KEY = "api.list.defaultLimit";
    private static String GENERIC_API_REQUEST_LOGGING_CONFIG;
    private static boolean GENERIC_API_REQUEST_EXTRACT_LIST;
    public static List<Map<String, String>> VERSION_INFO = new ArrayList<>();
    public static List<Class> ENTITIES_LIST = new ArrayList<>();
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
                DocumentResourceImpl.class,
                GenericJacksonProvider.class, ProductResourceImpl.class, OrderItemResourceImpl.class,
                OrderResourceImpl.class, AccountingArticleResourceImpl.class, ArticleMappingLineResourceImpl.class, ReportingResourceImpl.class,
                ArticleMappingResourceImpl.class, InvoiceResourceImpl.class, DiscountPlanResourceImpl.class, AccountingPeriodResourceImpl.class,
                DiscountPlanInstanceResourceImpl.class, RatedTransactionResourceImpl.class, RefundResourceImpl.class, ValidationExceptionMapper.class,
                BusinessExceptionMapper.class, InvoicingResourceImpl.class, ReportQueryResourceImpl.class, AccountsManagementResourceImpl.class,
                DunningSettingsResourceImpl.class, DunningLevelResourceImpl.class ,DunningActionImpl.class,
                QuoteOfferResourceImpl.class, ConflictExceptionMapper.class, UnprocessableEntityExceptionMapper.class, AccountReceivableResourceImpl.class,
                DunningAgentResourceImpl.class, CollectionPlanStatusResourceImpl.class,
                StandardReportResourceImpl.class, MediationResourceImpl.class, DunningPolicyResourceImpl.class, DunningStopReasonsResourceImpl.class, DunningPauseReasonsResourceImpl.class,
                DunningPaymentRetryResourceImpl.class, FileUploadResourceImpl.class, PricePlanResourceImpl.class, DunningTemplateResourceImpl.class, PricePlanMatrixResourceImpl.class,
                RollbackOnErrorExceptionMapper.class, ProviderResourceImpl.class, ImportExportResourceImpl.class,
                DunningCollectionPlanResourceImpl.class, AccountReceivableDeferralPaymentsResourceImpl.class,
                FinanceSettingsResourceImpl.class, SecurityDepositTemplateResourceImpl.class, SecurityDepositResourceImpl.class, UserAccountsResourceImpl.class,
                OpenOrderSettingResourceImpl.class, GlobalSettingsResourceImpl.class, Apiv2SwaggerGeneration.class,
                OpenOrderTemplateResourceImpl.class, AccountingResourceImpl.class, PaymentPlanResourceImpl.class, MediationSettingResourceImpl.class,
                OpenOrderQuoteResourceImpl.class, CpqQuoteResourceImpl.class, CommercialOrderResourceImpl.class,
                InvoiceLinesResourceImpl.class, CpqContractResourceImpl.class, OpenOrderResourceImpl.class,
                ContactCategoryResourceImpl.class, WalletOperationResourceImpl.class, InvoiceValidationRulesResourceImpl.class, InternationalSettingsResourceImpl.class,
                CustomTableResourceImpl.class, CustomerBalanceResourceImpl.class, FileTypeResourceImpl.class, DocumentCategoryResourceImpl.class, 
                ElectronicInvoicingResourceImpl.class,PaymentResourceImpl.class, PriceListResourceImpl.class, SellerResourceImpl.class, PriceListLineResourceImpl.class, CatalogPriceListResourceImpl.class,
				        SignatureRequestResourceImpl.class, AuditDataConfigurationResourceImpl.class, AuditDataLogResourceImpl.class)
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
                    if(jsonObject.get("commitDate") != null) {
                        versionInfo.put("commitDate", (String) jsonObject.get("commitDate"));
                    }

                    VERSION_INFO.add(versionInfo);
                } catch (ParseException | IOException e) {
                    log.warn(e.toString());
                    log.error("error = {}", e.getMessage(), e);
                }
            });
        } catch (IOException e) {
            log.error("There was a problem loading version information", e);
        }
    }

    private void loadEntitiesList() {
        List<Class> listEntities = new ArrayList<>();
        for (Map.Entry<String, Class> entry : GenericHelper.entitiesByName.entrySet()) {
            ENTITIES_LIST.add(entry.getValue());
        }

    }

    public boolean shouldExtractList() {
        return GENERIC_API_REQUEST_EXTRACT_LIST;
    }
}
