package examples.MiniMax;

import pacman.game.Constants;
import pacman.game.Game;

public class MyGhost {
    Constants.GHOST id;
    int index;

    public MyGhost(Game game, Constants.GHOST id) {
        this.id = id;
        this.index = game.getGhostCurrentNodeIndex(id);
    }

    public MyGhost(int index, Constants.GHOST id) {
        this.id = id;
        this.index = index;
    }

    public Constants.GHOST getId() {
        return id;
    }

    public void setId(Constants.GHOST id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
