package org.company.tasks;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface SectionManager {
    void start();
    void initOrderQuestions();
    void setAnswer(Integer numberOfQuestion, Character answer);
    SendMessage sendQuestion();
    void result();

}
