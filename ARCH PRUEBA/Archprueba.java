import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Archprueba extends JPanel implements ActionListener, KeyListener {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 500;
    private static final int PADDLE_WIDTH = 16;
    private static final int PADDLE_HEIGHT = 110;
    private static final int BALL_SIZE = 16;
    private static final int PLAYER_SPEED = 7;
    private static final int AI_SPEED = 5;

    private final Timer timer = new Timer(16, this);
    private final boolean[] keys = new boolean[256];

    private int leftY = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private int rightY = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private int leftScore = 0;
    private int rightScore = 0;

    private int ballX = WIDTH / 2 - BALL_SIZE / 2;
    private int ballY = HEIGHT / 2 - BALL_SIZE / 2;
    private int ballVelX = 5;
    private int ballVelY = 4;

    public Archprueba() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(17, 27, 33));
        setFocusable(true);
        addKeyListener(this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(20, 180, 90));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        g2.setColor(new Color(255, 255, 255));
        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT);
        g2.drawOval(WIDTH / 2 - 60, HEIGHT / 2 - 60, 120, 120);

        g2.setColor(new Color(255, 255, 255));
        g2.fillRoundRect(20, leftY, PADDLE_WIDTH, PADDLE_HEIGHT, 12, 12);
        g2.fillRoundRect(WIDTH - 20 - PADDLE_WIDTH, rightY, PADDLE_WIDTH, PADDLE_HEIGHT, 12, 12);

        g2.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);

        g2.setFont(new Font("Arial", Font.BOLD, 32));
        g2.drawString(String.valueOf(leftScore), WIDTH / 2 - 80, 50);
        g2.drawString(String.valueOf(rightScore), WIDTH / 2 + 55, 50);

        if (leftScore >= 5 || rightScore >= 5) {
            g2.setColor(new Color(0, 0, 0, 170));
            g2.fillRect(0, 0, WIDTH, HEIGHT);

            g2.setColor(Color.WHITE);
            String mensaje = leftScore >= 5 ? "¡Jugador 1 gana!" : "¡Jugador 2 gana!";
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            g2.drawString(mensaje, WIDTH / 2 - 170, HEIGHT / 2 - 10);
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.drawString("Presiona ESPACIO para reiniciar", WIDTH / 2 - 170, HEIGHT / 2 + 40);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (leftScore >= 5 || rightScore >= 5) {
            if (keys[KeyEvent.VK_SPACE]) {
                resetGame();
            }
            return;
        }

        movePlayer();
        moveAI();
        updateBall();
        repaint();
    }

    private void movePlayer() {
        if (keys[KeyEvent.VK_W] && leftY > 0) {
            leftY -= PLAYER_SPEED;
        }
        if (keys[KeyEvent.VK_S] && leftY + PADDLE_HEIGHT < HEIGHT) {
            leftY += PLAYER_SPEED;
        }
    }

    private void moveAI() {
        int targetY = rightY + PADDLE_HEIGHT / 2;
        if (targetY < ballY + BALL_SIZE / 2) {
            rightY += AI_SPEED;
        } else if (targetY > ballY + BALL_SIZE / 2) {
            rightY -= AI_SPEED;
        }

        if (rightY < 0) {
            rightY = 0;
        }
        if (rightY + PADDLE_HEIGHT > HEIGHT) {
            rightY = HEIGHT - PADDLE_HEIGHT;
        }
    }

    private void updateBall() {
        ballX += ballVelX;
        ballY += ballVelY;

        if (ballY <= 0 || ballY + BALL_SIZE >= HEIGHT) {
            ballVelY *= -1;
        }

        boolean hitLeftPaddle = ballX <= 20 + PADDLE_WIDTH &&
                ballY + BALL_SIZE >= leftY &&
                ballY <= leftY + PADDLE_HEIGHT &&
                ballX > 0;

        boolean hitRightPaddle = ballX + BALL_SIZE >= WIDTH - 20 - PADDLE_WIDTH &&
                ballY + BALL_SIZE >= rightY &&
                ballY <= rightY + PADDLE_HEIGHT &&
                ballX + BALL_SIZE < WIDTH;

        if (hitLeftPaddle || hitRightPaddle) {
            ballVelX *= -1;
            ballX += ballVelX;
        }

        if (ballX < 0) {
            rightScore++;
            resetBall(1);
        }

        if (ballX + BALL_SIZE > WIDTH) {
            leftScore++;
            resetBall(-1);
        }
    }

    private void resetBall(int direction) {
        ballX = WIDTH / 2 - BALL_SIZE / 2;
        ballY = HEIGHT / 2 - BALL_SIZE / 2;
        ballVelX = direction * (Math.abs(ballVelX) + 1);
        ballVelY = (int) (Math.random() * 6 - 3);
        if (ballVelY == 0) {
            ballVelY = 3;
        }
    }

    private void resetGame() {
        leftScore = 0;
        rightScore = 0;
        leftY = HEIGHT / 2 - PADDLE_HEIGHT / 2;
        rightY = HEIGHT / 2 - PADDLE_HEIGHT / 2;
        resetBall(1);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Juego de Tenis");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new Archprueba());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}