package cloud.chenh.doch;

import cloud.chenh.doch.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DochApplication {

    public static void main(String[] args) {
        SpringApplication.run(DochApplication.class, args);
    }
    
}
