package org.meveo.admin.action.catalog;

import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

import org.meveo.model.catalog.BusinessProductModel;

/**
 * @author Edward P. Legaspi
 */
@Named
@ConversationScoped
public class BusinessProductModelListBean extends BusinessProductModelBean {

    private static final long serialVersionUID = -5554917133121878760L;

    /**
     * @return list of business product model.
     */
    public List<BusinessProductModel> listInstalled() {
        return businessProductModelService.listInstalled();
    }

}
