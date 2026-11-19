package cz.unicorn.fitnesssystem.config;

import cz.unicorn.fitnesssystem.FitnessSystemApplication;
import cz.unicorn.fitnesssystem.api.model.MembershipFilter;
import cz.unicorn.fitnesssystem.constant.ApiPaths;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(ApiPaths.API, HandlerTypePredicate
                .forBasePackageClass(FitnessSystemApplication.class)
                .and(HandlerTypePredicate.forAnnotation(RestController.class)));
    }

    /**
     * Converts query parameters to generated enums by their wire value ({@code active}), which Spring's default
     * converter can't do because it matches the constant name ({@code ACTIVE}).
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, MembershipFilter.class, MembershipFilter::fromValue);
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}