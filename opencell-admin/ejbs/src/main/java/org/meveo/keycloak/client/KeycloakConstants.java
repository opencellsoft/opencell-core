package org.meveo.keycloak.client;

import com.google.common.collect.ImmutableList;

/**
 * @author Edward P. Legaspi
 * @since 10 Nov 2017
 **/
public class KeycloakConstants {

    public static final String ROLE_API_ACCESS = "apiAccess";
    public static final String ROLE_GUI_ACCESS = "guiAccess";
    public static final String ROLE_ADMINISTRATEUR = "administrateur";
    public static final String ROLE_USER_MANAGEMENT = "userManagement";
    public static final ImmutableList<String> ROLE_KEYCLOAK_DEFAULT_EXCLUDED = ImmutableList.of("uma_authorization", "offline_access");
}
