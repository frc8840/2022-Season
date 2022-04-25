package frc.robot.GUI;

import frc.robot.Main;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class TopLevelWindow {
    static JFrame frame;

    static HashMap<String, JLabel> labels = new HashMap<>();

    @Deprecated
    public static void createWindow() {
        frame = new JFrame("FRC Robot Controller");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();

        JLabel label = new JLabel("Current Speed: " + "0.0 ??");
        labels.put("stats", label);
        //label.setPreferredSize(new Dimension(800, 600));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JButton button = new JButton("Restart");
        button.addActionListener(e -> {
            //Main.newController();
        });
        button.setHorizontalAlignment(SwingConstants.CENTER);

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> {
            //Main.getController().stop();
        });
        stopButton.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(label);
        panel.add(button);
        panel.add(stopButton);

        frame.getContentPane().add(panel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Deprecated
    public static void updateWindow(String text) {
        labels.get("stats").setText(text);
    }
}
