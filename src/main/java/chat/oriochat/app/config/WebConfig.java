package chat.oriochat.app.config;

import chat.oriochat.app.filters.JwtTokenFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // Replace with the correct origin of your frontend app
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Add all necessary methods
                .allowedHeaders("Authorization", "Content-Type", "Accept") // Allow headers like Authorization
                .allowCredentials(true);
    }

    // @Bean
    // public WebMvcConfigurer corsConfigurer() {
    //     return new WebMvcConfigurer() {
    //         @Override
    //         public void addCorsMappings(CorsRegistry registry) {
    //             registry.addMapping("/**")
    //                     .allowedOrigins("http://localhost:3000") // Replace with the correct origin of your frontend app
    //                     .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Add all necessary methods
    //                     .allowedHeaders("Authorization", "Content-Type", "Accept") // Allow headers like Authorization
    //                     .allowCredentials(true);
    //         }
    //     };
    // }

    // @Override
    // public void FilterRegistrationBean<JwtTokenFilter> filterFilterRegistration() {
    //     FilterRegistrationBean<JwtTokenFilter> registrationBean = new FilterRegistrationBean<>();
    //     registrationBean.setFilter(new JwtTokenFilter());
    //     registrationBean.addUrlPatterns("/*");
    //     registrationBean.setOrder(1);
    //     return registrationBean;
    // }


    // @Bean
    // public FilterRegistrationBean<JwtTokenFilter> filterFilterRegistration() {
    //     FilterRegistrationBean<JwtTokenFilter> registrationBean = new FilterRegistrationBean<>();
    //     registrationBean.setFilter(new JwtTokenFilter());
    //     registrationBean.addUrlPatterns("/*");
    //     registrationBean.setOrder(1);
    //     return registrationBean;
    // }
}
