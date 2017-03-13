package org.meveo.util.view.composite;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIOutput;

/**
 * A hack that composite component would not be treated as a naming container and prepend it's own generated id
 */
@FacesComponent(value = "noNamingCC")
public class NotNamingCompositeComponent extends UIOutput{
    
    public final static String COMPONENT_TYPE = "noNamingCC";
    
    @Override
    public String getFamily()
    {
        return UINamingContainer.COMPONENT_FAMILY;
    }
	
}
