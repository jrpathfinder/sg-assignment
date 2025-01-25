package ae.cyberspeed.assignment;

import ae.cyberspeed.assignment.config.GameConfig;
import ae.cyberspeed.assignment.core.MatrixGenerator;
import ae.cyberspeed.assignment.core.ScratchGame;
import ae.cyberspeed.assignment.model.result.SGResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SGApp {

    private static final Logger log = LogManager.getLogger(SGApp.class);

    private static int bettingAmount;

    public static void main(String[] args) {
        log.info("Starting the Game. Validating args!");
        if (args.length < 4 || !"--config".equals(args[0]) || !"--betting-amount".equals(args[2])) {
            log.error("Usage: java -jar <jar-file> --config config.json --betting-amount 100");
            return;
        }
        String configFilePath = args[1];
        try {
            bettingAmount = Integer.parseInt(args[3]);
        } catch (NumberFormatException numberFormatException) {
            log.error("Invalid betting amount: {}", args[3], numberFormatException);
        }
        try {
            log.info("Loading config from file: {}", configFilePath);
            GameConfig gameConfig = GameConfig.loadConfig(configFilePath);

            ScratchGame game = new ScratchGame(gameConfig, new MatrixGenerator());

            log.info("Starting new game!");
            game.newGame();
            SGResult sgResult = game.evaluate(bettingAmount);

            log.info("Matrix:");
            for (String[] row : sgResult.getMatrix()) {
                log.info(String.join(", ", row));
            }
            log.info("Reward: " + sgResult.getReward());
            log.info("Winning Combinations: " + sgResult.getWinningCombinations());
            log.info("Applied Bonus: " + sgResult.getAppliedBonus());

        } catch (Exception exception) {
            log.error("An error occurred while running the game", exception);
        }
    }
}
