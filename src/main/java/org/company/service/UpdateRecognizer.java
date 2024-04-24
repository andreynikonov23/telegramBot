package org.company.service;

import org.apache.log4j.Logger;
import org.company.bot.TelegramBot;
import org.company.model.AnswerType;
import org.company.data.ActiveTests;
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
            case ("yes") -> ActiveTests.getIncompleteTest(chatId, arr[1]).continueTest();
            case ("no") -> {
                ActiveTests.clear(chatId, arr[1]);
                context.getBean(Test.class).start(chatId, arr[1]);
            }
            default -> {
                String answer = arr[0];
                int numberOfQuestion = Integer.parseInt(arr[1]);
                String tag = arr[2];
                ActiveTests.getIncompleteTest(chatId, tag).setCallbackAnswer(messageId, numberOfQuestion, answer);
            }
        }
    }

    public void recognizeAndCompleteText(long chatId, String text) {
        Test test = ActiveTests.getActiveTest(chatId);
        if (test.getActiveQuestion().getType() == AnswerType.INPUT) {
            test.setTextAnswer(text);
        } else {
            logger.debug(String.format("ChatId=%d unknown text (%s) message", chatId, text));
            bot.sendMessage(chatId, "Команда не распознана");
        }
    }
}
