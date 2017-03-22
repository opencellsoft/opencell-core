package org.meveo.util.view.composite;

import java.util.Map;

import javax.faces.component.FacesComponent;

/**
 * Backing UINamingContainer for minmaxSearchField.xhtml composite component.
 */
@FacesComponent(value = "minmaxSearchField")
public class MinmaxSearchFieldCompositeComponent extends
		BackingBeanBasedCompositeComponent {

	/**
	 * Helper method to get filters from backing bean.
	 */
	public Map<String, Object> getFilters() {
		return super.getBackingBeanFromParentOrCurrent().getFilters();
	}

	public String getMinmaxRangeSearchFilterName() {
		return "minmaxRange-" + getAttributes().get("minField") + "-"
				+ getAttributes().get("maxField");
	}

	public String getMinmaxRangeSearchFilterNameFromParent() {
		return "minmaxRange-"
				+ getCompositeComponentParent(this).getAttributes().get(
						"minField") + "-" + getAttributes().get("maxField");
	}
}
