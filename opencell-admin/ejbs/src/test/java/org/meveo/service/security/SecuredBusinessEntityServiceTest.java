package org.meveo.service.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.AccountEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SecuredBusinessEntityServiceTest {

	@Spy
	@InjectMocks
	private SecuredBusinessEntityService securedBusinessEntityService;

	@Mock
	ParamBeanFactory paramBeanFactory;

	@Mock
	ParamBean paramBean;

	@Before
	public void setUp() {
		Mockito.when(paramBeanFactory.getInstance()).thenReturn(paramBean);
		Mockito.when(paramBean.getBooleanValue("accessible.entity.allows.access.childs.seller", false))
				.thenReturn(false);

	}

	@Test
	public void should_access_all_direct_seller_hierarchy_but_not_parent() {

		Map<String, AccountEntity> hierarchy = new TreeMap<String, AccountEntity>();
		Seller parent = new Seller();
		parent.setCode("parent");
		addAccountToMap(parent, hierarchy);
		Seller s = addFullHierarchy("S0", parent, hierarchy);

		Map<Class<?>, Set<SecuredEntity>> allSecuredEntitiesMap = new HashMap<Class<?>, Set<SecuredEntity>>();
		Set<SecuredEntity> secured = new HashSet(Arrays.asList(new SecuredEntity(s)));
		allSecuredEntitiesMap.put(Seller.class, secured);

		for (AccountEntity entity : hierarchy.values()) {
			Mockito.doReturn(entity).when(securedBusinessEntityService).getEntityByCode(entity.getClass(),
					entity.getCode());
		}

		for (AccountEntity entity : hierarchy.values()) {
			boolean entityAllowed = securedBusinessEntityService.isEntityAllowed(entity, allSecuredEntitiesMap, false);
			if (parent.getCode() != entity.getCode()) {
				assertThat(entityAllowed).isTrue();
			} else {
				assertThat(entityAllowed).isFalse();
			}
		}
	}

	@Test
	public void should_not_access_other_sellers_hierarchy() {

		Map<String, AccountEntity> hierarchy = new TreeMap<String, AccountEntity>();
		Seller parent = new Seller();
		parent.setCode("parent");
		addAccountToMap(parent, hierarchy);
		Seller s3 = addFullHierarchy("S3", parent, hierarchy);
		Seller s2 = addFullHierarchy("S2", s3, hierarchy);
		addFullHierarchy("S1", s3, hierarchy);

		Map<Class<?>, Set<SecuredEntity>> allSecuredEntitiesMap = new HashMap<Class<?>, Set<SecuredEntity>>();
		Set<SecuredEntity> secured = new HashSet(Arrays.asList(new SecuredEntity(s2)));
		allSecuredEntitiesMap.put(Seller.class, secured);

		for (AccountEntity entity : hierarchy.values()) {
			Mockito.doReturn(entity).when(securedBusinessEntityService).getEntityByCode(entity.getClass(),
					entity.getCode());
		}

		for (AccountEntity entity : hierarchy.values()) {
			boolean entityAllowed = securedBusinessEntityService.isEntityAllowed(entity, allSecuredEntitiesMap, false);

			if (entity.getCode().startsWith(s2.getCode())) {
				assertThat(entityAllowed).isTrue();
			} else {
				assertThat(entityAllowed).isFalse();
			}
		}
	}

	@Test
	public void should_not_access_other_sellers_hierarchy_even_if_seller_hierarchy_enabled() {
		Mockito.when(paramBean.getBooleanValue("accessible.entity.allows.access.childs.seller", false))
				.thenReturn(true);
		Map<String, AccountEntity> hierarchy = new TreeMap<String, AccountEntity>();
		Seller parent = new Seller();
		parent.setCode("parent");
		addAccountToMap(parent, hierarchy);
		Seller s3 = addFullHierarchy("S3", parent, hierarchy);
		Seller s2 = addFullHierarchy("S2", s3, hierarchy);
		addFullHierarchy("S1", s3, hierarchy);

		Map<Class<?>, Set<SecuredEntity>> allSecuredEntitiesMap = new HashMap<Class<?>, Set<SecuredEntity>>();
		Set<SecuredEntity> secured = new HashSet(Arrays.asList(new SecuredEntity(s2)));
		allSecuredEntitiesMap.put(Seller.class, secured);

		for (AccountEntity entity : hierarchy.values()) {
			Mockito.doReturn(entity).when(securedBusinessEntityService).getEntityByCode(entity.getClass(),
					entity.getCode());
		}

		for (AccountEntity entity : hierarchy.values()) {
			boolean entityAllowed = securedBusinessEntityService.isEntityAllowed(entity, allSecuredEntitiesMap, false);

			if (entity.getCode().startsWith(s2.getCode())) {
				assertThat(entityAllowed).isTrue();
			} else {
				assertThat(entityAllowed).isFalse();
			}
		}
	}

	@Test
	public void should_access_all_super_seller_hierarchy_if_enabled() {
		Mockito.when(paramBean.getBooleanValue("accessible.entity.allows.access.childs.seller", false))
				.thenReturn(true);

		Map<String, AccountEntity> hierarchy = new TreeMap<String, AccountEntity>();
		Seller parent = new Seller();
		parent.setCode("parent");
		addAccountToMap(parent, hierarchy);
		Seller s3 = addFullHierarchy("S3", parent, hierarchy);
		Seller s2 = addFullHierarchy("S2", s3, hierarchy);
		addFullHierarchy("S1", s3, hierarchy);

		Map<Class<?>, Set<SecuredEntity>> allSecuredEntitiesMap = new HashMap<Class<?>, Set<SecuredEntity>>();
		Set<SecuredEntity> secured = new HashSet(Arrays.asList(new SecuredEntity(s3)));
		allSecuredEntitiesMap.put(Seller.class, secured);

		for (AccountEntity entity : hierarchy.values()) {
			Mockito.doReturn(entity).when(securedBusinessEntityService).getEntityByCode(entity.getClass(),
					entity.getCode());
		}

		for (AccountEntity entity : hierarchy.values()) {
			boolean entityAllowed = securedBusinessEntityService.isEntityAllowed(entity, allSecuredEntitiesMap, false);

			if (parent.getCode() != entity.getCode()) {
				assertThat(entityAllowed).isTrue();
			} else {
				assertThat(entityAllowed).isFalse();
			}
		}
	}

	@Test
	public void should_not_access_all_super_seller_hierarchy_if_disabled() {

		Map<String, AccountEntity> hierarchy = new TreeMap<String, AccountEntity>();
		Seller parent = new Seller();
		parent.setCode("parent");
		addAccountToMap(parent, hierarchy);
		Seller s3 = addFullHierarchy("S3", parent, hierarchy);
		Seller s2 = addFullHierarchy("S2", s3, hierarchy);
		addFullHierarchy("S1", s3, hierarchy);

		Map<Class<?>, Set<SecuredEntity>> allSecuredEntitiesMap = new HashMap<Class<?>, Set<SecuredEntity>>();
		Set<SecuredEntity> secured = new HashSet(Arrays.asList(new SecuredEntity(s3)));
		allSecuredEntitiesMap.put(Seller.class, secured);

		for (AccountEntity entity : hierarchy.values()) {
			Mockito.doReturn(entity).when(securedBusinessEntityService).getEntityByCode(entity.getClass(),
					entity.getCode());
		}

		for (AccountEntity entity : hierarchy.values()) {
			boolean entityAllowed = securedBusinessEntityService.isEntityAllowed(entity, allSecuredEntitiesMap, false);

			if (entity.getCode().startsWith(s3.getCode())) {
				assertThat(entityAllowed).isTrue();
			} else {
				assertThat(entityAllowed).isFalse();
			}
		}
	}

	private void addAccountToMap(AccountEntity entity, Map<String, AccountEntity> hierarchy) {
		hierarchy.put(entity.getCode(), entity);
	}

	private Seller addFullHierarchy(String sellerCode, Seller parent, Map<String, AccountEntity> hierarchy) {

		Seller seller = new Seller();
		Customer c = new Customer();
		CustomerAccount ca = new CustomerAccount();
		BillingAccount ba = new BillingAccount();
		UserAccount ua = new UserAccount();

		Customer c2 = new Customer();
		CustomerAccount ca2 = new CustomerAccount();
		BillingAccount ba2 = new BillingAccount();
		UserAccount ua2 = new UserAccount();

		seller.setSeller(parent);
		c.setSeller(seller);
		ca.setCustomer(c);
		ba.setCustomerAccount(ca);
		ua.setBillingAccount(ba);

		c2.setSeller(seller);
		ca2.setCustomer(c);
		ba2.setCustomerAccount(ca);
		ua2.setBillingAccount(ba);

		seller.setCode(sellerCode);
		c.setCode(sellerCode + "_C");
		ca.setCode(sellerCode + "_CA");
		ba.setCode(sellerCode + "_BA");
		ua.setCode(sellerCode + "_UA");

		c2.setCode(sellerCode + "_C2");
		ca2.setCode(sellerCode + "_CA2");
		ba2.setCode(sellerCode + "_BA2");
		ua2.setCode(sellerCode + "_UA2");

		AccountEntity[] array = { parent, seller, c, ca, ba, ua, c2, ca2, ba2, ua2 };
		final List<AccountEntity> values = Arrays.asList(array);
		values.stream().forEach(x -> addAccountToMap(x, hierarchy));
		return seller;
	}

}
