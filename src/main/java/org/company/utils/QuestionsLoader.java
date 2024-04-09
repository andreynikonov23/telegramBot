package org.company.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.company.tasks.AnswerType;
import org.company.tasks.Question;
import org.springframework.core.io.Resource;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Удалить hi-ci list чтобы сразу брать из файла а не из озу
@NoArgsConstructor
public class QuestionsLoader {
    private static final Logger logger = Logger.getLogger(QuestionsLoader.class);
    private Resource chiCiLocation;
    private List<Question> chiCiQuestions;

    public QuestionsLoader(Resource chiCiLocation) {
        this.chiCiLocation = chiCiLocation;
        initChiCiQuestionsList();
    }

    public Resource getChiCiLocation() {
        return chiCiLocation;
    }

    public List<Question> getChiCiQuestions() {
        return chiCiQuestions;
    }

    public void setChiCiLocation(Resource chiCiLocation) {
        this.chiCiLocation = chiCiLocation;
    }

    //Добавить проверку
    public void initChiCiQuestionsList() {
        logger.debug("/chi-ci/tasks.csv reading");
        chiCiQuestions = new ArrayList<>();
        List<String[]> strings;
        try(CSVReader reader = new CSVReaderBuilder(new FileReader(chiCiLocation.getFile().getAbsolutePath())).withSkipLines(1).build()) {;
            strings = reader.readAll();
        } catch (IOException | CsvException e) {
            logger.error("/chi-ci/tasks.csv reading error\n" + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
        parseToQuestionList(strings, chiCiQuestions);
        logger.debug("question for chi-ci have been loaded");
    }

    private void parseToQuestionList(List<String[]> strings, List<Question> questions){
        int id = 1;
        for (String[] arr : strings){
            Question question = new Question();
            question.setId(id);
            question.setQuestionTxt(arr[0]);
            question.setAnswerA(arr[1]);
            question.setAnswerB(arr[2]);
            question.setAnswerC(arr[3]);
            question.setAnswerD(arr[4]);
            question.setRightAnswer(arr[5]);
            question.setMediaFiles(new ArrayList<>(List.of(arr[6], arr[7], arr[8])));
            if (arr[9].equals("choice")){
                question.setType(AnswerType.CHOICE);
            } else if (arr[9].equals("input")){
                question.setType(AnswerType.INPUT);
            }
            questions.add(question);
            id++;
        }
    }
}
