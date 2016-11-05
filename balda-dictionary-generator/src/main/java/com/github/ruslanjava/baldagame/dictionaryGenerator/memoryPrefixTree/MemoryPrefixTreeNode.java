package com.github.ruslanjava.baldagame.dictionaryGenerator.memoryPrefixTree;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

final class MemoryPrefixTreeNode {
    
    private static final MemoryPrefixTreeNode[] EMPTY_CHILDREN = new MemoryPrefixTreeNode[0];
    private static final char[] EMPTY_CHAR_ARRAY = new char[0];
    
    private MemoryPrefixTreeNode parent;
    private char letter;

    private char[] letters;
    private MemoryPrefixTreeNode[] children;

    private byte[] data;

    int offset;

    MemoryPrefixTreeNode(MemoryPrefixTreeNode parent, char letter) {
        this.parent = parent;
        this.letter = letter;
        this.offset = -1;
    }

    MemoryPrefixTreeNode getParent() {
        return parent;
    }

    char getLetter() {
        return letter;
    }
    
    char[] getChildLetters() {
        if (letters != null) {
            return letters;
        }
        return EMPTY_CHAR_ARRAY;
    }
    
    MemoryPrefixTreeNode[] getChildren() {
        if (children != null) {
            return children;
        }
        return EMPTY_CHILDREN;
    }

    /**
     * Возвращает ссылку из текущего узла дерева на возможного ребенка.
     * 
     * @param letter буква, с которой ассоциирован нужный узел-ребенок.
     * @return ребенок, на которого можно перейти по заданной букве или null, если ребенка с такой буквой нет.
     */
    MemoryPrefixTreeNode getChild(char letter) {
        if (children != null) {
            int index = Arrays.binarySearch(letters, letter);
            if (index >= 0) {
                return children[index];
            }
        }
        return null;
    }
    
    /**
     * Добавляет ссылку из текущего узла на дочерний узел с заданной буквой и возвращает букву.
     * 
     * @param letter буква, с которой связан текущий ребенок.
     *
     * @return ссылка на созданного или уже имеющегося ребенка.
     */
    MemoryPrefixTreeNode addChild(char letter) {
        
        MemoryPrefixTreeNode oldChild = getChild(letter);
        if (oldChild != null) {
            return oldChild;
        }

        MemoryPrefixTreeNode newChild = new MemoryPrefixTreeNode(this, letter);
        if (children == null) {
            letters = new char[1];
            children = new MemoryPrefixTreeNode[1];
            letters[0] = letter;
            children[0] = newChild;
        } else {
            char[] newLetters = Arrays.copyOf(letters, letters.length + 1);
            MemoryPrefixTreeNode[] newChildren = Arrays.copyOf(children, children.length + 1);
            int position = letters.length;
            for (int i = letters.length - 1; i >= 0; i--) {
                if (newLetters[i] < letter) {
                    break;
                }
                newLetters[i + 1] = newLetters[i];
                newChildren[i + 1] = newChildren[i];
                position--;
            }
            newLetters[position] = letter;
            newChildren[position] = newChild;
            letters = null;
            children = null;
            letters = newLetters;
            children = newChildren;
        } 
        
        return newChild;
    }
    
    byte[] getData() {
        return data;
    }
    
    void setData(byte[] data) {
        this.data = data;
    }

    int getSize() {

        int result = 4; // parent offset 
        
        result += 2;    // letter                   
        
        result += 4;   // letters.length                     
        if (letters != null) {
            result += 2 * letters.length; // char[] letters
            result += 4 * letters.length; // BigTreeNode[] children  
        }
        
        result += 4; // valueSize
        
        if (data != null && data.length > 0) {
            result += data.length; // valueOffset
        }
        return result;
    }
    
    void write(DataOutputStream dos) throws IOException {
        
        int parentOffset = parent != null ? parent.offset : -1;
        
        if (parentOffset != -1) {
            dos.writeInt(parentOffset);
        } else {
            dos.writeInt(0);
        }
        
        dos.writeChar(letter);
        
        if (letters != null) {
            dos.writeInt(letters.length);
            for (int i = 0; i < letters.length; i++) {
                dos.writeChar(letters[i]);
                dos.writeInt(children[i].offset);
            }
        } else {
            dos.writeInt(0);
        }
        
        if (data != null && data.length > 0) {
            dos.writeInt(data.length);
            dos.write(data, 0, data.length);
        } else {
            dos.writeInt(0);
        }
        
        dos.flush();
    }
    
}
