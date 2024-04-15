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
    private int messageId;
    private TelegramBot bot;
    private List<Question> questions;
    private int rightAnswersCount;
    private final HashMap<Integer, String> USER_ANSWERS = new HashMap<>();
    private final ArrayList<Integer> ORDER_QUESTIONS = new ArrayList<>();

    public AbsSectionManager(long chatId, List<Question> questions, TelegramBot bot){
        this.chatId = chatId;
        this.questions = questions;
        this.bot = bot;
        rightAnswersCount = 0;
    }



    @Override
    public void start() {
        ActiveTests.addActiveTest(chatId, this);
        ActiveTests.saveTest(chatId, this);
        initOrderQuestions();
        sendQuestion();
    }
    public void continueTest() {
        ActiveTests.addActiveTest(chatId, this);
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
            result();
        } else{
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
    public void setCallbackAnswer(Integer messageId, Integer numberOfQuestion, String answer) {
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
        ORDER_QUESTIONS.remove(0);
        USER_ANSWERS.put(numberOfQuestion, answer);

        sendQuestion();
    }

    @Override
    public void setTextAnswer(String text) {
        check(text);
        USER_ANSWERS.put(ORDER_QUESTIONS.get(0), text.trim().toLowerCase());
        ORDER_QUESTIONS.remove(0);
        sendQuestion();
    }

    @Override
    public void result() {
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
            resultMessageText.append(String.format("Вы ответили: %s. ", USER_ANSWERS.get(i)));
            if (USER_ANSWERS.get(i).toLowerCase().replaceAll(" ", "").equals(question.getRightAnswer())){
                resultMessageText.append("Это правильно.\n\n");
            } else {
                resultMessageText.append(String.format("Это не правильный ответ. Правильный ответ: %s\n\n", question.getRightAnswer()));
            }
        }
        resultMessageText.append(String.format("Правильных ответов: %d из %d", rightAnswersCount, questions.size()));
        USER_ANSWERS.clear();
        ActiveTests.clear(chatId, tag);
        bot.sendMessage(chatId, resultMessageText.toString());
    }
    public void check(String answer){
        int numberOfQuestion = ORDER_QUESTIONS.get(0);
        String rightAnswer = questions.get(numberOfQuestion).getRightAnswer();
        if (rightAnswer.equals(answer.toLowerCase().replaceAll(" ", ""))){
            rightAnswersCount++;
            bot.sendMessage(chatId, "Правильно");
        } else {
            bot.sendMessage(chatId, "Неправильно!\nПравильный ответ: " + rightAnswer);
        }
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
        if (!(question.getMediaFiles().isEmpty())){
            sendAudio(question);
        }
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
