package cn.edu.iip.nju.common;

/**
 * Created by xu on 2017/9/21.
 */
public enum InjureLevel {
    LEVELONE("第0序列"),
    LEVELTWO("第1序列"),
    LEVELTHREE("第2序列");
    private String name;
    InjureLevel(String  name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
