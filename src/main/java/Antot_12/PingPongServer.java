package Antot_12;

import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PingPongServer {
    private static final int PORT = 12345;
    protected static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static Ball ball = new Ball(400, 300);
    private static Paddle paddle1 = new Paddle(50, 250);
    private static Paddle paddle2 = new Paddle(730, 250);
    private static int score1 = 0;
    private static int score2 = 0;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Ping Pong Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    static synchronized void updateGame(String message) {
        String[] parts = message.split(" ");
        if (parts.length == 2) {
            int paddleIndex = Integer.parseInt(parts[0]);
            int paddleY = Integer.parseInt(parts[1]);
            if (paddleIndex == 1) {
                paddle1.y = paddleY;
            } else if (paddleIndex == 2) {
                paddle2.y = paddleY;
            }

            ball.move();
            if (ball.checkCollision(paddle1, paddle2)) {
                if (ball.x <= 0) {
                    score2++;
                    resetBall();
                } else if (ball.x >= 800 - ball.diameter) {
                    score1++;
                    resetBall();
                }
            }

            broadcast(ball.x + " " + ball.y + " " + paddle1.y + " " + paddle2.y + " " + score1 + " " + score2);
        }
    }

    private static void resetBall() {
        ball.x = 400;
        ball.y = 300;
        ball.xVelocity = -ball.xVelocity;
    }
}

class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int playerNumber;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.playerNumber = PingPongServer.clients.size() + 1;
    }

    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("PLAYER " + playerNumber); // Send player number to client

            String message;
            while ((message = in.readLine()) != null) {
                PingPongServer.updateGame(playerNumber + " " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void sendMessage(String message) {
        out.println(message);
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

    void move() {
        x += xVelocity;
        y += yVelocity;
        if (y <= 0 || y >= 600 - diameter) {
            yVelocity *= -1;
        }
    }

    boolean checkCollision(Paddle p1, Paddle p2) {
        if (x <= p1.x + p1.width && y >= p1.y && y <= p1.y + p1.height) {
            xVelocity *= -1;
            x = p1.x + p1.width; // Ensure the ball doesn't pass through the paddle
            return false;
        } else if (x >= p2.x - diameter && y >= p2.y && y <= p2.y + p2.height) {
            xVelocity *= -1;
            x = p2.x - diameter; // Ensure the ball doesn't pass through the paddle
            return false;
        }
        return true;
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
}
