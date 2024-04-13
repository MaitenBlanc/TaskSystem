package gm.tasks.controller;

import gm.tasks.model.Task;
import gm.tasks.service.TaskService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class IndexController implements Initializable {
    
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private TaskService taskService;

    @FXML
    private TableView<Task> taskTable;

    @FXML
    private TableColumn<Task, Integer> idTaskColumn;

    @FXML
    private TableColumn<Task, String> nameTaskColumn;

    @FXML
    private TableColumn<Task, String> responsibleColumn;

    @FXML
    private TableColumn<Task, String> statusColumn;

    private final ObservableList<Task> taskList = FXCollections.observableArrayList();

    @FXML
    private TextField nameTaskText;

    @FXML
    private TextField responsibleText;

    @FXML
    private TextField statusText;

    private Integer idTaskIntern;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        taskTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        configureColumns();
        listTasks();
    }

    private void configureColumns() {
        idTaskColumn.setCellValueFactory(new PropertyValueFactory<>("idTask"));
        nameTaskColumn.setCellValueFactory(new PropertyValueFactory<>("nameTask"));
        responsibleColumn.setCellValueFactory(new PropertyValueFactory<>("responsible"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void listTasks() {
        logger.info("Executing tasks list...");
        taskList.clear();
        taskList.addAll(taskService.listTask());
        taskTable.setItems(taskList);
    }

    public void addTask() {
        if(nameTaskText.getText().isEmpty()) {
            showMessage("Validation Error ", "You must provide a task.");
            nameTaskText.requestFocus();
            return;
        } else {
            var task = new Task();
            collectDataForm(task);
            task.setIdTask(null);
            taskService.saveTask(task);
            showMessage("Information ", "Added task.");
            cleanForm();
            listTasks();
        }
    }

    public void loadTaskForm() {
        var task = taskTable.getSelectionModel().getSelectedItem();
        if(task != null) {
            idTaskIntern = task.getIdTask();
            nameTaskText.setText(task.getNameTask());
            responsibleText.setText(task.getResponsible());
            statusText.setText(task.getStatus());
        }
    }

    private void collectDataForm(Task task) {
        if(idTaskIntern != null) {
            task.setIdTask(idTaskIntern);
        }
        task.setNameTask(nameTaskText.getText());
        task.setResponsible(responsibleText.getText());
        task.setStatus(statusText.getText());
    }

    public void modifyTask() {
        if(idTaskIntern == null) {
            showMessage("Information", "You must select a task.");
            return;
        }
        if(nameTaskText.getText().isEmpty()) {
            showMessage("Validation Error", "You must provide a task");
            nameTaskText.requestFocus();
            return;
        }

        var task = new Task();
        collectDataForm(task);
        taskService.saveTask(task);
        showMessage("Information", "Modified task");
        cleanForm();
        listTasks();
    }

    public void deleteTask() {
        var task = taskTable.getSelectionModel().getSelectedItem();
        if(task != null) {
            logger.info("Task to delete: " + task.toString());
            taskService.deleteTask(task);
            showMessage("Information", "Deleted task: " + task.getIdTask());
            cleanForm();
            listTasks();
        } else {
            showMessage("Error", "No task selected.");
        }
    }

    public void cleanForm() {
        idTaskIntern = null;
        nameTaskText.clear();
        responsibleText.clear();
        statusText.clear();
    }

    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
