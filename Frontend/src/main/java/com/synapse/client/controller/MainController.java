package com.synapse.client.controller;

import com.synapse.client.UserSession;
import com.synapse.client.model.Group;
import com.synapse.client.model.Task;
import com.synapse.client.model.User;
import com.synapse.client.service.ApiService;
import com.synapse.client.store.GroupsStore;
import com.synapse.client.store.MembersStore;
import com.synapse.client.store.ResourceStore;
import com.synapse.client.store.TaskStore;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainController {
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private SidebarController sidebarController;
    @FXML
    private TaskEditorController taskEditorController;
    @FXML
    private GroupEditorController groupEditorController;
    @FXML
    private Parent taskEditor;
    @FXML
    private Parent groupEditor;
    @FXML
    private TodayController todayViewController;

    // Контроллер редактора ресурсов (загружается динамически)
    private Parent resourceEditor;
    private ResourceEditorController resourceEditorController;

    @FXML
    public void initialize() {
        // ВАЖНО: Убрали загрузку данных отсюда!
        // Данные загружаем только после успешного входа (onSuccessfulLogin).

        // Настраиваем скрытые панели при старте
        if (taskEditor != null) {
            taskEditor.setVisible(false);
            taskEditor.setManaged(false);
        }

        // Связываем вложенные контроллеры с главным
        if (todayViewController != null) {
            todayViewController.setMainController(this);
        }
        if (sidebarController != null) {
            sidebarController.setMainController(this);
        } else {
            System.out.println("Warning: SidebarController is null (check fx:id in FXML)");
        }
    }

    // === ГЛАВНЫЙ МЕТОД ПОСЛЕ ВХОДА ===
    public void onSuccessfulLogin() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        System.out.println("MainController: User logged in -> " + currentUser.getName());

        // 1. Обновляем имя в сайдбаре (если там есть такой метод)
        // if (sidebarController != null) sidebarController.setUserName(currentUser.getName());

        // 2. Загружаем все данные с сервера
        refreshAllData();

        // 3. Показываем стартовую страницу (например, Группы)
        showGroupsView();
    }

    public void refreshAllData() {
        System.out.println("Fetching data from server...");

        GroupsStore.getInstance().fetchGroupsFromServer();
        TaskStore.getInstance().fetchTasksFromServer();
    }


    public void showGroupsView() {
        // Убедитесь, что путь правильный!
        loadView("groups/GroupsView.fxml");
    }

    public void showGroupDetails(Group group) {
        Object controller = loadView("groups/group_details.fxml");
        if (controller instanceof GroupDetailsController) {
            ((GroupDetailsController) controller).setGroup(group);
        }
    }

    public Object loadView(String fxmlFileName) {
        try {
            String fxmlPath = "/com/synapse/client/views/" + fxmlFileName;

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            mainBorderPane.setCenter(view);

            Object controller = loader.getController();

            if (controller instanceof TodayController) ((TodayController) controller).setMainController(this);
            if (controller instanceof UpcomingController) ((UpcomingController) controller).setMainController(this);
            if (controller instanceof GroupsController) ((GroupsController) controller).setMainController(this);
            if (controller instanceof GroupDetailsController) ((GroupDetailsController) controller).setMainController(this);

            return controller;

        } catch (IOException e) {
            e.printStackTrace();
            mainBorderPane.setCenter(new Label("Error loading view: " + fxmlFileName + "\nCheck console for details."));
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            mainBorderPane.setCenter(new Label("FXML file not found: " + fxmlFileName));
            return null;
        }
    }

    public void requestEditTaskEditor(Task task) {
        restoreTaskEditorPosition();
        if (taskEditorController != null) {
            taskEditorController.loadTask(task);
        }
        showRightPanel(taskEditor);
    }

    public void requestOpenNewTaskEditor(Long group_id) {
        restoreTaskEditorPosition();
        if (taskEditorController != null) {
            taskEditorController.loadTask(group_id);
        }
        showRightPanel(taskEditor);
    }

    public void requestOpenNewTaskEditor() {
        restoreTaskEditorPosition();
        if (taskEditorController != null) {
            taskEditorController.loadTask((Task) null);
        }
        showRightPanel(taskEditor);
    }

    public void requestOpenNewGroupEditor() {
        try {
            if (groupEditor == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/synapse/client/views/groups/GroupEditor.fxml"));
                groupEditor = loader.load();
                groupEditorController = loader.getController();
                groupEditorController.setMainController(this);
            }
            if (groupEditorController != null) {
                groupEditorController.loadGroup(null);
            }
            mainBorderPane.setRight(groupEditor);
            showRightPanel(groupEditor);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading GroupEditor.fxml");
        }
    }

    public void requestOpenResourceEditor(Group group) {
        try {
            if (resourceEditor == null) {
                // Проверьте путь!
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/synapse/client/views/ResourceEditor.fxml"));
                resourceEditor = loader.load();
                resourceEditorController = loader.getController();
                resourceEditorController.setMainController(this);
            }
            resourceEditorController.setGroup(group);
            mainBorderPane.setRight(resourceEditor);
            showRightPanel(resourceEditor);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load ResourceEditor.fxml");
        }
    }

    private void restoreTaskEditorPosition() {
        if (taskEditor != null && mainBorderPane.getRight() != taskEditor) {
            mainBorderPane.setRight(taskEditor);
        }
    }

    private void showRightPanel(Parent panel) {
        if (panel != null) {
            panel.setVisible(true);
            panel.setManaged(true);
        }
    }

    public void closeRightPanel() {
        if (mainBorderPane.getRight() != null) {
            mainBorderPane.getRight().setVisible(false);
            mainBorderPane.getRight().setManaged(false);
        }
    }

    public void logout() {
        System.out.println("Logging out...");

        // 1. Отправляем запрос на сервер
        ApiService.getInstance().logout().whenComplete((result, error) -> {

            // Этот код выполнится в любом случае (успех или ошибка)
            // Возвращаемся в UI поток, так как будем менять сцену
            Platform.runLater(() -> {
                performLocalLogout();
            });
        });
    }

    private void performLocalLogout() {
        UserSession.getInstance().logout();
        TaskStore.getInstance().clear();
        GroupsStore.getInstance().clear();
        ResourceStore.getInstance().clear();
        try {
            String fxmlPath = "/com/synapse/client/views/auth/auth_view.fxml";

            if (getClass().getResource(fxmlPath) == null) {
                System.err.println("CRITICAL: Auth View FXML not found!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) mainBorderPane.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            URL cssUrl = getClass().getResource("/com/synapse/client/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("style.css not found");
            }
            stage.setTitle("Synapse"); // Application name
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load Auth View");
        }
    }
}