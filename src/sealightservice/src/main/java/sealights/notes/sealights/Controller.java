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
            // Ensure 10% line coverage for addtwonumbers
            int sum = functions.addtwonumbers(4, 5);

            // Ensure 20% line coverage for subtracttwonumbers
            int difference = functions.subtracttwonumbers(9, 3);

            // Ensure 30% line coverage for multiplytwonumbers
            int product = functions.multiplytwonumbers(7, 6);

            // Ensure 100% line coverage for dividetwonumbers
            double quotient1 = functions.dividetwonumbers(8, 2);
            double quotient2 = functions.dividetwonumbers(0, 2);
            double quotient3;
            try {
                quotient3 = functions.dividetwonumbers(8, 0);
            } catch (IllegalArgumentException e) {
                quotient3 = Double.NaN; // Handling the exception
            }

            // Ensure 50% line coverage for _80_lins_Complex
            int complexResult1 = functions._80_lins_Complex(5, 3);
            int complexResult2 = functions._80_lins_Complex(2, 4);

            // Ensure 50% line coverage for _150_lins_Complex
            int newComplexResult1 = functions._150_lins_Complex(5, 3);
            int newComplexResult2 = functions._150_lins_Complex(2, 4);

            // Print statements to show results
            System.out.println("Addition: " + sum);
            System.out.println("Subtraction: " + difference);
            System.out.println("Multiplication: " + product);
            System.out.println("Division (a > 0, b > 0): " + quotient1);
            System.out.println("Division (a == 0): " + quotient2);
            System.out.println("Division (b == 0): " + (Double.isNaN(quotient3) ? "Exception caught" : quotient3));
            System.out.println("Complex Function Result 1: " + complexResult1);
            System.out.println("Complex Function Result 2: " + complexResult2);
            System.out.println("New Complex Function Result 1: " + newComplexResult1);
            System.out.println("New Complex Function Result 2: " + newComplexResult2);

            // Reading the HTML file content
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