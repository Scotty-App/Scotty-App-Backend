package scottyapp.Order;

public class Order {
    private Integer idOrder;
    private String date;
    private Double total;
    private String status;
    private Integer idUser;

    public Order(Integer idOrder, String date, Double total, String status, Integer idUser) {
        this.idOrder = idOrder;
        this.date = date;
        this.total = total;
        this.status = status;
        this.idUser = idUser;
    }

    public Integer getIdOrder() { return idOrder; }
    public String getDate() { return date; }
    public Double getTotal() { return total; }
    public String getStatus() { return status; }
    public Integer getIdUser() { return idUser; }
}