package org.meveo.api.payment;

import static org.junit.Assert.assertEquals;
import static org.meveo.model.payments.AccountOperationStatus.EXPORTED;
import static org.meveo.model.payments.AccountOperationStatus.POSTED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AccountOperationStatus;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class AccountOperationApiTest {

    @InjectMocks
    private AccountOperationApi accountOperationApi;

    @Mock
    private AccountOperationService accountOperationService;

    @Mock
    private UserService userService;

    @Mock
    private MeveoUser currentUser;

    private static final String CURRENT_USER_NAME = "opencell admin";

    private AccountOperation accountOperation;
    private User user;
    private final Date newAccountingDate = new Date();

    @Before
    public void setUp() {
        accountOperation = createAccountOperation(POSTED, new Date());
        user = createUserWithRoles(true);

        when(accountOperationService.findById(any())).thenReturn(accountOperation);
        when(currentUser.getUserName()).thenReturn(CURRENT_USER_NAME);
        when(userService.findByUsername(any())).thenReturn(user);
        when(accountOperationService.update(any())).thenReturn(accountOperation);
    }

    @Test
    public void shouldUpdateAccountingDate() {
        AccountOperation accountOperation = accountOperationApi.updateAccountingDate(1l, newAccountingDate);

        assertEquals((Long) 1l, accountOperation.getId());
        assertEquals(newAccountingDate, accountOperation.getAccountingDate());
    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void testAccountingDateWhenAoNotFound() {
        when(accountOperationService.findById(any())).thenReturn(null);

        AccountOperation updatedAccountOperation = accountOperationApi.updateAccountingDate(2l, newAccountingDate);
        assertEquals(newAccountingDate, updatedAccountOperation.getAccountingDate());
    }

    @Test(expected = BusinessException.class)
    public void shouldNotUpdateAccountingDateWhenAOIsExported() {
        AccountOperation accountOperation = createAccountOperation(EXPORTED, new Date());

        when(accountOperationService.findById(any())).thenReturn(accountOperation);

        AccountOperation updatedAccountOperation = accountOperationApi.updateAccountingDate(1l, newAccountingDate);
        assertEquals(newAccountingDate, updatedAccountOperation.getAccountingDate());
    }

    @Test(expected = BusinessException.class)
    public void shouldNotUpdateAccountingDateWhenUserHasNotFinanceManagementPermission() {
        AccountOperation accountOperation = createAccountOperation(POSTED, new Date());
        User user = createUserWithRoles(false);

        when(accountOperationService.findById(any())).thenReturn(accountOperation);
        when(userService.findByUsername(any())).thenReturn(user);

        AccountOperation updatedAccountOperation = accountOperationApi.updateAccountingDate(1l, newAccountingDate);
        assertEquals(newAccountingDate, updatedAccountOperation.getAccountingDate());
    }

    private AccountOperation createAccountOperation(AccountOperationStatus status, Date accountingDate) {
        Auditable auditable = new Auditable();
        auditable.setUpdater("opencell.admin");
        auditable.setCreator("opencell.admin");
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setStatus(status);
        accountOperation.setId(1l);
        accountOperation.setCode("ACCOUNT_OPERATION_CODE");
        accountOperation.setDescription("ACCOUNT OPERATION CODE");
        accountOperation.setAuditable(auditable);
        accountOperation.setAccountingDate(accountingDate);
        return accountOperation;
    }

    private User createUserWithRoles(Boolean withFinanceManagementPermission) {
        User user = new User();
        user.setId(1l);
        user.setCode("opencell_admin_code");
        user.setUserName(CURRENT_USER_NAME);
        user.setDescription("opencell admin");
        Set<Permission> permissions = new HashSet<>();
        if (withFinanceManagementPermission) {
            Permission financeManagementPermission = new Permission();
            financeManagementPermission.setId(2l);
            financeManagementPermission.setName("financeManagement");
            financeManagementPermission.setPermission("financeManagement");
            permissions.add(financeManagementPermission);
        }
        Permission permission = new Permission();
        permission.setId(1l);
        permission.setName("visualizationManagement");
        permission.setPermission("visualizationManagement");
        permissions.add(permission);
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setName("ManagementRole");
        role.setDescription("ManagementRole");
        role.setPermissions(permissions);
        roles.add(role);
        user.setRoles(roles);
        return user;
    }
}
