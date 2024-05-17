package org.company.test.service;

import org.company.bot.TelegramBot;
import org.company.data.ActiveTasks;
import org.company.model.AnswerType;
import org.company.model.Question;
import org.company.model.TaskTags;
import org.company.service.Task;
import org.company.service.UpdateRecognizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
public class UpdateRecognizerTest {
    @Mock
    private Task taskMock;
    @Mock
    private TelegramBot botMock;
    @Mock
    private ApplicationContext contextMock;
    private UpdateRecognizer updateRecognizer;
    private MockedStatic<ActiveTasks> mockedStatic;

    @BeforeEach
    public void init(){
        updateRecognizer = new UpdateRecognizer(botMock, contextMock);
        mockedStatic = Mockito.mockStatic(ActiveTasks.class);
    }
    @AfterEach
    public void closeMockedStatic() {
        mockedStatic.close();
    }
    @Test
    public void recognizeContinuationOfTask() {
        mockedStatic.when(() -> ActiveTasks.getIncompleteTask(123, TaskTags.IAN_IANG_TAG)).thenReturn(taskMock);
        updateRecognizer.recognizeAndCompleteCallback(123, 123, "yes_ian-iang");
        Mockito.verify(taskMock).continueTest();
    }

    @Test
    public void recognizeNotContinuationOfTask() {
        Mockito.doReturn(taskMock).when(contextMock).getBean(Task.class);
        updateRecognizer.recognizeAndCompleteCallback(123L, 123, "no_ian-iang");
        mockedStatic.verify(() -> ActiveTasks.clear(123L, TaskTags.IAN_IANG_TAG));
        Mockito.verify(taskMock).start(123L, TaskTags.IAN_IANG_TAG);
    }

    @Test
    public void recognizeCallbackAnswer() {
        mockedStatic.when(() -> ActiveTasks.getIncompleteTask(123L, TaskTags.IAN_IANG_TAG)).thenReturn(taskMock);
        updateRecognizer.recognizeAndCompleteCallback(123L, 123, "b_2_ian-iang");
        Mockito.verify(taskMock).setCallbackAnswer(123, 2, "b");
    }

    @Test
    public void recognizeInvalidText() {
        mockedStatic.when(() ->ActiveTasks.getActiveTask(123L)).thenReturn(taskMock);

        Question testObject = new Question();
        testObject.setType(AnswerType.CHOICE);
        Mockito.doReturn(testObject).when(taskMock).getActiveQuestion();
        updateRecognizer.recognizeAndCompleteText(123L, "test");
        Mockito.verify(botMock).sendMessage(123L, "Команда не распознана");
    }

    @Test
    public void recognizeTextAnswer() {
        mockedStatic.when(() -> ActiveTasks.getActiveTask(123L)).thenReturn(taskMock);

        Question testObject = new Question();
        testObject.setType(AnswerType.INPUT);
        Mockito.doReturn(testObject).when(taskMock).getActiveQuestion();
        updateRecognizer.recognizeAndCompleteText(123L, "test");
        Mockito.verify(taskMock).setTextAnswer("test");
    }
}
