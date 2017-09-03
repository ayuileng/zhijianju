package cn.edu.iip.nju.util;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by 63117 on 2017/4/30.
 */
public class DownLoadUtil {

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


    @Test
    public void test(){
        String url = "http://file.szqts.gov.cn/cms/qts/2014/11/24/201411241050225512626763.xls";
//        String s = download(url, "./config/");
//        System.out.println(s);

    }
}
