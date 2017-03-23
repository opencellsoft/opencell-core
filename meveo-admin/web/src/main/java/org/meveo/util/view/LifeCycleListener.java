package org.meveo.util.view;

import java.util.Date;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

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