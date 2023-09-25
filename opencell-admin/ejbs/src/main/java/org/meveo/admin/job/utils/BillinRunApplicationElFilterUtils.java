package org.meveo.admin.job.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.ValueExpressionWrapper;

import java.util.*;
import java.util.stream.Collectors;

public class BillinRunApplicationElFilterUtils {
    private BillinRunApplicationElFilterUtils() {
    }

    /**
     * Evaluate BR by applicationEL and return the result.
     * @param billingRun BillingRun that have applicationEL
     * @param jobInstance the jobInstance
     * @return Sí / No
     */
    public static boolean isToProcessBR(BillingRun billingRun, JobInstance jobInstance) {
        return Objects.requireNonNullElse((Boolean) ValueExpressionWrapper.evaluateExpression(billingRun.getApplicationEl(),
                Map.of("br", billingRun, "jobInstance", jobInstance),
                Boolean.class), true);
    }

    /**
     * Colección de filtros de BillingRun usando la applicationEL
     *
     * @param billingRuns all fetched BillingRun
     * @param jobInstance instance used to evaluate applicationEL
     * @return filtered BillingRun
     */
    public static List<BillingRun> filterByApplicationEL(List<BillingRun> billingRuns, JobInstance jobInstance) {
        if (CollectionUtils.isEmpty(billingRuns)) {
            return Collections.emptyList();
        }

        return billingRuns.stream().filter(billingRun -> isToProcessBR(billingRun, jobInstance)).collect(Collectors.toList());
    }
}
