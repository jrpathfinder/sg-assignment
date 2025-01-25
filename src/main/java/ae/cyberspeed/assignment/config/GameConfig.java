package ae.cyberspeed.assignment.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * This class represents the configuration for the game, including columns, rows, symbols,
 * probabilities, and winning combinations. It provides functionality to load configuration from a file.
 *
 * @author Rustem Takalov
 */
@Getter
public class GameConfig {
    @JsonProperty("columns")
    public int columns;

    @JsonProperty("rows")
    public int rows;

    @JsonProperty("symbols")
    public Map<String, SymbolConfig> symbols;

    @JsonProperty("probabilities")
    public Probabilities probabilities;

    @JsonProperty("win_combinations")
    public Map<String, WinCombination> winCombinations;

    public static GameConfig loadConfig(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(filePath), GameConfig.class);
    }

    @Data
    public static class SymbolConfig {
        @JsonProperty("reward_multiplier")
        public double rewardMultiplier;

        @JsonProperty("type")
        public String type;

        @JsonProperty("impact")
        public String impact;

        @JsonProperty("extra")
        public Integer extra;
    }

    @Data
    public static class Probabilities {
        @JsonProperty("standard_symbols")
        public StandardSymbol[] standardSymbols;

        @JsonProperty("bonus_symbols")
        public BonusSymbol bonusSymbol;

        @Data
        public static class StandardSymbol {
            @JsonProperty("column")
            public int column;

            @JsonProperty("row")
            public int row;

            @JsonProperty("symbols")
            public Map<String, Integer> symbols;
        }

        @Data
        public static class BonusSymbol {
            @JsonProperty("symbols")
            public Map<String, Integer> symbols;
        }
    }

    @Data
    public static class WinCombination {
        @JsonProperty("reward_multiplier")
        public double rewardMultiplier;

        @JsonProperty("when")
        public String when;

        @JsonProperty("count")
        public int count;

        @JsonProperty("group")
        public String group;

        @JsonProperty("covered_areas")
        public String[][] coveredAreas;
    }
}
