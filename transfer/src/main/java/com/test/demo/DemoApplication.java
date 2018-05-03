package com.test.demo;

import com.test.demo.service.NewsDataService;
import com.test.demo.service.WebDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    public DemoApplication(WebDataService webDataService,NewsDataService newsDataService) {
        this.webDataService = webDataService;
        this.newsDataService = newsDataService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    final
    WebDataService webDataService;
    final
    NewsDataService newsDataService;

    @Override
    public void run(String... strings) throws Exception {
        System.out.println(newsDataService.countOri1());//13462
        System.out.println(newsDataService.countOri2());//24147
        newsDataService.transfer();
        System.out.println(newsDataService.countOri1());
        System.out.println(newsDataService.countOri2());

    }
}
