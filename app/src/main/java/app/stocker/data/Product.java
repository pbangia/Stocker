package app.stocker.data;

public class Product {

    private String title;
    private int quantity;
    private String notes;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    private double price;

    public Product(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public Product(String title, int quantity, String notes, double price){
        this.title = title;
        this.quantity = quantity;
        this.notes = notes;
        this.price = price;
    }

}
