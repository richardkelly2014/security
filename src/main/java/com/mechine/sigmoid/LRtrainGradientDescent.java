package com.mechine.sigmoid;

/*
 * 使用梯度下降算法训练LR模型
 */
public class LRtrainGradientDescent {

    int paraNum; //权重参数的个数
    double rate; //学习率
    int samNum; //样本个数
    double[][] feature; //样本特征矩阵
    double[] Label;//样本标签
    int maxCycle; //最大迭代次数

    public LRtrainGradientDescent(double[][] feature,
                                  double[] Label,
                                  int paraNum, double rate,
                                  int samNum, int maxCycle) {
        this.feature = feature;
        this.Label = Label;
        this.maxCycle = maxCycle;
        this.paraNum = paraNum;
        this.rate = rate;
        this.samNum = samNum;
    }

    // 权值矩阵初始化
    public double[] ParaInitialize(int paraNum) {
        double[] W = new double[paraNum];
        for (int i = 0; i < paraNum; i++) {
            W[i] = 1.0;
        }
        return W;
    }

    //计算每次迭代后的预测误差
    public double[] PreVal(int samNum, int paraNum, double[][] feature, double[] W) {
        double[] Preval = new double[samNum];
        for (int i = 0; i < samNum; i++) {
            double tmp = 0;
            for (int j = 0; j < paraNum; j++) {
                tmp += feature[i][j] * W[j];
            }
            Preval[i] = Sigmoid.sigmoid(tmp);
        }
        return Preval;
    }

    //计算误差率
    public double error_rate(int samNum, double[] Label, double[] Preval) {
        double sum_err = 0.0;
        for (int i = 0; i < samNum; i++) {
            sum_err += Math.pow(Label[i] - Preval[i], 2);
        }
        return sum_err;
    }

    //LR模型训练
    public double[] Updata(double[][] feature, double[] Label, int maxCycle, double rate) {
        // 先计算样本个数和特征个数
        int samNum = feature.length;
        int paraNum = feature[0].length;

        //初始化权重矩阵
        double[] W = ParaInitialize(paraNum);

        // 循环迭代优化权重矩阵
        for (int i = 0; i < maxCycle; i++) {
            // 每次迭代后，样本预测值
            double[] Preval = PreVal(samNum, paraNum, feature, W);
            double sum_err = error_rate(samNum, Label, Preval);
            if (i % 10 == 0) {
                System.out.println("第" + i + "次迭代的预测误差为:" + sum_err);
            }
            //预测值与标签的误差
            double[] err = new double[samNum];
            for (int j = 0; j < samNum; j++) {
                err[j] = Label[j] - Preval[j];
            }
            // 计算权重矩阵的梯度方向
            double[] Delt_W = new double[paraNum];
            for (int n = 0; n < paraNum; n++) {
                double tmp = 0;
                for (int m = 0; m < samNum; m++) {
                    tmp += feature[m][n] * err[m];
                }
                Delt_W[n] = tmp / samNum;
            }

            for (int m = 0; m < paraNum; m++) {
                W[m] = W[m] + rate * Delt_W[m];
            }
        }
        return W;
    }

}
