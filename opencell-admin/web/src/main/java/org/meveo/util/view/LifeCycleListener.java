/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.util.view;

import java.util.Date;

import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifeCycleListener implements PhaseListener {

    private static final long serialVersionUID = 3744688960206329587L;

    Logger log = LoggerFactory.getLogger(this.getClass());

    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

//    <!-- <lifecycle> -->
//    <!--     <phase-listener>org.meveo.util.view.LifeCycleListener</phase-listener> -->
//    <!-- </lifecycle> -->
    public void beforePhase(PhaseEvent event) {

        // GetFieldInformationHandler.time = 0;
        log.error("AKK START PHASE {}", event.getPhaseId());

        // FacesContext facesContext = FacesContext.getCurrentInstance();
        // VisitContext visitContext = VisitContext.createVisitContext(facesContext);
        //
        // CountingVisitCallback callback = new CountingVisitCallback();
        // if (facesContext != null && facesContext.getViewRoot() != null) {
        // facesContext.getViewRoot().visitTree(visitContext, callback);
        // log.error("Number of Components: {}", callback.getCount());
        // }
        // for (String info : callback.getComponentInfo()) {
        // LOG.log(Level.INFO, "Component found: " + info);
        // }

    }

    public void afterPhase(PhaseEvent event) {
        log.error("AKK end PHASE {} {}", event.getPhaseId(), (new Date()));
    }

    // /**
    // * VisitCallback that is used to gather information about the component tree. Keeps track of the total number of components and maintains a list of basic component
    // information.
    // */
    // private class CountingVisitCallback implements VisitCallback {
    //
    // private int count = 0;
    // private List componentInfo = new ArrayList();
    //
    // /**
    // * This method will be invoked on every node of the component tree.
    // */
    // @Override
    // public VisitResult visit(VisitContext context, UIComponent target) {
    //
    // count++;
    // getComponentInfo().add(target.getClientId() + " [" + target.getClass().getSimpleName() + "]");
    //
    // // descend into current subtree, if applicable
    // return VisitResult.ACCEPT;
    // }
    //
    // public int getCount() {
    // return count;
    // }
    //
    // public List getComponentInfo() {
    // return componentInfo;
    // }
    // }
}