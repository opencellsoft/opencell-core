package org.meveo.model.audit;

/**
 * Channel in which a entity was changed.
 *
 * @author Abdellatif BARI
 * @since 7.0
 */
public enum ChangeOrigin {
    API, JOB, GUI, OTHER;

    public String getLabel() {
        return name();
    }
}
