package com.mechine;

public class LinearRegression extends Regression {

    public double PreVal(Sample s) {
        double val = 0;
        for (int i = 0; i < paraNum; i++) {
            val += theta[i] * s.features[i];
        }
        return val;
    }

    public double CostFun() {
        double sum = 0;
        for (int i = 0; i < samNum; i++) {
            double d = PreVal(sam[i]) - sam[i].value;
            sum += Math.pow(d, 2);
        }
        return sum / (2 * samNum);
    }

    public void Update() {
        double former = 0; // the cost before update
        double latter = CostFun(); // the cost after update
        double[] p = new double[paraNum];
        do {
            former = latter;
            for (int i = 0; i < paraNum; i++) {
                // for theta[i]
                double d = 0;
                for (int j = 0; j < samNum; j++) {
                    d += (PreVal(sam[j]) - sam[j].value) * sam[j].features[i];
                }
                p[i] -= (rate * d) / samNum;
            }
            theta = p;
            latter = CostFun();
            if (former - latter < 0) {
                break;
            }
        } while (former - latter > th);
    }

    public double test(Sample s){

        double val = 0;
        for (int i = 0; i < paraNum; i++) {
            val += theta[i] * s.features[i];
        }

        return val;
    }
}
