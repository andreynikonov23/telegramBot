package org.company.service;

import lombok.NoArgsConstructor;
import org.company.bot.TelegramBot;
import org.company.model.Question;

import java.util.List;

@NoArgsConstructor
public class UFinalSectionManager extends AbsSectionManager{
    public UFinalSectionManager(long chatId, List<Question> questions) {
        super(chatId, questions);
        setTag("u-final");
    }
}
