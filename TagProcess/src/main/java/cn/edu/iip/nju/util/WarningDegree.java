package cn.edu.iip.nju.util;

public class WarningDegree {
	private static double[] weight = {9.84017782e-01,2.34856197e-05,1,1.77765615e-01,1.04110485e-02};
	
	public static double warningIndex(Long ... properties) throws Exception{
		double index = 0;
		if(properties.length < weight.length) {
			throw new Exception("less properties!");
		}
		for(int i = 0; i < weight.length; i++){
			index += weight[i] * properties.length;
		}
		return index;
	}
	
	public static double warningScore(Long ... properties) throws Exception {
		double index = warningIndex(properties);
		double tmp = Math.exp(index);
		double score = 0;
		if(Double.isInfinite(tmp)) {
			score = 100;
		} else {
			score = tmp / (1 + tmp) * 100;
		}
		return score;
	}
	
	public static String warningDegree(Long ... properties) throws Exception{
		double score = warningScore(properties);
		if (score >= 90) {
			return "红色预警";
		} else if (score >= 80) {
			return "橙色预警";
		} else if (score >= 70) {
			return "黄色预警";
		} else {
			return "蓝色预警";
		}
	}
}
