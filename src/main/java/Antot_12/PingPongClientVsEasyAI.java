package Antot_12;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PingPongClientVsEasyAI extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final Color ORANGE_YELLOW = new Color(255, 165, 0);

    private Canvas canvas;
    private Ball ball;
    private Paddle paddle1;
    private Paddle paddle2;
    private boolean upPressed = false;
    private boolean downPressed = false;

    private int score1 = 0;
    private int score2 = 0;

    public PingPongClientVsEasyAI() {
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        add(canvas, BorderLayout.CENTER);

        ball = new Ball(WIDTH / 2, HEIGHT / 2);
        paddle1 = new Paddle(50, HEIGHT / 2 - 50);
        paddle2 = new Paddle(WIDTH - 70, HEIGHT / 2 - 50);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> upPressed = true;
                    case KeyEvent.VK_DOWN -> downPressed = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> upPressed = false;
                    case KeyEvent.VK_DOWN -> downPressed = false;
                }
            }
        });

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        new Timer(16, e -> gameLoop()).start(); // Approximately 60 FPS

        setVisible(true);
    }

    private void gameLoop() {
        if (upPressed) {
            paddle1.moveUp();
        }
        if (downPressed) {
            paddle1.moveDown();
        }

        // Simple AI movement for paddle2
        int aiSpeed = 4; // Lower AI speed for easier difficulty
        if (ball.y < paddle2.y + paddle2.height / 2) {
            paddle2.smoothMoveUp(aiSpeed);
        } else if (ball.y > paddle2.y + paddle2.height / 2) {
            paddle2.smoothMoveDown(aiSpeed);
        }

        ball.move();
        checkCollisions();

        canvas.repaint();

        // Check for winning condition
        if (score1 >= 10 || score2 >= 10) {
            int result = JOptionPane.showConfirmDialog(this, "Game Over. Do you want to play again?", "Game Over", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                resetGame();
            } else {
                System.exit(0);
            }
        }
    }

    private void checkCollisions() {
        if (ball.x <= 0) {
            score2++;
            resetBall();
        } else if (ball.x >= WIDTH - ball.diameter) {
            score1++;
            resetBall();
        }

        if (ball.y <= 0 || ball.y >= HEIGHT - ball.diameter) {
            ball.yVelocity = -ball.yVelocity;
        }

        if (ball.getBounds().intersects(paddle1.getBounds())) {
            ball.xVelocity = Math.abs(ball.xVelocity);
        } else if (ball.getBounds().intersects(paddle2.getBounds())) {
            ball.xVelocity = -Math.abs(ball.xVelocity);
        }
    }

    private void resetBall() {
        ball.x = WIDTH / 2;
        ball.y = HEIGHT / 2;
        ball.xVelocity = -ball.xVelocity;
        ball.yVelocity = -ball.yVelocity;
    }

    private void resetGame() {
        score1 = 0;
        score2 = 0;
        resetBall();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PingPongClientVsEasyAI::new);
    }

    class Canvas extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.BLACK);
            g.setColor(Color.WHITE);
            ball.draw(g);
            g.setColor(ORANGE_YELLOW);
            paddle1.draw(g);
            paddle2.draw(g);
            drawScore(g);
        }

        private void drawScore(Graphics g) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(Color.CYAN);
            String scoreText = score1 + " : " + score2;
            // Draw the score in the center of the screen
            int stringWidth = g.getFontMetrics().stringWidth(scoreText);
            g.drawString(scoreText, 350, 55);
        }
    }

    class Ball {
        int x, y;
        int diameter = 20;
        int xVelocity = 6, yVelocity = 6; // Lower ball speed for easier difficulty

        Ball(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void draw(Graphics g) {
            g.fillOval(x, y, diameter, diameter);
        }

        void move() {
            x += xVelocity;
            y += yVelocity;
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, diameter, diameter);
        }
    }

    class Paddle {
        int x, y;
        int width = 20, height = 100;
        int speed = 10;

        Paddle(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void moveUp() {
            if (y > 0) {
                y -= speed;
            }
        }

        void smoothMoveUp(int factor) {
            if (y > 0) {
                y -= Math.min(speed, factor);
            }
        }

        void moveDown() {
            if (y < HEIGHT - height) {
                y += speed;
            }
        }

        void smoothMoveDown(int factor) {
            if (y < HEIGHT - height) {
                y += Math.min(speed, factor);
            }
        }

        void draw(Graphics g) {
            g.fillRect(x, y, width, height);
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
    }
}
