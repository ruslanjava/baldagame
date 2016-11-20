package com.github.ruslanjava.baldagame.dictionaryGenerator.textDictionaryReader;

import java.io.BufferedReader;
import java.io.IOException;

class TextParagraphReader implements AutoCloseable {

    private BufferedReader reader;
    private String unreadLine;

    private StringBuilder paragraphBuilder;

    TextParagraphReader(BufferedReader reader) {
        this.reader = reader;
        paragraphBuilder = new StringBuilder();
    }

    String readParagraph() throws IOException {
        String result;

        String line;
        while ((line = readLine()) != null) {
            if (line.startsWith("     ")) {
                if (paragraphBuilder.length() > 0) {
                    result = paragraphBuilder.toString();
                    paragraphBuilder.setLength(0);
                    unreadLine = line;
                    return result;
                } else {
                    paragraphBuilder.append(line.trim());
                }
            } else {
                if (paragraphBuilder.length() > 0) {
                    if (paragraphBuilder.charAt(paragraphBuilder.length() - 1) != ' ') {
                        paragraphBuilder.append(' ');
                    }
                    paragraphBuilder.append(line.trim());
                }
            }
        }

        if (paragraphBuilder.length() > 0) {
            result = paragraphBuilder.toString();
            paragraphBuilder.setLength(0);
            return result;
        }

        return null;
    }

    private String readLine() throws IOException {
        if (unreadLine != null) {
            String result = unreadLine;
            unreadLine = null;
            return result;
        }
        return reader.readLine();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

}
