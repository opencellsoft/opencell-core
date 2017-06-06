/**
 * 
 */
package org.meveo.admin.action.catalog;

import java.util.Comparator;

import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.ServiceTemplate;

/**
 * @author phung
 *
 */
public class DescriptionComparator implements Comparator<OfferServiceTemplate> {

	@Override
	public int compare(OfferServiceTemplate first, OfferServiceTemplate second) {
		if ((first == null) && (second != null)) {
			return -1;
		} if ((first != null) && (second == null)) {
			return 1;
		} else if ((first != null) && (second != null)) {
			ServiceTemplate firstServiceTemplate = first.getServiceTemplate();
			ServiceTemplate secondServiceTemplate = second.getServiceTemplate();
			if (firstServiceTemplate != null && secondServiceTemplate != null) {
				String firstDescription = firstServiceTemplate.getDescription();
				String secondDescription = secondServiceTemplate.getDescription();
				if (firstDescription != null && secondDescription != null) {
					return firstDescription.compareTo(secondDescription);
				}
			}

		}
		
		return 0;
	}

}
