package cn.edu.iip.nju.util;

import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InjureLevelUtil {
	private static String[] injureLevelOneArray = {"死亡","残疾","致残","截肢","瘫痪",
	"窒息","伤残","爆炸","火灾","骨折","失明","甲醛","休克"};
	private static String[] injureLevelTwoArray = {"住院","抢救","急救","着火","烧伤","灼伤","灼烧","烫伤",
	"起火","燃烧","着火","视力","听力","神经","生殖","细菌","门诊","就医",
	"流血","损伤","中毒","触电","电击","挫伤","扭伤","刺伤"};
	
	public static String  checkInjureLevel(String str) {
		if(Strings.isNullOrEmpty(str)) return "3";
		String[] strArr = str.split(" ");
		
		List<String> injureLevelOneList = Arrays.asList(injureLevelOneArray);
		List<String> injureLevelTwoList = Arrays.asList(injureLevelTwoArray);
		
		Set<String> injureLevelOneSet = new HashSet<String>(injureLevelOneList);
		Set<String> injureLevelTwoSet = new HashSet<String>(injureLevelTwoList);
		
		Boolean labelInjureOne = false;
		Boolean labelInjureTwo = false;
		
		for(int i = 0; i < strArr.length; i++) {
			if(injureLevelOneSet.contains(strArr[i])){
				labelInjureOne = true;
			} else if (injureLevelTwoSet.contains(strArr[i])){
				labelInjureTwo = true;
			}
		}
		if(labelInjureOne) {
			return "1";
		} else if(labelInjureTwo) {
			return "2";
		} else {
			return "3";
		}
		
	}
}
