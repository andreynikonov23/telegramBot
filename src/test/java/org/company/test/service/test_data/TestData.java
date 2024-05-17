package org.company.test.service.test_data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class TestData {
    public static long getTestChatId() {
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(TestData.class.getResourceAsStream("/test.properties")))) {
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String chatIdStr = properties.getProperty("chat");
        return Integer.parseInt(chatIdStr);
    }

    public static String getTestResultMessageTxt() {
        return "1. QuestionTest1:\n" +
                "A\n" +
                "B\n" +
                "C\n" +
                "D\n" +
                "Вы ответили: b. Это не правильный ответ. Правильный ответ: c\n" +
                "\n" +
                "2. QuestionTest2:\n" +
                "A\n" +
                "B\n" +
                "C\n" +
                "Вы ответили: a. Это правильно.\n" +
                "\n" +
                "3. QuestionTest3:\n" +
                "Вы ответили: test. Это правильно.\n" +
                "\n" +
                "Правильных ответов: 2 из 3";
    }
}
