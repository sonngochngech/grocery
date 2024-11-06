package com.grocery.app.config.constant;

public enum TermConfig {
    BREAKFAST("Breakfast"),
    BRUNCH("Brunch"),
    LUNCH("Lunch"),
    SNACK("Snack"),
    DINNER("Dinner"),
    SUPPER("Supper")
    ;

    private final String term;

    TermConfig(String term){
        this.term = term;
    }

    public String getTerm() {
        return term;
    }
}
