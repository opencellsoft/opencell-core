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
package org.meveo.admin.action.billing;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.crm.impl.ProviderService;

/**
 * Standard backing bean for {@link TradingLanguage} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Marouane ALAMI
 * @created 25-03-2013
 * 
 */
@Named
@ConversationScoped
public class TradingLanguageBean extends BaseBean<TradingLanguage> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link TradingLanguage} service. Extends {@link PersistenceService} .
     */
    @Inject
    private TradingLanguageService tradingLanguageService;

    @Inject
    private ProviderService providerService;

    @Inject
    private Messages messages;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public TradingLanguageBean() {
        super(TradingLanguage.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public TradingLanguage initEntity() {
        return super.initEntity();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
     */
    @Override
    public String saveOrUpdate(boolean killConversation) {
        String back = null;
        try {
            Provider currentProvider = providerService.findById(getCurrentProvider().getId());
            for (TradingLanguage tr : currentProvider.getTradingLanguages()) {
                if (tr.getLanguage().getLanguageCode().equalsIgnoreCase(entity.getLanguage().getLanguageCode()) && !tr.getId().equals(entity.getId())) {
                    throw new Exception();
                }
            }
            currentProvider.addTradingLanguage(entity);
            back = super.saveOrUpdate(killConversation);

        } catch (Exception e) {
            messages.error(new BundleKey("messages", "tradingLanguage.uniqueField"));
        }

        return back;
    }

    public void populateLanguages(Language language) {
        log.info("populatLanguages language", language != null ? language.getLanguageCode() : null);
        if (language != null) {
            entity.setLanguage(language);
            entity.setPrDescription(language.getDescriptionEn());
        }
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<TradingLanguage> getPersistenceService() {
        return tradingLanguageService;
    }

    @Override
    protected String getListViewName() {
        return "tradingLanguages";
    }

    @Override
    public String getNewViewName() {
        return "tradingLanguagesDetail";
    }

    @Override
    public String getEditViewName() {
        return "tradingLanguagesDetail";
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("language");
    }

    @Override
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("language");
    }
}
