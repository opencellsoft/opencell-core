package org.meveo.api.security.Interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.AccessDeniedException;
import org.meveo.api.security.config.FilterPropertyConfig;
import org.meveo.api.security.config.FilterResultsConfig;
import org.meveo.api.security.config.SecuredBusinessEntityConfig;
import org.meveo.api.security.config.SecuredMethodConfig;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.api.security.filter.SecureMethodResultFilter;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.admin.SecuredEntity.SecuredEntityPermissionEnum;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.security.MeveoUser;
import org.meveo.security.SecuredEntity;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.security.SecuredBusinessEntityService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SecuredBusinessEntityMethodInterceptorTest {

    @Spy
    @InjectMocks
    private SecuredBusinessEntityMethodInterceptor securedBusinessEntityMethodInterceptor;

    @Mock
    private UserService userService;

    @Mock
    private ParamBeanFactory paramBeanFactory;

    @Mock
    private ParamBean paramBean;

    @Mock
    private MeveoUser currentUser;

    @Mock
    private EntityManager entityManager;

    @Mock
    private SecuredBusinessEntityService securedBusinessEntityService;

    /**
     * Sets up a mocked current user with secured entities
     * 
     * @param considerSellerAsParent Shall Seller be considered as a a parent for entity hierarchy traversal
     * @param securedEntities A list of secured entities. A repeated set of two items - Secured entity class and it's code
     */
    private void setUpCurrentUser(boolean considerSellerAsParent, Object... entitiesAllowed) {

        List<SecuredEntity> securedEntities = new ArrayList<>(entitiesAllowed.length / 2);

        for (int i = 0; i < entitiesAllowed.length; i = i + 2) {
            securedEntities.add(new SecuredEntity(Long.valueOf(i), (String) entitiesAllowed[i + 1], (String) entitiesAllowed[i], SecuredEntityPermissionEnum.READ));
        }

        Mockito.when(securedBusinessEntityService.getSecuredEntitiesForCurentUser()).thenReturn(securedEntities);

        Mockito.when(paramBeanFactory.getInstance()).thenReturn(paramBean);
        Mockito.when(paramBean.getPropertyAsBoolean(eq("accessible.entity.allows.access.childs.seller"), anyBoolean())).thenReturn(considerSellerAsParent);
    }

    @Test
    public void testNoConfiguration() throws Exception {

        InvocationContext methodContext = getMethodContext();
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class);

        setUpCurrentUser(false, Customer.class.getSimpleName(), "cust1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters()).isEmpty();

    }

    // ---- TEST single property

    @Test
    public void testSinglePropertyNoAllowedEntities() throws Exception {

        InvocationContext methodContext = getMethodContext();
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", Customer.class, false);
        setUpCurrentUser(false);
        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters()).isEmpty();
    }

    @Test
    public void testSinglePropertySameAllowedEntityClass() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", Customer.class, false);

        setUpCurrentUser(false, Customer.class.getSimpleName(), "cust1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(criteria.getFilters().get("code")).isEqualTo("cust1");
        assertThat(criteria.getFilters().size()).isEqualTo(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSinglePropertySameAllowedEntityClass_AllowNull() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", Customer.class, true);

        setUpCurrentUser(false, Customer.class.getSimpleName(), "cust1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).size()).isEqualTo(2);
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("code")).isEqualTo("cust1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("AND_secured code")).isEqualTo("IS_NULL");
        assertThat(criteria.getFilters().size()).isEqualTo(2);
    }

    @Test
    public void testSinglePropertySameAllowedEntityClassMaintainExistingCriteria() throws Exception {

        InvocationContext methodContext = getMethodContext("code", "cust1");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", Customer.class, false);

        setUpCurrentUser(false, Customer.class.getSimpleName(), "cust1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("code")).isEqualTo("cust1");
        assertThat(criteria.getFilters().size()).isEqualTo(1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSinglePropertySameAllowedEntityClassMaintainExistingCriteria_AllowNull() throws Exception {

        InvocationContext methodContext = getMethodContext("code", "cust1");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", Customer.class, true);

        setUpCurrentUser(false, Customer.class.getSimpleName(), "cust1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("code")).isEqualTo("cust1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).size()).isEqualTo(2);
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("code")).isEqualTo("cust1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("AND_secured code")).isEqualTo("IS_NULL");
        assertThat(criteria.getFilters().size()).isEqualTo(2);
    }

    @Test
    public void testSinglePropertySameAllowedEntityClassExistingCriteriaAccessDenied() throws Exception {

        InvocationContext methodContext = getMethodContext("code", "will throw exception as not match single permited customer");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", Customer.class, false);

        setUpCurrentUser(false, Customer.class.getSimpleName(), "cust1");

        assertThatThrownBy(() -> {
            securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);
        }).isExactlyInstanceOf(AccessDeniedException.class);

    }

    @Test
    public void testSinglePropertyParentAllowedEntityClassUA_BA() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", UserAccount.class, false);

        setUpCurrentUser(false, BillingAccount.class.getSimpleName(), "ba1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(criteria.getFilters().get("billingAccount.code")).isEqualTo("ba1");
        assertThat(criteria.getFilters().size()).isEqualTo(2);
    }

    @Test
    public void testSinglePropertyParentAllowedEntityClassUA_CA() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", UserAccount.class, false);

        setUpCurrentUser(false, CustomerAccount.class.getSimpleName(), "ca1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(criteria.getFilters().get("billingAccount.customerAccount.code")).isEqualTo("ca1");
        assertThat(criteria.getFilters().size()).isEqualTo(2);
    }

    @Test
    public void testSinglePropertyParentAllowedEntityClassUA_CUST() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", UserAccount.class, false);

        setUpCurrentUser(false, Customer.class.getSimpleName(), "cust1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(criteria.getFilters().get("billingAccount.customerAccount.customer.code")).isEqualTo("cust1");
        assertThat(criteria.getFilters().size()).isEqualTo(2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSinglePropertyParentAllowedEntityClassUA_SELLER_sellerIsNotConsideredAsParent() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", UserAccount.class, false);

        setUpCurrentUser(false, Seller.class.getSimpleName(), "seller1");

        assertThatThrownBy(() -> {
            securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);
        }).isExactlyInstanceOf(AccessDeniedException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSinglePropertyParentAllowedEntityClassUA_SELLER() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", UserAccount.class, false);

        setUpCurrentUser(true, Seller.class.getSimpleName(), "seller1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).size()).isEqualTo(2);
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("billingAccount.customerAccount.customer.seller.code")).isEqualTo("seller1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("billingAccount.customerAccount.customer.seller.seller.code")).isEqualTo("seller1");
        assertThat(criteria.getFilters().size()).isEqualTo(2);
    }

    @Test
    public void testSinglePropertyParentAllowedEntityClassUA_SELLER_AccessDenied() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", UserAccount.class, false);

        setUpCurrentUser(false, Seller.class.getSimpleName(), "seller1");

        assertThatThrownBy(() -> {
            securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);
        }).isExactlyInstanceOf(AccessDeniedException.class);
    }

    @Test
    public void testSinglePropertyParentAllowedEntityClassBA_CUST() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", BillingAccount.class, false);

        setUpCurrentUser(false, Customer.class.getSimpleName(), "cust1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(criteria.getFilters().get("customerAccount.customer.code")).isEqualTo("cust1");
        assertThat(criteria.getFilters().size()).isEqualTo(2);
    }

    @Test
    public void testSinglePropertyAllowedEntityClassLowerThanAccesibleClass() throws Exception {

        InvocationContext methodContext = getMethodContext("code", "test");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", BillingAccount.class, false);

        setUpCurrentUser(false, UserAccount.class.getSimpleName(), "ua1");

        assertThatThrownBy(() -> {
            securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);
        }).isExactlyInstanceOf(AccessDeniedException.class);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSinglePropertyMultipleParentAllowedEntityClassBA_CUST() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", BillingAccount.class, false);

        setUpCurrentUser(false, Customer.class.getSimpleName(), "cust1", Customer.class.getSimpleName(), "cust2", Customer.class.getSimpleName(), "cust3");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(((List<String>) criteria.getFilters().get("inList customerAccount.customer.code")).size()).isEqualTo(3);
        assertThat(((List<String>) criteria.getFilters().get("inList customerAccount.customer.code")).contains("cust1")).isTrue();
        assertThat(((List<String>) criteria.getFilters().get("inList customerAccount.customer.code")).contains("cust2")).isTrue();
        assertThat(((List<String>) criteria.getFilters().get("inList customerAccount.customer.code")).contains("cust3")).isTrue();
        assertThat(criteria.getFilters().size()).isEqualTo(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSinglePropertyMultipleParentAllowedEntityClassBA_CUST_withExistingCodeCriteria() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test", "code", "keep me");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", BillingAccount.class, false);

        setUpCurrentUser(false, Customer.class.getSimpleName(), "cust1", Customer.class.getSimpleName(), "cust2", Customer.class.getSimpleName(), "cust3");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(criteria.getFilters().get("code")).isEqualTo("keep me");
        assertThat(((List<String>) criteria.getFilters().get("inList customerAccount.customer.code")).size()).isEqualTo(3);
        assertThat(((List<String>) criteria.getFilters().get("inList customerAccount.customer.code")).contains("cust1")).isTrue();
        assertThat(((List<String>) criteria.getFilters().get("inList customerAccount.customer.code")).contains("cust2")).isTrue();
        assertThat(((List<String>) criteria.getFilters().get("inList customerAccount.customer.code")).contains("cust3")).isTrue();
        assertThat(criteria.getFilters().size()).isEqualTo(3);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSinglePropertyInListSearchRemoveNotAllowedEntities() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test", "inList code", "cust1,cust2,cust3");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", Customer.class, false);

        setUpCurrentUser(false, Customer.class.getSimpleName(), "cust1", Customer.class.getSimpleName(), "cust2");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(((List<String>) criteria.getFilters().get("inList code")).size()).isEqualTo(2);
        assertThat(((List<String>) criteria.getFilters().get("inList code")).contains("cust1")).isTrue();
        assertThat(((List<String>) criteria.getFilters().get("inList code")).contains("cust2")).isTrue();
        assertThat(criteria.getFilters().size()).isEqualTo(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSinglePropertyInListSearchRemoveNotAllowedEntitiesOneEntity() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test", "inList code", "cust1,cust2,cust3");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", Customer.class, false);

        setUpCurrentUser(false, Customer.class.getSimpleName(), "cust1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(((List<String>) criteria.getFilters().get("inList code")).size()).isEqualTo(1);
        assertThat(((List<String>) criteria.getFilters().get("inList code")).contains("cust1")).isTrue();
        assertThat(criteria.getFilters().size()).isEqualTo(2);
    }

    @Test
    public void testSinglePropertyInListSearchRemoveNotAllowedEntities_AccessDenied() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test", "inList code", "cust1,cust2,cust3");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", Customer.class, false);

        setUpCurrentUser(false, Customer.class.getSimpleName(), "cust4");

        assertThatThrownBy(() -> {
            securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);
        }).isExactlyInstanceOf(AccessDeniedException.class);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSinglePropertyMultipleAllowedEntitiesBA_CUST() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "code", BillingAccount.class, false);

        setUpCurrentUser(false, Customer.class.getSimpleName(), "cust1", UserAccount.class.getSimpleName(), "ua1", BillingAccount.class.getSimpleName(), "ba1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).size()).isEqualTo(2);
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("code")).isEqualTo("ba1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("customerAccount.customer.code")).isEqualTo("cust1");
        assertThat(criteria.getFilters().size()).isEqualTo(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMultiplePropertiesMultipleParentAllowedEntityClass_sellerIsNotConsideredAsParent() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "seller", Seller.class, false, "userAccount", UserAccount.class, false);

        setUpCurrentUser(false, Customer.class.getSimpleName(), "cust1", Seller.class.getSimpleName(), "seller1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).size()).isEqualTo(2);
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("seller.code")).isEqualTo("seller1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("userAccount.billingAccount.customerAccount.customer.code")).isEqualTo("cust1");
        assertThat(criteria.getFilters().size()).isEqualTo(2);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testMultiplePropertiesMultipleParentAllowedEntityClass() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "seller", Seller.class, false, "userAccount", UserAccount.class, false);

        setUpCurrentUser(true, Customer.class.getSimpleName(), "cust1", Seller.class.getSimpleName(), "seller1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).size()).isEqualTo(5);
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("seller.code")).isEqualTo("seller1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("seller.seller.code")).isEqualTo("seller1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("userAccount.billingAccount.customerAccount.customer.code")).isEqualTo("cust1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("userAccount.billingAccount.customerAccount.customer.seller.code")).isEqualTo("seller1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("userAccount.billingAccount.customerAccount.customer.seller.seller.code")).isEqualTo("seller1");
        assertThat(criteria.getFilters().size()).isEqualTo(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMultiplePropertiesMultipleParentAllowedEntityClass_AllowNull() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "seller", Seller.class, true, "userAccount", UserAccount.class, true);

        setUpCurrentUser(true, Customer.class.getSimpleName(), "cust1", Seller.class.getSimpleName(), "seller1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).size()).isEqualTo(7);
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("seller.code")).isEqualTo("seller1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("seller.seller.code")).isEqualTo("seller1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("userAccount.billingAccount.customerAccount.customer.code")).isEqualTo("cust1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("userAccount.billingAccount.customerAccount.customer.seller.code")).isEqualTo("seller1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("userAccount.billingAccount.customerAccount.customer.seller.seller.code")).isEqualTo("seller1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("AND_secured seller")).isEqualTo("IS_NULL");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("AND_secured userAccount")).isEqualTo("IS_NULL");
        assertThat(criteria.getFilters().size()).isEqualTo(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMultiplePropertiesMultipleParentAllowedEntityClassWithExistingCriteria() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test", "seller.code", "keep me");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "seller", Seller.class, false, "userAccount", UserAccount.class, false);

        setUpCurrentUser(true, Customer.class.getSimpleName(), "cust1", Seller.class.getSimpleName(), "seller1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(criteria.getFilters().get("seller.code")).isEqualTo("keep me");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).size()).isEqualTo(5);
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("seller.code")).isEqualTo("seller1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("seller.seller.code")).isEqualTo("seller1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("userAccount.billingAccount.customerAccount.customer.code")).isEqualTo("cust1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("userAccount.billingAccount.customerAccount.customer.seller.code")).isEqualTo("seller1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("userAccount.billingAccount.customerAccount.customer.seller.seller.code")).isEqualTo("seller1");
        assertThat(criteria.getFilters().size()).isEqualTo(3);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMultiplePropertiesMultipleParentAllowedEntityClassWithExistingCriteria_SellerDisabled() throws Exception {

        InvocationContext methodContext = getMethodContext("description", "test", "seller.code", "keep me");
        SecuredBusinessEntityConfig sbeConfig = getSecurityConfig(ListFilter.class, "seller", Seller.class, false, "userAccount", UserAccount.class, false);

        setUpCurrentUser(false, Customer.class.getSimpleName(), "cust1", Seller.class.getSimpleName(), "seller1");

        securedBusinessEntityMethodInterceptor.checkForSecuredEntities(methodContext, sbeConfig);

        PagingAndFiltering criteria = (PagingAndFiltering) methodContext.getParameters()[1];
        assertThat(criteria.getFilters().get("description")).isEqualTo("test");
        assertThat(criteria.getFilters().get("seller.code")).isEqualTo("keep me");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).size()).isEqualTo(2);
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("seller.code")).isEqualTo("seller1");
        assertThat(((Map<String, Object>) criteria.getFilters().get("OR_secured")).get("userAccount.billingAccount.customerAccount.customer.code")).isEqualTo("cust1");
        assertThat(criteria.getFilters().size()).isEqualTo(3);
    }

    /**
     * Get a method's security context configuration
     * 
     * @param filterType Filter type
     * @param properties A list of properties being secured. A repeated set of 3 items, corresponding to a FilterPropertyConfig attributes: Property name, Secured entity class that corresponds to that property, Allow
     *        access if null
     * @return Method's security context configuration
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private SecuredBusinessEntityConfig getSecurityConfig(Class<? extends SecureMethodResultFilter> filterType, Object... properties) {

        SecuredBusinessEntityConfig sbeConfig = new SecuredBusinessEntityConfig();

        SecuredMethodConfig methodConfig = new SecuredMethodConfig();
        methodConfig.setResultFilter(filterType);
        sbeConfig.setSecuredMethodConfig(methodConfig);

        if (ListFilter.class.isAssignableFrom(filterType)) {
            FilterResultsConfig filterConfig = new FilterResultsConfig();
            FilterPropertyConfig[] itemProperties = new FilterPropertyConfig[properties.length / 3];

            for (int i = 0; i < properties.length; i = i + 3) {
                itemProperties[i / 3] = new FilterPropertyConfig((String) properties[i], (Class) properties[i + 1], (boolean) properties[i + 2]);
            }

            filterConfig.setItemPropertiesToFilter(itemProperties);
            sbeConfig.setFilterResultsConfig(filterConfig);
        }
        return sbeConfig;
    }

    /**
     * Get method's invocation context
     * 
     * @param parameters A list of parameters to set in pagingAndFiltering parameter. A repeated set of 2 items - property name and property value
     * @return Method's invocation context
     */
    private InvocationContext getMethodContext(String... parameters) {

        InvocationContext context = new InvocationContext() {

            PagingAndFiltering paging = null;

            @Override
            public Constructor<?> getConstructor() {
                return null;
            }

            @Override
            public Method getMethod() {
                try {
                    return this.getClass().getDeclaredMethod("getMethod");
                } catch (NoSuchMethodException | SecurityException e) {
                    return null;
                }
            }

            @Override
            public Object getTarget() {
                return null;
            }

            @Override
            public Object getTimer() {
                return null;
            }

            @Override
            public Object[] getParameters() {

                if (paging == null) {
                    Map<String, Object> filter = new HashMap<String, Object>(parameters.length / 2);
                    for (int i = 0; i < parameters.length; i = i + 2) {
                        filter.put(parameters[i], parameters[i + 1]);
                    }

                    paging = new PagingAndFiltering();
                    paging.setFilters(filter);
                }
                return new Object[] { "", paging, "kuku" };
            }

            @Override
            public void setParameters(Object[] params) {
            }

            @Override
            public Map<String, Object> getContextData() {
                return null;
            }

            @Override
            public Object proceed() throws Exception {
                return null;
            }

        };
        return context;
    }
}