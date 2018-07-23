package org.meveo.util.view;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Custom converter to get rid of scientific display for CF Multivalue double fields
 * 
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.1
 */
@FacesConverter("org.meveo.util.view.CFMultiValueConverter")
public class CFMultiValueConverter implements Converter {

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        
        if(value instanceof Double || value instanceof Long) {
            DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            df.setMaximumFractionDigits(340);
            return df.format(value);
        } 

        return (String) value;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return null;
    }

}
