package ae.cyberspeed.assignment.core;

import ae.cyberspeed.assignment.config.GameConfig;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * The MatrixGenerator class is responsible for generating a matrix of symbols
 * based on configured probabilities and dimensions. It also handles the placement of bonus symbols
 * within the generated matrix. This class utilizes randomness to ensure dynamic and varied outcomes
 * for each game round.
 *
 * @author Rustem Takalov
 */
public class MatrixGenerator {

    private Random random = new Random();
    private GameConfig config;

    /**
     * Generates a matrix based on the read config
     *
     * @param config
     * @return generated matrix
     */
    public String[][] generateMatrix(GameConfig config) {

        String[][] matrix = new String[config.getRows()][config.getColumns()];

        IntStream.range(0, config.getRows())
                .forEach(row -> IntStream.range(0, config.getColumns())
                        .forEach(col -> matrix[row][col] = generateSymbol(row, col, config)));

        addBonusSymbols(matrix, config);
        return matrix;
    }

    /**
     * Generates a symbol for the given cell based on its probabilities.
     *
     * @param row    the row index of the cell.
     * @param col    the column index of the cell.
     * @param config the game configuration containing probabilities for symbols.
     * @return the generated symbol as a String.
     */
    private String generateSymbol(int row, int col, GameConfig config) {
        Map<String, Integer> probabilities = getProbabilitiesForCell(row, col, config);
        int total = probabilities.values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = random.nextInt(total);
        int cumulative = 0;
        for (Map.Entry<String, Integer> entry : probabilities.entrySet()) {
            cumulative += entry.getValue();
            if (randomValue < cumulative) {
                return entry.getKey();
            }
        }
        return "MISS";
    }

    /**
     * Retrieves the probabilities for a specific cell. If no specific probabilities are configured
     * for the cell, the default probabilities are used.
     *
     * @param row    the row index of the cell.
     * @param col    the column index of the cell.
     * @param config the game configuration containing probabilities for symbols.
     * @return a map of symbols to their respective probabilities.
     */
    private Map<String, Integer> getProbabilitiesForCell(int row, int col, GameConfig config) {
        return Arrays.stream(config.getProbabilities().getStandardSymbols())
                .filter(symbol -> symbol.getRow() == row && symbol.getColumn() == col)
                .findFirst()
                .map(GameConfig.Probabilities.StandardSymbol::getSymbols)
                .orElse(config.getProbabilities().getStandardSymbols()[0].getSymbols());
    }


    /**
     * Adds bonus symbols to random positions in the matrix based on their probabilities.
     *
     * @param matrix the game matrix to which bonus symbols will be added.
     * @param config the game configuration containing probabilities for bonus symbols.
     */
    private void addBonusSymbols(String[][] matrix, GameConfig config) {
        Map<String, Integer> bonusProbabilities = config.getProbabilities().getBonusSymbol().getSymbols();
        int total = bonusProbabilities.values().stream().mapToInt(Integer::intValue).sum();
        AtomicInteger randomValue = new AtomicInteger(random.nextInt(total));

        bonusProbabilities.entrySet().stream()
                .filter(entry -> {
                    randomValue.addAndGet(-entry.getValue());
                    return randomValue.get() < 0;
                })
                .findFirst()
                .ifPresent(entry -> {
                    int row = random.nextInt(config.getRows());
                    int col = random.nextInt(config.getColumns());
                    matrix[row][col] = entry.getKey();
                });
    }

}
