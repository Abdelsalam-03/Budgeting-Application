package budgeting.application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import view.MainView;

public class BudgetingApplication extends Application{
        
    private BorderPane root;

    @Override
    public void start(Stage stage) {
        root = new BorderPane();

        showMainView();

        Scene scene = new Scene(root, 600, 600);
        stage.setTitle("Budgeting Application");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
    
     public void showMainView() {
         MainView mainView = new MainView();
         root.setCenter(mainView.getView());
     }

    public static void main(String[] args) {
        launch(args);
    }
    
}
