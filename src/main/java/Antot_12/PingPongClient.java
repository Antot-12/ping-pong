package Antot_12;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class PingPongClient extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final Color ORANGE_YELLOW = new Color(255, 165, 0);

    private Canvas canvas;
    private Ball ball;
    private Paddle paddle1;
    private Paddle paddle2;
    private boolean upPressed = false;
    private boolean downPressed = false;

    private PrintWriter out;
    private BufferedReader in;
    private int playerNumber;
    private int score1 = 0;
    private int score2 = 0;

    public PingPongClient() {
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

        connectToServer();
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        if (message.startsWith("PLAYER")) {
                            playerNumber = Integer.parseInt(message.split(" ")[1]);
                            setTitle("Ping Pong Client - Player " + playerNumber);
                        } else {
                            processServerMessage(message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processServerMessage(String message) {
        try {
            String[] parts = message.split(" ");
            if (parts.length == 6) {
                ball.x = Integer.parseInt(parts[0]);
                ball.y = Integer.parseInt(parts[1]);
                paddle1.y = Integer.parseInt(parts[2]);
                paddle2.y = Integer.parseInt(parts[3]);
                score1 = Integer.parseInt(parts[4]);
                score2 = Integer.parseInt(parts[5]);
                canvas.repaint(); // Ensure the canvas is repainted when the state is updated
                checkForWinner();
            } else {
                System.err.println("Invalid message format: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gameLoop() {
        if (upPressed) {
            if (playerNumber == 1) {
                paddle1.moveUp();
            } else if (playerNumber == 2) {
                paddle2.moveUp();
            }
        }
        if (downPressed) {
            if (playerNumber == 1) {
                paddle1.moveDown();
            } else if (playerNumber == 2) {
                paddle2.moveDown();
            }
        }

        ball.move();
        checkCollisions();

        // Send paddle position to the server
        if (out != null) {
            int paddleY = playerNumber == 1 ? paddle1.y : paddle2.y;
            out.println(paddleY);
        }

        canvas.repaint();
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

        // Send the updated game state to the server
        if (out != null) {
            out.println(ball.x + " " + ball.y + " " + paddle1.y + " " + paddle2.y + " " + score1 + " " + score2);
        }
    }

    private void checkForWinner() {
        if (score1 >= 10 || score2 >= 10) {
            int result = JOptionPane.showConfirmDialog(this, "Game Over. Do you want to play again?", "Game Over", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                resetGame();
            } else {
                System.exit(0);
            }
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
        SwingUtilities.invokeLater(PingPongClient::new);
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
        int xVelocity = 5, yVelocity = 5;

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

        void moveDown() {
            if (y < HEIGHT - height) {
                y += speed;
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
