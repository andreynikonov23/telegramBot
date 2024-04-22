package org.company.bot;

import org.apache.log4j.Logger;
import org.company.service.SectionFabric;
import org.company.utils.ActiveTests;
import org.company.utils.AnswerRecognizer;
import org.company.utils.UsersData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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
    private UsersData usersData;
    @Autowired
    private SectionFabric fabric;
    @Autowired
    private AnswerRecognizer receiver;



    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            Message message =  update.getMessage();
            //Сделать switch
            if (message.getText().equals("/start") || message.getText().equals("/help")){
                if (message.getText().equals("/start")){
                    logger.debug(String.format("%s : ChatId=%s use /start", message.getChat().getUserName(), message.getChatId()));
                    usersData.isNewUser(message.getChat().getUserName());
                    sendMainMenuMessage(update);
                }
                if (message.getText().equals("/help")){
                    logger.debug(String.format("%s : ChatId=%s use /help", message.getChat().getUserName(), message.getChatId()));
                    String text = "Бот предлагает увлекательные тесты для проверки ваших знаний и отслеживания прогресса в изучении китайского языка.\n" +
                            "Вы можете выбирать тесты по различным темам прямо из главного меню. " +
                            "Задания могут быть как с вариантами ответов, так и с возможностью ввода текста. " +
                            "По завершении теста вы получите свой результат. " +
                            "Бот также запоминает ваши ответы, если вы не завершили тест или решаете вернуться к нему позже, предлагая продолжить там, где остановились.";
                    sendMessage(message.getChatId(), text);
                    sendMainMenuMessage(update);

                }
            } else {
                receiver.setText(message.getChatId(), message.getText());
            }
        }
        //Сделать не так ёбнуто
        if (update.hasCallbackQuery()){
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String callBack = update.getCallbackQuery().getData();
            logger.debug(String.format("%s : ChatId=%s use callback %s", update.getCallbackQuery().getFrom().getUserName(),chatId, callBack));
            switch (callBack) {
                case (AnswerRecognizer.CHI_CI_TAG) ->{
                    if (ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.CHI_CI_TAG) == null){
                        fabric.getChiCiSection(chatId).start();
                    } else {
                        sendContinueMessage(chatId, AnswerRecognizer.CHI_CI_TAG);
                    }
                }
                case (AnswerRecognizer.BACK_LANG_FINALS_TAG) -> {
                    if (ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.BACK_LANG_FINALS_TAG) == null){
                        fabric.getBackLangFinalsSectionManager(chatId).start();
                    } else {
                        sendContinueMessage(chatId, AnswerRecognizer.BACK_LANG_FINALS_TAG);
                    }
                }
                case (AnswerRecognizer.ASPIRATED_INITIALS_TAG) -> {
                    if (ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.ASPIRATED_INITIALS_TAG) == null){
                        fabric.getAspiratedInitialsSectionManager(chatId).start();
                    } else {
                        sendContinueMessage(chatId, AnswerRecognizer.ASPIRATED_INITIALS_TAG);
                    }
                }
                case (AnswerRecognizer.E_FINAL_TAG) -> {
                    if (ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.E_FINAL_TAG) == null){
                        fabric.getEFinalSectionManager(chatId).start();
                    } else {
                        sendContinueMessage(chatId, AnswerRecognizer.E_FINAL_TAG);
                    }
                }
                case (AnswerRecognizer.IAN_IANG_TAG) -> {
                    if (ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.IAN_IANG_TAG) == null){
                        fabric.getIanIangSectionManager(chatId).start();
                    } else {
                        sendContinueMessage(chatId, AnswerRecognizer.IAN_IANG_TAG);
                    }
                }
                case (AnswerRecognizer.JQX_INITIALS_TAG) -> {
                    if (ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.JQX_INITIALS_TAG) == null){
                        fabric.getJqxInitialsSectionManager(chatId).start();
                    } else {
                        sendContinueMessage(chatId, AnswerRecognizer.JQX_INITIALS_TAG);
                    }
                }
                case (AnswerRecognizer.R_INITIAL_TAG) -> {
                    if (ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.R_INITIAL_TAG) == null){
                        fabric.getRInitialsSectionManager(chatId).start();
                    } else {
                        sendContinueMessage(chatId, AnswerRecognizer.R_INITIAL_TAG);
                    }
                }
                case (AnswerRecognizer.SPECIAL_FINALS_TAG) -> {
                    if (ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.SPECIAL_FINALS_TAG) == null){
                        fabric.getSpecialFinalSectionManager(chatId).start();
                    } else {
                        sendContinueMessage(chatId, AnswerRecognizer.SPECIAL_FINALS_TAG);
                    }
                }
                case (AnswerRecognizer.U_FINAL_TAG) -> {
                    if (ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.U_FINAL_TAG) == null){
                        fabric.getUFinalSectionManager(chatId).start();
                    } else {
                        sendContinueMessage(chatId, AnswerRecognizer.U_FINAL_TAG);
                    }
                }
                case ("yes-chi-ci") ->
                    ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.CHI_CI_TAG).continueTest();
                case ("no-chi-ci") ->
                        fabric.getChiCiSection(chatId).start();
                case ("yes-back-lang-finals") ->
                        ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.BACK_LANG_FINALS_TAG).continueTest();
                case ("no-back-lang-finals") ->
                        fabric.getBackLangFinalsSectionManager(chatId).start();
                case ("yes-aspirated-initials") ->
                        ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.ASPIRATED_INITIALS_TAG).continueTest();
                case ("no-aspirated-initials") ->
                        fabric.getAspiratedInitialsSectionManager(chatId).start();
                case ("yes-e-final") ->
                        ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.E_FINAL_TAG).continueTest();
                case ("no-e-final") ->
                        fabric.getEFinalSectionManager(chatId).start();
                case ("yes-ian-iang") ->
                        ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.IAN_IANG_TAG).continueTest();
                case ("no-ian-iang") ->
                        fabric.getIanIangSectionManager(chatId).start();
                case ("yes-jqx-initials") ->
                        ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.JQX_INITIALS_TAG).continueTest();
                case ("no-jqx-initials") ->
                        fabric.getJqxInitialsSectionManager(chatId).start();
                case ("yes-r-initials") ->
                        ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.R_INITIAL_TAG).continueTest();
                case ("no-r-initials") ->
                        fabric.getRInitialsSectionManager(chatId).start();
                case ("yes-special-final") ->
                        ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.SPECIAL_FINALS_TAG).continueTest();
                case ("no-special-final") ->
                        fabric.getSpecialFinalSectionManager(chatId).start();
                case ("yes-u-final") ->
                        ActiveTests.getIncompleteTest(chatId, AnswerRecognizer.U_FINAL_TAG).continueTest();
                case ("no-u-final") ->
                        fabric.getUFinalSectionManager(chatId).start();
                default -> receiver.setCallbackAnswer(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId(), callBack);
            }
        }

    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }


    public void sendMessage (Long chatId, String text){
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
    public void sendMainMenuMessage(Update update){
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
        row1.add(new InlineKeyboardButton().builder().text("[chi] и [ci]").callbackData("chi-ci").build());
        row2.add(new InlineKeyboardButton().builder().text("Заднеязычные финали").callbackData("back-lang-finals").build());
        row3.add(new InlineKeyboardButton().builder().text("Инициали [j][q][x]").callbackData("jqx-initials").build());
        row4.add(new InlineKeyboardButton().builder().text("Инициаль [r]").callbackData("r-initials").build());
        row5.add(new InlineKeyboardButton().builder().text("Особая финаль [er]").callbackData("special-final").build());
        row6.add(new InlineKeyboardButton().builder().text("Придыхательные инициали").callbackData("aspirated-initials").build());
        row7.add(new InlineKeyboardButton().builder().text("Финаль [ian] и [iang]").callbackData("ian-iang").build());
        row8.add(new InlineKeyboardButton().builder().text("Финаль [e]").callbackData("e-final").build());
        row9.add(new InlineKeyboardButton().builder().text("Финаль [Ü]").callbackData("u-final").build());

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);
        keyboard.add(row6);
        keyboard.add(row7);
        keyboard.add(row8);
        keyboard.add(row9);
        markup.setKeyboard(keyboard);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(markup);

        try {

            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(String.format("%s : ChatId=%s TelegramApiException in sendMainMenuMessage()", update.getMessage().getChat().getUserName(), update.getMessage().getChatId()));
            throw new RuntimeException(e);
        }
    }
    public void sendContinueMessage(long chatId, String tag){
        logger.debug(String.format("sendContinueMessage with parameters (%d, %s)", chatId, tag));
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("У вас есть незавершенный тест в этом разделе, желаете продолжить?");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(new InlineKeyboardButton().builder().text("Да").callbackData("yes-" + tag).build());
        row.add(new InlineKeyboardButton().builder().text("Нет").callbackData("no-" + tag).build());
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        message.setReplyMarkup(markup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(String.format("sendContinueMessage with parameters (%d, %s)", chatId, tag));
            logger.error(e.getStackTrace());
            throw new RuntimeException(e);
        }
    }
    //Перенести в Test
    public void editMessage(long chatId, int messageId, String question, String answer){
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setText(question + "\nОтвет: " + answer);
        List<InlineKeyboardButton> emptyRow = new ArrayList<>();
        List<List<InlineKeyboardButton>> emptyKeyboard = new ArrayList<>();
        emptyKeyboard.add(emptyRow);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(emptyKeyboard);
        message.setReplyMarkup(markup);
        String format = String.format("editMessage with parameters (%d, %d, %s, %s)", chatId, messageId, question, answer);
        try {
            logger.debug(format);
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(format);
            logger.error(e.getStackTrace());
            throw new RuntimeException(e);
        }
    }
}
