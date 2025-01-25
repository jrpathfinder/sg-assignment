package ae.cyberspeed.assignment.model.result;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
public class SGResult {
    private String[][] matrix;
    private double reward;
    private Map<String, List<String>> winningCombinations;
    private String appliedBonus;

    public SGResult(String[][] matrix, double reward, Map<String, List<String>> winningCombinations, String appliedBonus) {
        this.matrix = matrix;
        this.reward = reward;
        this.winningCombinations = winningCombinations;
        this.appliedBonus = appliedBonus;
    }
}
