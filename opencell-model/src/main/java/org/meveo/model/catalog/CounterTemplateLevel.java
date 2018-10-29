package org.meveo.model.catalog;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.3
 */
public enum CounterTemplateLevel {
    SI, SU, UA, BA;

    public String getLabel() {
        return "enum.counterTemplateLevel." + name();
    }

}
