package com.github.ruslanjava.baldagame.prefixTree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * <p>Узел префиксного дерева. Каждый узел является отдельной буквой слово.
 * 
 * <p>Узел может:
 * <ul>
 * <li>Являться началом всех  слов. Такой узел будет корнем префиксного дерева,</li>
 * <li>Иметь детей. Такой узел будет представлять первую или промежуточную букву слова.</li>
 * <li>Иметь ненулевую ссылку на область данных (ненулевое смещение). 
 * Такой узел будет представлять последнюю букву некоторого слова.</li>
 * </ul>
 * 
 * <p>Все узлы префиксного дерева хранятся в одном большом файле.
 * Каждый узел дерева записан в файле следующим образом:
 * <pre>
 * +-----------------+----------------------------+------------------------------------------+
 * | Поле            | Длина (в байтах)           | Назначение                               |
 * +-----------------+----------------------------+------------------------------------------+
 * | parentOffset    | 4                          | позиция узла-родителя в файле            |
 * +-----------------+----------------------------+------------------------------------------+
 * | letter          | 2                          | буква, которую представляет текущий узел |
 * +-----------------+----------------------------+------------------------------------------+
 * | childCount      | 4                          | количество детей (N)                     |
 * +-----------------+----------------------------+------------------------------------------+
 * | letters         | 2 * childCount             | список букв узлов-детей текущего узла    |
 * +-----------------+----------------------------+------------------------------------------+
 * | childrenOffsets | 4 * childCount             | позиции узлов-детей в файле              |
 * +-----------------+----------------------------+------------------------------------------+
 * | valueSize       | 4                          | размер области данных                    |
 * +-----------------+----------------------------+------------------------------------------+
 * | valueOffset     | 4                          | позиция области данных в файле           |
 * +-----------------+----------------------------+------------------------------------------+
 * </pre>
 * 
 * @author Руслан Щучинов
 */
public final class FilePrefixTreeNode {
    
    // ссылка на файл c быстрым чтением данных из произвольного его участка
    private final RandomAccessFile file;
    
    // смещение в файле, по которому находится узел-родитель текущего узла
    private final int parentOffset;
    
    // буква, которую представляет текущий узел
    private final char letter;
    
    // отсортированный список букв-детей, т.е. букв, продолжающих текущее начало слова.
    private final char[] letters;
    
    // список смещений узлов-детей в файле, соответствующий указанным в letters буквам.
    private final int[] childrenOffsets;
    
    // размер области данных, если она есть
    private final int valueSize;
    
    // смещение в файле, с которого начинается область данных 
    private final int valueOffset;
    
    /**
     * Конструктор.
     */
    FilePrefixTreeNode(RandomAccessFile file, int offset) {
        this.file = file;
        try {
            file.seek(offset);

            parentOffset = file.readInt();
            letter = file.readChar();

            int childCount = file.readInt();
            if (childCount > 100) {
                throw new IOException("Wrong child count:" + childCount);
            }

            letters = new char[childCount];
            childrenOffsets = new int[childCount];
            for (int i = 0; i < childCount; i++) {
                letters[i] = file.readChar();
                childrenOffsets[i] = file.readInt();
            }

            valueSize = file.readInt();
            if (valueSize > 0) {
                valueOffset = offset + 4 + 2 + 4 + (letters.length * 6) + 4;
            } else {
                valueOffset = -1;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    
    /**
     * Возвращает узел-родитель текущего узла или null, если узел является корнем префиксного дерева.
     * 
     * @return родитель текущего дерева. 
     * @throws IOException если не удалось прочитать структуру узла-родителя из файла.
     */
    public FilePrefixTreeNode getParent() throws IOException {
        if (parentOffset != 0) {
            return new FilePrefixTreeNode(file, parentOffset);
        }
        return null;
    }

    /**
     * Возвращает ребенка по заданной букве. Новый узел будет представлять букву letter, продолжающую текущее слово.
     * 
     * @param letter буква, по которой отыскивается нужный ребенок в префиксном дереве.
     * 
     * @return ребенок, представляющий букву letter.
     * @throws IOException если не удалось прочитать структуру ребенка в файле
     */
    public FilePrefixTreeNode getChild(char letter) {
        int index = Arrays.binarySearch(letters, letter);
        if (index >= 0) {
            try {
                return new FilePrefixTreeNode(file, childrenOffsets[index]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    
    /**
     * Возвращает буквы всех имеющихся детей в отсортированном порядке.
     * 
     * @return буквы всех имеющихся детей.
     */
    public char[] getChildLetters() {
        return letters;
    }
    
    /**
     * Определяет, имеет ли заданный узел значение (ссылку на область данных) или нет.
     * 
     * @return true, если узел имеет значение (ссылку на область данных).
     */
    public boolean hasValue() {
        return valueSize > 0;
    }
    
    /**
     * Возвращает значение, хранимое заданным узлом в виде байтового массива.
     * 
     * @return значение или null, если узел не содержит значение.
     * @throws IOException если возникла ошибка чтения значения (области данных) из файла.
     */
    public byte[] getValue() throws IOException {
        if (hasValue()) {
            file.seek(valueOffset);
            byte[] value = new byte[valueSize];
            file.read(value);
            return value;
        }
        return null;
    }

    /**
     * Возвращает представление текущего узла в виде строки. Строка содержит все буквы пути от корня
     * до текущего узла, исключая букву корня и включая букву текущего узла.
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        try {
            return toString(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
    
    /**
     * Возвращает представление в узла в виде строки.
     * 
     * @param buffer буфер, в который записываются буквы строки.
     * 
     * @return представление узла в виде строки. Строка содержит все буквы пути от корня до текущего узла,
     * исключая букву корня и включая букву текущего узла.
     * @throws IOException если не удалось прочитать из файла структуру очередного узла префиксного дерева.
     */
    String toString(StringBuilder buffer) throws IOException {
        buffer.setLength(0);
        FilePrefixTreeNode node = this;
        while (node != null) {
            if (node.letter != (char) 0) {
                buffer.append(node.letter);
            }
            node = node.getParent();
        }
        return buffer.reverse().toString();
    }

}
