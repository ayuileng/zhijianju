package cn.edu.iip.nju.common;


import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by xu on 2017/10/19.
 */
public class HospitalEnum {
    private static final Map<Integer,String> huji = Maps.newHashMap();
    private static final Map<Integer,String> educationDegree = Maps.newHashMap();
    private static final Map<Integer,String> vocation = Maps.newHashMap();
    private static final Map<Integer,String> injureReason = Maps.newHashMap();
    private static final Map<Integer,String> injureLocation = Maps.newHashMap();//受伤地点
    private static final Map<Integer,String> activityWhenInjure = Maps.newHashMap();
    private static final Map<Integer,String> ifIntentional = Maps.newHashMap();
    private static final Map<Integer,String> alcohol = Maps.newHashMap();
    private static final Map<Integer,String> injureType = Maps.newHashMap();
    private static final Map<Integer,String> injureSite = Maps.newHashMap();//伤害部位
    private static final Map<Integer,String> injureSystem = Maps.newHashMap();//伤害系统
    private static final Map<Integer,String> injureDegree = Maps.newHashMap();//伤害严重程度
    private static final Map<Integer,String> injureResult = Maps.newHashMap();//伤害结局
    private static final Map<Integer,String> howGetInjure = Maps.newHashMap();//发生伤害与使用产品的关系

    public static Map<Integer,String> getHuji(){
        huji.put(1,"本市/县");
        huji.put(2,"本省外地");
        huji.put(3,"外省");
        huji.put(4,"外籍");
        return huji;
    }
    public static Map<Integer,String> getEducationDegree(){
        educationDegree.put(1,"未上学儿童");
        educationDegree.put(2,"文盲、半文盲");
        educationDegree.put(3,"小学");
        educationDegree.put(4,"初中");
        educationDegree.put(5,"高中或中专");
        educationDegree.put(6,"大专");
        educationDegree.put(7,"大学及以上");
        return educationDegree;
    }
    public static Map<Integer,String> getVocation(){
        vocation.put(1,"学龄前儿童");
        vocation.put(2,"在校学生");
        vocation.put(3,"家务");
        vocation.put(4,"待业");
        vocation.put(5,"离退休人员");
        vocation.put(6,"专业技术人员");
        vocation.put(7,"办事人员和有关人员");
        vocation.put(8,"商业、服务业人员");
        vocation.put(9,"农牧渔水利业生产人员");
        vocation.put(10,"生产运输设备操作人员及有关人员");
        vocation.put(11,"军人");
        vocation.put(12,"其他/不清楚");
        return vocation;
    }
    public static Map<Integer,String> getInjureReason(){
        injureReason.put(1,"机动车车祸");
        injureReason.put(2,"非机动车车祸");
        injureReason.put(3,"跌倒/坠落");
        injureReason.put(4,"钝器伤");
        injureReason.put(5,"火器伤");
        injureReason.put(6,"刀/锐器伤");
        injureReason.put(7,"烧烫伤");
        injureReason.put(8,"窒息/悬吊");
        injureReason.put(9,"溺水");
        injureReason.put(10,"中毒");
        injureReason.put(11,"动物伤");
        injureReason.put(12,"性侵犯");
        injureReason.put(13,"不清楚/其他");
        return injureReason;
    }
    public static Map<Integer,String> getInjureLocation(){
        injureLocation.put(1,"家中");
        injureLocation.put(2,"公共居住场所");
        injureLocation.put(3,"学校与公共场所");
        injureLocation.put(4,"体育和运动场所");
        injureLocation.put(5,"公路/街道");
        injureLocation.put(6,"贸易和服务场所");
        injureLocation.put(7,"工业和建筑场所");
        injureLocation.put(8,"农田/农场");
        injureLocation.put(9,"其他/不清楚");
        return injureLocation;
    }
    public static Map<Integer,String> getActivityWhenInjure(){
        activityWhenInjure.put(1,"工作");
        activityWhenInjure.put(2,"家务");
        activityWhenInjure.put(3,"学习");
        activityWhenInjure.put(4,"体育活动");
        activityWhenInjure.put(5,"休闲活动");
        activityWhenInjure.put(6,"生命活动");
        activityWhenInjure.put(7,"驾乘交通工具");
        activityWhenInjure.put(8,"步行");
        activityWhenInjure.put(9,"其他/不清楚");
        return activityWhenInjure;
    }
    public static Map<Integer,String> getIfIntentional(){
        ifIntentional.put(1,"非故意（意外事故）");
        ifIntentional.put(2,"自残/自杀");
        ifIntentional.put(3,"故意（暴力、攻击）");
        ifIntentional.put(4,"不清楚/其他");
        return ifIntentional;
    }
    public static Map<Integer,String> getAlcohol(){
        alcohol.put(1,"饮用");
        alcohol.put(2,"未饮用");
        alcohol.put(3,"不清楚");
        return alcohol;
    }
    public static Map<Integer,String> getInjureType(){
        injureType.put(1,"骨折");
        injureType.put(2,"扭伤/拉伤");
        injureType.put(3,"锐器伤、咬伤、开放伤");
        injureType.put(4,"挫伤、擦伤");
        injureType.put(5,"烧烫伤");
        injureType.put(6,"脑震荡、脑挫裂伤");
        injureType.put(7,"内脏器官伤");
        injureType.put(8,"其他/不清楚");
        return injureType;
    }
    public static Map<Integer,String> getInjureSite(){
        injureSite.put(1,"头部");
        injureSite.put(2,"上肢");
        injureSite.put(3,"下肢");
        injureSite.put(4,"躯干");
        injureSite.put(5,"多部位");
        injureSite.put(6,"全身广泛受伤");
        injureSite.put(7,"其他/不清楚");
        return injureSite;
    }
    public static Map<Integer,String> getInjureSystem(){
        injureSystem.put(1,"中枢神经系统");
        injureSystem.put(2,"呼吸系统");
        injureSystem.put(3,"消化系统");
        injureSystem.put(4,"泌尿生殖系统");
        injureSystem.put(5,"运动系统");
        injureSystem.put(6,"多系统");
        injureSystem.put(7,"其他/不清楚");
        return injureSystem;
    }
    public static Map<Integer,String> getInjureDegree(){
        injureDegree.put(1,"轻度");
        injureDegree.put(2,"中度");
        injureDegree.put(3,"重度");
        return injureDegree;
    }
    public static Map<Integer,String> getInjureResult(){
        injureResult.put(1,"处理后离院");
        injureResult.put(2,"留观");
        injureResult.put(3,"转院");
        injureResult.put(4,"住院");
        injureResult.put(5,"死亡");
        injureResult.put(6,"其他");
        return injureResult;
    }
    public static Map<Integer,String> getHowGetInjure(){
        howGetInjure.put(1,"使用不当");
        howGetInjure.put(2,"与产品质量有关");
        howGetInjure.put(3,"像往常一样使用却突发事故");
        howGetInjure.put(4,"不确定");
        howGetInjure.put(5,"其他");
        return howGetInjure;
    }
}
