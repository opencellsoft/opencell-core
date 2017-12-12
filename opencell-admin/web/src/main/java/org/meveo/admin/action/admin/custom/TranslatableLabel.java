package org.meveo.admin.action.admin.custom;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents a translatable text
 * 
 * @author Andrius Karpavicius
 */
public class TranslatableLabel implements Serializable {

    private static final long serialVersionUID = 8925358878113274718L;

    private String TRANSLATION_SEPARATOR = "|";
    private String TRANSLATION_SEPARATOR_REVERSE = "\\|";

    private String label;

    private Map<String, String> labelI18n;

    public TranslatableLabel() {
    }

    /**
     * 
     * @param encodedLabel Label encoded in the following form: &lt;default value&gt;|&lt;language&gt;=&lt;translatedLabel&gt;|&lt;language&gt;=&lt;translatedLabel&gt;
     */
    public TranslatableLabel(String encodedLabel) {
        if (encodedLabel == null) {
            return;
        }

        String[] splitLabel = encodedLabel.split(TRANSLATION_SEPARATOR_REVERSE);
        if (splitLabel.length >= 1) {
            label = splitLabel[0];
        }

        if (splitLabel.length > 1) {
            labelI18n = new HashMap<>();
            for (int i = 1; i < splitLabel.length; i++) {
                String[] languageLabel = splitLabel[i].split("=");
                labelI18n.put(languageLabel[0], languageLabel[1]);
            }
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<String, String> getLabelI18n() {
        return labelI18n;
    }

    public void setLabelI18n(Map<String, String> labelI18n) {
        this.labelI18n = labelI18n;
    }

    public String getLabel(String language) {

        if (language == null || labelI18n == null || labelI18n.isEmpty()) {
            return label;
        }

        language = language.toUpperCase();
        if (!labelI18n.containsKey(language)) {
            return label;
        } else {
            return labelI18n.get(language);
        }
    }

    /**
     * Instantiate descriptionI18n field if it is null. NOTE: do not use this method unless you have an intention to modify it's value, as entity will be marked dirty and record
     * will be updated in DB
     * 
     * @return descriptionI18n value or instantiated descriptionI18n field value
     */
    public Map<String, String> getLabelI18nNullSafe() {
        if (labelI18n == null) {
            labelI18n = new HashMap<>();
        }
        return labelI18n;
    }

    @Override
    public String toString() {
        String encodedLabel = label == null ? "" : label;

        if (labelI18n != null) {
            for (Entry<String, String> translatedLabel : labelI18n.entrySet()) {
                if (!StringUtils.isBlank(translatedLabel.getValue())) {
                    encodedLabel = encodedLabel + TRANSLATION_SEPARATOR + translatedLabel.getKey() + "=" + translatedLabel.getValue();
                }
            }
        }
        return encodedLabel;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(label) && (labelI18n == null || labelI18n.isEmpty());
    }

    @Override
    public boolean equals(Object other) {

        if (label == null) {
            return other == null;
        }

        if (other == null) {
            return false;
        }

        // Compare to string - either just a label field or label and translations
        if (other instanceof String) {
            String otherString = (String) other;
            // Just a label
            if (label.equals(otherString)) {
                return true;
                // Label and translations
            } else if (toString().equals(other)) {
                return true;
                // Label only (translations removed)
            } else if (otherString.contains(TRANSLATION_SEPARATOR) && label.equals((otherString.substring(0, (otherString.indexOf(TRANSLATION_SEPARATOR)))))) {
                return true;
            }

        } else if (other instanceof TranslatableLabel) {
            return label.equals(((TranslatableLabel) other).getLabel());
        }
        return false;
    }
}