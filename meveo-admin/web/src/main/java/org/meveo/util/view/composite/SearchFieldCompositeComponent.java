package org.meveo.util.view.composite;

import java.util.Map;

import javax.faces.component.FacesComponent;

/**
 * Backing UINamingContainer for searchField.xhtml composite component.
 */
@FacesComponent(value = "searchField")
public class SearchFieldCompositeComponent extends
		BackingBeanBasedCompositeComponent {

	/**
	 * Helper method to get filters from backing bean.
	 */
	public Map<String, Object> getFilters() {
		return super.getBackingBeanFromParentOrCurrent().getFilters();
	}

	public String getFromRangeSearchFilterName() {
		return "fromRange-" + getAttributes().get("field");
	}

	public String getToRangeSearchFilterName() {
		return "toRange-" + getAttributes().get("field");
	}

	public String getFromRangeSearchFilterNameFromParent() {
		return "fromRange-"+getAttributes().get("field");
	}

	public String getToRangeSearchFilterNameFromParent() {
		return "toRange-"+getAttributes().get("field");
	}
}
