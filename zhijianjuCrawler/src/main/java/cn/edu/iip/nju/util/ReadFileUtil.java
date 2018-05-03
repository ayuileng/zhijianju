package cn.edu.iip.nju.util;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ReadFileUtil {
    private static Logger logger = LoggerFactory.getLogger(ReadFileUtil.class);


    public static Set<String> readKeyWords() {
        Resource resource = new ClassPathResource("keywords/product-keyword.txt");
        Set<String> keyWords = Sets.newHashSet();
        try {
            File file = resource.getFile();
            List<String> lines = Files.readLines(file, Charsets.UTF_8);
            for (String line : lines) {
                String[] split = line.split("-");
                String[] words = split[1].split(" ");
                for (String word : words) {
                    keyWords.add(split[0].trim() + " " + word.trim());
                }
            }
        } catch (IOException e) {
            logger.error("read keyword error");
            e.printStackTrace();
        }
        return keyWords;
    }
}
