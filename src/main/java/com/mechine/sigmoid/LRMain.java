package com.mechine.sigmoid;

import java.io.IOException;

public class LRMain {
    public static void main(String[] args) throws IOException {
        // filename
        String filename = "/Users/jiangfei/data1.txt";

        // 导入样本特征和标签
        double[][] feature = LoadData.Loadfeature(filename);
        // 标签
        double[] Label = LoadData.LoadLabel(filename);

        // 参数设置
        int samNum = feature.length;
        int paraNum = feature[0].length;

        double rate = 0.001;
        int maxCycle = 1000;

        // LR模型训练
        LRtrainGradientDescent LR = new LRtrainGradientDescent();
        // a b c
        double[] W = LR.Updata(feature, Label, maxCycle, rate);
        //保存模型9
        String model_path = "/Users/jiangfei/wrights1.txt";
        SaveModel.savemodel(model_path, W);
        //模型测试
        double[] pre_results = LRTest.lrtest(paraNum, samNum, feature, W);
        //保存测试结果
        String results_path = "/Users/jiangfei/pre_results1.txt";
        SaveModel.saveresults(results_path, pre_results);
        for (int i = 1; i <= 16; i++) {
            double[] f = {7, i, 1};
            System.out.println(lrtest(f, W));
        }


    }

    public static double lrtest(double[] feature, double[] W) {
        //for (int i = 0; i < samNum; i++) {
        double tmp = 0;
        for (int j = 0; j < 3; j++) {
            tmp += feature[j] * W[j];
        }
        return tmp;
        //}

    }
}
