package TM;

public class stocksTable {


    private String coID;
    private String co;
    private String pID;
    private String product;
    private String stock;
    private String price;
//    private String date;
//    private String time;

    public stocksTable() {
    }

    public stocksTable(String coID, String co, String pID, String product, String price,String stock) {
        this.coID = coID;
        this.co = co;
        this.pID = pID;
        this.product = product;
        this.stock = stock;
        this.price = price;
//        this.date = date;
//        this.time = time;
    }

    public String getCoID() {
        return coID;
    }

    public void setCoID(String coID) {
        this.coID = coID;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getpID() {
        return pID;
    }

    public void setpID(String pID) {
        this.pID = pID;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//
//    public String getTime() {
//        return time;
//    }
//
//    public void setTime(String time) {
//        this.time = time;
//    }
}
