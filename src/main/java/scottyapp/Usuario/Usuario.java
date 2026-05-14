package scottyapp.Usuario;

public class Usuario {
    private Integer idUser;
    private String name;
    private String email;
    private String password;
    private String role;
    private String address;
    private String phone;

    public Usuario(Integer idUser, String name, String email, String password, String role, String address, String phone) {
        this.idUser = idUser;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.address = address;
        this.phone = phone;
    }

    public Integer getIdUser() { return idUser; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
}
