package cn.edu.iip.nju.util;

import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

/**
 * Created by 63117 on 2017/4/30.
 */
public class DownLoadUtil {
    private static final Set<String> set = Sets.newHashSet("doc","docx","xls","xlsx","zip","rar");

    private static final Logger logger = LoggerFactory.getLogger(DownLoadUtil.class);

    private static String getFileNameFromUrl(String url){
        String name = Long.toString(System.currentTimeMillis()) + ".X";
        int index = url.lastIndexOf("/");
        if(index > 0){
            name = url.substring(index + 1);
            if(name.trim().length()>0){
                return name;
            }
        }
        return name;
    }

    public static String download(String url,String dir) {
        String fileName = getFileNameFromUrl(url);
        try {
            URL httpurl = new URL(url);

            //System.out.println(fileName);
            logger.info("downloading file: "+fileName);
            File f = new File(dir + fileName);
            try {
                FileUtils.copyURLToFile(httpurl, f);
                logger.info(f.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error when downloading file: "+fileName);

        }
        logger.info("downloading file: "+fileName+" success!");
        return fileName;

    }

    public static boolean isFile(String url){
        String suffix = url.substring(url.lastIndexOf('.')+1);
        return set.contains(suffix);
    }

}
