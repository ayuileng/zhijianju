package cn.edu.iip.nju.util;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ReadFileUtil {
    private static Logger logger = LoggerFactory.getLogger(ReadFileUtil.class);

    public static Set<String> readProducts() {
        Resource resource = new ClassPathResource("keywords/product.txt");
        Set<String> products = Sets.newHashSet();
        try {
            File file = resource.getFile();
            List<String> lines = Files.readLines(file, Charsets.UTF_8);
            for (String line : lines) {
                products.add(line.trim());
            }
        } catch (IOException e) {
            logger.error("read product error");
            e.printStackTrace();
        }
        return products;
    }

    public static Set<String> readCities() {
        Set<String> result = Sets.newHashSet();
        Resource resource = new ClassPathResource("keywords/city.txt");
        File file = null;
        try {
            file = resource.getFile();
            List<String> lines = Files.readLines(file, com.google.common.base.Charsets.UTF_8);
            for (String line : lines) {
                String[] split = line.split("-");
                if (split.length == 2) {
                    result.addAll(Arrays.asList(split[1].split(" ")));
                }
            }
        } catch (IOException e) {
            logger.error("read city error");
            e.printStackTrace();
        }
        return result;
    }
    public static Set<String> readInjureType() {
        Set<String> result = Sets.newHashSet();
        Resource resource = new ClassPathResource("keywords/injureType.txt");
        try {
            File file = resource.getFile();
            List<String> strings = Files.readLines(file, Charset.forName("utf-8"));
            for (int i = 0; i < strings.size(); i++) {
                String split = strings.get(i).split("：")[1];
                String[] words = split.split("，");
                result.addAll(Arrays.asList(words));
            }
            resource = new ClassPathResource("keywords/fengxianType.txt");
            file = resource.getFile();
            strings = Files.readLines(file, Charset.forName("utf-8"));
            for (int i = 0; i < strings.size(); i++) {
                String split = strings.get(i).split("-")[1];
                String[] words = split.split(" ");
                result.addAll(Arrays.asList(words));
            }
        } catch (IOException e) {
            logger.error("read injureType error");
            e.printStackTrace();
        }
        return result;
    }



    @Test
    public void test(){
        Set<String> strings = readInjureType();
        strings.forEach(System.out::println);
    }

}
