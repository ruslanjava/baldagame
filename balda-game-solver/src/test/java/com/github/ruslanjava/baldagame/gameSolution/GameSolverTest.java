package com.github.ruslanjava.baldagame.gameSolution;

import com.github.ruslanjava.baldagame.prefixTree.FilePrefixTree;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

public class GameSolverTest {

    private static FilePrefixTree tree;

    @BeforeClass
    static void init() {
        URL url = DictionaryTest.class.getResource(".");
        String fileName = urlString.substring("file:".length()) + "dictionary.rdict";
        tree = new FilePrefixTree(fileName);
        List<String> words = tree.getFiveLetterWords().toList().blockingGet();
        System.out.println(words);
    }

    @AfterClass
    static void destroy() {
        tree.close();
    }

    @Test(dataProvider = "data")
    void testSolver(String word, char[][] board) throws Exception {
        GameSolver solver = new GameSolver(tree);

        Set<String> words = new HashSet<>();
        Iterable<GameSolution> solutions = solver.getSolutions(board).blockingIterable();
        for (GameSolution solution : solutions) {
            words.add(solution.getWord());
        }
        assertThat(words, hasItem(word));
    }

    @DataProvider
    public Object[][] data() {
        return new Object[][]{
                {
                        "еда",
                        new char[][]{
                                {' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' '},
                                {'б', 'а', 'л', 'д', 'а'},
                                {' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' '},
                        }
                },
                {
                        "палка",
                        new char[][]{
                                {' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' '},
                                {'б', 'а', 'л', 'к', 'а'},
                                {' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' '},
                        }
                },
                {
                        "ковш",
                        new char[][]{
                                {' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' '},
                                {'с', 'о', 'в', 'о', 'к'},
                                {' ', ' ', ' ', ' ', ' '},
                                {' ', ' ', ' ', ' ', ' '},
                        }
                }
        };
    }

}
