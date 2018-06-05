package cn.edu.iip.nju.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.ToString;
import org.assertj.core.util.Sets;
import org.assertj.core.util.Strings;

import java.util.*;

@Data
@ToString
public class NegProduct implements Comparable<NegProduct> {
    private String productName;
    private List<InjureCase> injureCases = Lists.newArrayList();
    private Integer caseNum;
    private Set<String> area = Sets.newHashSet();
    private String areaString;
    private String mainReason;
    private Integer injureIndex;

    @Override
    public int compareTo(NegProduct o) {
        return this.caseNum.compareTo(o.caseNum);
    }

    public void increaseCaseNum() {
        this.caseNum++;
    }

    /**
     * 伤害指数计算：地点30分
     * 伤害程度70 5 3 1
     */
    public void computeInjureIndex() {
        double index = 0;
        int size = area.size();
        if (size <= 2) {
            index += 10;
        } else if (size > 2 && size <= 5) {
            index += 20;
        } else {
            index += 30;
        }
        double total = 5 * injureCases.size();
        double part = 0;
        TreeMap<Integer, String> treeMap = Maps.newTreeMap();
        HashMap<String, Integer> hashMap = Maps.newHashMap();
        for (InjureCase injureCase : injureCases) {
            if ("1".equals(injureCase.getInjureDegree())) {
                part += 5;
            } else if ("2".equals(injureCase.getInjureDegree())) {
                part += 3;
            } else {
                part += 1;
            }
            if (Strings.isNullOrEmpty(injureCase.getInjureType())) {
                injureCase.setInjureType("");
            }
            String[] types = injureCase.getInjureType().split(" ");
            for (String type : types) {
                if (!hashMap.containsKey(type)) {
                    hashMap.put(type, 1);
                } else {
                    hashMap.put(type, hashMap.get(type) + 1);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            treeMap.put(entry.getValue(), entry.getKey());
        }
        Integer[] arr = new Integer[treeMap.size()];
        treeMap.keySet().toArray(arr);
        Arrays.sort(arr);
        int key = 0;
        if(arr.length <= 4){
            key = arr[0];
        }else{
            key = arr[arr.length-4];
        }
        SortedMap<Integer, String> map = treeMap.tailMap(key);
        this.setMainReason(Strings.join(map.values()).with(" "));


        index += part / total * 70;
        setInjureIndex((int) index);
    }

}