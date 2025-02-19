package org.example;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

class FileFilterUtilityTest {

    private static final String OUTPUT_DIR = "testOutput/";
    private FileFilterUtility utility;

    @BeforeAll
    static void setUp() throws IOException {
        Files.createDirectories(Paths.get(OUTPUT_DIR));
    }

    @AfterAll
    static void tearDown() throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(OUTPUT_DIR))) {
            paths.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @BeforeEach
    void init() {
        utility = new FileFilterUtility(new ArrayList<>());
    }

    @Test
    void testIsInteger() {
        assertTrue(FileFilterUtility.isInteger("123"));
        assertFalse(FileFilterUtility.isInteger("abc"));
    }

    @Test
    void testReadIntegers() throws IOException {
        // Создаем тестовые файлы с данными
        String inputFile1 = "file1.txt";
        String inputFile2 = "file2.txt";
        Files.write(Paths.get(inputFile1), "123\n456\nabc\n789\n".getBytes());
        Files.write(Paths.get(inputFile2), "100\n200\n300\nabc\n400\n".getBytes());

        // Читаем целые числа из обоих файлов
        List<Integer> integers1 = utility.readIntegers(inputFile1);
        List<Integer> integers2 = utility.readIntegers(inputFile2);

        // Проверяем результаты для первого файла
        assertEquals(3, integers1.size());
        assertTrue(integers1.contains(123));
        assertTrue(integers1.contains(456));
        assertTrue(integers1.contains(789));

        // Проверяем результаты для второго файла
        assertEquals(4, integers2.size());
        assertTrue(integers2.contains(100));
        assertTrue(integers2.contains(200));
        assertTrue(integers2.contains(300));
        assertTrue(integers2.contains(400));

        // Удаляем тестовые файлы
        new File(inputFile1).delete();
        new File(inputFile2).delete();
    }

    @Test
    void testWriteToFile() throws IOException {
        String fileName = OUTPUT_DIR + "testOutput.txt";
        List<Integer> data = Arrays.asList(1, 2, 3);

        // Подсчитайте значения для параметров
        int size = data.size();
        boolean append = false;
        String type = "Integer";
        int min = Collections.min(data);
        int max = Collections.max(data);
        double sum = data.stream().mapToInt(i -> i).sum(); // Сумма всех значений
        double average = data.stream().mapToInt(i -> i).average().orElse(0.0); // Среднее значение

        // Обновляем вызов метода writeToFile
        utility.writeToFile(fileName, size, append, type, min, max, sum, average);

        // Проверяем существование файла
        assertTrue(Files.exists(Paths.get(fileName)), "Output file was not created.");

        List<String> lines = Files.readAllLines(Paths.get(fileName));
        assertEquals(5, lines.size()); // Изменено на 5, так как теперь 5 строк
        assertEquals("Integer count: 3", lines.get(0)); // Проверка на правильность первого значения
        assertEquals("Min: 1", lines.get(1));
        assertEquals("Max: 3", lines.get(2));
        assertEquals("Sum: 6.0", lines.get(3)); // Проверка на сумму
        assertEquals("Average: 2.0", lines.get(4)); // Проверка на среднее

        new File(fileName).delete();
    }

    @Test
    void testStatistics() {
        Statistics stats = utility.calculateStatistics(Arrays.asList(1, 2, 3, 4, 5));

        assertEquals(5, stats.getCount());
        assertEquals(1, stats.getMin());
        assertEquals(5, stats.getMax());
        assertEquals(3.0, stats.getAverage());
    }

    @Test
    void testErrorHandling() {
        Exception exception = assertThrows(IOException.class, () -> {
            utility.readIntegers("nonExistentFile.txt");
        });

        assertEquals("File not found: nonExistentFile.txt", exception.getMessage());
    }
}

