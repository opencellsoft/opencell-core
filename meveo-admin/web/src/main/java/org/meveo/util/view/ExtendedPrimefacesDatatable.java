package org.meveo.util.view;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;

/**
 * 
 * Create our own datatable component based on primefaces one, because its
 * impossible to use column sortBy attribute in composite component since EL
 * expression is not evaluated. Issue:
 * http://code.google.com/p/primefaces/issues/detail?id=2930
 */
@FacesComponent(value = "ExtendedPrimefacesDatatable")
public class ExtendedPrimefacesDatatable extends DataTable {

	/**
	 * @see org.primefaces.component.datatable.DataTable#resolveSortField()
	 */
	@Override
	public String resolveStaticField(ValueExpression expression) {
		if (expression != null) {
			FacesContext context = getFacesContext();
			ELContext eLContext = context.getELContext();

			return (String) expression.getValue(eLContext);
		} else {
			return null;
		}
	}

}
