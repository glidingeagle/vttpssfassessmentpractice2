package sg.edu.nus.iss.vttpssfassessmentpractice2.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import sg.edu.nus.iss.vttpssfassessmentpractice2.model.LineItems;
import sg.edu.nus.iss.vttpssfassessmentpractice2.model.PurchaseOrder;
import sg.edu.nus.iss.vttpssfassessmentpractice2.model.Quotation;
import sg.edu.nus.iss.vttpssfassessmentpractice2.service.QuotationService;

@RestController
@RequestMapping(path="/")
public class PurchaseOrderRestController {
    
    @Autowired
    QuotationService quoSvc;

    @Autowired
    PurchaseOrder po;

    @PostMapping (path="/api/po")
    public ResponseEntity<String> getInvoice (@RequestBody String payload) {
        
        /*using @RequestBody String payload to retrieve information
        given by userinput*/
        System.out.println(">>> payload: " + payload);

        //Task 4: present requestBody for responseBody
        List<String> itemNames = quoSvc.createResponse(payload);
        System.out.println(">>> Items: " + itemNames);

        //Task 5
        Optional <Quotation> obtainResponse = quoSvc.getQuotations(itemNames);
        if (obtainResponse.isPresent()) {
            Quotation quotation = obtainResponse.get();
            System.out.println(quotation);

            List<Integer> fruitNumber = new LinkedList<>();
            for (LineItems quantity : po.getLineItems()) {
                fruitNumber.add(quantity.getQuantity());
            }
            
            float total = 0f;
            for(int i = 0; i<itemNames.size(); i++) {
                total += quotation.getQuotation(itemNames.get(i))*fruitNumber.get(i);
            }

            JsonObject jsonObjToReturn = Json.createObjectBuilder()
                                        .add("invoiceId", quotation.getQuoteId())
                                        .add("name", po.getName())
                                        .add("total", total)
                                        .build();
            
            return ResponseEntity.ok(jsonObjToReturn.toString());
        } else {
            JsonObjectBuilder emptyBuilder = Json.createObjectBuilder();
            JsonObject emptyObj = emptyBuilder.build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(emptyObj.toString());
        }
    }
            
}
