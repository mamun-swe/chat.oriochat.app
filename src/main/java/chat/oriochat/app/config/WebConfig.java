package chat.oriochat.app.config;

import chat.oriochat.app.filters.JwtTokenFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    // CORS configuration to allow cross-origin requests
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    // Filter registration for JWT token validation
    // @Bean
    // public FilterRegistrationBean<JwtTokenFilter> filterFilterRegistration() {
    //     FilterRegistrationBean<JwtTokenFilter> registrationBean = new FilterRegistrationBean<>();
    //     registrationBean.setFilter(new JwtTokenFilter());
    //     registrationBean.addUrlPatterns("/api/v1/*");
    //     registrationBean.setOrder(1);
    //     return registrationBean;
    // }
}
