package org.company.service;


import org.company.bot.TelegramBot;
import org.company.config.SpringConfig;
import org.company.data.ActiveTasks;
import org.company.data.QuestionsLoader;
import org.company.model.AnswerType;
import org.company.model.Question;
import org.company.model.TaskTags;
import org.company.service.test_data.TestData;
import org.junit.jupiter.api.*;
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
        task.setChatId(TestData.getTestChatId());
    }

    @Test
    public void startTest() {
        task.setQuestionsLoader(new QuestionsLoader());
        Question testQuestion = new Question(TaskTags.IAN_IANG_TAG, "Отличается ли произношение гласных звуков в финалях [ian] и [iang]? ", "A. Нет, гласные звуки похожи на русский [e]", "B. Нет, гласные звуки похожи на русский [я] ", "C. В звуке [ian] – [e], в [iang] – [я]", "D. В звуке [ian] – [я], в [iang] – [е]", "", "c", new ArrayList<>(), AnswerType.CHOICE);

        try(MockedStatic<ActiveTasks> mockedStatic = Mockito.mockStatic(ActiveTasks.class)){
            task.start(TestData.getTestChatId(), TaskTags.IAN_IANG_TAG);
            Assertions.assertEquals(testQuestion, task.getQuestions().get(0));
            Assertions.assertEquals(5, task.getORDER_QUESTIONS().size());
            mockedStatic.verify(() -> ActiveTasks.activateTask(TestData.getTestChatId(), task));
            mockedStatic.verify(() -> ActiveTasks.saveTask(TestData.getTestChatId(), task));
        }
    }

    @Test
    public void continueTaskTest() {
        try(MockedStatic<ActiveTasks> mockedStatic = Mockito.mockStatic(ActiveTasks.class)) {
            task.setQuestions(questionsListMock);
            task.continueTest();
            mockedStatic.verify(() -> ActiveTasks.activateTask(TestData.getTestChatId(), task));
        }
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
        task.setChatId(TestData.getTestChatId());

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
        try (MockedStatic<ActiveTasks> activeTasksMock = Mockito.mockStatic(ActiveTasks.class);) {
            ArrayList<Integer> orderQuestions = new ArrayList<>();
            List<Question> questions = new ArrayList<>(List.of(new Question("chi-ci", "QuestionTest1", "A", "B", "C", "D", "", "c", new ArrayList<>(), AnswerType.CHOICE),
                    new Question("chi-ci", "QuestionTest2", "A", "B", "C", "", "", "a", new ArrayList<>(), AnswerType.CHOICE),
                    new Question("chi-ci", "QuestionTest3", "", "", "", "", "", "test", new ArrayList<>(), AnswerType.INPUT)));
            HashMap<Integer, String> usersAnswers = new HashMap<>(Map.of(0, "b", 1, "a", 2, "test"));

            task.setTag(TaskTags.CHI_CI_TAG);
            task.setORDER_QUESTIONS(orderQuestions);
            task.setQuestions(questions);
            task.setUSER_ANSWERS(usersAnswers);

            task.sendQuestion();
            Assertions.assertTrue(task.getUSER_ANSWERS().isEmpty());
            activeTasksMock.verify(() -> ActiveTasks.clear(TestData.getTestChatId(), TaskTags.CHI_CI_TAG));
            activeTasksMock.verify(ActiveTasks::serialize);
            Mockito.verify(botMock).sendMessage(TestData.getTestChatId(), TestData.getTestResultMessageTxt());
        }
    }

    @Test
    public void checkRightAnswerTest() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("chi-ci", "QuestionTest2", "A", "B", "C", "", "", "a", new ArrayList<>(), AnswerType.CHOICE));

        task.setQuestions(questions);
        task.checkAnswer(0, "a");

        Mockito.verify(botMock).sendMessage(TestData.getTestChatId(), "Правильно");
    }

    @Test
    public void checkNotAnswerTest() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("chi-ci", "QuestionTest2", "A", "B", "C", "", "", "a", new ArrayList<>(), AnswerType.CHOICE));

        task.setQuestions(questions);
        task.checkAnswer(0, "b");

        Mockito.verify(botMock).sendMessage(TestData.getTestChatId(), "Неправильно!\nПравильный ответ: a");
    }

    @Test
    public void setCallbackAnswerTest() throws TelegramApiException {
        List<Question> questions = new ArrayList<>(List.of(new Question("chi-ci", "QuestionTest1", "A", "B", "C", "D", "", "c", new ArrayList<>(), AnswerType.CHOICE),
                new Question("chi-ci", "QuestionTest2", "A", "B", "C", "", "", "a", new ArrayList<>(), AnswerType.CHOICE),
                new Question("chi-ci", "QuestionTest3", "", "", "", "", "", "test", new ArrayList<>(), AnswerType.INPUT)));

        task.setTag(TaskTags.CHI_CI_TAG);
        task.setQuestions(questions);
        task.initOrderQuestions();
        task.setQuestions(questions);

        try(MockedStatic<ActiveTasks> mockedStatic = Mockito.mockStatic(ActiveTasks.class)){
            task.setCallbackAnswer(1, 1, "a");
            Mockito.verify(botMock).execute(Mockito.any(EditMessageText.class));
            Assertions.assertEquals(2, task.getORDER_QUESTIONS().size());
            Assertions.assertEquals("a", task.getUSER_ANSWERS().get(1));
            mockedStatic.verify(ActiveTasks::serialize);
        }
    }

    @Test
    public void setTextAnswer() {
        List<Question> questions = new ArrayList<>(List.of(new Question("chi-ci", "QuestionTest1", "A", "B", "C", "D", "", "c", new ArrayList<>(), AnswerType.CHOICE),
                new Question("chi-ci", "QuestionTest2", "A", "B", "C", "", "", "a", new ArrayList<>(), AnswerType.CHOICE),
                new Question("chi-ci", "QuestionTest3", "", "", "", "", "", "test", new ArrayList<>(), AnswerType.INPUT)));
        ArrayList<Integer> orderQuestions = new ArrayList<>(List.of(2, 1, 0));

        task.setTag(TaskTags.CHI_CI_TAG);
        task.setQuestions(questions);
        task.setORDER_QUESTIONS(orderQuestions);

        try(MockedStatic<ActiveTasks> mockedStatic = Mockito.mockStatic(ActiveTasks.class)) {
            task.setTextAnswer("test");
            Assertions.assertEquals("test", task.getUSER_ANSWERS().get(2));
            Assertions.assertEquals(2, task.getORDER_QUESTIONS().size());
            mockedStatic.verify(ActiveTasks::serialize);
        }
    }
}
