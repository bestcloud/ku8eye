package org.ku8eye;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

 
@SpringBootApplication
public class App {  
    public static void main(String[] args) {  
        SpringApplication app = new SpringApplication(App.class);  
        app.setWebEnvironment(true);  
        app.setShowBanner(false);  
          
        Set<Object> set = new HashSet<Object>();  
        //set.add("classpath:applicationContext.xml");  
        app.setSources(set);  
        app.run(args);  
    }    
}  