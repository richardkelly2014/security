package com.mechine.sigmoid;

/*
 * 使用训练好的LR模型对样本进行预测
 */
public class LRTest {
    public static double[] lrtest(int paraNum, int samNum, double[][] feature, double[] W) {
        double[] pre_results = new double[samNum];
        for (int i = 0; i < samNum; i++) {
            double tmp = 0;
            for (int j = 0; j < paraNum; j++) {
                tmp += feature[i][j] * W[j];
            }
            if (tmp >= 0.5) {
                pre_results[i] = 1;
            } else {
                pre_results[i] = 0;
            }
        }
        return pre_results;
    }

}
