package ae.cyberspeed.assignment.core;

import ae.cyberspeed.assignment.model.result.SGResult;

/**
 * This interface defines the contract for evaluating a game platform.
 * It provides methods to evaluate the outcome of a game round based on a bet amount
 * and to initialize or start a new game. Implementing classes are expected to handle
 * game-specific logic such as generating game states, evaluating results, and managing rewards.
 *
 * @author  Rustem Takalov
 */
interface GamePlatformEval {
    SGResult evaluate(int betAmount);
    void newGame();
}
