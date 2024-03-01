package bomb;

import bomb.gui.GameBoard;

public class Game {
    public static void main(String[] args) {
        GameBoard gameBoard = new GameBoard(16, 16);
        gameBoard.init();
    }
}
