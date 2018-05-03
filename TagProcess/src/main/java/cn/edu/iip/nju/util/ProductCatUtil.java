package cn.edu.iip.nju.util;

import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;

/**
 * Created by xu on 2018/1/19.
 * 服装：1
 * 家具及家庭生活用品：2
 * 文教体育用品：3
 * 锐器：4
 * 玩具：5
 * 家用电器：6
 * 其他：7
 */
public class ProductCatUtil {
    private static HashSet<String> cloths = Sets.newHashSet();
    private static HashSet<String> jiaju = Sets.newHashSet();
    private static HashSet<String> eduSports = Sets.newHashSet();
    private static HashSet<String> ruiqi = Sets.newHashSet();
    private static HashSet<String> toy = Sets.newHashSet();
    private static HashSet<String> elec = Sets.newHashSet();
    private static HashSet<String> other = Sets.newHashSet();

    public static final String clothsStr = "服装";
    public static final String jiajuStr = "家具及家庭生活用品";
    public static final String eduSportsStr = "文教体育用品";
    public static final String ruiqiStr = "锐器";
    public static final String toyStr = "玩具";
    public static final String elecStr = "家用电器";
    public static final String otherStr = "其他";

    static {
        Resource resource = new ClassPathResource("hospital/cat.txt");
        try {
            File catFile = resource.getFile();
            List<String> strings = Files.readLines(catFile, Charset.forName("utf-8"));
            for (String string : strings) {
                String[] split = string.trim().split(" ");
                int catNum = Integer.parseInt(split[0]);
                switch (catNum) {
                    case 1:
                        cloths.add(split[1]);
                        break;
                    case 2:
                        jiaju.add(split[1]);
                        break;
                    case 3:
                        eduSports.add(split[1]);
                        break;
                    case 4:
                        ruiqi.add(split[1]);
                        break;
                    case 5:
                        toy.add(split[1]);
                        break;
                    case 6:
                        elec.add(split[1]);
                        break;
                    case 7:
                        other.add(split[1]);
                        break;
                    default:
                        other.add(split[1]);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashSet<String> getCloths() {
        return cloths;
    }

    public static HashSet<String> getJiaju() {
        return jiaju;
    }

    public static HashSet<String> getEduSports() {
        return eduSports;
    }

    public static HashSet<String> getRuiqi() {
        return ruiqi;
    }

    public static HashSet<String> getToy() {
        return toy;
    }

    public static HashSet<String> getElec() {
        return elec;
    }

    public static HashSet<String> getOther() {
        return other;
    }

    public static void main(String[] args) {
        HashSet<String> cloths = ProductCatUtil.getJiaju();
        cloths.forEach(System.out::println);
    }
}
