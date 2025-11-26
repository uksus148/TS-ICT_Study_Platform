package com.synapse.client.controller;

import com.synapse.client.model.Group;
import com.synapse.client.model.Resource;
import com.synapse.client.store.ResourceStore;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import java.io.File;

public class ResourceEditorController {

    private MainController mainController;
    private Group currentGroup;

    @FXML private TextField nameField;
    @FXML private TextField pathField;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setGroup(Group group) {
        this.currentGroup = group;
        // Очищаем поля при открытии
        nameField.clear();
        pathField.clear();
    }

    @FXML
    public void onChooseFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(pathField.getScene().getWindow());
        if (file != null) {
            pathField.setText(file.getAbsolutePath());
            if (nameField.getText().isEmpty()) {
                nameField.setText(file.getName());
            }
        }
    }

    @FXML
    public void onSave() {
        if (currentGroup == null) {
            System.err.println("Error: No group selected");
            return;
        }

        String name = nameField.getText();
        String path = pathField.getText();

        // 1. Простая валидация
        if (name.isEmpty() || path.isEmpty()) {
            // Можно показать Alert
            System.out.println("Validation Error: Name or Path is empty");
            return;
        }


        Resource resource = new Resource();
        resource.setGroup_id(currentGroup.getGroup_id());
        resource.setPath(path);
        resource.setName(nameField.getText());
        if (path.startsWith("http://") || path.startsWith("https://")) {
            resource.setType("LINK");
            resource.setName(nameField.getText());
        } else {
            resource.setType("FILE");
        }
        resource.setCreated_by("Me");

        // 3. Сохраняем в Store
        ResourceStore.getInstance().addResource(resource);

        // 4. Закрываем панель
        System.out.println("Saving resource for group: " + currentGroup.getName());
        onClose();
    }

    @FXML
    public void onClose() {
        if (mainController != null) {
            mainController.closeRightPanel();
        }
    }
}