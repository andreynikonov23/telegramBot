package org.company.bot;

import org.apache.log4j.Logger;
import org.company.service.SectionFabric;
import org.company.utils.AnswerReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
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
    private SectionFabric fabric;
    @Autowired
    private AnswerReceiver receiver;



    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            Message message =  update.getMessage();
            if (message.getText().equals("/start") || message.getText().equals("/help")){
                if (message.getText().equals("/start")){
                    logger.debug(String.format("%s : ChatId=%s use /start", message.getChat().getUserName(), message.getChatId()));
                    sendMainMenuMessage(update);
                }
                if (message.getText().equals("/help")){
                    logger.debug(String.format("%s : ChatId=%s use /help", message.getChat().getUserName(), message.getChatId()));
                    String text = "/start - главное меню\n/help - помощь";
                    sendMessage(message.getChatId(), text);
                    sendMainMenuMessage(update);

                }
            } else {
                receiver.setText(message.getChatId(), message.getText());
            }
        }
        if (update.hasCallbackQuery()){
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String callBack = update.getCallbackQuery().getData();
            switch (callBack) {
                case ("chi-ci") ->
                    fabric.getChiCiSection(chatId).start();
                case ("back-lang-finals") ->
                    fabric.getBackLangFinalsSectionManager(chatId).start();
                case ("aspirated-initials") ->
                    fabric.getAspiratedInitialsSectionManager(chatId).start();
                case ("e-final") ->
                    fabric.getEFinalSectionManager(chatId).start();
                case ("ian-iang") ->
                    fabric.getIanIangSectionManager(chatId).start();
                case ("jqx-initials") ->
                    fabric.getJqxInitialsSectionManager(chatId).start();
                case ("r-initials") ->
                    fabric.getRInitialsSectionManager(chatId).start();
                case ("special-final") ->
                    fabric.getSpecialFinalSectionManager(chatId).start();
                case ("u-final") ->
                    fabric.getUFinalSectionManager(chatId).start();
                default -> receiver.setCallbackAnswer(chatId, callBack);
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
        row4.add(new InlineKeyboardButton().builder().text("Инициаль [r]").callbackData("r-inital").build());
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


}
