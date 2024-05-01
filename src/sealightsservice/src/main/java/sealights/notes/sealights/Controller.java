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
    public String getDiscount(@RequestBody String txt) {
        double number = Double.parseDouble(txt);
        return  String.format("%.2f",number*0.9);
    }
}
