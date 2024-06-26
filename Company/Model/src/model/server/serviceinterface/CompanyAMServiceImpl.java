package model.server.serviceinterface;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import javax.interceptor.Interceptors;

import model.CompanyAMImpl;

import model.common.serviceinterface.CompanyAMService;

import oracle.jbo.common.Diagnostic;
import oracle.jbo.common.sdo.SDOHelper;
import oracle.jbo.server.svc.ServiceContextInterceptor;
import oracle.jbo.server.svc.ServiceImpl;
import oracle.jbo.service.errors.ServiceException;

import oracle.webservices.annotations.PortableWebService;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Sat Nov 16 14:43:24 EST 2013
// ---------------------------------------------------------------------
@Stateless(name="model.common.CompanyAMServiceBean", mappedName="CompanyAMServiceBean")
@Remote(CompanyAMService.class)
@PortableWebService(targetNamespace="/model/common/", serviceName="CompanyAMService",
    portName="CompanyAMServiceSoapHttpPort", endpointInterface="model.common.serviceinterface.CompanyAMService")
@Interceptors( { ServiceContextInterceptor.class })
public class CompanyAMServiceImpl extends ServiceImpl implements CompanyAMService {
    private static boolean _isInited = false;
    private static final Map _map = new HashMap();

    /**
     * This is the default constructor (do not remove).
     */
    public CompanyAMServiceImpl() {
        init();
        setApplicationModuleDefName("model.CompanyAM");
        setConfigurationName("CompanyAMService");
    }

    /**
     * Generated method. Do not modify. Do initialization in the constructor
     */
    protected void init() {
        if (_isInited) {
            return;
        }
        synchronized (CompanyAMServiceImpl.class) {
            if (_isInited) {
                return;
            }
            try {
                SDOHelper.INSTANCE.defineSchema("model/common/serviceinterface/", "CompanyAMService.xsd");
                _map.put("validate",
                         CompanyAMImpl.class.getMethod("validate", new Class[] { }));
                _isInited = true;
            } catch (Exception ex) {
                Diagnostic.printStackTrace(ex);
            }
        }
    }

    /**
     * validate: generated method. Do not modify.
     */
    public void validate() throws ServiceException {
        invokeCustom((Method) _map.get("validate"), new Object[] {  }, new String[] {  }, false);
    }
}
