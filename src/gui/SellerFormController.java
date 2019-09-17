package gui;

import java.net.URL;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exception.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable{

	private Seller seller;
	
	private SellerService service;
	
	private DepartmentService departmentService;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>(); //Objetos que querem receber o evento

	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private TextField txtBaseSalary;

	@FXML
	private DatePicker dpBirthDate;
	
	@FXML
	private ComboBox<Department> comboBoxDepartment;
	
	@FXML
	private Label lblErrorName;
	
	@FXML
	private Label lblErrorEmail;
	
	@FXML
	private Label lblErrorBaseSalary;

	@FXML
	private Label lblErrorBirthDate;
	
	@FXML
	private Label lblErrorDepartment;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	@FXML
	private ObservableList<Department> obsDepList;

	@FXML
	public void onBtSaveAction(ActionEvent event)	{
		System.out.println("clicou bt save");
		if (seller == null)	{
			throw new IllegalArgumentException("Seller was null");
		}
		if (service == null)	{
			throw new IllegalArgumentException("Service was null");
		}
		try	{
			seller = getFormData();
			service.saveOrUpdate(seller);
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

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	private void setErrorMessages(Map<String, String> errors)	{
		Set<String> fields = errors.keySet();
		
		lblErrorName.setText(fields.contains("Name") ? errors.get("Name") : "");	//if ? then : else
		lblErrorEmail.setText(fields.contains("Email") ? errors.get("Email") : "");
		lblErrorBaseSalary.setText(fields.contains("BaseSalary") ? errors.get("BaseSalary") : "");
		lblErrorBirthDate.setText(fields.contains("BirthDate") ? errors.get("BirthDate") : "");
		lblErrorDepartment.setText(fields.contains("Department") ? errors.get("Department") : "");
	}

	private void notifyDataChangeListener() {
		for(DataChangeListener listener : dataChangeListeners)	{
			listener.onDataChanged();
		}
		
	}

	public void subscribeDataChangeListener(DataChangeListener listener)	{
		dataChangeListeners.add(listener);
	}

	private Seller getFormData() {
		Seller obj = new Seller();
		ValidationException exception = new ValidationException("Validation exception!");

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addErrors("Name", "Field can't be empty");
		}
		obj.setName(txtName.getText());

		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addErrors("Email", "Field can't be empty");
		}
		obj.setEmail(txtEmail.getText());
		
		if (dpBirthDate.getValue() == null)	{
			exception.addErrors("BirthDate", "Field can't be empty");
		}	else	{
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}

		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			exception.addErrors("BaseSalary", "Field can't be empty");
		}
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
		
		if (comboBoxDepartment.getValue() == null)	{
			exception.addErrors("Department", "Field can't be empty");
		}
		obj.setDepartment(comboBoxDepartment.getValue());
		
		if (exception.getErrors().size() > 0)	{
			throw exception;
		}
		return obj;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
	}
	
	private void initializeNodes()	{
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}
	
	protected void updateFormData()	{
		if (seller == null) {
			throw new IllegalStateException ("Entity was null!");
		}
		Locale.setDefault(Locale.US);
		txtId.setText(String.valueOf(seller.getId()));
		txtName.setText(seller.getName());
		txtEmail.setText(seller.getEmail());
		txtBaseSalary.setText(String.format("%.2f", seller.getBaseSalary()));
		if (seller.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(seller.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if (seller.getDepartment() != null) {
			comboBoxDepartment.setValue(seller.getDepartment());
		}	
	}
	
	protected void loadAssociatedObjects()	{
		if (departmentService == null)	{
			throw new IllegalStateException("Department was null!");
		}
		List<Department> list = departmentService.findAll();
		obsDepList = FXCollections.observableList(list);
		comboBoxDepartment.setItems(obsDepList);
	}
	
	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

}
