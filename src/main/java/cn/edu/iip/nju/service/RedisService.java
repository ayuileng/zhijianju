package cn.edu.iip.nju.service;

import com.google.common.io.Files;
import org.apache.commons.io.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by xu on 2017/9/21.
 * 将关键字从txt中读取到redis
 */
@Service
public class RedisService {
    @Autowired
    private StringRedisTemplate template;


    //读取所有伤害类型关键字到redis
    public void readInjureWords(){
        Resource resource = new ClassPathResource("keywords/injureType.txt");
        try {
            File file = resource.getFile();
            List<String> strings = Files.readLines(file, Charsets.UTF_8);
            for (int i = 0; i < strings.size(); i++) {
                String split = strings.get(i).split("：")[1];
                String[] words = split.split("，");
                template.opsForSet().add("第"+i+"序列",words);
                template.opsForSet().add("allInjureType",words);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //返回所有的伤害关键字
    public Set<String> getAllInjureWords(){
        return template.opsForSet().members("allInjureType");
    }


    //返回某一序列的关键字（例： 第1序列）
    public Set<String> getInjureWords(String injureType){
        return template.opsForSet().members(injureType);
    }


    //根据风险类型返回对应的关键字列表
    public Set<String> getFengxianWords(String fengxianType){
        return template.opsForSet().members(fengxianType);
    }


    //读取所有风险关键字到redis
    public void readFengxianWord(){
        Resource resource = new ClassPathResource("keywords/fengxianType.txt");
        try {
            File file = resource.getFile();
            List<String> strings = Files.readLines(file, Charsets.UTF_8);
            for (int i = 0; i < strings.size(); i++) {
                String split = strings.get(i).split("-")[1];
                String[] words = split.split(" ");
                template.opsForSet().add("fengxian"+i,words);
                template.opsForSet().add("allFengxian",words);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //返回所有风险关键字集合
    public Set<String> getAllFengxianWords(){
        return template.opsForSet().members("allFengxian");
    }

    //读取产品关键字到redis
    public void readProductName(){
        Resource resource = new ClassPathResource("keywords/product.txt");
        try {
            File file = resource.getFile();
            List<String> strings = Files.readLines(file, Charsets.UTF_8);
            for (String string : strings) {
                String[] products = string.split("\\|");
                template.opsForSet().add("product",products);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取地区关键字到redis
    public void readCityToRedis() {
        Resource resource = new ClassPathResource("keywords/city.txt");
        File file = null;
        try {
            file = resource.getFile();
            List<String> lines = Files.readLines(file, com.google.common.base.Charsets.UTF_8);
            for (String line : lines) {
                String[] split = line.split("-");
                template.opsForSet().add("省份",split[0]);
                if (split.length == 2) {
                    template.opsForSet().add(split[0], split[1].split(" "));
                    template.opsForSet().add("所有城市",split[1].split(" "));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> readCityList(String provinceName) {
        return template.opsForSet().members(provinceName);
    }

    public String fromWhichProvince(String cityName){
        Set<String> provinces = template.opsForSet().members("省份");
        for (String province : provinces) {
            if (template.opsForSet().isMember(province,cityName)) {
                return province;
            }
        }
        return " ";
    }



}
