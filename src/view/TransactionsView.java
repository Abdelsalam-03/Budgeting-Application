package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import resource.TransactionResource;
import manager.TransactionManager;
import view.component.TransactionCell;

public class TransactionsView {

    private final BorderPane root;

    private final TransactionManager transactionManager;

    public TransactionsView() {
        root = new BorderPane();
        transactionManager = new TransactionManager();
    }

    public VBox getView() {

        VBox layout = new VBox();
        VBox.setVgrow(root, Priority.ALWAYS);

        root.setLeft(createSideBar());

        showTransactions();

        layout.getChildren().add(root);
        return layout;

    }

    private VBox createSideBar() {
        ToggleButton transactionsBtn = new ToggleButton("Show Transactions");
        ToggleButton addTransactionBtn = new ToggleButton("Add Transaction");

        ToggleGroup group = new ToggleGroup();
        transactionsBtn.setToggleGroup(group);
        addTransactionBtn.setToggleGroup(group);

        // Default selected
        transactionsBtn.setSelected(true);

        // Actions
        transactionsBtn.setOnAction(e -> showTransactions());
        addTransactionBtn.setOnAction(e -> showAddTransactionView());

        VBox sideBar = new VBox(10, transactionsBtn, addTransactionBtn);
        sideBar.setStyle("-fx-padding: 10; -fx-background-color: white;");

        return sideBar;
    }

    public void showTransactions() {
        ObservableList<TransactionResource> transactions = FXCollections.observableArrayList();
        ListView<TransactionResource> listView = new ListView<>(transactions);

        listView.setCellFactory(param -> new TransactionCell(transactions, id -> transactionManager.deleteTransaction(id)));

        transactions.addAll(
                transactionManager.getTransactions()
        );
        root.setCenter(listView);
    }

    public void showAddTransactionView() {
        AddTransactionView view = new AddTransactionView();
        root.setCenter(view.getView());
    }

}
