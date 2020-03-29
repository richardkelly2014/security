package com.mechine;

public class Sample {
    double[] features;
    int feaNum; // the number of sample's features
    double value; // value of sample in regression
    int label; // class of sample

    public Sample(int number) {
        feaNum = number;
        features = new double[feaNum];
    }

    public void outSample() {
        System.out.println("The sample's features are:");
        for (int i = 0; i < feaNum; i++) {
            System.out.print(features[i] + " ");
        }
        System.out.println();
        System.out.println("The label is: " + label);
        System.out.println("The value is: " + value);
    }
}
