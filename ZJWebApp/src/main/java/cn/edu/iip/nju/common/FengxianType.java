package cn.edu.iip.nju.common;

/**
 * Created by xu on 2017/9/21.
 */
public enum FengxianType {
    机械伤害("fengxian0"),
    化学伤害("fengxian1"),
    电气伤害("fengxian2"),
    标识缺陷("fengxian3"),
    其它("fengxian4");
    private String name;
    FengxianType(String  name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
