package sg.edu.nus.iss.vttpssfassessmentpractice2.service;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import sg.edu.nus.iss.vttpssfassessmentpractice2.model.LineItems;
import sg.edu.nus.iss.vttpssfassessmentpractice2.model.PurchaseOrder;
import sg.edu.nus.iss.vttpssfassessmentpractice2.model.Quotation;

@Service
public class QuotationService {
    Logger logger = Logger.getLogger(QuotationService.class.getName());

    public List<String> createResponse (String payload) {
        /*Read the payload and set it to the variables in the PurchaseOrder model from line 30 to line 57*/
        JsonReader jsonReader = Json.createReader(new StringReader(payload));
        
        /*This part is to read JsonObject of the payload */
        JsonObject jsonPayload = jsonReader.readObject();
        PurchaseOrder po = new PurchaseOrder();
        po.setName(jsonPayload.getString("name"));
        po.setAddress(jsonPayload.getString("address"));
        po.setEmail(jsonPayload.getString("email"));
        
        /*To reduce my own confusion, I changed JsonArray lineItems to JsonArray orders*/
        /*This part is to read JsonArray of the payload */
        JsonArray orders = jsonPayload.getJsonArray("lineItems");
        List <LineItems> lineItems = new LinkedList<>();
        for (int i = 0; i<orders.size(); i++) {
            LineItems fruits = new LineItems();
            fruits.setItem(orders.asJsonObject().getString("item"));
            String qty = orders.asJsonObject().getString("quantity");
            fruits.setQuantity(Integer.parseInt(qty));
            lineItems.add(fruits);
        }
        po.setLineItems(lineItems);

        //Building a List of itemNames
        List<String> itemNames = new LinkedList<>(); 
        for (LineItems item: po.getLineItems()) {
            itemNames.add(item.getItem());
        }
        return itemNames;
    }

    public Optional <Quotation> getQuotations (List<String> itemNames) { 
        
        //Line 63 - Line 86: use to get ResponseBody from Qsys Application
        String url = "https://quotation.chuklee.com/quotation";

        RequestEntity<String> request = RequestEntity
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept", "application/json")
                .body(itemNames.toString(), String.class); 

        RestTemplate template = new RestTemplate ();
        ResponseEntity<String> response = template.exchange(request, String.class);
        String returnResponse = response.getBody();
        try {
        JsonReader reader = Json.createReader(new StringReader(returnResponse));
        JsonObject jsonObjFromQsys = reader.readObject();

        Quotation quotation = new Quotation();
        quotation.setQuoteId(jsonObjFromQsys.getString("quoteId"));
            
        JsonArray quotationFruits = jsonObjFromQsys.getJsonArray("quotations");
        System.out.println(">>> quotationFruits: " + quotationFruits.toString());
        for (JsonValue fruit : quotationFruits) {
            String eachFruit = fruit.asJsonObject().getString("item");
            JsonValue unitPrice = fruit.asJsonObject().get("unitPrice");
            quotation.addQuotation(eachFruit, Float.parseFloat(unitPrice.toString()));
        }
        return Optional.of(quotation);
        } catch (Exception ex) {
            logger.warning(ex.getMessage());
            return Optional.empty();
        }
    }
}
