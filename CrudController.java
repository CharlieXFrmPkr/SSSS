package com.example.pctest2b222;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class CrudController {

    @FXML
    private TextField taskIdField;
    @FXML
    private TextField taskNameField;
    @FXML
    private TextField taskDescriptionField;
    @FXML
    private TextField taskStatusField;

    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TableColumn<Task, Integer> taskIdColumn;
    @FXML
    private TableColumn<Task, String> taskNameColumn;
    @FXML
    private TableColumn<Task, String> taskDescriptionColumn;
    @FXML
    private TableColumn<Task, String> taskStatusColumn;

    private ObservableList<Task> taskList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        taskIdColumn.setCellValueFactory(new PropertyValueFactory<>("TaskId"));
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<>("TaskName"));
        taskDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("TaskDescription"));
        taskStatusColumn.setCellValueFactory(new PropertyValueFactory<>("TaskStatus"));

        loadTasks(); // Automatically load tasks when the view is initialized
    }

    @FXML
    private void handleInsert(ActionEvent event) {
        String taskName = taskNameField.getText();
        String taskDescription = taskDescriptionField.getText();
        String taskStatus = taskStatusField.getText();

        String jdbcUrl = "jdbc:mysql://localhost:3306/PC3TEST2B";
        String dbUser = "root";
        String dbPassword = "";

        String query = "INSERT INTO Project_Task_List (TaskName, TaskDescription, TaskStatus) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, taskName);
            statement.setString(2, taskDescription);
            statement.setString(3, taskStatus);

            statement.executeUpdate();
            loadTasks(); // Reload tasks after insert
            clearFields(); // Clear input fields after successful insert

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Insert Failed", "Error inserting task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        int taskId = Integer.parseInt(taskIdField.getText());
        String taskName = taskNameField.getText();
        String taskDescription = taskDescriptionField.getText();
        String taskStatus = taskStatusField.getText();

        String jdbcUrl = "jdbc:mysql://localhost:3306/PC3TEST2B";
        String dbUser = "root";
        String dbPassword = "";

        String query = "UPDATE Project_Task_List SET TaskName = ?, TaskDescription = ?, TaskStatus = ? WHERE TaskID = ?";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, taskName);
            statement.setString(2, taskDescription);
            statement.setString(3, taskStatus);
            statement.setInt(4, taskId);

            statement.executeUpdate();
            loadTasks(); // Reload tasks after update
            clearFields(); // Clear input fields after successful update

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Error updating task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        int taskId = Integer.parseInt(taskIdField.getText());

        String jdbcUrl = "jdbc:mysql://localhost:3306/PC3TEST2B";
        String dbUser = "root";
        String dbPassword = "";

        String query = "DELETE FROM Project_Task_List WHERE TaskID = ?";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, taskId);

            statement.executeUpdate();
            loadTasks(); // Reload tasks after delete
            clearFields(); // Clear input fields after successful delete

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Delete Failed", "Error deleting task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoad(ActionEvent event) {
        loadTasks(); // Triggered when the "Load" button is pressed
    }

    @FXML
    private void handleFetch(ActionEvent event) {
        loadTasks(); // Triggered when the "Fetch" button is pressed
    }

    private void loadTasks() {
        taskList.clear();

        String jdbcUrl = "jdbc:mysql://localhost:3306/PC3TEST2B";
        String dbUser = "root";
        String dbPassword = "";

        String query = "SELECT * FROM Project_Task_List";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int taskId = resultSet.getInt("TaskID");
                String taskName = resultSet.getString("TaskName");
                String taskDescription = resultSet.getString("TaskDescription");
                String taskStatus = resultSet.getString("TaskStatus");

                taskList.add(new Task(taskId, taskName, taskDescription, taskStatus));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Load Failed", "Error loading tasks: " + e.getMessage());
            e.printStackTrace();
        }

        taskTable.setItems(taskList);
    }

    private void clearFields() {
        taskIdField.clear();
        taskNameField.clear();
        taskDescriptionField.clear();
        taskStatusField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
