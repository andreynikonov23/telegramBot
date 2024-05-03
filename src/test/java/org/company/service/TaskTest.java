package org.company.service;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggingEvent;
import org.company.bot.TelegramBot;
import org.company.data.QuestionsLoader;
import org.company.model.AnswerType;
import org.company.model.Question;
import org.company.model.TaskTags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TaskTest {
    @Mock
    private Appender appenderMock;
    @Mock
    private TelegramBot botMock;
    @Mock
    private QuestionsLoader questionsLoaderMock;
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
    public void sendQuestionTest() throws TelegramApiException {
        ArrayList<Integer> orderQuestions = new ArrayList<>();
        orderQuestions.add(0);
        List<Question> questionListMock = Mockito.mock(ArrayList.class);

        Question question = new Question("chi-ci", "questionTxt", "answerA", "answerB", "answerC", "", "", "rightAnswer", new ArrayList<>(), AnswerType.CHOICE);
        Mockito.doReturn(question).when(questionListMock).get(Mockito.anyInt());

        task.setORDER_QUESTIONS(orderQuestions);
        task.setQuestions(questionListMock);
        task.sendQuestion();

        Mockito.verify(botMock).execute(Mockito.any(SendMessage.class));
    }

}
