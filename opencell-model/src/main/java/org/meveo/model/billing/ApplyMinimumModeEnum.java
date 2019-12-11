package org.meveo.model.billing;

public enum ApplyMinimumModeEnum {
    /**
     * Apply all invoice minimum rules.
     */
    ALL,
    /**
     * Apply no invoice minimum rule.
     */
    NONE,
    /**
     * Don't apply rules set on parent accounts.
     */
    NO_PARENT
}
