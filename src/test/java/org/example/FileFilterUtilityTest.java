package org.example;

public class Statistics {
    private final int count;
    private final int min;
    private final int max;
    private final double average;

    public Statistics(int count, int min, int max, double average) {
        this.count = count;
        this.min = min;
        this.max = max;
        this.average = average;
    }

    public int getCount() {
        return count;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public double getAverage() {
        return average;
    }
}
