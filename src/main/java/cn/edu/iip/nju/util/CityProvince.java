import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

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

    private HashMap<String, String> cityProvince = new HashMap<String, String>();

    private ArrayList<ArrayList<String>> companyNameSegment = new ArrayList<ArrayList<String>>();


    //读取文件形成key：城市，value：省的哈希表
    public HashMap<String, String> getCityProvince(String filePath) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
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
    public ArrayList<ArrayList<String>> getCompanyProvince(String filePath) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
        String lineText = br.readLine();
//        ArrayList<ArrayList<String>> companyNameSegment = new ArrayList<ArrayList<String>>();
        while (lineText != null) {
            ArrayList<String> tem = new ArrayList<String>();
            String[] textSplit = lineText.trim().split(",");
//            System.out.println(textSplit[3].length());
            if (textSplit[3] != null || textSplit[3].length() != 0) {
                List<Term> termList = StandardTokenizer.segment(textSplit[3]);
                for (Term term : termList) {
                    tem.add(term.word.toString());
                }
                companyNameSegment.add(tem);
            }
            lineText = br.readLine();
        }
        return companyNameSegment;
    }

    //输入分词后的公司名称，返回相应省份，若没有返回null
    public String chooseProvinceOfCompany(ArrayList<String> companyName) {
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
        String cityFilePath = "/Users/Felix/Desktop/city.txt";
        String companyFilePath = "/Users/Felix/Desktop/company.csv";
        CityProvince cityProvince = new CityProvince();
        HashMap<String, String> tem = cityProvince.getCityProvince(cityFilePath);
//        System.out.println(tem.toString());
        ArrayList<ArrayList<String>> companyTem = cityProvince.getCompanyProvince(companyFilePath);
        System.out.println(companyTem.toString());
//        晶, 澳, （, 扬州, ）, 太阳能, 科技, 有限公司
        ArrayList<String> text = new ArrayList<String>() {{
            add("晶");
            add("澳");
            add("扬州");
            add("睿");
        }};
        String result = cityProvince.chooseProvinceOfCompany(text);
        System.out.println(result);

    }
}
