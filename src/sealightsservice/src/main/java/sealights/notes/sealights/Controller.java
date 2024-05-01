package sealights.notes.sealights;

import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Controller {

    @PostMapping("/getDiscount")
    public String getDiscount(@RequestBody Map<String, Object> requestData) {
        double number = (Double) requestData.get("number");
        return  String.format("%.2f",number*0.9);
    }
}
