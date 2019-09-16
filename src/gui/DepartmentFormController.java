package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exception.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable{

	private Department department;
	
	private DepartmentService service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>(); //Objetos que querem receber o evento

	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label lblErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;

	@FXML
	public void onBtSaveAction(ActionEvent event)	{
		System.out.println("clicou bt save");
		if (department == null)	{
			throw new IllegalArgumentException("Department was null");
		}
		if (service == null)	{
			throw new IllegalArgumentException("Service was null");
		}
		try	{
			department = getFormData();
			service.saveOrUpdate(department);
			notifyDataChangeListener();
			Utils.currentStage(event).close();
		}	catch (ValidationException e)	{
			setErrorMessages(e.getErrors());
		}	catch (DbException e)	{
			Alerts.showAlert("Error saving objects", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	@FXML
	public void onBtCancelAction(ActionEvent event)	{
		System.out.println("clicou cancel");
		Utils.currentStage(event).close();
		
	}

	private void notifyDataChangeListener() {
		for(DataChangeListener listener : dataChangeListeners)	{
			listener.onDataChanged();
		}
		
	}

	public void subscribeDataChangeListener(DataChangeListener listener)	{
		dataChangeListeners.add(listener);
	}

	private Department getFormData() {
		Department obj = new Department();
		ValidationException exception = new ValidationException("Validation exception!");

		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addErrors("Name", "Field can't be empty");
		}
		obj.setName(txtName.getText());
		if (exception.getErrors().size() > 0)	{
			throw exception;
		}
		return obj;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public void setService(DepartmentService service) {
		this.service = service;
	}
	
	private void setErrorMessages(Map<String, String> errors)	{
		Set<String> fields = errors.keySet();
		if (fields.contains("Name"))	{
			lblErrorName.setText(errors.get("Name"));
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
	}
	
	private void initializeNodes()	{
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
	protected void updateFormData()	{
		if (department == null) {
			throw new IllegalStateException ("Entity was null!");
		}
		txtId.setText(String.valueOf(department.getId()));
		txtName.setText(department.getName());
	}

}
