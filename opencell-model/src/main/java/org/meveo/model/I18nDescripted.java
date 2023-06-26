package org.meveo.model;
import java.util.Map;
public interface I18nDescripted extends ISearchable {
	public String getDescriptionOrCode();
    public Map<String, String> getDescriptionI18n();
}