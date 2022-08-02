package org.meveo.util.view.composite;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.CDI;
import javax.faces.component.UIComponent;

import org.meveo.security.AccessScopeEnum;
import org.meveo.util.view.PageAccessHandler;
import org.primefaces.component.menuitem.UIMenuItem;

public class UISubmenu extends org.primefaces.component.submenu.UISubmenu {

    @Override
    public boolean isRendered() {

        boolean accessible = super.isRendered();

        List<String> outcomes = getChildrenOutcomes(getChildren());
        PageAccessHandler pageAccessHandler = (PageAccessHandler) CDI.current().select(PageAccessHandler.class).get();
        accessible = accessible && pageAccessHandler.isOutcomeAccesible(AccessScopeEnum.LIST.getHttpMethod(), outcomes.toArray(new String[0]));
        return accessible;
    }

    private List<String> getChildrenOutcomes(List<UIComponent> children) {

        List<String> outcomes = new ArrayList<String>();
        for (UIComponent child : children) {
            if (child instanceof UIMenuItem) {
                outcomes.add(((UIMenuItem) child).getOutcome());
            } else if (child instanceof org.primefaces.component.submenu.UISubmenu) {
                outcomes.addAll(getChildrenOutcomes(child.getChildren()));
            }
        }
        return outcomes;
    }
}