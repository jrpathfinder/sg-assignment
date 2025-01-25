package ae.cyberspeed.assignment.core;

import ae.cyberspeed.assignment.config.GameConfig;
import ae.cyberspeed.assignment.model.result.SGResult;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ae.cyberspeed.assignment.util.SGConsts.*;

/**
 * This class implements the ScratchGame logic, handling the evaluation of the game matrix,
 * bonus application, and management of winning combinations.
 *
 * @author Rustem Takalov
 */
public class ScratchGame implements GamePlatformEval {

    public ScratchGame(GameConfig gameConfig, MatrixGenerator matrixGenerator){
        this.config = gameConfig;
        this.matrixGenerator = matrixGenerator;
    }

    private GameConfig config;

    private MatrixGenerator matrixGenerator;

    private String matrix[][];

    @Override
    public SGResult evaluate(int betAmount) {
        Map<String, List<String>> winningCombinations = new HashMap<>();
        double reward = config.getWinCombinations().entrySet().stream()
                .mapToDouble(entry -> {
                    GameConfig.WinCombination combination = entry.getValue();
                    if (SAME_SYMBOLS.equals(combination.getWhen())) {
                        return evaluateSameSymbols(matrix, combination, betAmount, winningCombinations);
                    } else if (LINEAR_SYMBOLS.equals(combination.getWhen())) {
                        return evaluateLinearSymbols(matrix, combination, betAmount, winningCombinations);
                    }
                    return 0;
                })
                .sum();

        SGResult result = applyBonuses(matrix, reward > 0 ? reward : 0);

        result.setWinningCombinations(winningCombinations);

        return result;
    }

    private SGResult applyBonuses(String[][] matrix, double initialReward) {
        final Double[] reward = {initialReward};
        String appliedReward = Arrays.stream(matrix)
                .flatMap(Arrays::stream)
                .filter(cell -> config.getSymbols().containsKey(cell))
                .map(cell -> applyBonusToReward(cell, reward))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        return new SGResult(matrix, reward[0], null,  appliedReward);
    }

    private String applyBonusToReward(String cell, Double[] reward) {
        GameConfig.SymbolConfig symbolConfig = config.getSymbols().get(cell);
        if (symbolConfig != null && BONUS_TYPE.equals(symbolConfig.type)) {
            switch (symbolConfig.impact) {
                case MULTIPLY_REWARD:
                    reward[0] *= symbolConfig.getRewardMultiplier();
                    break;
                case EXTRA_BONUS:
                    reward[0] += symbolConfig.getExtra();
                    break;
                case MISS:
                    return MISS;
            }
            return cell;
        }
        return null;
    }

    private double evaluateLinearSymbols(String[][] matrix, GameConfig.WinCombination combination, int betAmount, Map<String, List<String>> winningCombinations) {
        return Arrays.stream(combination.coveredAreas)
                .mapToDouble(area -> {
                    Set<String> symbols = Arrays.stream(area)
                            .map(position -> {
                                String[] indices = position.split(":");
                                int row = Integer.parseInt(indices[0]);
                                int col = Integer.parseInt(indices[1]);
                                return matrix[row][col];
                            })
                            .collect(Collectors.toSet());

                    if (symbols.size() == 1) {
                        String symbol = symbols.iterator().next();
                        double symbolReward = betAmount * config.symbols.get(symbol).rewardMultiplier * combination.rewardMultiplier;

                        winningCombinations.computeIfAbsent(symbol, k -> new ArrayList<>()).add(combination.group);
                        return symbolReward;
                    }
                    return 0;
                })
                .sum();
    }

    private double evaluateSameSymbols(String[][] matrix, GameConfig.WinCombination combination, int betAmount, Map<String, List<String>> winningCombinations) {
        Map<String, Long> counts = Arrays.stream(matrix)
                .flatMap(Arrays::stream)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return counts.entrySet().stream()
                .filter(entry -> entry.getValue() >= combination.count)
                .mapToDouble(entry -> {
                    String symbol = entry.getKey();
                    double symbolReward = betAmount * config.getSymbols().get(symbol).getRewardMultiplier() * combination.getRewardMultiplier();

                    winningCombinations.computeIfAbsent(symbol, k -> new ArrayList<>()).add(combination.group);
                    return symbolReward;
                })
                .sum();
    }

    @Override
    public void newGame() {
        matrix = matrixGenerator.generateMatrix(config);
    }
}
