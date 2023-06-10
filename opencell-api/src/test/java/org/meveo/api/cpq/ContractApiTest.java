package org.meveo.api.cpq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.contract.BillingRule;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.contract.ContractItem;
import org.meveo.model.cpq.contract.ContractRateTypeEnum;
import org.meveo.model.cpq.enums.ContractStatusEnum;
import org.meveo.model.cpq.enums.PriceVersionTypeEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.security.MeveoUser;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.meveo.service.cpq.ContractService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContractApiTest {

    @InjectMocks
    private ContractApi contractApi;

    @Mock
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

    @Mock
    private PricePlanMatrixService pricePlanMatrixService;

    @Mock
    private ContractService contractService;

    @Mock
    private MeveoUser currentUser = new MeveoUser() {
    };

    @Test
    public void shouldDuplicate() {

        // Given a contractCode
        String contractCode = "myContractCode";

        Seller seller = new Seller();
        seller.setCode("TEST-SELLER");

        Customer customer = new Customer();
        customer.setCode("TEST-CUSTOMER");

        CustomerAccount customerAccount = new CustomerAccount();
        customer.setCode("TEST-CA");

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("TEST-BA");

        OfferTemplate offerTemplate = new OfferTemplate();
        offerTemplate.setCode("TEST-OT");
        Product product = new Product();
        product.setCode("TEST-PRODUCT");
        UsageChargeTemplate chargeTemplate = new UsageChargeTemplate();
        chargeTemplate.setCode("TEST-CHARGE-TEMPLATE");

        Contract source = new Contract();
        source.setId(1L);
        source.setCode("contractCode");
        source.setDescription("contractDescription");
        source.setBeginDate(new GregorianCalendar(2023, Calendar.MAY, 1).getTime());
        source.setEndDate(new GregorianCalendar(2023, Calendar.MAY, 31).getTime());
        source.setContractDate(new GregorianCalendar(2023, Calendar.MAY, 15).getTime());
        source.setSeller(seller);
        source.setCustomer(customer);
        source.setCustomerAccount(customerAccount);
        source.setBillingAccount(billingAccount);
        source.setApplicationEl("ApplicationEL");
        source.setContractDuration(123);
        source.setRenewal(false);

        // Billing Rules
        BillingRule brSource = new BillingRule();
        brSource.setPriority(1);
        brSource.setCriteriaEL("criteriaEL");
        brSource.setInvoicedBACodeEL("invoicedBACodeEL");

        source.setBillingRules(List.of(brSource));

        // Customer Fields
        source.setCfValue("customField1", "Value 1");
        source.setCfValue("customField2", "Value 2");
        source.setCfValue("customField3", "Value 3");

        ContractItem ciSource = new ContractItem();
        ciSource.setCode("contractItemCode");
        ciSource.setApplicationEl("ApplicationEL");
        ciSource.setOfferTemplate(offerTemplate);
        ciSource.setProduct(product);
        ciSource.setChargeTemplate(chargeTemplate);
        ciSource.setContractRateType(ContractRateTypeEnum.FIXED);
        ciSource.setSeparateDiscount(false);
        ciSource.setRate(1000d);

        source.setContractItems(List.of(ciSource));

        // Price Plan
        PricePlanMatrix pricePlan = new PricePlanMatrix();
        pricePlan.setCode("PPM-CODE");
        PricePlanMatrixVersion ppmv = new PricePlanMatrixVersion();
        ppmv.setLabel("PV_01");
        ppmv.setVersion(1);
        ppmv.setPriceVersionType(PriceVersionTypeEnum.FIXED);

        PricePlanMatrixLine ppml = new PricePlanMatrixLine();
        ppml.setDescription("PPML Description");
        ppmv.setLines(Set.of(ppml));

        pricePlan.setVersions(List.of(ppmv));
        ciSource.setPricePlan(pricePlan);

        when(pricePlanMatrixService.findDuplicateCode(pricePlan)).thenReturn(pricePlan.getCode()+"-COPY");

        PricePlanMatrixVersion duplicatedPPMV = new PricePlanMatrixVersion(ppmv);
        duplicatedPPMV.setStatus(VersionStatusEnum.DRAFT);
        duplicatedPPMV.setPriceVersionType(ppmv.getPriceVersionType());

        when(pricePlanMatrixVersionService.duplicate(any(PricePlanMatrixVersion.class), any(PricePlanMatrix.class), any(), any(VersionStatusEnum.class), any(PriceVersionTypeEnum.class), anyBoolean(), anyInt()))
                .thenReturn(duplicatedPPMV);

        when(contractService.findByCode(contractCode)).thenReturn(source);


        // When duplicate a contract based on its contractCode
        contractApi.duplicateContract(contractCode);

        // then
        // Should find a contract to duplicate
        ArgumentCaptor<String> findCaptor = ArgumentCaptor.forClass(String.class);
        verify(contractService, atLeastOnce()).findByCode(findCaptor.capture());
        assertThat(findCaptor.getValue()).isNotNull();

        // should save the duplicated contract
        ArgumentCaptor<Contract> saveCaptor = ArgumentCaptor.forClass(Contract.class);
        verify(contractService).create(saveCaptor.capture());

        Contract toCheck = saveCaptor.getValue();
        assertThat(toCheck).isNotNull();
        assertThat(toCheck).isNotSameAs(source);
        assertThat(toCheck.getStatus()).isEqualTo(ContractStatusEnum.DRAFT.getValue());
        assertThat(toCheck.getStatusDate()).isNotNull();
        assertThat(toCheck.getId()).isNotEqualTo(source.getId());
        assertThat(toCheck.getCode()).isEqualTo(source.getCode()+"-COPY");
        assertThat(toCheck.getDescription()).isNull();
        assertThat(toCheck.getBeginDate()).isEqualTo(source.getBeginDate());
        assertThat(toCheck.getEndDate()).isEqualTo(source.getEndDate());
        assertThat(toCheck.getContractDate()).isNull();
        assertThat(toCheck.getSeller()).isNotNull();
        assertThat(toCheck.getSeller().getCode()).isEqualTo(source.getSeller().getCode());
        assertThat(toCheck.getCustomer()).isNotNull();
        assertThat(toCheck.getCustomer().getCode()).isEqualTo(source.getCustomer().getCode());
        assertThat(toCheck.getCustomerAccount()).isNotNull();
        assertThat(toCheck.getCustomerAccount().getCode()).isEqualTo(source.getCustomerAccount().getCode());
        assertThat(toCheck.getBillingAccount()).isNotNull();
        assertThat(toCheck.getBillingAccount().getCode()).isEqualTo(source.getBillingAccount().getCode());
        assertThat(toCheck.getApplicationEl()).isEqualTo(source.getApplicationEl());
        assertThat(toCheck.getContractDuration()).isEqualTo(source.getContractDuration());
        assertThat(toCheck.isRenewal()).isEqualTo(source.isRenewal());

        // Check duplicated Billing Rules
        assertThat(toCheck.getBillingRules()).isNotEmpty();
        assertThat(toCheck.getBillingRules().size()).isEqualTo(source.getBillingRules().size());
        BillingRule brTocheck = toCheck.getBillingRules().get(0);
        assertThat(brTocheck.getCriteriaEL()).isEqualTo(brSource.getCriteriaEL());
        assertThat(brTocheck.getPriority()).isEqualTo(brSource.getPriority());
        assertThat(brTocheck.getInvoicedBACodeEL()).isEqualTo(brSource.getInvoicedBACodeEL());

        // Check duplicated Custom Fields
        assertThat(toCheck.getCfValues()).isNotNull();
        assertThat(toCheck.getCfValue("customField1")).isEqualTo("Value 1");
        assertThat(toCheck.getCfValue("customField2")).isEqualTo("Value 2");
        assertThat(toCheck.getCfValue("customField3")).isEqualTo("Value 3");

        // Check duplicated Contract Item
        assertThat(toCheck.getContractItems()).isNotEmpty();
        assertThat(toCheck.getContractItems().size()).isEqualTo(source.getContractItems().size());
        ContractItem ciToCheck = toCheck.getContractItems().get(0);

        assertThat(ciToCheck.getCode()).isEqualTo(ciSource.getCode()+"-COPY");
        assertThat(ciToCheck.getOfferTemplate()).isEqualTo(ciSource.getOfferTemplate());
        assertThat(ciToCheck.getProduct()).isEqualTo(ciSource.getProduct());
        assertThat(ciToCheck.getChargeTemplate()).isEqualTo(ciSource.getChargeTemplate());
        assertThat(ciToCheck.getServiceTemplate()).isEqualTo(ciSource.getServiceTemplate());
        assertThat(ciToCheck.getContractRateType()).isEqualTo(ciSource.getContractRateType());
        assertThat(ciToCheck.getRate()).isEqualTo(ciSource.getRate());
        assertThat(ciToCheck.getApplicationEl()).isEqualTo(ciSource.getApplicationEl());
        assertThat(ciToCheck.isSeparateDiscount()).isEqualTo(ciSource.isSeparateDiscount());

        // check priceplan
        assertThat(ciToCheck.getPricePlan()).isNotNull();
        PricePlanMatrix ppmToCheck = ciToCheck.getPricePlan();
        assertThat(ppmToCheck.getCode()).isEqualTo(pricePlan.getCode()+"-COPY");
        assertThat(ppmToCheck.getVersions()).isNotEmpty();
        assertThat(ppmToCheck.getVersions().size()).isEqualTo(pricePlan.getVersions().size());
        assertThat(ppmToCheck.getVersions().get(0).getStatus()).isEqualTo(VersionStatusEnum.DRAFT);


    }

    @Test
    public void shouldTriggerExceptionContractNotFound() {
        // given a inexsitant contractCode
        String contractCode = "notfound";

        // when try to duplicate, an not found exception is trigger
        assertThatExceptionOfType(EntityDoesNotExistsException.class).isThrownBy(() -> {
            contractApi.duplicateContract(contractCode);
        });

    }

    @Test
    public void shouldTriggerExceptionDuplicatedCode() {
        // given contractCode
        String contractCode = "myContractCode";

        Contract source = new Contract();
        source.setCode(contractCode);
        when(contractService.findByCode(contractCode)).thenReturn(source);
        when(contractService.findByCode(contractCode+"-COPY")).thenReturn(new Contract());

        // when try to duplicate, an not found exception is trigger
        assertThatExceptionOfType(EntityAlreadyExistsException.class).isThrownBy(() -> {
            contractApi.duplicateContract(contractCode);
        });

    }
}
