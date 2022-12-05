/*
 * Copyright 2009-2015 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.util.view.composite;

import jakarta.faces.application.ResourceDependencies;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.context.FacesContext;

import org.primefaces.component.menu.AbstractMenu;

@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "primefaces.css"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "primefaces.js")
})
public class OpencellMenu extends AbstractMenu implements org.primefaces.component.api.Widget {

    public static final String COMPONENT_TYPE = "org.primefaces.component.OpencellMenu";
    public static final String COMPONENT_FAMILY = "org.primefaces.component";
    private static final String DEFAULT_RENDERER = "org.primefaces.component.OpencellMenuRenderer";

    protected enum PropertyKeys {

        widgetVar, model, style, styleClass;

        String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        public String toString() {
            return ((this.toString != null) ? this.toString : super.toString());
        }
    }

    public OpencellMenu() {
        setRendererType(DEFAULT_RENDERER);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public org.primefaces.model.menu.MenuModel getModel() {
        return (org.primefaces.model.menu.MenuModel) getStateHelper().eval(PropertyKeys.model, null);
    }

    public void setModel(org.primefaces.model.menu.MenuModel _model) {
        getStateHelper().put(PropertyKeys.model, _model);
    }

    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(java.lang.String _style) {
        getStateHelper().put(PropertyKeys.style, _style);
    }

    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(java.lang.String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }

    public String resolveWidgetVar() {
        FacesContext context = getFacesContext();
        String userWidgetVar = (String) getAttributes().get("widgetVar");

        if (userWidgetVar != null) {
            return userWidgetVar;
        } else {
            return "widget_" + getClientId(context).replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
        }
    }
    @Override
    public boolean isRendered() {
        return true;
    }
}
