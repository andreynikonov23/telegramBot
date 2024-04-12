package org.company.service;

import lombok.Data;
import org.company.bot.TelegramBot;
import org.company.model.AnswerType;
import org.company.model.Question;
import org.company.utils.ActiveTests;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.*;

@Data
public abstract class AbsSectionManager implements SectionManager {
    private String tag;
    private long chatId;
    private TelegramBot bot;
    private List<Question> questions;
    private final HashMap<Integer, String> USER_ANSWERS = new HashMap<>();
    private final ArrayList<Integer> ORDER_QUESTIONS = new ArrayList<>();

    public AbsSectionManager(long chatId, List<Question> questions, TelegramBot bot){
        this.chatId = chatId;
        this.questions = questions;
        this.bot = bot;
    }



    @Override
    public void start() {
        ActiveTests.addActiveTest(chatId, this);
        ActiveTests.saveTest(chatId, this);
        initOrderQuestions();
        sendQuestion();
    }

    @Override
    public void initOrderQuestions(){
        for (int i = 0; i < questions.size(); i++) {
            ORDER_QUESTIONS.add(i);
        }
        Collections.shuffle(ORDER_QUESTIONS);
    }

    @Override
    public void sendQuestion() {
        if (ORDER_QUESTIONS.isEmpty()){
            StringBuilder str = new StringBuilder();
            for (Map.Entry<Integer, String> entry : USER_ANSWERS.entrySet()){
                str.append(entry.getKey()).append("--").append(entry.getValue()).append("\n");
            }
            ActiveTests.clear(chatId, this);
            bot.sendMessage(chatId, str.toString());
        } else{
            // добавить проверки на choice и input
            int num = ORDER_QUESTIONS.get(0);
            Question question = questions.get(num);
            if (question.getType() == AnswerType.CHOICE){
                sendChoiceQuestion(question, num);
            } else {
                sendInputQuestion(question, num);
            }

        }
    }

    @Override
    public void setCallbackAnswer(Integer numberOfQuestion, String answer) {
        ORDER_QUESTIONS.remove(0);
        USER_ANSWERS.put(numberOfQuestion, answer);
        sendQuestion();
    }

    @Override
    public void setTextAnswer(String text) {
        USER_ANSWERS.put(ORDER_QUESTIONS.get(0), text);
        ORDER_QUESTIONS.remove(0);
        sendQuestion();
    }

    public Question getActiveQuestion(){
        return questions.get(ORDER_QUESTIONS.get(0));
    }
    private void sendChoiceQuestion(Question question, int num){
        SendMessage message = new SendMessage();
        String text = question.getQuestionTxt();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>(List.of(new InlineKeyboardButton().builder().callbackData("a_" + num + "_" + tag).text(question.getAnswerA()).build()));
        List<InlineKeyboardButton> row2 = new ArrayList<>(List.of(new InlineKeyboardButton().builder().callbackData("b_" + num + "_" + tag).text(question.getAnswerB()).build()));
        List<InlineKeyboardButton> row3 = new ArrayList<>(List.of(new InlineKeyboardButton().builder().callbackData("c_" + num + "_" + tag).text(question.getAnswerC()).build()));
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        if (!(question.getAnswerD().equals(""))){
            List<InlineKeyboardButton> row4 = new ArrayList<>(List.of(new InlineKeyboardButton().builder().callbackData("d_" + num + "_" + tag).text(question.getAnswerD()).build()));
            keyboard.add(row4);
        }
        if (!(question.getAnswerE().equals(""))){
            List<InlineKeyboardButton> row4 = new ArrayList<>(List.of(new InlineKeyboardButton().builder().callbackData("e_" + num + "_" + tag).text(question.getAnswerE()).build()));
            keyboard.add(row4);
        }
        markup.setKeyboard(keyboard);

        message.setText(text);
        message.setChatId(chatId);
        message.setReplyMarkup(markup);

        try {
            bot.execute(message);
            if (!(question.getMediaFiles().isEmpty())){
                sendAudio(question);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    private void sendInputQuestion(Question question, int num){

        bot.sendMessage(chatId, question.getQuestionTxt());
    }
    private void sendAudio(Question question){
        for(File file : question.getMediaFiles()){
            SendAudio audio = new SendAudio();
            audio.setChatId(chatId);
            audio.setAudio(new InputFile(file));
            try {
                bot.execute(audio);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
