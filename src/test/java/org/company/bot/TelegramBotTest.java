package org.company.bot;

import org.company.config.SpringConfig;
import org.company.data.ActiveTasks;
import org.company.data.UsersData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.logging.Logger;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class TelegramBotTest {
    @Autowired
    private TelegramBot bot;
    private Update update;
    @Mock
    private UsersData usersData;

    @BeforeEach
    public void init() {
        update = new Update();

        Message testMessage = new Message();
        Chat testChat = new Chat();

        testChat.setId(2222L);
        testChat.setUserName("testUsername");
        testMessage.setMessageId(1111);
        testMessage.setChat(testChat);
        update.setMessage(testMessage);

        Mockito.mockStatic(Logger.class);
        Mockito.mockStatic(ActiveTasks.class);
    }

    @Test
    public void startBotTest() {

    }
}
