package view.component;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import manager.TransactionManager;
import resource.TransactionResource;

public class TransactionCell extends ListCell<TransactionResource> {

    private HBox root = new HBox(10);

    private Label amountLabel = new Label();
    private Label dateLabel = new Label();
    private Label typeIcon = new Label(); // ↑ or ↓

    private Button deleteBtn = new Button("Delete");

    private ObservableList<TransactionResource> listRef;

    public TransactionCell(ObservableList<TransactionResource> listRef) {
        this.listRef = listRef;

        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(10));

        VBox infoBox = new VBox(5, amountLabel, dateLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        root.getChildren().addAll(typeIcon, infoBox, spacer, deleteBtn);

        root.setOnMouseClicked(e -> {
            if (getItem() != null) {
                ViewTransactionDialog.show(getItem());
                System.out.println("Clicked transaction ID: " + getItem().id);
            }
        });

        deleteBtn.setOnAction(e -> {
            try {
                TransactionManager mg = new TransactionManager();
                mg.deleteTransaction(getItem().id);
                TransactionResource item = getItem();
                if (item != null) {
                    listRef.remove(item);
                }
            } catch (Exception exception) {
                System.out.println("Exception occurred");
            }
        });
    }

    @Override
    protected void updateItem(TransactionResource item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            amountLabel.setText(String.format("%.2f EGP", item.amount));
            dateLabel.setText(item.date.toString());

            if (item.isIncome) {
                typeIcon.setText("↓");
                typeIcon.setStyle("-fx-text-fill: green; -fx-font-size: 16;");
            } else {
                typeIcon.setText("↑");
                typeIcon.setStyle("-fx-text-fill: red; -fx-font-size: 16;");
            }

            setGraphic(root);
        }
    }
}

