package org.company.tasks;

import lombok.Data;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Chat;

import java.util.HashMap;

@Data
public abstract class AbstractTask {
    private Integer questionCount;
    private Chat chat;
    private final HashMap<Integer, String> COMPLETED = new HashMap<>();
    private String questionStr;
    private String answerA;



    public AbstractTask(Chat chat){
        questionCount=5;
    }


    public void start() {

    }

    protected void mix(){}

}
