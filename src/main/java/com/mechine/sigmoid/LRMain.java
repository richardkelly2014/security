package com.mechine.sigmoid;

import java.io.IOException;

public class LRMain {
    public static void main(String[] args) throws IOException {
        // filename
        String filename = "/Users/jiangfei/data.txt";
        // 导入样本特征和标签
        double[][] feature = LoadData.Loadfeature(filename);
        double[] Label = LoadData.LoadLabel(filename);
        // 参数设置
        int samNum = feature.length;
        int paraNum = feature[0].length;
        double rate = 0.01;
        int maxCycle = 1000;
        // LR模型训练
        LRtrainGradientDescent LR = new LRtrainGradientDescent(feature, Label, paraNum, rate, samNum, maxCycle);
        double[] W = LR.Updata(feature, Label, maxCycle, rate);
        //保存模型
        String model_path = "/Users/jiangfei/wrights.txt";
        SaveModel.savemodel(model_path, W);
        //模型测试
        double[] pre_results = LRTest.lrtest(paraNum, samNum, feature, W);
        //保存测试结果
        String results_path = "/Users/jiangfei/pre_results.txt";
        SaveModel.saveresults(results_path, pre_results);
    }
}
