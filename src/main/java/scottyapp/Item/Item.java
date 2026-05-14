package scottyapp.Item;

public class Item {
    private Integer idProduct;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String category;

    public Item(Integer idProduct, String name, String description, Double price, Integer stock, String category) {
        this.idProduct = idProduct;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    public Integer getIdProduct() { return idProduct; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public Integer getStock() { return stock; }
    public String getCategory() { return category; }
}
