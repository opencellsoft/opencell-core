package org.meveo.multitenancy;

/**
 * 
 * NOT USED FOR NOW, as same code is implemented inside the CurrentUserProvider class. Left here only in case we need static access in the future.
 * 
 * 
 * Thread local storage of the current provider/tenant. This is the only place, where the provider name is available across all calls and beans. <br/>
 * Note, this is raw storage only and might not be initialized. Use currentUserProvider.getCurrentUserProviderCode(); to retrieve and/or initialize current provider value instead.
 * 
 * {@link https://www.tomas-dvorak.cz/posts/jpa-multitenancy/}
 */
public class CurrentTenantHolder {

    private static final InheritableThreadLocal<String> currentTenant = new InheritableThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "NA";
        }
    };

    /**
     * Check if current tenant value is set (differs from the initial value)
     * 
     * @return If current tenant value was set
     */
    public static boolean isCurrentTenantSet() {
        return !"NA".equals(currentTenant.get());
    }

    /**
     * Returns a current provider code. Note, this is raw storage only and might not be initialized. Use currentUserProvider.getCurrentUserProviderCode(); to retrieve and/or
     * initialize current provider value instead.
     * 
     * @return Current provider code
     */
    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    public static void setCurrentTenant(final String tenantName) {
        currentTenant.remove();
        currentTenant.set(tenantName);
    }
}