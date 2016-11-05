package com.github.ruslanjava.baldagame.gameSolution;

import com.github.ruslanjava.baldagame.prefixTree.FilePrefixTree;
import com.github.ruslanjava.baldagame.prefixTree.FilePrefixTreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameSolver {

    private FilePrefixTreeNode root;
    private CellBoard cellBoard;

    private int[] pathX;
    private int[] pathY;

    public GameSolver(FilePrefixTree tree) {
        cellBoard = new CellBoard();
        root = tree.getRoot();

        pathX = new int[25];
        pathY = new int[25];
    }

    /**
     * Вычисляет все возможные решения для текущей доски.
     *
     * @param board доска 5x5 символов.
     *
     * @return список возможных решений.
     */
    public List<GameSolution> solve(char[][] board) {
        List<GameSolution> result = new ArrayList<>();
        cellBoard.update(board);
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                Cell cell = cellBoard.get(x, y);
                searchSolution(result, root, 0, cell, ' ');
            }
        }
        Collections.sort(result);
        return result;
    }

    private void searchSolution(List<GameSolution> result, FilePrefixTreeNode node, int level,
                                Cell cell, char newLetter) {
        if (cell == null) {
            return;
        }
        if (cell.visited) {
            return;
        }

        pathX[level] = cell.x;
        pathY[level] = cell.y;

        // случай А - ячейка не пустая
        if (cell.letter != ' ') {
            FilePrefixTreeNode child = node.getChild(cell.letter);
            if (child == null) {
                return;
            }

            if (child.hasValue() && newLetter != ' ') {
                GameSolution decision = getSolution(level, newLetter);
                result.add(decision);
            }

            cell.visited = true;
            searchSolution(result, child, level + 1, cell.up, newLetter);
            searchSolution(result, child, level + 1, cell.left, newLetter);
            searchSolution(result, child, level + 1, cell.right, newLetter);
            searchSolution(result, child, level + 1, cell.down, newLetter);
            cell.visited = false;
            return;
        }

        // случай Б - ячейка пустая, но новую букву уже разместили
        if (newLetter != ' ') {
            return;
        }

        // случай В - ячейка пустая и новой буквы еще не было
        char[] letters = node.getChildLetters();
        for (char possibleNewLetter : letters) {
            FilePrefixTreeNode child = node.getChild(possibleNewLetter);
            cell.visited = true;

            if (child.hasValue()) {
                GameSolution decision = getSolution(level, possibleNewLetter);
                result.add(decision);
            }

            searchSolution(result, child, level + 1, cell.up, possibleNewLetter);
            searchSolution(result, child, level + 1, cell.left, possibleNewLetter);
            searchSolution(result, child, level + 1, cell.right, possibleNewLetter);
            searchSolution(result, child, level + 1, cell.down, possibleNewLetter);

            cell.visited = false;
        }

    }

    private GameSolution getSolution(int level, char newLetter) {
        GameSolution solution = new GameSolution();
        List<int[]> path = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= level; i++) {
            int[] letterXY = new int[]{pathX[i], pathY[i]};
            path.add(letterXY);
            Cell cell = cellBoard.get(pathX[i], pathY[i]);
            if (cell.letter == ' ') {
                solution.setNewLetter(newLetter);
                solution.setNewLetterPosition(letterXY);
                builder.append(newLetter);
            } else {
                builder.append(cell.letter);
            }
        }
        solution.setPath(path);
        solution.setWord(builder.toString());
        return solution;
    }

}
