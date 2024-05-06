package org.company.service;

import org.company.bot.TelegramBot;
import org.company.config.SpringConfig;
import org.company.data.QuestionsLoader;
import org.company.model.AnswerType;
import org.company.model.Question;
import org.company.model.TaskTags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@ExtendWith(MockitoExtension.class)
public class TaskTest {
    @Mock
    private TelegramBot botMock;
    @Mock
    private QuestionsLoader questionsLoaderMock;
    @Mock
    private List<Question> questionsListMock;
    private Task task;

    @BeforeEach
    public void init(){
        task = new Task(botMock, questionsLoaderMock);
    }
    @Test
    public void loadQuestionsTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        task.setTag(TaskTags.IAN_IANG_TAG);

        Method method = Task.class.getDeclaredMethod("loadQuestions");
        method.setAccessible(true);
        method.invoke(task);
        Mockito.verify(questionsLoaderMock).getQuestionList(TaskTags.IAN_IANG_TAG);
    }

    @Test
    public void loadUnitQuestionsTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        task.setTag(TaskTags.UNIT);

        Method method = Task.class.getDeclaredMethod("loadQuestions");
        method.setAccessible(true);
        method.invoke(task);
        Mockito.verify(questionsLoaderMock).getRandomQuestionList();
    }

    @Test
    public void initOrderQuestionsTest() {
        task.setQuestions(questionsListMock);
        Mockito.doReturn(5).when(questionsListMock).size();
        List<Integer> orderQuestionTest = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            orderQuestionTest.add(i);
        }

        task.initOrderQuestions();
        ArrayList<Integer> orderQuestions = task.getORDER_QUESTIONS();
        Assertions.assertNotEquals(orderQuestionTest, orderQuestions);
        Collections.sort(orderQuestions);
        Assertions.assertEquals(orderQuestionTest, orderQuestions);
    }

    @Test
    public void sendQuestionTest() throws TelegramApiException {
        ArrayList<Integer> orderQuestions = new ArrayList<>();
        orderQuestions.add(0);

        Question question = new Question("chi-ci", "questionTxt", "answerA", "answerB", "answerC", "", "", "rightAnswer", new ArrayList<>(), AnswerType.CHOICE);
        Mockito.doReturn(question).when(questionsListMock).get(Mockito.anyInt());

        task.setORDER_QUESTIONS(orderQuestions);
        task.setQuestions(questionsListMock);
        task.sendQuestion();

        Mockito.verify(botMock).execute(Mockito.any(SendMessage.class));
    }

    @Test
    public void sendQuestionWithAudio() throws TelegramApiException {
        ArrayList<Integer> orderQuestions = new ArrayList<>();
        orderQuestions.add(0);

        List<String> mediaFiles = new ArrayList<>();
        mediaFiles.add("запись 1.mp3");
        Question question = new Question("chi-ci", "questionTxt", "answerA", "answerB", "answerC", "", "", "rightAnswer", mediaFiles, AnswerType.CHOICE);
        Mockito.doReturn(question).when(questionsListMock).get(Mockito.anyInt());

        task.setORDER_QUESTIONS(orderQuestions);
        task.setQuestions(questionsListMock);
        task.sendQuestion();

        Mockito.verify(botMock).execute(Mockito.any(SendMessage.class));
        Mockito.verify(botMock).execute(Mockito.any(SendVoice.class));
    }

    @Test
    public void sendAudioError() {
        TelegramBot bot = new AnnotationConfigApplicationContext(SpringConfig.class).getBean(TelegramBot.class);
        task.setBot(bot);
        task.setChatId(getTestChatId());

        ArrayList<Integer> orderQuestions = new ArrayList<>();
        orderQuestions.add(0);

        List<String> mediaFiles = new ArrayList<>();
        mediaFiles.add("non-existent file");
        Question question = new Question("chi-ci", "questionTxt", "answerA", "answerB", "answerC", "", "", "rightAnswer", mediaFiles, AnswerType.CHOICE);
        Mockito.doReturn(question).when(questionsListMock).get(Mockito.anyInt());

        task.setORDER_QUESTIONS(orderQuestions);
        task.setQuestions(questionsListMock);

        Assertions.assertThrows(RuntimeException.class, () -> task.sendQuestion());
    }

    @Test
    public void sendResult() {

    }

    private int getTestChatId(){
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/test.properties")))) {
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String chatIdStr = properties.getProperty("chat");
        return Integer.parseInt(chatIdStr);
    }
}
