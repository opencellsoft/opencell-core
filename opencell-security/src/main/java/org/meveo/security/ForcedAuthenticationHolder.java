package org.meveo.security;

/**
 * Thread local storage of the current overriden username for authentication purpose in jobs.
 * 
 * {@link https://www.tomas-dvorak.cz/posts/jpa-multitenancy/}
 */
public class ForcedAuthenticationHolder {

    private static final InheritableThreadLocal<String> forcedUserUsername = new InheritableThreadLocal<String>();

    public static String getForcedUsername() {
        return forcedUserUsername.get();
    }

    public static void setForcedUsername(final String username) {
        forcedUserUsername.set(username);
    }

    public static void cleanup() {
        forcedUserUsername.remove();
    }
}