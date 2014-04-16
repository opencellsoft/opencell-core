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
package org.meveo.admin.dunning;

import java.util.Date;

import javax.inject.Named;

import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.payments.DunningLevelEnum;

@Named
public class DunningUtils {

    /**
     * Returns the next DunningLevel
     * 
     * @param dunningLevel
     * @return nextLevel or null if dunningLevel is the last
     */
    public static DunningLevelEnum getNextDunningLevel(DunningLevelEnum dunningLevel) {
        DunningLevelEnum nextLevel = null;
        try {
            nextLevel = DunningLevelEnum.getValue((dunningLevel.getId().intValue() + 1));
        } catch (Exception e) {

        }
        return nextLevel;
    }

    /**
     * Returns the previous DunningLevel
     * 
     * @param dunningLevel
     * @return previousLevel or null if dunningLevel is the first
     */
    public static DunningLevelEnum getPreviousDunningLevel(DunningLevelEnum dunningLevel) {
        DunningLevelEnum previousLevel = null;
        try {
            previousLevel = DunningLevelEnum.getValue((dunningLevel.getId().intValue() - 1));
        } catch (Exception e) {
        }
        return previousLevel;
    }

    /**
     * Returns Auditable
     * 
     * @param user
     * @param isUpdate
     * @return Auditable for create if isUpdate = false, Auditable for update
     *         otherwise
     * @throws Exception
     */
    public static Auditable getAuditable(User user) throws Exception {
        Auditable auditable = new Auditable();
        auditable.setCreated(new Date());
        auditable.setCreator(user);
        return auditable;
    }

  
}
