/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package puzzle_game;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author CoD
 */
public class SwingPuzzleGame extends PuzzleGame {
    
    private static final String BLANK_TEXT = "";
    
    private final JLabel moveCountLabel;
    private final JLabel timerLabel;
    private final JPanel btnsPanel;

    public SwingPuzzleGame(JLabel moveCountLabel, JLabel timerLabel, JPanel btnsPanel, int size) {
        super(size);
        this.moveCountLabel = moveCountLabel;
        this.timerLabel = timerLabel;
        this.btnsPanel = btnsPanel;
    }
    
    void init(GameForm gameForm) {
        timerLabel.setText("Elapsed: 0");
        moveCountLabel.setText("Move count: 0");
        int[] tiles = getTiles();
        System.out.println(Arrays.toString(tiles));
        btnsPanel.removeAll();
        btnsPanel.setPreferredSize(new Dimension(getSize() * 60, getSize() * 60));
        btnsPanel.setLayout(new GridLayout(getSize(), getSize()));
        for (int i = 0; i < tiles.length; i++) {
            String text = tiles[i] == 0 ? BLANK_TEXT : String.valueOf(tiles[i]);
            final JButton btn = new JButton(text);
            btn.setFont(new Font("arial", Font.PLAIN, 20));
            final int j = i;
            btn.addActionListener(e -> move(j));
            btnsPanel.add(btn);
        }
        gameForm.setBounds(gameForm.getBounds().x, gameForm.getBounds().y, getSize() * 60 + 50, getSize() * 60 + 200);
        gameForm.validate();
    }

    @Override
    protected void onStart(int[] tiles, int blankPos) {
    }

    @Override
    protected void onTimeIncrement(long time) {
        timerLabel.setText("Elapsed: " + time);
    }

    @Override
    protected void onMoveCountIncrement(long moveCount) {
        moveCountLabel.setText("Move count: " + moveCount);
    }

    @Override
    protected void onWin(long time, long moveCount) {
        JOptionPane.showMessageDialog(null, "You won!");
    }

    @Override
    protected void onMove(int oldBlankPos, int newBlankPos) {
        // Swap btns' texts
        JButton btn1 = (JButton) btnsPanel.getComponent(oldBlankPos);
        JButton btn2 = (JButton) btnsPanel.getComponent(newBlankPos);
        String temp = btn1.getText();
        btn1.setText(btn2.getText());
        btn2.setText(temp);
    }
    
}
