package org.meveo.util.view.composite;

import java.util.Map;

import javax.faces.component.FacesComponent;

/**
 * Backing UINamingContainer for likeCriteriasSearchField.xhtml composite component.
 */
@FacesComponent(value = "likeCriteriasSearchField")
public class LikeCriteriasSearchFieldCompositeComponent extends
		BackingBeanBasedCompositeComponent {

	/**
	 * Helper method to get filters from backing bean.
	 */
	public Map<String, Object> getFilters() {
		return super.getBackingBeanFromParentOrCurrent().getFilters();
	}

	public String getLikeCriteriasRangeSearchFilterName() {
		String fields_key=(String)getAttributes().get("fields");
		StringBuilder sb=new StringBuilder();
		String[] fields=fields_key.split(",");
		for(int i=0;i<fields.length;i++){
			if(i!=0){
				sb.append("-");
			}
			sb.append(fields[i]);
		}
		return "likeCriterias-" + sb.toString();
	}
}
