package org.company.data;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.company.model.AnswerType;
import org.company.model.Question;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
public class QuestionsLoader {
    private static final Logger logger = Logger.getLogger(QuestionsLoader.class);


    //Добавить проверку
    public List<Question> getQuestionList(String tag) {
        String questionDir = "/media/" + tag + "/";
        logger.debug(questionDir + "tasks.csv reading");
        List<Question> questions = new ArrayList<>();

        List<String[]> strings;
        try (CSVReader reader = new CSVReaderBuilder(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(questionDir + "tasks.csv")))).withSkipLines(1).build()) {
            strings = reader.readAll();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            parseToQuestionList(strings, questions);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        logger.debug("question for" + tag + "have been loaded");
        return questions;
    }

    private void parseToQuestionList(List<String[]> strings, List<Question> questions) throws IOException {
        for (String[] arr : strings) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = new String(arr[i].getBytes(), StandardCharsets.UTF_8);
            }
            Question question = new Question();
            question.setQuestionTxt(arr[0]);
            question.setAnswerA(arr[1]);
            question.setAnswerB(arr[2]);
            question.setAnswerC(arr[3]);
            question.setAnswerD(arr[4]);
            question.setAnswerE(arr[5]);
            question.setRightAnswer(arr[6]);

            List<String> mediaFiles = new ArrayList<>();
            if (!(arr[7].equals(""))) {
                mediaFiles.add(arr[7]);
            }
            if (!(arr[8].equals(""))) {
                mediaFiles.add(arr[8]);
            }
            if (!(arr[9].equals(""))) {
                mediaFiles.add(arr[9]);
            }
            question.setMediaFiles(mediaFiles);

            if (arr[10].equals("choice")) {
                question.setType(AnswerType.CHOICE);
            } else if (arr[10].equals("input")) {
                question.setType(AnswerType.INPUT);
            }

            questions.add(question);
        }
    }
}
