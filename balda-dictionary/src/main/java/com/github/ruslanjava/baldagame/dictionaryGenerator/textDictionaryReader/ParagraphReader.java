package com.github.ruslanjava.baldagame.dictionaryGenerator.textDictionaryReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ParagraphReader implements AutoCloseable {

    private TextParagraphReader reader;
    private PluralReplacer pluralReplacer;

    public ParagraphReader(BufferedReader reader) {
        this.reader = new TextParagraphReader(reader);
        pluralReplacer = new PluralReplacer();
    }

    public Paragraph readParagraph() throws IOException {
        String line;
        while ((line = reader.readParagraph()) != null) {
            Paragraph paragraph = parseParagraph(line);
            if (paragraph != null) {
                return paragraph;
            }
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    private Paragraph parseParagraph(String line) throws UnsupportedEncodingException {
        // разделительная запятая должна встретиться в первых 25 символах:
        // "АБАЖУР,  -а,  м. Колпак для лампы,  светильника.  Зеленый  а. ||  прил. абажурный, -ая, -ое."
        int commaIndex = line.indexOf(',');
        if (commaIndex == -1 || commaIndex > 25) {
            return null;
        }

        // слово должно содержать только буквы:
        // "АБАЖУР"
        String word = line.substring(0, commaIndex).toLowerCase();
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (!Character.isLetter(ch)) {
                return null;
            }
        }

        String value = line.substring(commaIndex + 1);
        return parseParagraph(word, value);
    }

    private Paragraph parseParagraph(String word, String value) {
        // текст со значением словарного слова должен начинаться с первой заглавной буквы:
        // "Колпак для лампы, светильника."
        int capitalLetterIndex = getCapitalLetterIndex(value);
        if (capitalLetterIndex == -1) {
            return null;
        }

        String meaning = value.substring(capitalLetterIndex);
        String markers = value.substring(0, capitalLetterIndex);
        // текст между словом и его значением должен содержать род существительного:
        // "АБАЖУР,  -а,  м.
        if (!markers.contains("м.") && !markers.contains("ж.") && !markers.contains("ср.")) {
            return null;
        }

        // ед. - признак слова, записанного в словаре во множественном числе
        int singularIndex = markers.indexOf("ед.");
        if (singularIndex != -1 && !markers.contains("ед.)")) {
            word = pluralReplacer.replace(word, markers);
            return parseParagraphFinal(word, meaning);
        }
        return parseParagraphFinal(word, meaning);
    }

    private Paragraph parseParagraphFinal(String word, String meaning) {
        if (word != null && word.length() > 1 && meaning.length() > 10) {
            return new Paragraph(word, meaning);
        }
        return null;
    }

    private int getCapitalLetterIndex(String word) {
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (Character.isUpperCase(ch) || Character.isDigit(ch)) {
                return i;
            }
        }
        return -1;
    }

}
