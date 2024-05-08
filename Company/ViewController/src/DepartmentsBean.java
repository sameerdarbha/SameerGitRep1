import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.event.DialogEvent;

public class DepartmentsBean {
    
    private RichPopup deleteConfirmationPopup;

    public DepartmentsBean() {
    }

    public void confirmDelete(DialogEvent dialogEvent) {
        // Add event code here...
        if(DialogEvent.Outcome.yes.equals(dialogEvent.getOutcome()))
            //  Delete
            ADFUtils.invokeEL("#{bindings.Delete.execute}");
        else
            ADFUtils.hidePopup(getDeleteConfirmationPopup().getClientId(FacesContext.getCurrentInstance()));
    }

    public void showDeleteConfirmationPopup(ActionEvent actionEvent) {
        // Add event code here...
        //  Show the delete confirmation popup
        ADFUtils.invokePopup(getDeleteConfirmationPopup().getClientId(FacesContext.getCurrentInstance()));
        
    }

    public void setDeleteConfirmationPopup(RichPopup deleteConfirmationPopup) {
        this.deleteConfirmationPopup = deleteConfirmationPopup;
    }

    public RichPopup getDeleteConfirmationPopup() {
        return deleteConfirmationPopup;
    }
}
