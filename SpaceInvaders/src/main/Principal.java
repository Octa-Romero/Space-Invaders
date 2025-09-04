package main;

import javax.swing.*;
import gui.SpaceInvaders;

public class Principal {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame ventana = new JFrame("Space Invaders");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setSize(800, 800);
            ventana.setResizable(false);

            ventana.setLocationRelativeTo(null);

            SpaceInvaders juego = new SpaceInvaders();
            ventana.add(juego);

            ventana.setVisible(true);
        });
    }
}