import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
/*
Comments on Server modified on server
*/
import java.io.Serializable;

import java.util.Map;

import java.util.Properties;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.model.DataControlFrame;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.JboException;

import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;


/**
 * Provides various utility methods that are handy to
 * have around when working with ADF.
 */
public class ADFUtils implements Serializable {
    
    private static final String PROPERTIES_FILE_PATH = "config.properties";
    private static Properties properties;
    
    static {
        InputStream is = null;
        try {
            is = ADFUtils.class.getClassLoader().getResourceAsStream((ADFUtils.PROPERTIES_FILE_PATH));
            if(is == null) {
                is = new FileInputStream(ADFUtils.PROPERTIES_FILE_PATH);
            }
            properties = new Properties();
            properties.load(is);
        }
        catch(Exception e) {
            throw new RuntimeException("Could not load " + PROPERTIES_FILE_PATH + " properties file.", e);
        }
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch(IOException ioe) {
                    throw new RuntimeException("Exception during closing InputStream for properties file", ioe);
                }
            }
        }
    }

    /**
     * When a bounded task flow manages a transaction (marked as requires-transaction,.
     * requires-new-transaction, or requires-existing-transaction), then the
     * task flow must issue any commits or rollbacks that are needed. This is
     * essentially to keep the state of the transaction that the task flow understands
     * in synch with the state of the transaction in the ADFbc layer.
     *
     * Use this method to issue a commit in the middle of a task flow while staying
     * in the task flow.
     */
    public static void saveAndContinue() {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        BindingContext context = (BindingContext)sessionMap.get(BindingContext.CONTEXT_ID);
        String currentFrameName = context.getCurrentDataControlFrame();
        DataControlFrame dcFrame = context.findDataControlFrame(currentFrameName);

        dcFrame.commit();
        dcFrame.beginTransaction(null);
    }

    /**
     * Programmatic evaluation of EL.
     *
     * @param el EL to evaluate
     * @return Result of the evaluation
     */
    public static Object evaluateEL(String el) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext elContext = facesContext.getELContext();
        ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
        ValueExpression exp = expressionFactory.createValueExpression(elContext, el, Object.class);

        return exp.getValue(elContext);
    }

    /**
     * Programmatic invocation of a method that an EL evaluates to.
     * The method must not take any parameters.
     *
     * @param el EL of the method to invoke
     * @return Object that the method returns
     */
    public static Object invokeEL(String el) {
        return invokeEL(el, new Class[0], new Object[0]);
    }

    /**
     * Programmatic invocation of a method that an EL evaluates to.
     *
     * @param el EL of the method to invoke
     * @param paramTypes Array of Class defining the types of the parameters
     * @param params Array of Object defining the values of the parametrs
     * @return Object that the method returns
     */
    public static Object invokeEL(String el, Class[] paramTypes,
                                  Object[] params) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext elContext = facesContext.getELContext();
        ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
        MethodExpression exp = expressionFactory.createMethodExpression(elContext, el,Object.class, paramTypes);

        return exp.invoke(elContext, params);
    }

    /**
     * Sets a value into an EL object. Provides similar functionality to
     * the <af:setActionListener> tag, except the from is
     * not an EL. You can get similar behavior by using the following...

     * setEL(to, evaluateEL(from))
     *
     * @param el EL object to assign a value
     * @param val Value to assign
     */
    public static void setEL(String el, Object val) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext elContext = facesContext.getELContext();
        ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
        ValueExpression exp = expressionFactory.createValueExpression(elContext, el, Object.class);

        exp.setValue(elContext, val);
    }
    
    public static void adfFacesErrorMessage(String msg) {
        FacesContext ctx = getFacesContext();
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, "");
        ctx.addMessage(getRootViewComponentId(), fm);
    }
    
    public static void adfFacesErrorMessage(String attributeName, String msg) {
        FacesContext ctx = getFacesContext();
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, "");
        ctx.addMessage(attributeName, fm);
    }
    
    public static String getRootViewComponentId() {
        return getFacesContext().getViewRoot().getId();
    }
    
    public static FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
    
    public static void closeWindow() {
        FacesContext ctx = getFacesContext();
        ExtendedRenderKitService service = Service.getRenderKitService(ctx, ExtendedRenderKitService.class);
        service.addScript(ctx, "window.opener = self;window.close();");
    }
    
    public static void invokeBindingOperation(String operationName) {
        OperationBinding operationBinding = getBindingContainer().getOperationBinding(operationName);
        if(operationBinding == null) {
            throw new RuntimeException("Operation '" + operationName + "' not found.");
        }
        operationBinding.execute();
        if(operationBinding.getErrors() != null && operationBinding.getErrors().size() > 0) {
            throw new JboException (operationBinding.getErrors().toString());
        }
    }
    
    public static BindingContainer getBindingContainer() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }
    
    public static DCIteratorBinding findIterator(String iterator) {
        DCIteratorBinding iter = getDCBindingContainer().findIteratorBinding(iterator);
        if(iter == null) {
            throw new RuntimeException("Iterator '" + iterator + "' not found.");
        }
        return iter;
    }
            
    public static DCBindingContainer getDCBindingContainer() {
        return (DCBindingContainer)getBindingContainer();
    }
    
    public static void invokePopup(String popupId) {
        if(popupId == null || "".equals(popupId.trim())) {
            throw new RuntimeException("Popup Id is null.");
        }
        ExtendedRenderKitService service = Service.getRenderKitService(getFacesContext(), ExtendedRenderKitService.class);
        StringBuffer showPopup = new StringBuffer();
        showPopup.append("var hints = new Object;");
        showPopup.append("var popupObj = AdfPage.PAGE.findComponent('" + popupId + "'); popupObj.show(hints);");
        service.addScript(getFacesContext(), showPopup.toString());
    }
    
    public static void hidePopup(String popupId) {
        if(popupId == null || "".equals(popupId.trim())) {
            throw new RuntimeException("Popup Id is null.");
        }
        ExtendedRenderKitService service = Service.getRenderKitService(getFacesContext(), ExtendedRenderKitService.class);
        StringBuffer hidePopup = new StringBuffer();
        hidePopup.append("var popupObj = AdfPage.PAGE.findComponent('" + popupId + "'); popupObj.hide();");
        service.addScript(getFacesContext(), hidePopup.toString());
    }
    
    public static Object getFromRequest(String attributeName) {
        return getFacesContext().getExternalContext().getRequestMap().get(attributeName);
    }
    
    public static Object getFromSession(String key) {
        FacesContext ctx = getFacesContext();
        Map sessionState = ctx.getExternalContext().getSessionMap();
        if(sessionState.containsKey(key)) {
            return sessionState.get(key);
        }
        else {
            return null;
        }
    }
    
    public static void removeFromSession(String key) {
        FacesContext ctx = getFacesContext();
        Map sessionState = ctx.getExternalContext().getSessionMap();
        sessionState.remove(key);
    }
    
    public static void removeFromRequest(String key) {
        FacesContext ctx = getFacesContext();
        Map requestMap = ctx.getExternalContext().getRequestMap();
        requestMap.remove(key);
    }
    
    public static void storeOnRequest(String key, String value) {
        getFacesContext().getExternalContext().getRequestMap().put(key, value);
    }
    
    public static void storeOnSession(String key, String value) {
        getFacesContext().getExternalContext().getSessionMap().put(key, value);
    }
    
    public static String getPropertyValue(String propertyName) {
        return ADFUtils.properties.getProperty(propertyName);
    }
    
    /**
     * Following method can be used from Bean code to invoke operations with parameters 
     * Ex : 
     * OperationBinding operationBinding = ADFUtils.findBindingOperation("operationName");
     * Map paramsMap = operationBinding.getParamsMap();
     * paramsMap.put("param1", "value");
     * Object result = operationBinding.execute();
     * 
     * 
     * @param operationName
     * @return
     */
    public static OperationBinding findBindingOperation(String operationName) {
        OperationBinding operationBinding = getBindingContainer().getOperationBinding(operationName);
        if(operationBinding == null) {
            throw new RuntimeException("Operation '" + operationName + "' not found.");
        }
        return operationBinding;                                          
    }
    
}
