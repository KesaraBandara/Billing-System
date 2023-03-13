package controller;

import TM.itemsTable;
import TM.stocksTable;
import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class StockViewFormController {


    public TableView<stocksTable> tblStocks;
    public TextField txtProduct;
    public Button btnAddOnAction;
    public TextField txtStock;
    public Label lblProductID;
    public AnchorPane root;
    public Label lblCoID;
    public  TextField txtCo;
    public  TextField txtPrice;

    String co_id;
    String product_id;


    public void initialize(){
        btnAddOnAction.setVisible(false);

        tblStocks.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("coID"));
        tblStocks.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("co"));
        tblStocks.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("pID"));
        tblStocks.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("product"));
        tblStocks.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("price"));
        tblStocks.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("stock"));

        setTable();


        tblStocks.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<stocksTable>() {
            @Override
            public void changed(ObservableValue<? extends stocksTable> observableValue, stocksTable oldValue, stocksTable newValue) {

                TM.stocksTable selectedItem = tblStocks.getSelectionModel().getSelectedItem();


                lblCoID.setText(selectedItem.getCoID());
                txtCo.setText(selectedItem.getCo());
                lblProductID.setText(selectedItem.getpID());
                txtProduct.setText(selectedItem.getProduct());
                txtPrice.setText(selectedItem.getPrice());
                txtStock.setText(selectedItem.getStock());

                if (selectedItem== null) {

                    return;
                }

                co_id = newValue.getCoID();
                product_id =newValue.getpID();

            }
        });



    }

 public void btnAddOnAction(ActionEvent actionEvent) {


        if (lblCoID.getText().trim().isEmpty()|| txtCo.getText().trim().isEmpty() || lblProductID.getText().trim().isEmpty() || txtProduct.getText().trim().isEmpty() || txtPrice.getText().trim().isEmpty() || txtStock.getText().trim().isEmpty() ){

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Not Compleate Text feald", ButtonType.OK);
            Optional<ButtonType> buttonType = alert.showAndWait();

            tblStocks.setDisable(true);

        }

        else{

            String text = lblCoID.getText();
            String text1 = txtCo.getText();
            String text2 = lblProductID.getText();
            String text3 = txtProduct.getText();
            String text4 = txtPrice.getText();
            String text5 = txtStock.getText();

            Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            PreparedStatement preparedStatement = connection.prepareStatement("insert into stock values (?,?,?,?,?,?)");
            preparedStatement.setObject(1,text);
            preparedStatement.setObject(2,text1);
            preparedStatement.setObject(3,text2);
            preparedStatement.setObject(4,text3);
            preparedStatement.setObject(5,text4);
            preparedStatement.setObject(6,text5);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }

        lblCoID.setText("");
        txtCo.clear();
        lblProductID.setText("");
        txtProduct.clear();
        txtPrice.clear();
        txtStock.clear();

        setTable();
        btnAddOnAction.setVisible(false);
    }

    public void setTable (){


        ObservableList<stocksTable> items = tblStocks.getItems();
        items.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from stock ");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){

                String coID = resultSet.getString(1);
                String co = resultSet.getString(2);
                String pID = resultSet.getString(3);
                String product = resultSet.getString(4);
                String price = resultSet.getString(5);
                String stock = resultSet.getString(6);

                stocksTable setStock = new stocksTable(coID,co,pID,product,price,stock);
                items.add(setStock);

            }
            tblStocks.refresh();
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }

    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, " do you want to delete the select record.", ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();


        if(buttonType.get().equals(ButtonType.YES)) {

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from stock where co_id = ?");
                preparedStatement.setObject(1,co_id);
                preparedStatement.executeUpdate();

                setTable();

                lblCoID.setText("");
                txtCo.clear();
                lblProductID.setText("");
                txtProduct.clear();
                txtPrice.clear();
                txtStock.clear();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }


    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {


        String  co = txtCo.getText();

        String  product = txtProduct.getText();
        String  price = txtPrice.getText();
        String  stock = txtStock.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update stock set co=?,product_id=?, product=?, price=?, stocks=? where co_id=?");

            preparedStatement.setObject(1,co);
            preparedStatement.setObject(2,product_id);
            preparedStatement.setObject(3,product);
            preparedStatement.setObject(4,price);
            preparedStatement.setObject(5,stock);
            preparedStatement.setObject(6,co_id);

            preparedStatement.executeUpdate();

            setTable();

            lblCoID.setText("");
            txtCo.clear();
            lblProductID.setText("");
            txtProduct.clear();
            txtPrice.clear();
            txtStock.clear();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void autoGenarateproductID(){

        Connection connection = DBConnection.getInstance().getConnection();

        try {

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select product_id from stock order by product_id desc limit 1;");

            boolean next = resultSet.next();

            if (next) {

                String oldID = resultSet.getString(1);

                int length = oldID.length();

                String ID = oldID.substring(1, length);
                int intID = Integer.parseInt(ID);

                intID = intID + 1;

                lblProductID.setText("P00"+intID);

                if (intID < 10) {

                    lblProductID.setText("P00"+intID);

                }
                else if (intID < 100 ) {
                    lblProductID.setText("P0"+intID);

                }else {

                    lblProductID.setText("P"+intID);
                }


            }
            else {
                lblProductID.setText("P001");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public void autoGenarateCoID(){

        Connection connection = DBConnection.getInstance().getConnection();

        try {

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select co_id from stock order by co_id desc limit 1;");

            boolean next = resultSet.next();

            if (next) {

                String oldID = resultSet.getString(1);

                int length = oldID.length();

                String ID = oldID.substring(1, length);
                int intID = Integer.parseInt(ID);

                intID = intID + 1;

                    lblCoID.setText("C00"+intID);

                if (intID < 10) {

                    lblCoID.setText("C00"+intID);

                }
                else if (intID < 100 ) {
                    lblCoID.setText("C0"+intID);

                }else {

                    lblCoID.setText("C"+intID);
                }


            }
            else {
                lblCoID.setText("C001");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void BackBillingSystemOnAction(ActionEvent actionEvent) throws IOException {

        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/mainViewForm.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) this.root.getScene().getWindow();

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();

    }
    public void btnNewOnAction (ActionEvent actionEvent){


        tblStocks.setDisable(false);
        btnAddOnAction.setVisible(true);
        autoGenarateCoID();
        autoGenarateproductID();

        String text = lblCoID.getText();
        }
    }
