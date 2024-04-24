package sealights.notes.sealights;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Controller {

    @GetMapping("/about")
    public String hello() {
        try{
            Path filePath = Path.of("src/main/resources/templates/page.html");
            String f=Files.readString(filePath);
            return f;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading file";
        }

    }
}
