package org.meveo.util.view;

import java.io.Serializable;
import java.util.Locale;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
@SessionScoped
public class LocaleSelector implements Serializable {

	private static final long serialVersionUID = -4072480474117257543L;

	private Locale currentLocale;

	/**
	 * Change user locale
	 * 
	 * @param localeCode
	 *            Language/country code
	 * @return
	 */
	public void setLocale(String localeCode) {
		setCurrentLocale(new Locale(localeCode));
	}

	public Locale getCurrentLocale() {
		if (currentLocale != null)
			return currentLocale;
		else
			return new Locale("en");
	}

	public void setCurrentLocale(Locale currentLocale) {
		FacesContext.getCurrentInstance().getViewRoot().setLocale(currentLocale);
		this.currentLocale = currentLocale;
	}

}
