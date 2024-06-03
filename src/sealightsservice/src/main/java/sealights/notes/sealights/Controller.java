package sealights.notes.sealights;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Controller {

    @GetMapping("/about")
    public String about() {
        try {
            int sum = functions.addtwonumbers(4, 5);
            int difference = functions.subtracttwonumbers(9, 3);
            int product = functions.multiplytwonumbers(7, 6);
            double quotient = functions.dividetwonumbers(8, 2);

            System.out.println("Addition: " + sum);
            System.out.println("Subtraction: " + difference);
            System.out.println("Multiplication: " + product);
            System.out.println("Division: " + quotient);

            ClassPathResource resource = new ClassPathResource("templates/page.html");
            Path filePath = Paths.get(resource.getURI());
            String fileContent = Files.readString(filePath);
            return fileContent;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading file";
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
