package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FileFilterUtility {
    private final List<String> inputFiles;
    private String outputDir = "./";
    private String prefix = "";
    private boolean appendMode = false;
    private boolean fullStats = false;

    private int intCount = 0;
    private int floatCount = 0;
    private int stringCount = 0;

    private int minInt = Integer.MAX_VALUE;
    private int maxInt = Integer.MIN_VALUE;
    private double intSum = 0;
    private double intAverage = 0;

    private int minLength = Integer.MAX_VALUE;
    private int maxLength = Integer.MIN_VALUE;

    public FileFilterUtility(List<String> inputFiles) {
        this.inputFiles = inputFiles != null ? inputFiles : new ArrayList<>();
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setAppendMode(boolean appendMode) {
        this.appendMode = appendMode;
    }

    public void setFullStats(boolean fullStats) {
        this.fullStats = fullStats;
    }

    public void addInputFile(String fileName) {
        this.inputFiles.add(fileName);
    }

    public void processFiles() {
        for (String fileName : inputFiles) {
            File file = new File(fileName);
            if (!file.exists()) {
                System.out.println("File not found: " + fileName);
                continue;
            }

            try {
                List<String> lines = Files.readAllLines(file.toPath());
                for (String line : lines) {
                    if (isInteger(line)) {
                        processInteger(Integer.parseInt(line));
                    } else if (line.matches("-?\\d*\\.\\d+")) {
                        processFloat(Double.parseDouble(line));
                    } else {
                        processString(line);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + fileName);
            }
        }

        printStatistics();

        writeToFile("integers.txt", intCount, appendMode, "Integer", minInt, maxInt, intSum, intAverage);
        writeToFile("floats.txt", floatCount, appendMode, "Float", 0, 0, intSum, 0);
        writeToFile("strings.txt", stringCount, appendMode, "String", minLength, maxLength, 0, 0);
    }

    public List<Integer> readIntegers(String fileName) throws IOException {
        List<Integer> integers = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        for (String line : lines) {
            if (isInteger(line)) {
                integers.add(Integer.parseInt(line));
            }
        }
        return integers;
    }

    public Statistics calculateStatistics(List<Integer> integers) {
        if (integers.isEmpty()) {
            return new Statistics(0, 0, 0, 0);
        }
        int count = integers.size();
        int min = Collections.min(integers);
        int max = Collections.max(integers);
        double sum = integers.stream().mapToInt(Integer::intValue).sum();
        double average = sum / count;

        return new Statistics(count, min, max, average);
    }

    private void processInteger(int number) {
        intCount++;
        intSum += number;
        intAverage = intSum / intCount;

        if (number < minInt) minInt = number;
        if (number > maxInt) maxInt = number;
    }

    private void processFloat(double number) {
        floatCount++;
        intSum += number; // Обновляем сумму целых чисел (можно разделить на floatSum, если нужно)
    }

    private void processString(String line) {
        if (line.trim().isEmpty()) return;

        stringCount++;
        int length = line.length();
        if (length < minLength) minLength = length;
        if (length > maxLength) maxLength = length;
    }

    private void printStatistics() {
        System.out.println("Statistics:");
        // Вывод статистики как было ранее...
    }

    private void writeToFile(String fileName, int count, boolean appendMode, String type, int min, int max, double sum, double average) {
        if (count == 0) return;
        // Логика записи в файл как была...
    }

    public static boolean isInteger(String input) {
        return input != null && input.matches("-?\\d+");
    }
}
