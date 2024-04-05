package org.company.tasks;

import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

@Data
public abstract class AbsSectionManager implements SectionManager {
    private Chat chat;
    private Update update;
    private Integer testSize;
    private final HashMap<Integer, Character> USER_ANSWERS = new HashMap<>();
    private final ArrayList<Integer> numbersOfQuestionsMix = new ArrayList<>();

    public AbsSectionManager(Chat chat, Update update){
        this.chat = chat;
        this.update = update;
        testSize = 5;
    }
}
