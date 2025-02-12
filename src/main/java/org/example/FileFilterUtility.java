package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;  // Этот импорт добавляем для работы с Files
import java.nio.file.Paths; // Этот импорт добавляем для работы с путями файлов
import java.util.*;

public class FileFilterUtility {
    private final List<String> inputFiles;
    private String outputDir = "./"; // По умолчанию текущая папка
    private String prefix = "";
    private boolean appendMode = false;
    private boolean fullStats = false;

    // Статистика
    private int intCount = 0;
    private int floatCount = 0;
    private int stringCount = 0;

    private int minInt = Integer.MAX_VALUE;
    private int maxInt = Integer.MIN_VALUE;
    private double intSum = 0;
    private double intAverage = 0;

    private int minLength = Integer.MAX_VALUE;
    private int maxLength = Integer.MIN_VALUE;

    // Конструктор
    public FileFilterUtility(List<String> inputFiles) {
        this.inputFiles = inputFiles != null ? inputFiles : new ArrayList<>();
        this.outputDir = Paths.get("C:\\Users\\Пользователь\\IdeaProjects\\laba1").toAbsolutePath().toString();
    }

    // Установить выходной каталог
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    // Установить префикс
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    // Установить режим добавления
    public void setAppendMode(boolean appendMode) {
        this.appendMode = appendMode;
    }

    // Установить полноту статистики
    public void setFullStats(boolean fullStats) {
        this.fullStats = fullStats;
    }

    // Добавить входной файл
    public void addInputFile(String fileName) {
        this.inputFiles.add(fileName);
    }

    // Обработка файлов
    public void processFiles() {
        // Проходим по каждому файлу
        for (String fileName : inputFiles) {
            File file = new File(fileName);
            if (!file.exists()) {
                System.out.println("File not found: " + fileName);
                continue;
            }

            // Добавляем вывод содержимого файла
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                System.out.println("Contents of " + fileName + ": ");
                for (String line : lines) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + fileName);
            }

            // Далее идет обработка данных файла
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Проверяем, к какому типу данных относится строка
                    try {
                        if (line.matches("-?\\d+")) {
                            processInteger(Integer.parseInt(line));
                        } else if (line.matches("-?\\d*\\.\\d+")) {
                            processFloat(Double.parseDouble(line));
                        } else {
                            processString(line);
                        }
                    } catch (NumberFormatException e) {
                        processString(line);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + fileName);
            }
        }

        // Печать статистики
        printStatistics();

        // Запись данных в файлы
        writeToFile("integers.txt", intCount, appendMode, "Integer", minInt, maxInt, intSum, intAverage);
        System.out.println("Number of floats to write: " + floatCount); // Добавим количество чисел с плавающей точкой

        // Теперь записываем данные о float в отдельный файл
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputDir + "/floats.txt", appendMode), StandardCharsets.UTF_8))) {
            writer.write("Number of floats: " + floatCount + "\n"); // Запишем количество вещественных чисел
            writer.write("Sum of floats: " + intSum + "\n"); // Запишем сумму вещественных чисел
            writer.write("Average of floats: " + (floatCount > 0 ? intSum / floatCount : 0) + "\n"); // Среднее значение, если есть вещественные числа
        } catch (IOException e) {
            System.out.println("Error writing to floats.txt");
            e.printStackTrace();
        }

        writeToFile("test.txt", floatCount, appendMode, "Float", 0, 0, 0, 0);
        writeToFile("strings.txt", stringCount, appendMode, "String", minLength, maxLength, 0, 0);
    }

    // Обработка целого числа
    private void processInteger(int number) {
        intCount++;  // Увеличиваем счетчик целых чисел
        intSum += number;  // Добавляем значение числа в сумму
        intAverage = intSum / intCount;  // Рассчитываем среднее значение

        // Обновляем минимальное и максимальное значения
        if (number < minInt) minInt = number;
        if (number > maxInt) maxInt = number;
    }

    // Обработка вещественного числа
    private void processFloat(double number) {
        floatCount++;  // Увеличиваем счетчик вещественных чисел
        System.out.println("Processing float: " + number);  // Для отладки
        intSum += number; // Добавляем в общую сумму
    }

    // Обработка строки
    private void processString(String line) {
        if (line.trim().isEmpty()) return;  // Игнорируем пустые строки

        stringCount++;  // Увеличиваем счетчик строк
        int length = line.length();
        if (length < minLength) minLength = length;
        if (length > maxLength) maxLength = length;
    }

    // Печать статистики
    private void printStatistics() {
        System.out.println("Statistics:");

        // Статистика для целых чисел
        if (fullStats) {
            System.out.println("Full statistics for integers:");
            System.out.println("Minimum: " + minInt);
            System.out.println("Maximum: " + maxInt);
            System.out.println("Sum: " + intSum);
            System.out.println("Average: " + intAverage);  // Используем intAverage
        } else {
            System.out.println("Summary statistics for integers:");
            System.out.println("Number of integers: " + intCount);  // Количество целых чисел
        }

        // Статистика для строк
        if (fullStats) {
            System.out.println("Full statistics for strings:");
            System.out.println("Minimum string length: " + minLength);
            System.out.println("Maximum string length: " + maxLength);
        } else {
            System.out.println("Summary statistics for strings:");
            System.out.println("Number of strings: " + stringCount);  // Количество строк
        }

        // Статистика для вещественных чисел
        if (fullStats) {
            System.out.println("Full statistics for floats:");
            System.out.println("Number of floats: " + floatCount);  // Количество вещественных чисел
        } else {
            System.out.println("Summary statistics for floats:");
            System.out.println("Number of floats: " + floatCount);  // Количество вещественных чисел
        }
    }


    // Запись в файл
    private void writeToFile(String fileName, int count, boolean appendMode, String type, int min, int max, double sum, double average) {
        if (count == 0) return;

        try {
            // Используем текущую папку проекта как выходную папку
            String fullFileName = outputDir + "/" + (prefix.isEmpty() ? "" : prefix) + fileName;
            System.out.println("Attempting to write to file: " + fullFileName); // Добавим вывод полного пути

            // Убедимся, что папка существует
            File outputDirFile = new File(outputDir);
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs();  // Создаём папку, если она не существует
            }

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullFileName, appendMode), StandardCharsets.UTF_8));

            if (type.equals("Integer")) {
                writer.write("Minimum: " + min + "\n");
                writer.write("Maximum: " + max + "\n");
                writer.write("Sum: " + sum + "\n");
                writer.write("Average: " + average + "\n");
            } else if (type.equals("Float")) {
                writer.write("Number of floats: " + count + "\n"); // Для вещественных чисел
            } else if (type.equals("String")) {
                writer.write("Minimum string length: " + min + "\n");
                writer.write("Maximum string length: " + max + "\n");
            }

            writer.close();
            System.out.println("Data successfully written to " + fullFileName); // Сообщение об успешной записи
        } catch (IOException e) {
            System.out.println("Error writing to file: " + fileName);
            e.printStackTrace(); // Печатаем стек ошибок для диагностики
        }
    }
}
