package org.company.service;

import org.company.bot.TelegramBot;
import org.company.config.SpringConfig;
import org.company.data.QuestionsLoader;
import org.company.model.Question;
import org.company.model.TaskTags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskTest {
    @Mock
    private TelegramBot bot;
    private final Task task = new AnnotationConfigApplicationContext(SpringConfig.class).getBean(Task.class);


    @Test
    public void loadQuestionsTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        QuestionsLoader questionsLoader = Mockito.mock(QuestionsLoader.class);
        task.setQuestionsLoader(questionsLoader);
        task.setTag(TaskTags.IAN_IANG_TAG);

        Method method = Task.class.getDeclaredMethod("loadQuestions");
        method.setAccessible(true);
        method.invoke(task);
        Mockito.verify(questionsLoader).getQuestionList(TaskTags.IAN_IANG_TAG);
    }

    @Test
    public void loadUnitQuestionsTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        QuestionsLoader questionsLoader = Mockito.mock(QuestionsLoader.class);
        task.setQuestionsLoader(questionsLoader);
        task.setTag(TaskTags.UNIT);

        Method method = Task.class.getDeclaredMethod("loadQuestions");
        method.setAccessible(true);
        method.invoke(task);
        Mockito.verify(questionsLoader).getRandomQuestionList();
    }

    @Test
    public void initOrderQuestionsTest() {
        List<Question> questionListMock = Mockito.mock(ArrayList.class);

        task.setQuestions(questionListMock);
        Mockito.doReturn(5).when(questionListMock).size();
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
    public void sendQuestionTest(){
        ArrayList<Integer> orderQuestions = new ArrayList<>();
        orderQuestions.add(0);
        List<Question> questionListMock = Mockito.mock(ArrayList.class);
        SendVoice
        Question question = new Question();
        question.setMediaFiles(new ArrayList<>());
        Mockito.doReturn().when(questionListMock).get(Mockito.anyInt());

        task.setORDER_QUESTIONS(orderQuestions);

    }
}
