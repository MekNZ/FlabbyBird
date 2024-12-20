import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {
        final int boardWidth = 360;
        final int boardHeight = 640;

        JFrame frame = new JFrame("Flabby Bird");
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(boardWidth, boardHeight);
        FlappyBird flabbyBird = new FlappyBird();
        frame.add(flabbyBird);
        frame.pack();
        flabbyBird.requestFocus();
        frame.setVisible(true);

    }
}
