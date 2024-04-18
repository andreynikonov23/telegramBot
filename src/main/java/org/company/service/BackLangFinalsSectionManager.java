package org.company.service;

import lombok.NoArgsConstructor;
import org.company.model.Question;

import java.util.List;

@NoArgsConstructor
public class BackLangFinalsSectionManager extends AbsSectionManager{
    public BackLangFinalsSectionManager(long chatId, List<Question> questions) {
        super(chatId, questions);
        setTag("back-lang-finals");
    }
}
