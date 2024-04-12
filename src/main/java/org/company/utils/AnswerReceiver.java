package org.company.utils;

import org.company.bot.TelegramBot;
import org.company.model.AnswerType;
import org.company.service.*;


public class AnswerReceiver {
    private TelegramBot bot;
    public static final String CHI_CI_TAG = "chi-ci";
    public static final String ASPIRATED_INITIALS_TAG = "aspirated-initials";
    public static final String BACK_LANG_FINALS_TAG = "back-lang-finals";
    public static final String JQX_INITIALS_TAG = "jqx-initials";
    public static final String R_INITIAL_TAG = "r-inital";
    public static final String SPECIAL_FINALS_TAG = "special-final";
    public static final String IAN_IANG_TAG = "ian-iang";
    public static final String E_FINAL_TAG = "e-final";
    public static final String U_FINAL_TAG = "u-final";

    public AnswerReceiver(TelegramBot bot) {
        this.bot = bot;
    }

    public void setCallbackAnswer(long chatId, String answerTag){
        String[] arr = answerTag.split("_");
        String answer = arr[0];
        int numberOfQuestion = Integer.parseInt(arr[1]);
        String tag = arr[2];
        switch (tag) {
            case (CHI_CI_TAG) -> {
                ChiCiSectionManager chiCiSectionManager;
                for (AbsSectionManager sectionManager : ActiveTests.getSectionManagersSet(chatId)){
                    if (sectionManager.getTag().equals(CHI_CI_TAG)){
                        chiCiSectionManager = (ChiCiSectionManager) sectionManager;
                        chiCiSectionManager.setCallbackAnswer(numberOfQuestion, answer);
                    }
                }
            }
            case (ASPIRATED_INITIALS_TAG) ->{
                AspiratedInitialsSectionManager aspiratedInitialsSectionManager;
                for (AbsSectionManager sectionManager : ActiveTests.getSectionManagersSet(chatId)){
                    if (sectionManager.getTag().equals(ASPIRATED_INITIALS_TAG)){
                        aspiratedInitialsSectionManager = (AspiratedInitialsSectionManager) sectionManager;
                        aspiratedInitialsSectionManager.setCallbackAnswer(numberOfQuestion, answer);
                    }
                }
            }
            case (BACK_LANG_FINALS_TAG) ->{
                BackLangFinalsSectionManager backLangFinalsSectionManager;
                for (AbsSectionManager sectionManager : ActiveTests.getSectionManagersSet(chatId)){
                    if (sectionManager.getTag().equals(BACK_LANG_FINALS_TAG)){
                        backLangFinalsSectionManager = (BackLangFinalsSectionManager) sectionManager;
                        backLangFinalsSectionManager.setCallbackAnswer(numberOfQuestion, answer);
                    }
                }
            }
            case (E_FINAL_TAG) ->{
                EFinalSectionManager eFinalSectionManager;
                for (AbsSectionManager sectionManager : ActiveTests.getSectionManagersSet(chatId)){
                    if (sectionManager.getTag().equals(E_FINAL_TAG)){
                        eFinalSectionManager = (EFinalSectionManager) sectionManager;
                        eFinalSectionManager.setCallbackAnswer(numberOfQuestion, answer);
                    }
                }
            }
            case (IAN_IANG_TAG) ->{
                IanIangSectionManager ianIangSectionManager;
                for (AbsSectionManager sectionManager : ActiveTests.getSectionManagersSet(chatId)){
                    if (sectionManager.getTag().equals(IAN_IANG_TAG)){
                        ianIangSectionManager = (IanIangSectionManager) sectionManager;
                        ianIangSectionManager.setCallbackAnswer(numberOfQuestion, answer);
                    }
                }
            }
            case (JQX_INITIALS_TAG) ->{
                JqxInitialsSectionManager jqxInitialsSectionManager;
                for (AbsSectionManager sectionManager : ActiveTests.getSectionManagersSet(chatId)){
                    if (sectionManager.getTag().equals(JQX_INITIALS_TAG)){
                        jqxInitialsSectionManager = (JqxInitialsSectionManager) sectionManager;
                        jqxInitialsSectionManager.setCallbackAnswer(numberOfQuestion, answer);
                    }
                }
            }
            case (R_INITIAL_TAG) ->{
                RInitialsSectionManager rInitialsSectionManager;
                for (AbsSectionManager sectionManager : ActiveTests.getSectionManagersSet(chatId)){
                    if (sectionManager.getTag().equals(R_INITIAL_TAG)){
                        rInitialsSectionManager = (RInitialsSectionManager) sectionManager;
                        rInitialsSectionManager.setCallbackAnswer(numberOfQuestion, answer);
                    }
                }
            }
            case (SPECIAL_FINALS_TAG) ->{
                SpecialFinalSectionManager specialFinalSectionManager;
                for (AbsSectionManager sectionManager : ActiveTests.getSectionManagersSet(chatId)){
                    if (sectionManager.getTag().equals(SPECIAL_FINALS_TAG)){
                        specialFinalSectionManager = (SpecialFinalSectionManager) sectionManager;
                        specialFinalSectionManager.setCallbackAnswer(numberOfQuestion, answer);
                    }
                }
            }
            case (U_FINAL_TAG) ->{
                UFinalSectionManager uFinalSectionManager;
                for (AbsSectionManager sectionManager : ActiveTests.getSectionManagersSet(chatId)){
                    if (sectionManager.getTag().equals(U_FINAL_TAG)){
                        uFinalSectionManager = (UFinalSectionManager) sectionManager;
                        uFinalSectionManager.setCallbackAnswer(numberOfQuestion, answer);
                    }
                }
            }
        }
    }
    public void setText(long chatId, String text){
        //тут проверка
        AbsSectionManager sectionManager = ActiveTests.getActiveSectionManager(chatId);
        if (sectionManager.getActiveQuestion().getType() == AnswerType.INPUT){
            sectionManager.setTextAnswer(text);
        } else {
            bot.sendMessage(chatId, "Команда не распознана");
        }
    }
}
