package org.company.data;


import org.company.service.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.logging.Logger;

@RunWith(SpringRunner.class)
public class ActiveTasksTest {
    private final MockedStatic<Logger> loggerMockedStatic = Mockito.mockStatic(Logger.class);


    @Test
    public void saveDataTest(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        Task testTask = context.getBean(Task.class);
        long testChatId = 1111;

        MockedStatic<ActiveTasks> activeTasksMockedStatic = Mockito.mockStatic(ActiveTasks.class);

    }
}
