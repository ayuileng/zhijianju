package cn.edu.iip.nju;

import cn.edu.iip.nju.service.CompanyNegativeListService;
import cn.edu.iip.nju.service.InjureCaseService;
import cn.edu.iip.nju.service.NewsDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;

@SpringBootApplication
public class TagProcessApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TagProcessApplication.class, args);
    }

    @Autowired
    CompanyNegativeListService companyNegativeListService;

    @Autowired
    @Qualifier(value = "threadPool")
    ExecutorService threadPool;

    @Autowired
    NewsDataService newsDataService;
    @Autowired
    InjureCaseService injureCaseService;

    @Override
    public void run(String... strings) throws Exception {

        injureCaseService.removeProductName();
    }
}
