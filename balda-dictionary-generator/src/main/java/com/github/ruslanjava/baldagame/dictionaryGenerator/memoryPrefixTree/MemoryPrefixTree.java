package com.github.ruslanjava.baldagame.dictionaryGenerator.memoryPrefixTree;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MemoryPrefixTree {
    
    private MemoryPrefixTreeNode root;
    
    public MemoryPrefixTree() {
        root = new MemoryPrefixTreeNode(null, (char) 0);
    }

    public void add(String word, byte[] data) {
        MemoryPrefixTreeNode node = root;
        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            node = node.addChild(letter);
        }
        node.setData(data);
    }

    public void write(DataOutputStream dos) throws IOException {
        calculateNodeOffsets(0);
        writeNodes(dos);
    }
    
    private int calculateNodeOffsets(int offset) {
        List<MemoryPrefixTreeNode> oldChildren = new ArrayList<>();
        oldChildren.add(root);
        List<MemoryPrefixTreeNode> newChildren = new ArrayList<>();
        while (oldChildren.size() > 0) {
            
            for (MemoryPrefixTreeNode oldChild : oldChildren) {
                oldChild.offset = offset;
                offset += oldChild.getSize();
                
                MemoryPrefixTreeNode[] tempChildren = oldChild.getChildren();
                Collections.addAll(newChildren, tempChildren);
            }
            List<MemoryPrefixTreeNode> temp = oldChildren;
            oldChildren = newChildren;
            newChildren = temp;
            newChildren.clear();
        }
        return offset;
    }
    
    private void writeNodes(DataOutputStream dos) throws IOException {
        List<MemoryPrefixTreeNode> oldChildren = new ArrayList<>();
        oldChildren.add(root);
        List<MemoryPrefixTreeNode> newChildren = new ArrayList<>();
        while (oldChildren.size() > 0) {
            for (MemoryPrefixTreeNode oldChild : oldChildren) {
                oldChild.write(dos);
                MemoryPrefixTreeNode[] tempChildren = oldChild.getChildren();
                Collections.addAll(newChildren, tempChildren);
            }
            List<MemoryPrefixTreeNode> temp = oldChildren;
            oldChildren = newChildren;
            newChildren = temp;
            newChildren.clear();
        }
    }

}
