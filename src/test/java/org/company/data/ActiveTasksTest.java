package org.company.data;


import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;


@RunWith(PowerMockRunner.class)
@PrepareForTest(ActiveTasks.class)
public class ActiveTasksTest {

    @Test
    public void serializeDeserializeTest() throws FileNotFoundException {
        FileOutputStream stream = Mockito.spy(new FileOutputStream("C:\\telegramBotConf\\file.dat"));
    }
}
