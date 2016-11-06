package com.github.ruslanjava.baldagame.dictionaryGenerator;

import com.github.ruslanjava.baldagame.dictionaryGenerator.memoryPrefixTree.MemoryPrefixTree;
import com.github.ruslanjava.baldagame.dictionaryGenerator.textDictionaryReader.Paragraph;
import com.github.ruslanjava.baldagame.dictionaryGenerator.textDictionaryReader.ParagraphReader;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class DictionaryGenerator {

    private static final String[] FILE_NAMES = {
            "ozhegow_a_d.txt",
            "ozhegow_e_l.txt",
            "ozhegow_m_o.txt",
            "ozhegow_p_r.txt",
            "ozhegow_s_q.txt"
    };

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            args = new String[] { "dictionary.rdict" };
        }

        String destionationFile = args[0];
        MemoryPrefixTree tree = new MemoryPrefixTree();

        for (String fileName : FILE_NAMES) {
            InputStreamReader isReader = new InputStreamReader(
                    // DictionaryGenerator.class.getResourceAsStream(fileName),
                    new FileInputStream("/home/ruslan/android/baldagame/balda-game-solver-dictionary-generator/src/main/resources/com/github/ruslanjava/baldagame/dictionaryGenerator/" + fileName),
                    "koi8-r"
            );
            try (ParagraphReader reader = new ParagraphReader(new BufferedReader(isReader))) {
                Paragraph paragraph;
                while ((paragraph = reader.readParagraph()) != null) {
                    String word = paragraph.getWord();
                    String meaning = paragraph.getMeaning();
                    tree.add(word, meaning.getBytes("UTF-8"));
                }
            }
        }

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(destionationFile))) {
            tree.write(dos);
        }
    }

}
