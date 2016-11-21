package com.github.ruslanjava.baldagame.dictionaryGenerator.textDictionaryReader;

public class Paragraph {

    private String word;
    private String meaning;

    Paragraph(String word, String meaning) {
        this.word = word;
        this.meaning = meaning;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }

}
