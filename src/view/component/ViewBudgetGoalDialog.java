package view.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;

import resource.BudgetGoalResource;

/**
 * ViewBudgetGoalDialog — modal detail view for a single budget goal.
 *
 * SRP  : Only responsible for displaying goal details; no business logic.
 * OCP  : Add new fields by extending addRow() calls; nothing else changes.
 *
 * Mirrors {@link ViewTransactionDialog} in structure and style.
 */
public class ViewBudgetGoalDialog {

    public static void show(BudgetGoalResource goal) {

        Stage stage = new Stage();
        stage.setTitle("Budget Goal Details");
        stage.initModality(Modality.APPLICATION_MODAL);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // ─── Header ───────────────────────────────────────────────────────────

        Label title = new Label("Budget Goal Details");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        String badgeText  = goal.isCompleted ? "COMPLETED" : "IN PROGRESS";
        String badgeColor = goal.isCompleted ? "#4CAF50"   : "#3F51B5";
        Label statusBadge = new Label(badgeText);
        statusBadge.setStyle(
                "-fx-text-fill: white;"
                + "-fx-padding: 4 10;"
                + "-fx-background-radius: 20;"
                + "-fx-background-color: " + badgeColor + ";"
        );

        HBox header = new HBox(10, title, statusBadge);
        header.setAlignment(Pos.CENTER_LEFT);

        // ─── Details grid ─────────────────────────────────────────────────────

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);

        addRow(grid, 0, "ID:",          String.valueOf(goal.id));
        addRow(grid, 1, "Name:",        goal.name);
        addRow(grid, 2, "Target:",      String.format("%.2f EGP", goal.targetAmount));
        addRow(grid, 3, "Saved so far:", String.format("%.2f EGP", goal.currentAmount));
        addRow(grid, 4, "Category ID:", String.valueOf(goal.categoryId));
        addRow(grid, 5, "Deadline:",
                goal.deadline != null ? sdf.format(goal.deadline) : "None");

        // ─── Progress section ─────────────────────────────────────────────────

        double ratio = goal.progressRatio();

        Label progressTitle = new Label("Progress");
        progressTitle.setStyle("-fx-font-weight: bold;");

        ProgressBar bar = new ProgressBar(ratio);
        bar.setPrefWidth(340);
        bar.setPrefHeight(14);
        bar.setStyle("-fx-accent: " + badgeColor + ";");

        Label progressText = new Label(String.format("%.0f%%  (%.2f / %.2f EGP)",
                ratio * 100, goal.currentAmount, goal.targetAmount));
        progressText.setStyle("-fx-font-size: 12; -fx-text-fill: #555;");

        VBox progressBox = new VBox(6, progressTitle, bar, progressText);
        progressBox.setStyle(
                "-fx-background-color: #f5f5f5;"
                + "-fx-padding: 10;"
                + "-fx-background-radius: 8;"
        );

        // ─── Root layout ──────────────────────────────────────────────────────

        VBox root = new VBox(20, header, grid, progressBox);
        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(20));
        root.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 15;"
                + "-fx-border-radius: 15;"
                + "-fx-border-color: #dddddd;"
        );

        Scene scene = new Scene(root, 420, 390);

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

    // ─── Helper ───────────────────────────────────────────────────────────────

    private static void addRow(GridPane grid, int row, String labelText, String value) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        Label content = new Label(value);
        grid.add(label,   0, row);
        grid.add(content, 1, row);
    }
}