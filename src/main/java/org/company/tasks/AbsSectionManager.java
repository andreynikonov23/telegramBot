package org.company.tasks;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class AbsSectionManager implements SectionManager {
    private Chat chat;
    private Update update;
    private List<Question> questions;
    private final HashMap<Question, Character> USER_ANSWERS = new HashMap<>();
    private final ArrayList<Integer> ORDER_QUESTIONS = new ArrayList<>();

    public AbsSectionManager(Chat chat, Update update, List<Question> questions){
        this.chat = chat;
        this.update = update;
        this.questions = questions;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }


    @Override
    public void start() {
        initOrderQuestions();
    }

    @Override
    public void initOrderQuestions(){
        for (Question question : questions){
            ORDER_QUESTIONS.add(question.getId());
        }
        Collections.shuffle(ORDER_QUESTIONS);
    }

    public void setAnswer(){

    }

    @Override
    public SendMessage sendQuestion() {
        SendMessage sendMessage = new SendMessage();

        if (!(ORDER_QUESTIONS.isEmpty())){
            int num = ORDER_QUESTIONS.get(0);
            Question question = questions.get(num);
            String questionTxt = question.getQuestionTxt();

            if (question.getType() == AnswerType.CHOICE){
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

                if (question.getMediaFiles() != null){
                    //надо добавить сюда голосовые сообщения
                    Message message = new Message();

                    Voice voice = new Voice();

                }

                List<InlineKeyboardButton> row1 = new ArrayList<>(List.of(new InlineKeyboardButton().builder().text(question.getAnswerA()).callbackData("a-" + num + "-chi-ci").build()));
                List<InlineKeyboardButton> row2 = new ArrayList<>(List.of(new InlineKeyboardButton().builder().text(question.getAnswerB()).callbackData("b-" + num + "-chi-ci").build()));
                List<InlineKeyboardButton> row3 = new ArrayList<>(List.of(new InlineKeyboardButton().builder().text(question.getAnswerC()).callbackData("c-" + num + "-chi-ci").build()));
                keyboard.add(row1);
                keyboard.add(row2);
                keyboard.add(row3);

                if (question.getAnswerD() != null){
                    List<InlineKeyboardButton> row4 = new ArrayList<>(List.of(new InlineKeyboardButton().builder().text(question.getAnswerD()).callbackData("d-" + num + "-chi-ci").build()));
                    keyboard.add(row4);
                }

            }
        } else{
            //end
        }
        return null;
    }
}
