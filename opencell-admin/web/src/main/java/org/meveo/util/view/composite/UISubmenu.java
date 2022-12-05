package org.meveo.util.view.composite;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.component.UIComponent;

import org.meveo.security.AccessScopeEnum;
import org.meveo.util.view.PageAccessHandler;
import org.primefaces.component.menuitem.UIMenuItem;

public class UISubmenu extends org.primefaces.component.submenu.UISubmenu {

    @Override
    public boolean isRendered() {

        boolean accessible = super.isRendered();

        Set<String> outcomes = getChildrenOutcomes(getChildren());
        if (!outcomes.isEmpty()) {
            PageAccessHandler pageAccessHandler = (PageAccessHandler) CDI.current().select(PageAccessHandler.class).get();
            accessible = accessible && pageAccessHandler.isOutcomeAccesible(AccessScopeEnum.LIST.getHttpMethod(), outcomes.toArray(new String[0]));
        }
        return accessible;
    }

    private Set<String> getChildrenOutcomes(List<UIComponent> children) {

        Set<String> outcomes = new HashSet<String>();
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