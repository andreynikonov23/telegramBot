package org.company.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.company.bot.TelegramBot;
import org.company.config.SpringConfig;
import org.company.model.AnswerType;
import org.company.model.Question;
import org.company.utils.ActiveTests;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.*;

@Data
@NoArgsConstructor
public abstract class AbsSectionManager implements SectionManager, Serializable {
    private String tag;
    private String user;
    private long chatId;
    private int messageId;
    private transient TelegramBot bot;
    private List<Question> questions;
    private int rightAnswersCount;
    private HashMap<Integer, String> userAnswers;
    private ArrayList<Integer> orderQuestions;
    private static final Logger logger = Logger.getLogger(AbsSectionManager.class);

    public AbsSectionManager(long chatId, List<Question> questions){
        this.chatId = chatId;
        this.questions = questions;
        rightAnswersCount = 0;
    }



    @Override
    public void start() {
        logger.info(String.format("ChatId=%d test %s start", chatId, tag));
        if (userAnswers == null){
            userAnswers = new HashMap<>();
        }
        if (orderQuestions == null){
            orderQuestions = new ArrayList<>();
        }
        ActiveTests.addActiveTest(chatId, this);
        ActiveTests.saveTest(chatId, this);
        initOrderQuestions();
        sendQuestion();
    }
    public void continueTest() {
        logger.info(String.format("ChatId=%d test %s continue", chatId, tag));
        ActiveTests.addActiveTest(chatId, this);
        sendQuestion();
    }

    @Override
    public void initOrderQuestions(){
        logger.info(String.format("ChatId=%d test %s - initOrderQuestion()", chatId, tag));
        for (int i = 0; i < questions.size(); i++) {
            orderQuestions.add(i);
        }
        Collections.shuffle(orderQuestions);
    }

    @Override
    public void sendQuestion() {
        if (orderQuestions.isEmpty()){
            result();
        } else{
            int num = orderQuestions.get(0);
            Question question = questions.get(num);
            if (question.getType() == AnswerType.CHOICE){
                sendChoiceQuestion(question, num);
            } else {
                sendInputQuestion(question, num);
            }

        }
    }

    @Override
    public void setCallbackAnswer(Integer messageId, Integer numberOfQuestion, String answer) {
        logger.info(String.format("ChatId=%d test %s - setCallbackAnswer with parameters (%d, %d, %s)",chatId, tag, messageId, numberOfQuestion, answer));
        Question question = questions.get(numberOfQuestion);
        String answerTxt;
        switch (answer) {
            case "a" -> answerTxt = question.getAnswerA();
            case "b" -> answerTxt = question.getAnswerB();
            case "c" -> answerTxt = question.getAnswerC();
            case "d" -> answerTxt = question.getAnswerD();
            case "e" -> answerTxt = question.getAnswerE();
            default -> answerTxt = answer;
        }
        bot.editMessage(chatId, messageId, question.getQuestionTxt(), answerTxt);
        check(answer);
        orderQuestions.remove(0);
        userAnswers.put(numberOfQuestion, answer);
        ActiveTests.serialize();

        sendQuestion();
    }

    @Override
    public void setTextAnswer(String text) {
        logger.info(String.format("ChatId=%d test %s - setTextAnswer with parameter (%s)",chatId, tag, text));
        check(text);
        userAnswers.put(orderQuestions.get(0), text.trim().toLowerCase());
        orderQuestions.remove(0);
        ActiveTests.serialize();

        sendQuestion();
    }

    @Override
    public void result() {
        logger.info(String.format("ChatId=%d test %s - send result test", chatId, tag));
        StringBuilder resultMessageText = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String questionAnswers = String.format("%d. %s:\n", i + 1, question.getQuestionTxt(), question.getAnswerA(), question.getAnswerB(), question.getAnswerC());
            resultMessageText.append(questionAnswers);
            if (!(question.getAnswerA().equals(""))){
                resultMessageText.append(question.getAnswerA()).append("\n");
            }
            if (!(question.getAnswerB().equals(""))){
                resultMessageText.append(question.getAnswerB()).append("\n");
            }
            if (!(question.getAnswerC().equals(""))){
                resultMessageText.append(question.getAnswerC()).append("\n");
            }
            if (!(question.getAnswerD().equals(""))){
                resultMessageText.append(question.getAnswerD()).append("\n");
            }
            if (!(question.getAnswerE().equals(""))){
                resultMessageText.append(question.getAnswerE()).append("\n");
            }
            resultMessageText.append(String.format("Вы ответили: %s. ", userAnswers.get(i)));
            if (userAnswers.get(i).toLowerCase().replaceAll(" ", "").equals(question.getRightAnswer())){
                resultMessageText.append("Это правильно.\n\n");
            } else {
                resultMessageText.append(String.format("Это не правильный ответ. Правильный ответ: %s\n\n", question.getRightAnswer()));
            }
        }
        resultMessageText.append(String.format("Правильных ответов: %d из %d", rightAnswersCount, questions.size()));
        userAnswers.clear();
        ActiveTests.clear(chatId, tag);
        ActiveTests.serialize();
        bot.sendMessage(chatId, resultMessageText.toString());
    }
    public void check(String answer){
        logger.debug(String.format("ChatId=%d test %s check with parameter (%s)",chatId, tag, answer));
        int numberOfQuestion = orderQuestions.get(0);
        String rightAnswer = questions.get(numberOfQuestion).getRightAnswer();
        if (rightAnswer.equals(answer.toLowerCase().replaceAll(" ", ""))){
            logger.info("send \"is right\"");
            rightAnswersCount++;
            bot.sendMessage(chatId, "Правильно");
        } else {
            logger.info("send \"isn't right\"");
            bot.sendMessage(chatId, "Неправильно!\nПравильный ответ: " + rightAnswer);
        }
    }

    public Question getActiveQuestion(){
        return questions.get(orderQuestions.get(0));
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
            logger.info(String.format("ChatId=%d test %s- sendChoiceAnswer with parameters (%s, %s)",chatId, tag, question, num));
            bot.execute(message);
            if (!(question.getMediaFiles().isEmpty())){
                sendVoice(question);
            }
        } catch (TelegramApiException e) {
            logger.error(String.format("ChatId=%d test %s- sendChoiceAnswer with parameters (%s, %s)", chatId, tag, question, num));
            logger.error(e.getStackTrace());
            throw new RuntimeException(e);
        }
    }
    private void sendInputQuestion(Question question, int num){
        logger.info(String.format("ChatId=%d test %s- sendInputAnswer with parameters (%s, %s)", chatId, tag, question, num));
        bot.sendMessage(chatId, question.getQuestionTxt());
        if (!(question.getMediaFiles().isEmpty())){
            sendVoice(question);
        }
    }
    private void sendVoice(Question question) {
        logger.info(String.format("ChatId=%d test %s- sendVoice with parameters (%s)", chatId, tag, question));
        for(String file : question.getMediaFiles()){
            SendVoice voice = new SendVoice();
            voice.setChatId(chatId);
            voice.setVoice(new InputFile(new BufferedInputStream(getClass().getResourceAsStream("/media/" + tag + "/" + file)), file));
            try {
                bot.execute(voice);
                break;
            } catch (TelegramApiException e) {
                logger.error(String.format("ChatId=%d test %s- sendVoice with parameters (%s)", chatId, tag, question));
                logger.error(e.getStackTrace());
                throw new RuntimeException(e);
            }
        }
    }

}
