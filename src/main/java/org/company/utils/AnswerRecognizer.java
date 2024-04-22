package org.company.utils;

import org.apache.log4j.Logger;
import org.company.bot.TelegramBot;
import org.company.model.AnswerType;
import org.company.service.*;


public class AnswerRecognizer {
    private TelegramBot bot;
    //Вывести в какой-нибудь отдельный класс
    private static final Logger logger = Logger.getLogger(AnswerRecognizer.class);
    public static final String CHI_CI_TAG = "chi-ci";
    public static final String ASPIRATED_INITIALS_TAG = "aspirated-initials";
    public static final String BACK_LANG_FINALS_TAG = "back-lang-finals";
    public static final String JQX_INITIALS_TAG = "jqx-initials";
    public static final String R_INITIAL_TAG = "r-initials";
    public static final String SPECIAL_FINALS_TAG = "special-final";
    public static final String IAN_IANG_TAG = "ian-iang";
    public static final String E_FINAL_TAG = "e-final";
    public static final String U_FINAL_TAG = "u-final";

    public AnswerRecognizer(TelegramBot bot) {
        this.bot = bot;
    }

    public void setCallbackAnswer(long chatId, int messageId, String answerTag){
        String[] arr = answerTag.split("_");
        String answer = arr[0];
        int numberOfQuestion = Integer.parseInt(arr[1]);
        String tag = arr[2];
        switch (tag) {
            case (CHI_CI_TAG) -> {
                ChiCiSectionManager chiCiSectionManager;
                for (Test sectionManager : ActiveTests.getTestsSet(chatId)){
                    if (sectionManager.getTag().equals(CHI_CI_TAG)){
                        chiCiSectionManager = (ChiCiSectionManager) sectionManager;
                        chiCiSectionManager.setCallbackAnswer(messageId, numberOfQuestion, answer);
                    }
                }
            }
            case (ASPIRATED_INITIALS_TAG) ->{
                AspiratedInitialsSectionManager aspiratedInitialsSectionManager;
                for (Test sectionManager : ActiveTests.getTestsSet(chatId)){
                    if (sectionManager.getTag().equals(ASPIRATED_INITIALS_TAG)){
                        aspiratedInitialsSectionManager = (AspiratedInitialsSectionManager) sectionManager;
                        aspiratedInitialsSectionManager.setCallbackAnswer(messageId, numberOfQuestion, answer);
                    }
                }
            }
            case (BACK_LANG_FINALS_TAG) ->{
                BackLangFinalsSectionManager backLangFinalsSectionManager;
                for (Test sectionManager : ActiveTests.getTestsSet(chatId)){
                    if (sectionManager.getTag().equals(BACK_LANG_FINALS_TAG)){
                        backLangFinalsSectionManager = (BackLangFinalsSectionManager) sectionManager;
                        backLangFinalsSectionManager.setCallbackAnswer(messageId, numberOfQuestion, answer);
                    }
                }
            }
            case (E_FINAL_TAG) ->{
                EFinalSectionManager eFinalSectionManager;
                for (Test sectionManager : ActiveTests.getTestsSet(chatId)){
                    if (sectionManager.getTag().equals(E_FINAL_TAG)){
                        eFinalSectionManager = (EFinalSectionManager) sectionManager;
                        eFinalSectionManager.setCallbackAnswer(messageId, numberOfQuestion, answer);
                    }
                }
            }
            case (IAN_IANG_TAG) ->{
                IanIangSectionManager ianIangSectionManager;
                for (Test sectionManager : ActiveTests.getTestsSet(chatId)){
                    if (sectionManager.getTag().equals(IAN_IANG_TAG)){
                        ianIangSectionManager = (IanIangSectionManager) sectionManager;
                        ianIangSectionManager.setCallbackAnswer(messageId, numberOfQuestion, answer);
                    }
                }
            }
            case (JQX_INITIALS_TAG) ->{
                JqxInitialsSectionManager jqxInitialsSectionManager;
                for (Test sectionManager : ActiveTests.getTestsSet(chatId)){
                    if (sectionManager.getTag().equals(JQX_INITIALS_TAG)){
                        jqxInitialsSectionManager = (JqxInitialsSectionManager) sectionManager;
                        jqxInitialsSectionManager.setCallbackAnswer(messageId, numberOfQuestion, answer);
                    }
                }
            }
            case (R_INITIAL_TAG) ->{
                RInitialsSectionManager rInitialsSectionManager;
                for (Test sectionManager : ActiveTests.getTestsSet(chatId)){
                    if (sectionManager.getTag().equals(R_INITIAL_TAG)){
                        rInitialsSectionManager = (RInitialsSectionManager) sectionManager;
                        rInitialsSectionManager.setCallbackAnswer(messageId, numberOfQuestion, answer);
                    }
                }
            }
            case (SPECIAL_FINALS_TAG) ->{
                SpecialFinalSectionManager specialFinalSectionManager;
                for (Test sectionManager : ActiveTests.getTestsSet(chatId)){
                    if (sectionManager.getTag().equals(SPECIAL_FINALS_TAG)){
                        specialFinalSectionManager = (SpecialFinalSectionManager) sectionManager;
                        specialFinalSectionManager.setCallbackAnswer(messageId, numberOfQuestion, answer);
                    }
                }
            }
            case (U_FINAL_TAG) ->{
                UFinalSectionManager uFinalSectionManager;
                for (Test sectionManager : ActiveTests.getTestsSet(chatId)){
                    if (sectionManager.getTag().equals(U_FINAL_TAG)){
                        uFinalSectionManager = (UFinalSectionManager) sectionManager;
                        uFinalSectionManager.setCallbackAnswer(messageId, numberOfQuestion, answer);
                    }
                }
            }
        }
    }
    public void setText(long chatId, String text){
        //тут проверка
        Test sectionManager = ActiveTests.getActiveTest(chatId);
        if (sectionManager.getActiveQuestion().getType() == AnswerType.INPUT){
            sectionManager.setTextAnswer(text);
        } else {
            logger.debug(String.format("ChatId=%d unknown text (%s) message", chatId, text));
            bot.sendMessage(chatId, "Команда не распознана");
        }
    }
}
