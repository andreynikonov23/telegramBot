package org.company.service;

import lombok.NoArgsConstructor;
import org.company.bot.TelegramBot;
import org.company.model.Question;

import java.util.List;

@NoArgsConstructor
public class ChiCiSectionManager extends AbsSectionManager{
    public ChiCiSectionManager(long chatId, List<Question> questions) {
        super(chatId, questions);
        setTag("chi-ci");
    }
}
