package cn.edu.iip.nju.util;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Felix on 2017/10/28.
 */
public class CityProvince {
    //读取文件形成key：城市，value：省的哈希表
    private static HashMap<String, String> getCityProvince() throws Exception {
        HashMap<String, String> cityProvince = new HashMap<>();
        Resource resource = new ClassPathResource("keywords/citydetail.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(resource.getFile()), "utf-8"));
        String lineText = br.readLine();

        while (lineText != null) {
            String[] textSplit = lineText.trim().split(" ");
            String province = textSplit[0];
            for (int i = 0; i < textSplit.length; i++) {
                if (textSplit[i].length() > 1) {
                    cityProvince.put(textSplit[i].substring(0, textSplit[i].length()), province);
                }
            }
            lineText = br.readLine();
        }
        br.close();
        return cityProvince;
    }

    //读取文件进行公司名称分词
    private static ArrayList<String> getCompanyProvince(String comName) throws Exception {
        ArrayList<String> list = Lists.newArrayList();
        if (comName==null){
            return list;
        }
        List<Term> termList = StandardTokenizer.segment(comName);
        for (Term term : termList) {
            list.add(term.word.toString());
        }
        return list;
    }


    //输入分词后的公司名称，返回相应省份，若没有返回null
    public static String chooseProvinceOfCompany(String comName) throws Exception {
        ArrayList<String> companyName = getCompanyProvince(comName);
        HashMap<String, String> cityProvince = getCityProvince();
        for (String companySegment : companyName) {
            if (cityProvince.get(companySegment) != null) {
                return cityProvince.get(companySegment);
            } else if (cityProvince.get(companySegment + "市") != null) {
                return cityProvince.get(companySegment + "市");
            } else if (cityProvince.get(companySegment + "县") != null) {
                return cityProvince.get(companySegment + "县");
            } else if (cityProvince.get(companySegment + "区") != null) {
                return cityProvince.get(companySegment + "区");
            } else
                continue;

        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(CityProvince.chooseProvinceOfCompany(null));
    }
}