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
package org.meveo.model.billing;

/**
 * Task status.
 */
public enum TaskStatusEnum {

    SCHEDULED(1, "taskStatus.scheduled"),
    ONGOING(2, "taskStatus.ongoing"),
    PAUSED(3, "taskStatus.paused"),
    CANCELLED(4, "taskStatus.cancelled"),
    RESCHEDULING(5, "taskStatus.resheduling"),
    TERMINATED(6, "taskStatus.terminated");

    private Integer id;
    private String label;

    private TaskStatusEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    public static TaskStatusEnum getValue(Integer id) {
        if (id != null) {
            for (TaskStatusEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
}
