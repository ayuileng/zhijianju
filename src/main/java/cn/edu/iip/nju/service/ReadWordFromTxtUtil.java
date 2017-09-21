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
public class ReadWordFromTxtUtil {
    @Autowired
    private StringRedisTemplate template;

    public void readInjureWords(){
        Resource resource = new ClassPathResource("keywords/injureType.txt");
        try {
            File file = resource.getFile();
            List<String> strings = Files.readLines(file, Charsets.UTF_8);
            for (int i = 0; i < strings.size(); i++) {
                String split = strings.get(i).split("：")[1];
                String[] words = split.split("，");
                template.opsForSet().add("第"+i+"序列",words);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getInjureWords(String injureType){
        return template.opsForSet().members(injureType);
    }

    public Set<String> getFengxianWords(String fengxianType){
        return template.opsForSet().members(fengxianType);
    }

    public void readFengxianWord(){
        Resource resource = new ClassPathResource("keywords/fengxianType.txt");
        try {
            File file = resource.getFile();
            List<String> strings = Files.readLines(file, Charsets.UTF_8);
            for (int i = 0; i < strings.size(); i++) {
                String split = strings.get(i).split("-")[1];
                String[] words = split.split(" ");
                template.opsForSet().add("fengxian"+i,words);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
