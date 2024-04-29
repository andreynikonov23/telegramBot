package org.company.model;

import java.util.HashSet;
import java.util.Set;

public class TaskTags {
    public static final String CHI_CI_TAG = "chi-ci";
    public static final String ASPIRATED_INITIALS_TAG = "aspirated-initials";
    public static final String BACK_LANG_FINALS_TAG = "back-lang-finals";
    public static final String JQX_INITIALS_TAG = "jqx-initials";
    public static final String R_INITIAL_TAG = "r-initials";
    public static final String SPECIAL_FINALS_TAG = "special-final";
    public static final String IAN_IANG_TAG = "ian-iang";
    public static final String E_FINAL_TAG = "e-final";
    public static final String U_FINAL_TAG = "u-final";
    public static final String UNIT = "unit";
    private static final Set<String> ALL_TAGS = new HashSet<>(Set.of(ASPIRATED_INITIALS_TAG,
            CHI_CI_TAG,
            BACK_LANG_FINALS_TAG,
            JQX_INITIALS_TAG,
            R_INITIAL_TAG,
            SPECIAL_FINALS_TAG,
            IAN_IANG_TAG,
            E_FINAL_TAG,
            U_FINAL_TAG));


    public static boolean isTag(String tag) {
        return ALL_TAGS.contains(tag);
    }

    public static Set<String> getAllTags() {
        return new HashSet<>(ALL_TAGS);
    }
}
