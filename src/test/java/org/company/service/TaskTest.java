package org.company.service;

import org.company.bot.TelegramBot;
import org.company.config.SpringConfig;
import org.company.data.ActiveTasks;
import org.company.data.QuestionsLoader;
import org.company.model.AnswerType;
import org.company.model.Question;
import org.company.model.TaskTags;
import org.company.service.test_data.TaskTestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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
    public void init() {
        task = new Task(botMock, questionsLoaderMock);
        task.setChatId(TaskTestData.getTestChatId());
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
        task.setChatId(TaskTestData.getTestChatId());

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

    //verify ActiveTasks
    @Test
    public void sendResult() {
        try (MockedStatic<ActiveTasks> activeTasksMock = Mockito.mockStatic(ActiveTasks.class);) {
            ArrayList<Integer> orderQuestions = new ArrayList<>();
            List<Question> questions = new ArrayList<>(List.of(new Question("chi-ci", "QuestionTest1", "A", "B", "C", "D", "", "c", new ArrayList<>(), AnswerType.CHOICE),
                    new Question("chi-ci", "QuestionTest2", "A", "B", "C", "", "", "a", new ArrayList<>(), AnswerType.CHOICE),
                    new Question("chi-ci", "QuestionTest3", "", "", "", "", "", "test", new ArrayList<>(), AnswerType.INPUT)));
            HashMap<Integer, String> usersAnswers = new HashMap<>(Map.of(0, "b", 1, "a", 2, "test"));

            task.setORDER_QUESTIONS(orderQuestions);
            task.setQuestions(questions);
            task.setUSER_ANSWERS(usersAnswers);

            task.sendQuestion();
            Assertions.assertTrue(task.getUSER_ANSWERS().isEmpty());
            Mockito.verify(botMock).sendMessage(TaskTestData.getTestChatId(), TaskTestData.getTestResultMessageTxt());
        }
    }

    @Test
    public void checkRightAnswerTest() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("chi-ci", "QuestionTest2", "A", "B", "C", "", "", "a", new ArrayList<>(), AnswerType.CHOICE));

        task.setQuestions(questions);
        task.checkAnswer(0, "a");

        Mockito.verify(botMock).sendMessage(TaskTestData.getTestChatId(), "Правильно");
    }

    @Test
    public void checkNotAnswerTest() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("chi-ci", "QuestionTest2", "A", "B", "C", "", "", "a", new ArrayList<>(), AnswerType.CHOICE));

        task.setQuestions(questions);
        task.checkAnswer(0, "b");

        Mockito.verify(botMock).sendMessage(TaskTestData.getTestChatId(), "Неправильно!\nПравильный ответ: a");
    }

    @Test
    public void setCallbackAnswerTest() throws TelegramApiException {
        try(MockedStatic<ActiveTasks> activeTasksMock = Mockito.mockStatic(ActiveTasks.class)) {
            List<Question> questions = new ArrayList<>(List.of(new Question("chi-ci", "QuestionTest1", "A", "B", "C", "D", "", "c", new ArrayList<>(), AnswerType.CHOICE),
                    new Question("chi-ci", "QuestionTest2", "A", "B", "C", "", "", "a", new ArrayList<>(), AnswerType.CHOICE),
                    new Question("chi-ci", "QuestionTest3", "", "", "", "", "", "test", new ArrayList<>(), AnswerType.INPUT)));
            HashMap<Integer, String> usersAnswers = new HashMap<>(Map.of(0, "b", 1, "a", 2, "test"));

            task.setTag(TaskTags.CHI_CI_TAG);
            task.setQuestions(questions);
            task.initOrderQuestions();
            task.setQuestions(questions);
            task.setUSER_ANSWERS(usersAnswers);

            task.setCallbackAnswer(1, 1, "a");
            Mockito.verify(botMock).execute(Mockito.any(EditMessageText.class));
            Assertions.assertEquals(2, task.getORDER_QUESTIONS().size());
            Assertions.assertEquals("a", task.getUSER_ANSWERS().get(1));
        }

    }
}
