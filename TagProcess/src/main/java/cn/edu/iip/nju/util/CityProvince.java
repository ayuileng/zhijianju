package cn.edu.iip.nju.util;

import com.google.common.collect.Lists;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.assertj.core.util.Strings;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Felix on 2017/10/28.
 */
public class CityProvince {
    //读取文件形成key：城市，value：省的哈希表
    private static HashMap<String, String> getCityProvince() throws IOException {
        HashMap<String, String> cityProvince = new HashMap<>();
        Resource resource = new ClassPathResource("keywords/citydetail.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(resource.getFile()), "utf-8"));
        String lineText = br.readLine();

        while (lineText != null) {
            String[] textSplit = lineText.trim().split(" ");
            String province = textSplit[0];
            for (int i = 0; i < textSplit.length; i++) {
                if (textSplit[i].length() > 1) {
                    cityProvince.put(textSplit[i], province);
                }
            }
            lineText = br.readLine();
        }
        br.close();
        return cityProvince;
    }

    //读取文件进行公司名称分词
    private static ArrayList<String> getCompanyProvince(String comName) {
        ArrayList<String> list = Lists.newArrayList();
        if (comName == null) {
            return list;
        }
        List<Term> termList = StandardTokenizer.segment(comName);
        for (Term term : termList) {
            list.add(term.word);
        }
        return list;
    }


    //输入分词后的公司名称，返回相应省份，若没有返回null
    public static String chooseProvinceOfCompany(String comName)  {
        ArrayList<String> companyName = getCompanyProvince(comName);
        HashMap<String, String> cityProvince = null;
        try {
            cityProvince = getCityProvince();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String companySegment : companyName) {
            if (!Strings.isNullOrEmpty(cityProvince.get(companySegment)) ) {
                return cityProvince.get(companySegment);
            } else if (cityProvince.get(companySegment + "省") != null) {
                return cityProvince.get(companySegment + "省");
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

    public static void main(String[] args) throws IOException {
        System.out.println(getCompanyProvince("郑州宇晨电缆电线有限公司"));
        System.out.println(chooseProvinceOfCompany("郑州宇晨电缆电线有限公司"));
    }
}