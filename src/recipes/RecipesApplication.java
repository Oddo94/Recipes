package recipes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"recipes/controller", "recipes/model", "recipes/repository", "recipes/exception","recipes/service", "recipes/utils"})
@EnableConfigurationProperties
@EntityScan(basePackages = {"recipes/model"})
public class RecipesApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecipesApplication.class, args);
    }
}
