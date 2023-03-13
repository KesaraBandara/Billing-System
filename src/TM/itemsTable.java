package TM;

public class itemsTable {

    private String product;
    private String price;
    private String qty;
    private String total;

    public itemsTable() {
    }

    public itemsTable(String product, String price, String qty, String total) {
        this.product = product;
        this.price = price;
        this.qty = qty;
        this.total = total;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
