package org.company.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.company.model.AnswerType;
import org.company.model.Question;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Удалить hi-ci list чтобы сразу брать из файла а не из озу
@NoArgsConstructor
public class QuestionsLoader {
    private static final Logger logger = Logger.getLogger(QuestionsLoader.class);
    private Resource media;


    public QuestionsLoader(Resource media) throws IOException {
        this.media = media;
    }

    public Resource getMedia() {
        return media;
    }

    public void setMedia(Resource media) {
        this.media = media;
    }


    //Добавить проверку
    public List<Question> getChiCiQuestionsList() {
        return getQuestionList(AnswerReceiver.CHI_CI_TAG);
    }
    public List<Question> getAspiratedInitialsQuestionsList(){
        return getQuestionList(AnswerReceiver.ASPIRATED_INITIALS_TAG);
    }
    public List<Question> getBackLangFinalsQuestionsList(){
        return getQuestionList(AnswerReceiver.BACK_LANG_FINALS_TAG);
    }
    public List<Question> getEFinalQuestionsList(){
        return getQuestionList(AnswerReceiver.E_FINAL_TAG);
    }
    public List<Question> getJqxInitialsQuestionsList(){
        return getQuestionList(AnswerReceiver.JQX_INITIALS_TAG);
    }
    public List<Question> getRInitialsQuestionsList(){
        return getQuestionList(AnswerReceiver.R_INITIAL_TAG);
    }
    public List<Question> getSpecialFinalQuestionsList(){
        return getQuestionList(AnswerReceiver.SPECIAL_FINALS_TAG);
    }
    public List<Question> getIanIangQuestionsList(){
        return getQuestionList(AnswerReceiver.IAN_IANG_TAG);
    }
    public List<Question> getUFinalQuestionsList(){
        return getQuestionList(AnswerReceiver.U_FINAL_TAG);
    }

    private List<Question> getQuestionList(String tag) {
        String dir = "/" + tag + "/";
        logger.debug(dir + "tasks.csv reading");
        List<Question> questions = new ArrayList<>();
        List<String[]> strings;
        try(CSVReader reader = new CSVReaderBuilder(new FileReader(media.getFile().getAbsolutePath() + dir + "tasks.csv")).withSkipLines(1).build()) {
            strings = reader.readAll();
        } catch (IOException | CsvException e) {
            logger.error(dir + "tasks.csv reading error\n" + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
        try {
            parseToQuestionList(strings, questions, dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.debug("question for chi-ci have been loaded");
        return questions;
    }
    private void parseToQuestionList(List<String[]> strings, List<Question> questions, String dir) throws IOException {
        int id = 1;
        for (String[] arr : strings){
            Question question = new Question();
            question.setId(id);
            question.setQuestionTxt(arr[0]);
            question.setAnswerA(arr[1]);
            question.setAnswerB(arr[2]);
            question.setAnswerC(arr[3]);
            question.setAnswerD(arr[4]);
            question.setAnswerE(arr[5]);
            question.setRightAnswer(arr[6]);

            loadFiles(arr, question, dir);

            if (arr[10].equals("choice")){
                question.setType(AnswerType.CHOICE);
            } else if (arr[10].equals("input")){
                question.setType(AnswerType.INPUT);
            }
            questions.add(question);
            id++;
        }
    }
    private void loadFiles(String[] arr, Question question, String dir) throws IOException {
        List<File> mediaFiles = new ArrayList<>();
        if (arr[7] != null){
            File file = new File(media.getFile().getAbsolutePath() + dir + arr[7]);
            mediaFiles.add(file);
        }
        if (arr[8] != null){
            File file = new File(media.getFile().getAbsolutePath() + dir + arr[8]);
            mediaFiles.add(file);
        }
        if (arr[9] != null){
            File file = new File(media.getFile().getAbsolutePath() + dir + arr[9]);
            mediaFiles.add(file);
        }
        question.setMediaFiles(mediaFiles);
    }

}
