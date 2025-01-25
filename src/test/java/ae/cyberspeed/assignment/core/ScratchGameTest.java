package ae.cyberspeed.assignment.core;

import ae.cyberspeed.assignment.config.GameConfig;
import ae.cyberspeed.assignment.model.result.SGResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScratchGameTest {

    private GameConfig gameConfig;
    private MatrixGenerator matrixGenerator;
    private ScratchGame scratchGame;

    @BeforeEach
    void setUp() {
        gameConfig = mock(GameConfig.class);
        matrixGenerator = mock(MatrixGenerator.class);

        // Sample mock configuration
        when(gameConfig.getRows()).thenReturn(3);
        when(gameConfig.getColumns()).thenReturn(3);

        Map<String, GameConfig.SymbolConfig> symbols = new HashMap<>();
        GameConfig.SymbolConfig symbolA = new GameConfig.SymbolConfig();
        symbolA.rewardMultiplier = 5;
        symbolA.type = "standard";
        symbols.put("A", symbolA);

        GameConfig.SymbolConfig bonus10x = new GameConfig.SymbolConfig();
        bonus10x.rewardMultiplier = 10;
        bonus10x.type = "bonus";
        bonus10x.impact = "multiply_reward";
        symbols.put("10x", bonus10x);

        when(gameConfig.getSymbols()).thenReturn(symbols);

        GameConfig.WinCombination winCombination = new GameConfig.WinCombination();
        winCombination.rewardMultiplier = 2;
        winCombination.when = "same_symbols";
        winCombination.count = 3;

        Map<String, GameConfig.WinCombination> winCombinations = new HashMap<>();
        winCombinations.put("same_symbol_3_times", winCombination);
        when(gameConfig.getWinCombinations()).thenReturn(winCombinations);

        scratchGame = new ScratchGame(gameConfig, matrixGenerator);
    }

    @Test
    void testEvaluateWinningScenario() {
        // Mock generated matrix
        String[][] matrix = {
                {"A", "A", "A"},
                {"B", "C", "D"},
                {"E", "F", "G"}
        };
        when(matrixGenerator.generateMatrix(gameConfig)).thenReturn(matrix);
        scratchGame.newGame();

        int betAmount = 100;
        SGResult result = scratchGame.evaluate(betAmount);

        assertNotNull(result);
        assertEquals(1000, result.getReward()); // 100 * 5 * 2
        assertTrue(result.getWinningCombinations().containsKey("A"));
        assertEquals(1, result.getWinningCombinations().get("A").size());
    }

    @Test
    void testEvaluateLosingScenario() {
        // Mock generated matrix
        String[][] matrix = {
                {"A", "B", "C"},
                {"D", "E", "F"},
                {"G", "H", "I"}
        };
        when(matrixGenerator.generateMatrix(gameConfig)).thenReturn(matrix);
        scratchGame.newGame();

        int betAmount = 100;
        SGResult result = scratchGame.evaluate(betAmount);

        assertNotNull(result);
        assertEquals(0, result.getReward());
        assertTrue(result.getWinningCombinations().isEmpty());
    }

    @Test
    void testBonusApplied() {
        // Mock generated matrix with a bonus
        String[][] matrix = {
                {"A", "A", "A"},
                {"10x", "B", "C"},
                {"D", "E", "F"}
        };
        when(matrixGenerator.generateMatrix(gameConfig)).thenReturn(matrix);
        scratchGame.newGame();

        int betAmount = 100;
        SGResult result = scratchGame.evaluate(betAmount);

        assertNotNull(result);
        assertEquals(10000, result.getReward()); // 100 * 5 * 2 * 10
        assertEquals("10x", result.getAppliedBonus());
    }

    @Test
    void testEdgeCaseEmptyMatrix() {
        String[][] matrix = {};
        when(matrixGenerator.generateMatrix(gameConfig)).thenReturn(matrix);
        scratchGame.newGame();

        int betAmount = 100;
        SGResult result = scratchGame.evaluate(betAmount);

        assertNotNull(result);
        assertEquals(0, result.getReward());
        assertTrue(result.getWinningCombinations().isEmpty());
    }

    @Test
    void testEdgeCaseNoSymbolsMatch() {
        String[][] matrix = {
                {"Z", "Y", "X"},
                {"W", "V", "U"},
                {"T", "S", "R"}
        };
        when(matrixGenerator.generateMatrix(gameConfig)).thenReturn(matrix);
        scratchGame.newGame();

        int betAmount = 100;
        SGResult result = scratchGame.evaluate(betAmount);

        assertNotNull(result);
        assertEquals(0, result.getReward());
        assertTrue(result.getWinningCombinations().isEmpty());
    }
}
