package sealights.notes.sealights;

import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Controller {

    public string GetPricePostDiscount(String txt){

        double number = Double.parseDouble(txt);
        if (number > 200 && number <= 300)  {
            return  String.format("%.2f",number*0.78);
        }
        else if(number > 300){
            return  String.format("%.2f",number*0.70);
        }
        return String.format("%.2f",number*0.9);
    }

    @PostMapping("/getDiscount")
    public String getDiscount(@RequestBody String txt) {
        return GetPricePostDiscount(txt);
    }
}
