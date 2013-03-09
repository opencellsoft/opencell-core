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
package org.meveo.service.crm.local;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Local;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.communication.MediaEnum;
import org.meveo.model.communication.MessageSenderConfig;
import org.meveo.model.crm.Email;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.local.IPersistenceService;

/**
 * Email service interface.
 * 
 */
@Local
public interface EmailServiceLocal extends IPersistenceService<Email> {

	public void sendEmail(String from,List<String> to,List<String> cc,String subject,String body,List<File> files) throws BusinessException;
	public HashMap<MediaEnum, List<MessageSenderConfig>> getMediaConfig(Provider provider);

}
