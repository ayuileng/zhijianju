package cn.edu.iip.nju;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ZhijianjuApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZhijianjuApplication.class, args);
	}
}
