package sg.edu.nus.iss.vttpssfassessmentpractice2.model;

import java.util.List;

public class PurchaseOrder {
    private String name;
    private String address;
    private String email;
    private List <LineItems> lineItems;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public List<LineItems> getLineItems() {
        return lineItems;
    }
    public void setLineItems(List<LineItems> lineItems) {
        this.lineItems = lineItems;
    }
}
