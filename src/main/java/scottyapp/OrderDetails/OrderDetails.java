package scottyapp.OrderDetails;

public class OrderDetails {
    private Integer idDetail;
    private Integer quantity;
    private Double subtotal;
    private Integer idOrder;
    private Integer idProduct;

    public OrderDetails(Integer idDetail, Integer quantity, Double subtotal, Integer idOrder, Integer idProduct) {
        this.idDetail = idDetail;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.idOrder = idOrder;
        this.idProduct = idProduct;
    }

    public Integer getIdDetail() { return idDetail; }
    public Integer getQuantity() { return quantity; }
    public Double getSubtotal() { return subtotal; }
    public Integer getIdOrder() { return idOrder; }
    public Integer getIdProduct() { return idProduct; }
}