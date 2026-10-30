package cz.unicorn.fitnesssystem.config;

import cz.unicorn.fitnesssystem.FitnessSystemApplication;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api", HandlerTypePredicate
                .forBasePackageClass(FitnessSystemApplication.class)
                .and(HandlerTypePredicate.forAnnotation(RestController.class)));
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}