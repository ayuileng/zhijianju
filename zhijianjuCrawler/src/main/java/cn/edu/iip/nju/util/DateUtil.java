package cn.edu.iip.nju.util;

import com.google.common.base.Strings;
import org.assertj.core.util.Lists;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {
    /**
     * 表示连续的8位日期，例如http://www.baidu.com/20140311/2356.html
     */
    private static String url_reg_whole = "([-|/|_]{1}20\\d{6})";
    /**
     * 表示 用-或者/隔开的日期,有年月日的，例如 http://www.baidu.com/2014-3-11/2356.html
     */
    private static String url_reg_sep_ymd = "([-|/|_]{1}20\\d{2}[-|/|_]{1}\\d{1,2}[-|/|_]{1}\\d{1,2})";
    /**
     * 表示 用-或者/隔开的日期,只有年和月份的，例如 http://www.baidu.com/2014-3/2356.html
     */
    private static String url_reg_sep_ym = "([-|/|_]{1}20\\d{2}[-|/|_]{1}\\d{1,2})";
    /**
     * 格式正确的时间正则表达式
     */
    private static String rightTimeReg = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";


    private static Calendar current = Calendar.getInstance();

    public static String extractDateFromContent(String content) {
        List<String> list = Lists.newArrayList();
        String str = content.replaceAll("r?n", " ");
//        Pattern p_detail = Pattern.compile("(20\\d{2}[-/]\\d{1,2}[-/]\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2})|(20\\d{2}年\\d{1,2}月\\d{1,2}日)", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
        Pattern p = Pattern.compile("(20\\d{2}[-/]\\d{1,2}[-/]\\d{1,2})|(20\\d{2}年\\d{1,2}月\\d{1,2}日)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
//        Matcher matcher_detail = p_detail.matcher(str);
        Matcher matcher = p.matcher(str);
        if (matcher.find(0) && matcher.groupCount() >= 1) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                if (!Strings.isNullOrEmpty(matcher.group(i))) {
                    list.add(matcher.group(i));
                }
            }
        }
        String date;
        if (list.size() == 0) {
            return null;
        } else {
            String s = list.get(0);
            date = s.replace("/", "-").replace("年", "-").replace("月", "-").replace("日", "");

        }
        return date;
    }


    public static Date getDate(String content){
        String dateStr = extractDateFromContent(content);
        if(Strings.isNullOrEmpty(dateStr)){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar calendar = Calendar.getInstance();
            Date date = sdf.parse(dateStr);
            calendar.setTime(date);
            //不能比当前日期晚
            if (current.compareTo(calendar)>=0) {
                return date;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }
    @Test
    public void test() throws Exception {

        String content = "日期:20100517作者：记者 车少远来源";
        String s = extractDateFromContent(content);
        System.out.println(s);
    }


}
