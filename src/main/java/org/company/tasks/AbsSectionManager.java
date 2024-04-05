package org.company.tasks;

import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;

@Data
public abstract class AbsSectionManager implements SectionManager {
    private Chat chat;
    private HashMap<Integer, Character> userAnswers = new HashMap<>();
    private Integer answerCount;
    private Update update;

    public AbsSectionManager(Chat chat, Update update){
        this.chat = chat;
        this.update = update;
        answerCount = 5;
    }




    protected void mix(){

    }

    @Override
    public void question1() {

    }

    @Override
    public void question2() {

    }

    @Override
    public void question3() {

    }

    @Override
    public void question4() {

    }

    @Override
    public void question5() {

    }
}
