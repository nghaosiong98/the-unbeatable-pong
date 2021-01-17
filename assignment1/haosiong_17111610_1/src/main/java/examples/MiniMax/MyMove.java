package examples.MiniMax;

import pacman.game.Constants;

public class MyMove {
    double reward;
    Constants.MOVE move;

    public MyMove(double reward, Constants.MOVE move) {
        this.reward = reward;
        this.move = move;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public Constants.MOVE getMove() {
        return move;
    }

    public void setMove(Constants.MOVE move) {
        this.move = move;
    }
}
