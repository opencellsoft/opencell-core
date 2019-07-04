package org.meveo.model.audit;

/**
 * Channel in which a entity was changed.
 *
 * @author Abdellatif BARI
 * @since 7.0
 */
public enum ChangeOriginEnum {
    API, JOB, GUI, INBOUND_REQUEST, OTHER;

    public String getLabel() {
        return name();
    }
}
