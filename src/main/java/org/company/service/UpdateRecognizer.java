package org.company.service;

import org.apache.log4j.Logger;
import org.company.bot.TelegramBot;
import org.company.model.AnswerType;
import org.company.data.ActiveTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;


public class UpdateRecognizer {
    private static final Logger logger = Logger.getLogger(UpdateRecognizer.class);
    private TelegramBot bot;
    @Autowired
    private ApplicationContext context;


    public UpdateRecognizer(TelegramBot bot) {
        this.bot = bot;
    }


    public void recognizeAndCompleteCallback(long chatId, int messageId, String callback) {
        String[] arr = callback.split("_");
        switch (arr[0]) {
            case ("yes") -> ActiveTasks.getIncompleteTask(chatId, arr[1]).continueTest();
            case ("no") -> {
                ActiveTasks.clear(chatId, arr[1]);
                context.getBean(Task.class).start(chatId, arr[1]);
            }
            default -> {
                String answer = arr[0];
                int numberOfQuestion = Integer.parseInt(arr[1]);
                String tag = arr[2];
                ActiveTasks.getIncompleteTask(chatId, tag).setCallbackAnswer(messageId, numberOfQuestion, answer);
            }
        }
    }

    public void recognizeAndCompleteText(long chatId, String text) {
        Task task = ActiveTasks.getActiveTask(chatId);
        if (task.getActiveQuestion().getType() == AnswerType.INPUT) {
            task.setTextAnswer(text);
        } else {
            logger.debug(String.format("ChatId=%d unknown text (%s) message", chatId, text));
            bot.sendMessage(chatId, "Команда не распознана");
        }
    }
}
