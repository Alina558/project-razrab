package org.example;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Сначала создаем объект для фильтрации
        FileFilterUtility utility = new FileFilterUtility(new ArrayList<>());
        System.out.println("Program started with args: " + Arrays.toString(args));

        // Обработка аргументов командной строки
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o":
                    if (i + 1 < args.length) {
                        utility.setOutputDir(args[i + 1]);
                        i++; // Пропускаем аргумент, так как мы его уже обработали
                    }
                    break;
                case "-p":
                    if (i + 1 < args.length) {
                        utility.setPrefix(args[i + 1]);
                        i++;
                    }
                    break;
                case "-a":
                    utility.setAppendMode(true);
                    break;
                case "-s":
                    utility.setFullStats(false);
                    break;
                case "-f":
                    utility.setFullStats(true);
                    break;
                default:
                    utility.addInputFile(args[i]);  // Добавляем все остальные как файлы для обработки
                    break;
            }
        }

        // Обработка файлов
        try {
            utility.processFiles();
        } catch (IOException e) {
            System.out.println("Error processing files: " + e.getMessage());
        }
    }
}
