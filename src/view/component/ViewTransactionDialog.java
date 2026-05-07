package view.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

import resource.TransactionResource;

public class ViewTransactionDialog {

    public static void show(TransactionResource transaction) {

        Stage stage = new Stage();
        stage.setTitle("Transaction Details");
        stage.initModality(Modality.APPLICATION_MODAL);

        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern("dd MMM yyyy - hh:mm a");

        Label title = new Label("Transaction Details");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label typeBadge = new Label(transaction.isIncome ? "INCOME" : "EXPENSE");
        typeBadge.setStyle(
                "-fx-text-fill: white;"
                + "-fx-padding: 4 10;"
                + "-fx-background-radius: 20;"
                + (transaction.isIncome
                        ? "-fx-background-color: #4CAF50;"
                        : "-fx-background-color: #F44336;")
        );

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);

        addRow(grid, 0, "ID:", String.valueOf(transaction.id));
        addRow(grid, 1, "Amount:", String.format("%.2f EGP", transaction.amount));
        addRow(grid, 2, "Date:", transaction.date.format(formatter));
        if (!transaction.isIncome) {
            addRow(grid, 3, "Category: ", transaction.categoryName);
        }

        Label notesTitle = new Label("Notes:");
        notesTitle.setStyle("-fx-font-weight: bold;");

        Label notesContent = new Label(
                transaction.notes == null || transaction.notes.isEmpty()
                ? "No notes"
                : transaction.notes
        );
        notesContent.setWrapText(true);
        notesContent.setStyle(
                "-fx-background-color: #f5f5f5;"
                + "-fx-padding: 10;"
                + "-fx-background-radius: 8;"
        );

        VBox notesBox = new VBox(5, notesTitle, notesContent);

        // Header (title + badge)
        HBox header = new HBox(10, title, typeBadge);
        header.setAlignment(Pos.CENTER_LEFT);

        // Root layout
        VBox root = new VBox(20,
                header,
                grid,
                notesBox
        );

        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(20));

        root.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 15;"
                + "-fx-border-radius: 15;"
                + "-fx-border-color: #dddddd;"
        );

        Scene scene = new Scene(root, 400, 350);

        // Close on ESC
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ESCAPE:
                    stage.close();
            }
        });

        stage.setScene(scene);
        stage.showAndWait();
    }

    // Helper method for cleaner rows
    private static void addRow(GridPane grid, int row, String labelText, String value) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");

        Label content = new Label(value);

        grid.add(label, 0, row);
        grid.add(content, 1, row);
    }
}
