/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package puzzle_game;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A <a href="https://en.wikipedia.org/wiki/15_puzzle">15 puzzle game</a> that
 * provides features: moving, move counting, timer, resizing.<br>
 * 
 * Extend this class to custom the game on platforms
 * 
 * @author Cod
 */
public abstract class PuzzleGame {
    private static final int DEFAULT_SIZE = 3;
    
    private static final int BLANK_VALUE = 0;
    
    public static enum Status {
        READY, RUNNING, WON;
    }
    
    public static enum Direction {
        LEFT {
            @Override
            protected int move(int[] tiles, int blankPos, int size, PuzzleGame game) {
                tiles[blankPos] = tiles[blankPos+1];
                tiles[blankPos+1] = BLANK_VALUE;
                game.onMove(blankPos, blankPos+1);
                return ++blankPos;
            }
        },
        RIGHT {
            @Override
            protected int move(int[] tiles, int blankPos, int size, PuzzleGame game) {
                tiles[blankPos] = tiles[blankPos-1];
                tiles[blankPos-1] = BLANK_VALUE;
                game.onMove(blankPos, blankPos-1);
                return --blankPos;
            }
        },
        UP {
            @Override
            protected int move(int[] tiles, int blankPos, int size, PuzzleGame game) {
                tiles[blankPos] = tiles[blankPos+size];
                tiles[blankPos+size] = BLANK_VALUE;
                int movedNumber = tiles[blankPos];
                game.onMove(blankPos, blankPos+size);
                blankPos += size;
                return blankPos;
            }
        },
        DOWN {
            @Override
            protected int move(int[] tiles, int blankPos, int size, PuzzleGame game) {
                tiles[blankPos] = tiles[blankPos-size];
                tiles[blankPos-size] = BLANK_VALUE;
                int movedNumber = tiles[blankPos];
                game.onMove(blankPos, blankPos-size);
                blankPos -= size;
                return blankPos;
            }
        };
        
        protected abstract int move(int[] tiles, int blankPos, int size, PuzzleGame game);
        
        /**
         * Get direction that a tile at <code>index</code> will move to
         * <code>blankPos</code>
         *
         * @param index index of moving tile
         * @param blankPos position of blank tile
         * @param size game size
         * @return {@code Direction} that the tile will moves, or
         * <code>null</code> if the tile can not move.
         */
        private static final Direction getDirection(int index, int blankPos, int size) {
            if (index == blankPos + size)
                return UP;
            else if (index == blankPos - size)
                return DOWN;
            else if (index == blankPos - 1)
                return RIGHT;
            else if (index == blankPos + 1)
                return LEFT;
            else
                return null;
        }
    }
    
    private final AtomicLong time = new AtomicLong();
    private final AtomicLong moveCount = new AtomicLong();
    private int size = 3, blankPos;
    
    /**
     * <code>tiles</code> actually should be kept in a 2d array
     */
    private int[] tiles;
    private Status status = Status.READY;
//    private List<Direction> movableDirections;
    private final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> timer;

    public PuzzleGame(int size) {
        if (size <= 1) throw new IllegalArgumentException("size must be greater than 1");
        this.size = size;
        this.tiles = new int[size * size];
        this.blankPos = generateNumbers(tiles, size);
        scheduledThreadPool.schedule(() -> onStart(tiles, blankPos), 0, TimeUnit.SECONDS);
    }

    public PuzzleGame() {
        this(DEFAULT_SIZE);
    }
    
    protected abstract void onStart(int[] tiles, int blankPos);
    
    protected abstract void onTimeIncrement(long time);
    
    protected abstract void onMoveCountIncrement(long moveCount);
    
    protected abstract void onWin(long time, long moveCount);
    
    protected abstract void onMove(int oldBlankPos, int newBlankPos);
    
    /**
     * 
     * @param direction direction to move, must not be null
     * @throws IllegalStateException if <code>direction</code> is not available
     * to move or the game has ended
     */
    public void move(Direction direction) throws IllegalStateException {
        if (this.status == Status.WON)
            throw new IllegalStateException("The game has ended!");
        if (this.status == Status.READY) {
            this.status = Status.RUNNING;
            // Start timer
            final Runnable timerAction = () -> {
                this.onTimeIncrement(time.incrementAndGet());
            };
            timer = scheduledThreadPool.scheduleAtFixedRate(timerAction, 1, 1, TimeUnit.SECONDS);
        }
        try {
            blankPos = direction.move(tiles, blankPos, size, this);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalStateException("Direction " + direction + " is not available to move!");
        }
        onMoveCountIncrement(moveCount.incrementAndGet());
        if (isSolved(tiles)) {
            this.status = Status.WON;
            if (timer != null)
                timer.cancel(true);
            onWin(time.get(), moveCount.get());
        }
    }
    
    /**
     * Move the tile at <code>index</code> if possible, otherwise do nothing
     * @param index index of the tile that's being moved
     */
    public void move(int index) {
        final Direction direction = Direction.getDirection(index, blankPos, size);
        if (direction != null)
            move(direction);
    }

    /**
     * Generate and shuffle numbers to tiles
     * @param tiles tiles to hold result
     * @param size game size
     * @return position of blank tile
     */
    private static int generateNumbers(int[] tiles, int size) {
        int i, randomPosition, temp, blankPos;
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        for (i = 0; i < tiles.length-1; i++){
            tiles[i] = i+1;
        }
        tiles[i] = BLANK_VALUE;
        // Swap each tile to a random tile except blank tile which is last tile
        for (i = 0; i < tiles.length-1; i++) {
            randomPosition = random.nextInt(tiles.length-1);
            temp = tiles[i];
            tiles[i] = tiles[randomPosition];
            tiles[randomPosition] = temp;
        }
        // Swap blank tile to a random tile
        blankPos = random.nextInt(tiles.length-1);
        tiles[tiles.length-1] = tiles[blankPos];
        tiles[blankPos] = 0;
        // If game is NOT solvable, simply swap 2 close tiles to make it solvable
        if (!isSolvable(tiles, blankPos, size)){
            if (blankPos != 1 && blankPos != 0){
                temp = tiles[0];
                tiles[0] = tiles[1];
                tiles[1] = temp;
            }
            else{
                temp = tiles[tiles.length-1];
                tiles[tiles.length-1] = tiles[tiles.length-2];
                tiles[tiles.length-2] = temp;
            }
        }
        return blankPos;
    }

    /**
     * Check if tiles can be solved with
     * <a href="https://www.geeksforgeeks.org/check-instance-15-puzzle-solvable/">algorithm</a>
     * @param tiles tiles to check
     * @param blankPos position of blank tile
     * @param size tiles size
     * @return true if tiles is solvable
     */
    private static boolean isSolvable(int[] tiles, int blankPos, int size) {
        int Inversions = 0;
        for (int i = 0; i < tiles.length - 1; i++) {
            for (int j = i + 1; j < tiles.length; j++) {
                if (tiles[i] > tiles[j] && tiles[i] != BLANK_VALUE && tiles[j] != BLANK_VALUE)
                    Inversions++;
                }
            }
        if (size % 2 != 0) {
            return Inversions % 2 == 0;
        }
//        System.out.println("countInversions : "+ Inversions%2 +"\nblankPos/size"+ (blank / size)%2);
        return (Inversions %2) != (((blankPos / size)%2));
    }

    /**
     * Check if tiles are in solved state
     * @param tiles tiles to check
     * @return true if tiles are in solved state
     */
    public static boolean isSolved(int[] tiles) {
        for (int i = 0; i < tiles.length - 1; i++) {
            if (tiles[i] != i+1) {
                return false;
            }
        }
        return true;
    }
    
    protected int[] getTiles() {
        return tiles;
    }
    
    public int getSize() {
        return size;
    }
    
    public Status getStatus() {
        return status;
    }
    
}
