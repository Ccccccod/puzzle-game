/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package puzzle_game;

import java.awt.event.ActionEvent;
import java.util.stream.IntStream;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 * @author CoD
 */
public class SwingPuzzleGameController {
    private final GameForm gameForm;
    
    private static final int MIN_SIZE = 2;
    private static final int MAX_SIZE = 10;
    private static final int DEFAULT_SIZE = 3;
    
    private int selectedSize = DEFAULT_SIZE;
    
    private SwingPuzzleGame swingPuzzleGame;

    public SwingPuzzleGameController(GameForm gameForm) {
        this.gameForm = gameForm;
        
        // Set up sizeComboBox
        final JComboBox<String> sizeComboBox = gameForm.getjComboBox1();
        sizeComboBox.removeAllItems();
        IntStream.range(MIN_SIZE, MAX_SIZE + 1)
                .mapToObj(i -> i + " x " + i)
                .forEach(size -> sizeComboBox.addItem(size));
        sizeComboBox.addActionListener((ActionEvent e) -> {
            selectedSize = sizeComboBox.getSelectedIndex() + MIN_SIZE;
        });
        sizeComboBox.setSelectedIndex(DEFAULT_SIZE - MIN_SIZE);
        
        // Set up btnNewGame
        final JButton btnNewGame = this.gameForm.getBtnNewGame();
        startNewGame();
        btnNewGame.addActionListener((ActionEvent e) -> {
            if (JOptionPane.showConfirmDialog(null, "Are you sure?", "New game", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                startNewGame();
            }
        });
    }
    
    private void startNewGame() {
        swingPuzzleGame = new SwingPuzzleGame(gameForm.getjLabelCount(), gameForm.getjLabelTime(), gameForm.getjPanelBut(), selectedSize);
        swingPuzzleGame.init(gameForm);
    }
    
}
