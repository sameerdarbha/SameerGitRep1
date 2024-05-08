import javax.faces.event.ActionEvent;

import oracle.adf.controller.TaskFlowId;

import oracle.ui.pattern.dynamicShell.TabContext;

public class CompanyDashboardBean {
    
    private String taskFlowId = "/WEB-INF/DepartmentsTF.xml#DepartmentsTF";

    public CompanyDashboardBean() {
    }
    
    

    public TaskFlowId getDynamicTaskFlowId() {
        return TaskFlowId.parse(taskFlowId);
    }

    public void launchWelcomeTF(ActionEvent actionEvent) {
        // Add event code here...
          TabContext tabContext = TabContext.getCurrentInstance(); 
          try 
          { 
            taskFlowId = "/WEB-INF/WelcomeTF.xml#WelcomeTF";
            tabContext.setMainContent(taskFlowId); 
          } 
          catch (TabContext.TabContentAreaDirtyException toe) 
          { 
              // TODO: warn user TabContext api needed for this use case. 
          } 
        
    }

    public void setTaskFlowId(String taskFlowId) {
        this.taskFlowId = taskFlowId;
    }

    public String getTaskFlowId() {
        return taskFlowId;
    }

    public void launchDepartmentsTF(ActionEvent actionEvent) {
        // Add event code here...
        TabContext tabContext = TabContext.getCurrentInstance(); 
        try 
        { 
          taskFlowId = "/WEB-INF/DepartmentsTF.xml#DepartmentsTF";
          tabContext.setMainContent(taskFlowId); 
        } 
        catch (TabContext.TabContentAreaDirtyException toe) 
        { 
            // TODO: warn user TabContext api needed for this use case. 
        } 
    }
}
