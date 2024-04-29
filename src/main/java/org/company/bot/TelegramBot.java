package org.company.bot;

import org.apache.log4j.Logger;
import org.company.model.TaskTags;
import org.company.service.Task;
import org.company.data.ActiveTasks;
import org.company.service.UpdateRecognizer;
import org.company.data.UsersData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@PropertySource(value = "classpath:/data.properties")
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger logger = Logger.getLogger(TelegramBot.class);

    @Value("${bot.name}")
    private String name;
    @Value("${bot.token}")
    private String token;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private UsersData usersData;
    @Autowired
    private UpdateRecognizer recognizer;


    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            switch (message.getText()) {
                case ("/start") -> {
                    logger.debug(String.format("%s : ChatId=%s use /start", message.getChat().getUserName(), message.getChatId()));
                    usersData.addIfItIsNewUser(message.getChat().getUserName());
                    sendMainMenuMessage(update);
                }
                case ("/help") -> {
                    logger.debug(String.format("%s : ChatId=%s use /help", message.getChat().getUserName(), message.getChatId()));
                    String text = "Бот предлагает увлекательные тесты для проверки ваших знаний и отслеживания прогресса в изучении китайского языка.\n" +
                            "Вы можете выбирать тесты по различным темам прямо из главного меню. " +
                            "Задания могут быть как с вариантами ответов, так и с возможностью ввода текста. " +
                            "По завершении теста вы получите свой результат. " +
                            "Бот также запоминает ваши ответы, если вы не завершили тест или решаете вернуться к нему позже, предлагая продолжить там, где остановились.";
                    sendMessage(message.getChatId(), text);
                    sendMainMenuMessage(update);
                }
                default -> recognizer.recognizeAndCompleteText(message.getChatId(), message.getText());
            }
        }

        if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            String callBack = update.getCallbackQuery().getData();
            logger.debug(String.format("%s : ChatId=%s use callback %s", update.getCallbackQuery().getFrom().getUserName(), chatId, callBack));

            if (TaskTags.isTag(callBack)) {
                if (ActiveTasks.getIncompleteTask(chatId, callBack) == null) {
                    Task task = context.getBean(Task.class);
                    task.start(chatId, callBack);
                } else {
                    sendContinueMessage(chatId, callBack);
                }
            } else {
                recognizer.recognizeAndCompleteCallback(chatId, messageId, callBack);
            }
        }

    }


    public void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void sendMainMenuMessage(Update update) {
        String text = "Добро пожаловать в фонетический тренажер основ китайского.\nВыберете раздел который хотите потренировать.\n/help - помощь.";
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        List<InlineKeyboardButton> row6 = new ArrayList<>();
        List<InlineKeyboardButton> row7 = new ArrayList<>();
        List<InlineKeyboardButton> row8 = new ArrayList<>();
        List<InlineKeyboardButton> row9 = new ArrayList<>();
        List<InlineKeyboardButton> row11 = new ArrayList<>();
        row1.add(new InlineKeyboardButton().builder().text("[chi] и [ci]").callbackData(TaskTags.CHI_CI_TAG).build());
        row2.add(new InlineKeyboardButton().builder().text("Заднеязычные финали").callbackData(TaskTags.BACK_LANG_FINALS_TAG).build());
        row3.add(new InlineKeyboardButton().builder().text("Инициали [j][q][x]").callbackData(TaskTags.JQX_INITIALS_TAG).build());
        row4.add(new InlineKeyboardButton().builder().text("Инициаль [r]").callbackData(TaskTags.R_INITIAL_TAG).build());
        row5.add(new InlineKeyboardButton().builder().text("Особая финаль [er]").callbackData(TaskTags.SPECIAL_FINALS_TAG).build());
        row6.add(new InlineKeyboardButton().builder().text("Придыхательные инициали").callbackData(TaskTags.ASPIRATED_INITIALS_TAG).build());
        row7.add(new InlineKeyboardButton().builder().text("Финаль [ian] и [iang]").callbackData(TaskTags.IAN_IANG_TAG).build());
        row8.add(new InlineKeyboardButton().builder().text("Финаль [e]").callbackData(TaskTags.E_FINAL_TAG).build());
        row9.add(new InlineKeyboardButton().builder().text("Финаль [Ü]").callbackData(TaskTags.U_FINAL_TAG).build());
        row11.add(new InlineKeyboardButton().builder().text("Рандомные темы").callbackData(TaskTags.UNIT).build());

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);
        keyboard.add(row6);
        keyboard.add(row7);
        keyboard.add(row8);
        keyboard.add(row9);
        keyboard.add(row11);
        markup.setKeyboard(keyboard);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(markup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(String.format("%s : ChatId=%s TelegramApiException in sendMainMenuMessage()", update.getMessage().getChat().getUserName(), update.getMessage().getChatId()));
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void sendContinueMessage(long chatId, String tag) {
        logger.debug(String.format("sendContinueMessage with parameters (%d, %s)", chatId, tag));
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("У вас есть незавершенный тест в этом разделе, желаете продолжить?");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(new InlineKeyboardButton().builder().text("Да").callbackData("yes_" + tag).build());
        row.add(new InlineKeyboardButton().builder().text("Нет").callbackData("no_" + tag).build());
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        message.setReplyMarkup(markup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(String.format("sendContinueMessage with parameters (%d, %s)", chatId, tag));
            e.getStackTrace();
            throw new RuntimeException(e);
        }
    }
}
