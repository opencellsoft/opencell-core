package org.meveo.service.payments.impl;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.DDRequestBuilderTypeEnum;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.payment.DDRequestBuilderScriptInterface;

/**
 * 
 * @author anasseh
 *
 */
@Stateless
public class DDRequestBuilderFactory implements Serializable {

    private static final long serialVersionUID = -8799566002123225845L;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public DDRequestBuilderInterface getInstance(DDRequestBuilder ddRequestBuilder) throws Exception {
        DDRequestBuilderInterface ddRequestBuilderInterface = null;

        DDRequestBuilderTypeEnum ddRequestBuilderType = ddRequestBuilder.getType();
        if (ddRequestBuilderType == DDRequestBuilderTypeEnum.CUSTOM) {
            ddRequestBuilderInterface = new CustomDDRequestBuilder((DDRequestBuilderScriptInterface) scriptInstanceService.getScriptInstance(ddRequestBuilder.getScriptInstance().getCode()));
        }
        if (ddRequestBuilderType == DDRequestBuilderTypeEnum.NATIF) {
            Class<?> clazz = Class.forName(ddRequestBuilder.getImplementationClassName());
            ddRequestBuilderInterface = (DDRequestBuilderInterface) clazz.newInstance();
        }

        return ddRequestBuilderInterface;
    }
}
