package com.mechine;

public abstract class Regression {

    double[] theta; //parameters
    int paraNum; //the number of parameters
    double rate; //learning rate

    Sample[] sam; // samples
    int samNum; // the number of samples
    double th; // threshold value

    public void Initialize(Sample[] s, int num) {
        samNum = num;
        sam = new Sample[samNum];
        for (int i = 0; i < samNum; i++) {
            sam[i] = s[i];
        }
    }

    public void setPara(double[] para, double learning_rate, double threshold) {
        paraNum = para.length;
        theta = para;
        rate = learning_rate;
        th = threshold;
    }

    public abstract double PreVal(Sample s);

    public abstract double CostFun();


    public abstract void Update();

    public void OutputTheta() {
        System.out.println("The parameters are:");
        for (int i = 0; i < paraNum; i++) {
            System.out.print(theta[i] + " ");
        }
        System.out.println(CostFun());
    }

}
