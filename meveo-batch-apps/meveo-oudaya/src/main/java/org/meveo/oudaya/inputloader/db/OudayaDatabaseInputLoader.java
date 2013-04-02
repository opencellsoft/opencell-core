/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.oudaya.inputloader.db;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.meveo.commons.utils.StringUtils;
import org.meveo.core.inputhandler.TaskExecution;
import org.meveo.core.inputloader.AbstractInputLoader;
import org.meveo.core.inputloader.Input;
import org.meveo.core.inputloader.InputNotLoadedException;
import org.meveo.model.billing.BillingRun;
import org.meveo.oudaya.OudayaConfig;
import org.meveo.persistence.MeveoPersistence;

public class OudayaDatabaseInputLoader extends AbstractInputLoader {

    /** Logger. */
    private static final Logger logger = Logger.getLogger(OudayaDatabaseInputLoader.class);

    @SuppressWarnings("unchecked")
    public Input loadInput() {
        try {
            logger.info("Load billingRun");
            EntityManager em = MeveoPersistence.getEntityManager();
            List<BillingRun> billingRuns = (List<BillingRun>) em.createQuery(
                    "from BillingRun where provider.code in ("
                            + StringUtils.getArrayElements(OudayaConfig.getProviderCodes())
                            + ") and status in ('NEW','ON_GOING','VALIDATED') and disabled=:status").setParameter("status", false).getResultList();
            if (billingRuns != null && billingRuns.size() > 0) {
                StringBuilder inputNameBuilder = new StringBuilder("_BillingRunIds_");
                for (BillingRun billingRun : billingRuns) {
                    inputNameBuilder.append(billingRun.getId()).append("_");
                }
                String inputName = inputNameBuilder.toString();
                logger.info(String.format("Loaded input name: %s", inputNameBuilder));
                return new Input(inputName, billingRuns);
            } else {
                logger.info("No billingRuns found to load.");
                return null;
            }
        } catch (Exception e) {
            logger.error("Unexpected exception when searching billingRuns", e);
            throw new InputNotLoadedException(e.getMessage());
        }
    }

    public void handleInputAfterFailure(Input arg0, Throwable arg1) {
    }

    @SuppressWarnings("unchecked")
    public void handleInputAfterProcessing(Input arg0, TaskExecution arg1) {
    }

}
