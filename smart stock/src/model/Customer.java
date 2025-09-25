package model;

import java.util.Date;

public class Customer {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Date registrationDate; // Add this field

    // Updated constructor with registrationDate
    public Customer(int id, String name, String email, String phone, String address, Date registrationDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registrationDate = registrationDate;
    }

    // Existing constructor for backward compatibility
    public Customer(int id, String name, String email, String phone, String address) {
        this(id, name, email, phone, address, new Date()); // Default to current date
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public Date getRegistrationDate() { return registrationDate; } // Added method

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
}
