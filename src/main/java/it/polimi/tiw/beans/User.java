package it.polimi.tiw.beans;

public class User {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String address;
    private int addressNumber;

    public User() {}

    // costruttore prima di metterlo nel db, quindi non ha id
    public User(String username, String password, String name, String surname, String address, int addressNumber) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.addressNumber = addressNumber;
    }

    public User(Integer id, String username, String password, String name, String surname, String address, int addressNumber) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.addressNumber = addressNumber;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getAddress() {
        return address;
    }

    public int getAddressNumber() {
        return addressNumber;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAddressNumber(int addressNumber) {
        this.addressNumber = addressNumber;
    }

    public Integer getId() {
        return id;
    }
}
