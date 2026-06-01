package scottyapp.OrderDetails;

public class OrderDetails {
    private Integer idDetail;
    private Integer quantity;
    private Double subtotal;
    private Integer idOrder;
    private Integer idProduct;
    private String nombreProducto;

    public OrderDetails(Integer idDetail, Integer quantity, Double subtotal, Integer idOrder, Integer idProduct, String nombreProducto) {
        this.idDetail = idDetail;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.idOrder = idOrder;
        this.idProduct = idProduct;
        this.nombreProducto = nombreProducto != null ? nombreProducto : "";
    }

    public Integer getIdDetail() { return idDetail; }
    public Integer getQuantity() { return quantity; }
    public Double getSubtotal() { return subtotal; }
    public Integer getIdOrder() { return idOrder; }
    public Integer getIdProduct() { return idProduct; }
    public String getNombreProducto() { return nombreProducto; }
}