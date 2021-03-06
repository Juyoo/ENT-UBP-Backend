package org.ubp.ent.backend.config.webmvc;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Anthony on 04/12/2015.
 */
@Configuration
public class EntWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {

    /*
     * Enable Cross Origin Resources Sharing
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // TODO : UPDATE THIS BEFORE DEPLOYING TO PRODUCTION !!!!!
        registry.addMapping("/**");
    }

}
