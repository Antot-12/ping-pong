package Antot_12;

import javax.swing.*;
import java.awt.*;

public class PingPongGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Ping Pong Game");
            frame.setSize(400, 300);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setBackground(Color.DARK_GRAY);
            frame.add(panel);
            placeComponents(panel);

            frame.setVisible(true);
        });
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel userLabel = new JLabel("Choose a game mode:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 18));
        userLabel.setForeground(Color.CYAN);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);

        JButton twoPlayerButton = createButton("2 Players");
        gbc.gridy = 1;
        panel.add(twoPlayerButton, gbc);
        twoPlayerButton.addActionListener(e -> {
            startServerAndClients();
        });

        JButton easyAIButton = createButton("1 Player vs Easy AI");
        gbc.gridy = 2;
        panel.add(easyAIButton, gbc);
        easyAIButton.addActionListener(e -> new PingPongClientVsEasyAI());

        JButton hardAIButton = createButton("1 Player vs Hard AI");
        gbc.gridy = 3;
        panel.add(hardAIButton, gbc);
        hardAIButton.addActionListener(e -> new PingPongClientVsAI());
    }

    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(255, 165, 0));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);

        return button;
    }

    private static void startServerAndClients() {
        new Thread(() -> {
            PingPongServer.main(new String[]{});
        }).start();

        try {
            // Delay to ensure server starts before clients try to connect
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new PingPongServer();
            new PingPongClient();
            new PingPongClient2();
        });
    }
}
