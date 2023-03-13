package controller;

import TM.itemsTable;
import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.converter.BigDecimalStringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Optional;

//import static com.sun.org.apache.xml.internal.serializer.Version.getProduct;

public class mainViewFormController {


    public TextField txtProduct;
    public TextField txtRate;
    public TableView<itemsTable> tblItems;
    public TextField txtPrice;
    public Label lblGrandTotal;
    public TextArea txtAraBill;
    public Label lblTotal;
    public TextField txtPaidAmount;
    public AnchorPane rootMain;
    public ListView lstVwProduct;
    public Label lblSaleID;
    public Button btnAddOnAction;
    public Button btnFinishOnAction;
    public Button btnPrintOnAction;
    public ComboBox cbProduct;

    double total = 0;

    public void initialize() {

        btnPrintOnAction.setVisible(false);
        txtProduct.setVisible(false);
        txtRate.requestFocus();


         autoGenarateproductID();
        setcomproduct();

        tblItems.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("product"));
        tblItems.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("price"));
        tblItems.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblItems.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("total"));

        txtAraBill.setText("\n***********************************************-----****************************************************\n" +
                "Address : \n" +
                "T.Number : 070-0000000/035-0000000/071-0000000\n" +
                "Product \t\t\t Price \t\t\t Rate \t\t\t Total");

        validation();



//        ##############

        tblItems.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<itemsTable>() {
            @Override
            public void changed(ObservableValue<? extends itemsTable> observableValue, itemsTable itemsTable, itemsTable t1) {

                TM.itemsTable selectedItem = tblItems.getSelectionModel().getSelectedItem();

             //   cbProduct.setItems(selectedItem.getProduct());

                cbProduct.setVisible(false);
                txtProduct.setVisible(true);
                txtProduct.setText(selectedItem.getProduct());
                txtProduct.setDisable(true);
                txtPrice.setDisable(true);
                txtPrice.setText(selectedItem.getPrice());
                txtRate.setText(selectedItem.getQty());
                lblTotal.setText(selectedItem.getTotal());
                btnAddOnAction.setVisible(false);

            }
        });


    }


    public void validation() {

        DecimalFormat decimalFormat = new DecimalFormat("");
        decimalFormat.setParseBigDecimal(true);

        TextFormatter<BigDecimal> formatter = new TextFormatter<>(new BigDecimalStringConverter(), new BigDecimal("0"), c -> {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }
            ParsePosition parsePosition = new ParsePosition(0);
            Object object = decimalFormat.parse(c.getControlNewText(), parsePosition);
            if (object == null || parsePosition.getIndex() < c.getControlNewText().length()) {
                return null;
            } else {
                return c;
            }
        });

        txtRate.setTextFormatter(formatter);


        DecimalFormat dF = new DecimalFormat("");
        decimalFormat.setParseBigDecimal(true);

        TextFormatter<BigDecimal> ftr = new TextFormatter<>(new BigDecimalStringConverter(), new BigDecimal("0"), c -> {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }
            ParsePosition parsePosition = new ParsePosition(0);
            Object object = dF.parse(c.getControlNewText(), parsePosition);
            if (object == null || parsePosition.getIndex() < c.getControlNewText().length()) {
                return null;
            } else {
                return c;
            }
        });

        txtPrice.setTextFormatter(ftr);

        DecimalFormat DF = new DecimalFormat("");
        decimalFormat.setParseBigDecimal(true);

        TextFormatter<BigDecimal> FORMATTER = new TextFormatter<>(new BigDecimalStringConverter(), new BigDecimal("0"), c -> {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }
            ParsePosition parsePosition = new ParsePosition(0);
            Object object = DF.parse(c.getControlNewText(), parsePosition);
            if (object == null || parsePosition.getIndex() < c.getControlNewText().length()) {
                return null;
            } else {
                return c;
            }
        });

        txtPaidAmount.setTextFormatter(FORMATTER);
    }

    public void setcomproduct(){

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(" select product from stock");

            ObservableList product = FXCollections.observableArrayList();

            while (resultSet.next()) {
                product.add(new String(resultSet.getString(1)));
            }

            cbProduct.setItems(product);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        cbProduct.setOnAction(e -> {
            try {
                String selectedProduct = cbProduct.getValue().toString();
                PreparedStatement ps = connection.prepareStatement("SELECT price FROM stock WHERE product = ?");
                ps.setString(1, selectedProduct);
                ResultSet resultSet = ps.executeQuery();

                while (resultSet.next()) {
                    String price = resultSet.getString(1);
                    txtPrice.setText(price);
                    txtProduct.setText(selectedProduct);
                }

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }



    public void detailAddOnAction(ActionEvent actionEvent) {


        String product = txtProduct.getText();
        String price = txtPrice.getText();
        String rate = txtRate.getText();


        double sTotal;

        double PRICE = Double.parseDouble(price);
        double RATE = Double.parseDouble(rate);
        //calculate the subtotal
        sTotal = PRICE * RATE;

        String subTotal = Double.toString(sTotal);
        lblTotal.setText(subTotal);
        ObservableList<itemsTable> items = tblItems.getItems();
        itemsTable newItems = new itemsTable(product, price, rate, subTotal);
        items.add(newItems);
        tblItems.refresh();

        setTemplate();

        txtProduct.clear();
        txtPrice.clear();
        txtRate.clear();



        // calculate the grand total
        total += sTotal;

        System.out.println("total" + total);
        System.out.println("sTotal :" + sTotal);
//        lblGrandTotal.setText("");
        String st = Double.toString(total);

        // print grand total
        lblGrandTotal.setText("Rs : " + st + " /=");

    }

    public void autoGenarateproductID(){



        Connection connection = DBConnection.getInstance().getConnection();

        try {

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select sale_id from sale_item order by sale_id desc limit 1;");

            boolean next = resultSet.next();

            if (next) {

                String oldID = resultSet.getString(1);

                int length = oldID.length();

                String ID = oldID.substring(1, length);
                int intID = Integer.parseInt(ID);

                intID = intID + 1;

                lblSaleID.setText("S00"+intID);

                if (intID < 10) {

                    lblSaleID.setText("S00"+intID);

                }
                else if (intID < 100 ) {
                    lblSaleID.setText("S0"+intID);

                }else {

                    lblSaleID.setText("S"+intID);
                }


            }
            else {
                lblSaleID.setText("S001");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }


    public void setTemplate(){


        txtAraBill.setText(txtAraBill.getText() + "\n"+ txtProduct.getText()+":\t\t\t"+txtPrice.getText()+"\t\t\t\t"+txtRate.getText()+"\t\t\t\t"+lblTotal.getText());

    }

    public void btnPrintOnAction(ActionEvent actionEvent) {
        // update the text in the txtAraBill TextArea
        String paidAmount = txtPaidAmount.getText();
        String grandTotal = lblGrandTotal.getText().replace("Rs : ", "").replace(" /=", ""); // remove "Rs : " and " /=" from label text
        double amount = Double.parseDouble(paidAmount) - Double.parseDouble(grandTotal);

        txtAraBill.setText(txtAraBill.getText() + "\nsubtotal ;\t\t\t\t\t\t" + lblGrandTotal.getText());
        txtAraBill.setText(txtAraBill.getText() + "\nPaid  ;\t\t\t\t\t\t"+ txtPaidAmount.getText() + "\namount ;\t\t\t\t\t\t" + amount);

        // print the contents of the txtAraBill TextArea
        TextArea printArea = txtAraBill;

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(printArea.getScene().getWindow())) {
            boolean success = job.printPage(printArea);
            if (success) {
                job.endJob();
            }
        }


        // clear the text fields and table
        txtProduct.clear();
        txtPrice.clear();
        txtRate.clear();
        txtPaidAmount.clear();
        lblTotal.setText("");
        lblGrandTotal.setText("");
        tblItems.getItems().clear();
        txtAraBill.setText("\n***********************************************-----****************************************************\n" +
                "Address : \n" +
                "T.Number : 070-0000000/035-0000000/071-0000000\n"+
                "Product \t\t\t Price \t\t\t Rate \t\t\t Total");
        total = 0;
        btnPrintOnAction.setVisible(false);


}


public void btnDeleteOnAction(ActionEvent actionEvent) {




    // Get the selected item in the table
    itemsTable selectedItem = tblItems.getSelectionModel().getSelectedItem();

    String grandTotal = lblGrandTotal.getText().replace("Rs : ", "").replace(" /=", ""); // remove "Rs : " and " /=" from label text
    double amount =  Double.parseDouble(grandTotal)- Double.parseDouble(selectedItem.getTotal());

    lblGrandTotal.setText(String.valueOf(amount));



// Remove the selected item from the table
    tblItems.getItems().remove(selectedItem);

// Split the text in the TextArea into an array of lines
    String[] lines = txtAraBill.getText().split("\n");

// Find the index of the line that corresponds to the selected item in the table
    int lineIndex = -1;
    for (int i = 0; i < lines.length; i++) {
        if (lines[i].contains((CharSequence) selectedItem.getProduct())) {
            lineIndex = i;
            //break;
        }
    }

// Remove the line from the TextArea
    if (lineIndex != -1) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i != lineIndex) {
                sb.append(lines[i]);
                sb.append("\n");
            }
        }
        txtAraBill.setText(sb.toString());
    }

// Clear the input fields
    txtProduct.clear();
    txtPrice.clear();
    txtRate.clear();
    txtProduct.setDisable(false);
    txtPrice.setDisable(false);
    lblTotal.setText("");

    btnAddOnAction.setVisible(true);
    cbProduct.setVisible(true);

    total = 0;

}


    public void btnUpdateOnAction(ActionEvent actionEvent) {

        itemsTable selectedItem = tblItems.getSelectionModel().getSelectedItem();

        String grandTotal = lblGrandTotal.getText().replace("Rs : ", "").replace(" /=", ""); // remove "Rs : " and " /=" from label text
        double amount =  Double.parseDouble(grandTotal)- Double.parseDouble(selectedItem.getTotal());


        // Split the text in the TextArea into an array of lines
        String[] lines = txtAraBill.getText().split("\n");

// Find the index of the line that corresponds to the selected item in the table
        int lineIndex = -1;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains((CharSequence) selectedItem.getProduct())) {
                lineIndex = i;
                break;
            }
        }

// Remove the line from the TextArea
        if (lineIndex != -1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                if (i != lineIndex) {
                    sb.append(lines[i]);
                    sb.append("\n");
                }
            }
            txtAraBill.setText(sb.toString());



            String product = txtProduct.getText();
            String price = txtPrice.getText();
            String rate = txtRate.getText();

            double sTotal;

            double PRICE = Double.parseDouble(price);
            double RATE = Double.parseDouble(rate);
            //calculate the subtotal
            sTotal = PRICE * RATE;

            String subTotal = Double.toString(sTotal);
            lblTotal.setText(subTotal);
            ObservableList<itemsTable> items = tblItems.getItems();
            itemsTable newItems = new itemsTable(product, price, rate, subTotal);
            items.add(newItems);
            tblItems.refresh();

            setTemplate();

            // calculate the grand total
            amount += sTotal;

            System.out.println("total"+total);
            System.out.println("sTotal :"+sTotal);
//        lblGrandTotal.setText("");
            String st = Double.toString(amount);
            total = Double.parseDouble(st);
            // print grand total
            lblGrandTotal.setText( "Rs : "+ st + " /=");

            tblItems.getItems().remove(selectedItem);

            txtProduct.clear();
            txtPrice.clear();
            txtRate.clear();
            txtProduct.setDisable(false);
            txtPrice.setDisable(false);

            btnAddOnAction.setVisible(true);
            cbProduct.setVisible(true);

        }

    }

    public void btnViewStocksOnAction(ActionEvent actionEvent) throws IOException {

        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/stockViewForm.fxml"));

        Scene scene = new Scene(parent);

        Stage primaryStage = (Stage) this.rootMain.getScene().getWindow();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Stocks");
        primaryStage.centerOnScreen();

    }

    public void btnFinishOnAction(ActionEvent actionEvent) {

        String sale_id = lblSaleID.getText();
        String GT = lblGrandTotal.getText();
        String new2String;

        ObservableList<itemsTable> items = tblItems.getItems();

        if(items.size() == 0){

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Not Compleate", ButtonType.OK);
            Optional<ButtonType> buttonType = alert.showAndWait();
        }

        else {

            btnPrintOnAction.setVisible(true);

            for (int i = 0; i < items.size(); i++) {
            itemsTable item = items.get(i);

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                Statement statement = connection.createStatement();
                PreparedStatement preparedStatement = connection.prepareStatement("insert into sale_item (sale_id, product, price, rate, total,grand_total) values (?,?,?,?,?,?)");
                preparedStatement.setObject(1, sale_id);
                preparedStatement.setString(2, item.getProduct().toString());
                preparedStatement.setString(3, item.getPrice());
                preparedStatement.setString(4, item.getQty());
                preparedStatement.setString(5, item.getTotal());
                preparedStatement.setObject(6, GT);

                preparedStatement.executeUpdate();


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

//            ########################

                try {
                    Statement statement = connection.createStatement();
                    PreparedStatement preparedStatement = connection.prepareStatement("update stock set stocks = ? where product = ?");



                        String qty = item.getQty();

                        ResultSet resultSet = statement.executeQuery("SELECT stocks FROM stock WHERE product = '" + item.getProduct() + "'");
                        resultSet.next();
                        String stocks = resultSet.getString(1);

                        int newStock = Integer.parseInt(stocks) - Integer.parseInt(qty);
                        String newStockString = String.valueOf(newStock);

                        preparedStatement.setObject(1, newStockString);
                        preparedStatement.setString(2, item.getProduct());

                        preparedStatement.executeUpdate();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                btnPrintOnAction.setVisible(true);

        }}}
}

