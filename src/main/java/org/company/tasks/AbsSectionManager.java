package org.company.tasks;

import org.company.utils.QuestionsLoader;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
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
    private final HashMap<Integer, Character> USER_ANSWERS = new HashMap<>();
    private final ArrayList<Integer> ORDER_QUESTIONS = new ArrayList<>();

    public AbsSectionManager(Chat chat, Update update){
        this.chat = chat;
        this.update = update;
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
    public void shuffleOrderQuestions(){
        for (int i = 0; i < questions.size(); i++) {
            ORDER_QUESTIONS.add(i + 1);
        }
        Collections.shuffle(ORDER_QUESTIONS);
    }
    @Override
    public void addAnswer(Integer numberOfQuestion, Character answer){
        USER_ANSWERS.put(numberOfQuestion, answer);
    }

    @Override
    public void sendQuestion() {
        if (ORDER_QUESTIONS.size() != 0){
            Question question = questions.get(ORDER_QUESTIONS.get(0));
            String questionTxt = question.getQuestion();
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

            if (question.)
        }
    }
}
