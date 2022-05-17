/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package puzzle_game;

import java.util.Scanner;

/**
 *
 * @author Cod
 */
public class ConsolePuzzleGame extends PuzzleGame {
    
    private int[] tiles;

    public ConsolePuzzleGame(int size) {
        super(size);
    }

    public ConsolePuzzleGame() {
    }

    @Override
    protected void onStart(int[] tiles, int blankPos) {
        this.tiles = tiles;
        printTiles(tiles);
    }

    @Override
    protected void onTimeIncrement(long time) {
    }

    @Override
    protected void onMoveCountIncrement(long moveCount) {
    }

    @Override
    protected void onWin(long time, long moveCount) {
        System.out.println("You Won in " + time + " seconds, " + moveCount + " moves");
    }

    @Override
    protected void onMove(int oldBlankPos, int newBlankPos) {
        printTiles(tiles);
    }
    
    private void printTiles(int[] tiles) {
        for (int i = 0; i < tiles.length; i++) {
            if (i % getSize() == 0)
                System.out.println();
            System.out.printf("%6d", tiles[i]);
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ConsolePuzzleGame game = new ConsolePuzzleGame();
        while (game.getStatus() != Status.WON) {
            System.out.println("Press w or s or a or d to move!");
            String s = sc.nextLine();
            Direction direction;
            switch (s) {
                case "w":
                    direction = Direction.UP;
                    break;
                case "s":
                    direction = Direction.DOWN;
                    break;
                case "a":
                    direction = Direction.LEFT;
                    break;
                default:
                    direction = Direction.RIGHT;
            }
            try {
                game.move(direction);
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
}
