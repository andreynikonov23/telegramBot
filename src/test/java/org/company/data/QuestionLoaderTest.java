package org.company.data;

import org.company.config.SpringConfig;
import org.company.model.AnswerType;
import org.company.model.Question;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class QuestionLoaderTest {
    @Test
    public void getQuestionListTest() {
        Mockito.mockStatic(Logger.class);
        QuestionsLoader loader = new AnnotationConfigApplicationContext(SpringConfig.class).getBean(QuestionsLoader.class);

        Question testFirstQuestion = new Question("ian-iang", "Отличается ли произношение гласных звуков в финалях [ian] и [iang]? ", "A. Нет, гласные звуки похожи на русский [e]", "B. Нет, гласные звуки похожи на русский [я] ", "C. В звуке [ian] – [e], в [iang] – [я]", "D. В звуке [ian] – [я], в [iang] – [е]", "", "c", new ArrayList<>(), AnswerType.CHOICE);

        List<Question> questionList = loader.getQuestionList("ian-iang");
        Assertions.assertEquals(5, questionList.size());
        Assertions.assertEquals(testFirstQuestion, questionList.get(0));
    }
}
