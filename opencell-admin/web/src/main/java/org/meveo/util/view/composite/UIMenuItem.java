package org.meveo.util.view.composite;

import javax.enterprise.inject.spi.CDI;

import org.meveo.security.AccessScopeEnum;
import org.meveo.util.view.PageAccessHandler;

public class UIMenuItem extends org.primefaces.component.menuitem.UIMenuItem {

    @Override
    public boolean isRendered() {

        boolean accessible = super.isRendered();

        PageAccessHandler pageAccessHandler = (PageAccessHandler) CDI.current().select(PageAccessHandler.class).get();
        accessible = accessible && pageAccessHandler.isOutcomeAccesible(AccessScopeEnum.LIST.getHttpMethod(), getOutcome());
        return accessible;
    }
}
