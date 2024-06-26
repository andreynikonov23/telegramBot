package org.company.service;

import lombok.Data;
import org.apache.log4j.Logger;
import org.company.bot.TelegramBot;
import org.company.model.AnswerType;
import org.company.model.Question;
import org.company.data.ActiveTasks;
import org.company.data.QuestionsLoader;
import org.company.model.TaskTags;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


@Data
public class Task implements Serializable {
    private static final Logger logger = Logger.getLogger(Task.class);
    private String tag;
    private String user;
    private long chatId;
    private int messageId;
    private transient TelegramBot bot;
    private transient QuestionsLoader questionsLoader;
    private List<Question> questions;
    private HashMap<Integer, String> USER_ANSWERS = new HashMap<>();
    private ArrayList<Integer> ORDER_QUESTIONS = new ArrayList<>();

    public Task(TelegramBot bot, QuestionsLoader questionsLoader) {
        this.bot = bot;
        this.questionsLoader = questionsLoader;
    }


    public void start(long chatId, String tag) {
        logger.info(String.format("ChatId=%d test %s start", chatId, tag));

        this.chatId = chatId;
        this.tag = tag;

        loadQuestions();
        initOrderQuestions();
        sendQuestion();
        ActiveTasks.activateTask(chatId, this);
        ActiveTasks.saveTask(chatId, this);
    }

    public void continueTest() {
        logger.info(String.format("ChatId=%d test %s continue", chatId, tag));
        ActiveTasks.activateTask(chatId, this);
        sendQuestion();
    }

    public Question getActiveQuestion() {
        return questions.get(ORDER_QUESTIONS.get(0));
    }

    public void initOrderQuestions() {
        logger.info(String.format("ChatId=%d test %s - initOrderQuestion()", chatId, tag));
        for (int i = 0; i < questions.size(); i++) {
            ORDER_QUESTIONS.add(i);
        }
        Collections.shuffle(ORDER_QUESTIONS);
    }

    public void sendQuestion() {
        if (ORDER_QUESTIONS.isEmpty()) {
            sendResult();
        } else {
            int numberOfQuestion = ORDER_QUESTIONS.get(0);
            Question question = questions.get(numberOfQuestion);
            if (question.getType() == AnswerType.CHOICE) {
                sendChoiceQuestion(question, numberOfQuestion);
            } else {
                sendInputQuestion(question, numberOfQuestion);
            }

        }
    }

    public void setCallbackAnswer(Integer messageId, Integer numberOfQuestion, String answer) {
        logger.info(String.format("ChatId=%d test %s - setCallbackAnswer with parameters (%d, %d, %s)", chatId, tag, messageId, numberOfQuestion, answer));
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
        editMessage(chatId, messageId, question.getQuestionTxt(), answerTxt);
        checkAnswer(ORDER_QUESTIONS.get(0), answer);
        ORDER_QUESTIONS.remove(0);
        USER_ANSWERS.put(numberOfQuestion, answer);
        ActiveTasks.serialize();

        sendQuestion();
    }

    public void setTextAnswer(String text) {
        logger.info(String.format("ChatId=%d test %s - setTextAnswer with parameter (%s)", chatId, tag, text));
        checkAnswer(ORDER_QUESTIONS.get(0), text);
        USER_ANSWERS.put(ORDER_QUESTIONS.get(0), text.trim().toLowerCase());
        ORDER_QUESTIONS.remove(0);
        ActiveTasks.serialize();

        sendQuestion();
    }

    public void sendResult() {
        logger.info(String.format("ChatId=%d test %s - send result test", chatId, tag));
        StringBuilder resultMessageText = new StringBuilder();
        int rightAnswersCount = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String questionAndAnswersTxt = String.format("%d. %s:\n", i + 1, question.getQuestionTxt());
            resultMessageText.append(questionAndAnswersTxt);
            if (!(question.getAnswerA().equals(""))) {
                resultMessageText.append(question.getAnswerA()).append("\n");
            }
            if (!(question.getAnswerB().equals(""))) {
                resultMessageText.append(question.getAnswerB()).append("\n");
            }
            if (!(question.getAnswerC().equals(""))) {
                resultMessageText.append(question.getAnswerC()).append("\n");
            }
            if (!(question.getAnswerD().equals(""))) {
                resultMessageText.append(question.getAnswerD()).append("\n");
            }
            if (!(question.getAnswerE().equals(""))) {
                resultMessageText.append(question.getAnswerE()).append("\n");
            }
            resultMessageText.append(String.format("Вы ответили: %s. ", USER_ANSWERS.get(i)));
            if (USER_ANSWERS.get(i).toLowerCase().replaceAll(" ", "").equals(question.getRightAnswer())) {
                resultMessageText.append("Это правильно.\n\n");
                rightAnswersCount++;
            } else {
                resultMessageText.append(String.format("Это не правильный ответ. Правильный ответ: %s\n\n", question.getRightAnswer()));
            }
        }
        resultMessageText.append(String.format("Правильных ответов: %d из %d", rightAnswersCount, questions.size()));
        USER_ANSWERS.clear();
        ActiveTasks.clear(chatId, tag);
        ActiveTasks.serialize();
        bot.sendMessage(chatId, resultMessageText.toString());
    }

    public void checkAnswer(int numberOfQuestion, String answer) {
        logger.debug(String.format("ChatId=%d test %s check with parameter (%s)", chatId, tag, answer));
        String rightAnswer = questions.get(numberOfQuestion).getRightAnswer();
        if (rightAnswer.equals(answer.toLowerCase().replaceAll(" ", ""))) {
            logger.info("send \"is right\"");
            bot.sendMessage(chatId, "Правильно");
        } else {
            logger.info("send \"isn't right\"");
            bot.sendMessage(chatId, "Неправильно!\nПравильный ответ: " + rightAnswer);
        }
    }

    public void editMessage(long chatId, int messageId, String question, String answer) {
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
            bot.execute(message);
        } catch (TelegramApiException e) {
            logger.error(format);
            e.getStackTrace();
            throw new RuntimeException(e);
        }
    }


    private void loadQuestions() {
        if (this.tag.equals(TaskTags.UNIT)){
            questions = questionsLoader.getRandomQuestionList();
        } else {
            questions = questionsLoader.getQuestionList(tag);
        }
    }

    private void sendChoiceQuestion(Question question, int num) {
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
        if (!(question.getAnswerD().equals(""))) {
            List<InlineKeyboardButton> row4 = new ArrayList<>(List.of(new InlineKeyboardButton().builder().callbackData("d_" + num + "_" + tag).text(question.getAnswerD()).build()));
            keyboard.add(row4);
        }
        if (!(question.getAnswerE().equals(""))) {
            List<InlineKeyboardButton> row4 = new ArrayList<>(List.of(new InlineKeyboardButton().builder().callbackData("e_" + num + "_" + tag).text(question.getAnswerE()).build()));
            keyboard.add(row4);
        }
        markup.setKeyboard(keyboard);

        message.setText(text);
        message.setChatId(chatId);
        message.setReplyMarkup(markup);


        try {
            logger.info(String.format("ChatId=%d test %s- sendChoiceAnswer with parameters (%s, %s)", chatId, tag, question, num));
            bot.execute(message);
            if (!(question.getMediaFiles().isEmpty())) {
                sendVoice(question);
            }
        } catch (TelegramApiException e) {
            logger.error(String.format("ChatId=%d test %s- sendChoiceAnswer with parameters (%s, %s)", chatId, tag, question, num));
            logger.error(e.getStackTrace());
            throw new RuntimeException(e);
        }
    }

    private void sendInputQuestion(Question question, int num) {
        logger.info(String.format("ChatId=%d test %s- sendInputAnswer with parameters (%s, %s)", chatId, tag, question, num));
        bot.sendMessage(chatId, question.getQuestionTxt());
        if (!(question.getMediaFiles().isEmpty())) {
            sendVoice(question);
        }
    }

    private void sendVoice(Question question) {
        logger.info(String.format("ChatId=%d test %s- sendVoice with parameters (%s)", chatId, tag, question));
        for (String file : question.getMediaFiles()) {
            if (file.equals("")) {
                break;
            }
            SendVoice voice = new SendVoice();
            voice.setChatId(chatId);
            voice.setVoice(new InputFile(new BufferedInputStream(getClass().getResourceAsStream("/media/" + question.getTag() + "/" + file)), file));
            try {
                bot.execute(voice);
            } catch (TelegramApiException e) {
                logger.error(String.format("ChatId=%d test %s- sendVoice with parameters (%s)", chatId, tag, question));
                logger.error(e.getStackTrace());
                throw new RuntimeException(e);
            }
        }
    }
}
