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
package org.meveo.model.communication;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;

@Entity
@Table(name = "COM_PROVIDER_POLICY")
//@AttributeOverride(name = "provider", column = @Column(name = "PROVIDER_ID", nullable = false, unique = true))
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "COM_PROV_POL_SEQ")
public class ProviderPolicy extends BaseEntity {

    private static final long serialVersionUID = -1L;

    @Embedded
    private CommunicationPolicy policy;

    public CommunicationPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(CommunicationPolicy policy) {
        this.policy = policy;
    }

}
