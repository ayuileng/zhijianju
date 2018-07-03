package com.test.demo;

import com.test.demo.service.WebDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {


    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Autowired
    WebDataService webDataService;


    @Override
    public void run(String... strings) throws Exception {
        webDataService.transfer();
        System.out.println("finish");

    }
}
