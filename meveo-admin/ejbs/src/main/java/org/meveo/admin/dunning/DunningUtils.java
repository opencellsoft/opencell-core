/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
