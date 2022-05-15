import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Thread.sleep;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class controller implements ActionListener {
    gameForm main;
    JButton[] btn;
    JLabel jLabelCount, jLabelTime;
    int count = 0, time = 0, size = 3, blank;
    int[] tiles;
    boolean win = false;
    Random RANDOM = new Random();
    Thread cTime = new Thread(){
        @Override
        public void run() {
            while (true) {
                jLabelTime.setText("Elapsed: " + time++ + " sec");
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };

    public controller(gameForm p) {
        main = p;
        jLabelCount = p.getjLabelCount();
        jLabelTime = p.getjLabelTime();
        for (int i = 0; i < 9; i++) {
            p.getjComboBox1().insertItemAt((i + 2) + " x " + (i + 2), i);
        }
        p.getjComboBox1().setSelectedIndex(1);
        createNewGame();
    }

    private boolean isSolvable() {
        int Inversions = 0;
        for (int i = 0; i < tiles.length - 1; i++) {
            for (int j = i + 1; j < tiles.length; j++) {
                if (tiles[i] > tiles[j] && tiles[i] != 0 && tiles[j] != 0)
                    Inversions++;
                }
            }
        if (size % 2 != 0) {
            return Inversions % 2 == 0;
        }
        System.out.println("countInversions : "+ Inversions%2 +"\nblankPos/size"+ (blank / size)%2);
        return (Inversions %2) != (((blank / size)%2));
    }
    
    public void createNewGame() {
        cTime.suspend();
        if (count != 0 && !win){
            if (JOptionPane.showConfirmDialog(null, "Are you sure?", "New game", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
                if (!win){
                    cTime.resume();
                }
                return;
            }
        }
        win = false;
        time = count = 0;
        jLabelCount.setText("Move Count: 0");
        size = main.getjComboBox1().getSelectedIndex() + 2;
        if (cTime.isAlive()){
            cTime.resume();
        } else {
            cTime.start();
        }
        tiles = new int[size * size];
        main.getjPanelBut().removeAll();
        main.getjPanelBut().setPreferredSize(new Dimension(size * 60, size * 60));
        main.getjPanelBut().setLayout(new GridLayout(size, size));
        shuffleNumber();
        btn = new JButton[size * size];
        for (int i = 0; i < btn.length; i++) {
            if (tiles[i] == 0) {
                btn[i] = new JButton("");
            } else {
                btn[i] = new JButton("" + tiles[i]);
            }
            btn[i].setFont(new Font("arial", Font.PLAIN, 20));
            main.getjPanelBut().add(btn[i]);
            btn[i].addActionListener(this);
        }
        main.setBounds(main.getBounds().x, main.getBounds().y, size * 60 + 50, size * 60 + 200);
    }

    private void shuffleNumber() {
        int i,randomPosition, temp;
        for (i = 0; i < tiles.length-1; i++){
            tiles[i] = i+1;
        }
        tiles[i] = 0;
        for (i = 0; i < tiles.length-1; i++) {
            randomPosition = RANDOM.nextInt(tiles.length-1);
            temp = tiles[i];
            tiles[i] = tiles[randomPosition];
            tiles[randomPosition] = temp;
            System.out.println(randomPosition);
        }
        blank = RANDOM.nextInt(tiles.length-1);
        tiles[tiles.length-1] = tiles[blank];
        tiles[blank] = 0;
        if (!isSolvable()){
            if (blank != 1 && blank != 0){
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
    }

    public void checkWin() {
        for (int i = 0; i < btn.length - 1; i++) {
            if (!btn[i].getText().equals(i + 1 + "")) {
                win = false;
                return;
            }
        }
        win = true;
    }

    //Swap
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!win) {
            for (int i = 0; i < btn.length; i++) {
                if (e.getSource() == btn[i]) {
                    if ((blank - 1 == i && blank % size != 0) || (blank + 1 == i && i % size != 0) || (blank - size == i) || (blank + size == i)) {
                        btn[blank].setText(btn[i].getText());
                        btn[i].setText("");
                        jLabelCount.setText("Move count: " + ++count);
                        blank = i;
                        checkWin();
                    }
                    if (win) {
                        cTime.suspend();
                        JOptionPane.showMessageDialog(null, "You won!");
                    }
                    return;
                }
            }
        } else{
            JOptionPane.showMessageDialog(null, "Press Enter");
        }
    }
}