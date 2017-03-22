package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.api.dto.response.module.MeveoModuleDtoResponse;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 * 
 **/
@WebService
public interface ModuleWs extends IBaseWs {

    @WebMethod
    public ActionStatus create(@WebParam(name = "module") MeveoModuleDto moduleDto);

    @WebMethod
    public ActionStatus update(@WebParam(name = "module") MeveoModuleDto moduleDto);

    @WebMethod
    public ActionStatus delete(@WebParam(name = "code") String code);

    @WebMethod
    public MeveoModuleDtosResponse list();

    @WebMethod
    public MeveoModuleDtoResponse get(@WebParam(name = "code") String code);

    @WebMethod
    public ActionStatus createOrUpdate(@WebParam(name = "module") MeveoModuleDto moduleDto);

    @WebMethod
    public ActionStatus installModule(@WebParam(name = "module") MeveoModuleDto moduleDto);

    @WebMethod
    public ActionStatus uninstallModule(@WebParam(name = "code") String code);

    @WebMethod
    public ActionStatus enableModule(@WebParam(name = "code") String code);

    @WebMethod
    public ActionStatus disableModule(@WebParam(name = "code") String code);

}