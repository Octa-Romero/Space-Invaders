package gui;

import java.awt.*;
import javax.swing.ImageIcon;

public class Enemigo {
    private int x, y;
    private int ancho = 80, alto = 60;
    private int vidas;
    private boolean vivo = true;
    public boolean bajo = false;
    private String tipo;
    private Image imagen;
    private Image explosion;
    private boolean explotando = false;
    private int tiempoExplosion = 0;
    private boolean sonidoExplotado = false; // nuevo: para disparar sonido solo 1 vez
    private SpaceInvaders game; // referencia para reproducir sonido

    public Enemigo(String tipo, int x, int y, SpaceInvaders game) {
        this.tipo = tipo;
        this.x = x;
        this.y = y;
        this.game = game;

        explosion = new ImageIcon(getClass().getResource("/media/explosion.png")).getImage();

        switch(tipo) {
            case "movil1", "disparo1", "kamikaze1" -> vidas = 1;
            case "movil2", "disparo2", "kamikaze2" -> vidas = 2;
            case "movil3", "disparo3", "kamikaze3" -> vidas = 3;
            default -> vidas = 1;
        }

        switch(tipo) {
            case "movil1", "movil2", "movil3" -> { ancho = 100; alto = 80; }
            case "disparo1", "disparo2", "disparo3",
                 "kamikaze1", "kamikaze2", "kamikaze3" -> { ancho = 60; alto = 40; }
        }

        String ruta = null;
        switch(tipo) {
            case "movil1","movil2","movil3" -> ruta = "/media/movil1.png";
            case "disparo1","disparo2","disparo3" -> ruta = "/media/movil2.png";
            case "kamikaze1","kamikaze2","kamikaze3" -> ruta = "/media/movil3.png";
        }
        if (ruta != null) {
            try { imagen = new ImageIcon(getClass().getResource(ruta)).getImage(); }
            catch(Exception e){ imagen = null; }
        }
    }

    public void dibujar(Graphics g) {
        if (vivo) {
            if (imagen != null) g.drawImage(imagen, x, y, ancho, alto, null);
            else {
                if (tipo.startsWith("movil")) g.setColor(Color.CYAN);
                else if (tipo.startsWith("disparo")) g.setColor(Color.MAGENTA);
                else if (tipo.startsWith("kamikaze")) g.setColor(Color.ORANGE);
                else g.setColor(Color.GRAY);
                g.fillRect(x, y, ancho, alto);
            }
        } else if (explotando) {
            g.drawImage(explosion, x, y, ancho, alto, null);
            if (!sonidoExplotado) {
                game.reproducirSonido("/media/explosion.wav"); // sonido solo 1 vez
                sonidoExplotado = true;
            }
            tiempoExplosion--;
            if (tiempoExplosion <= 0) {
                explotando = false;
            }
        }
    }

    public void recibirDano(int d) { 
        vidas -= d;
        if (vidas <= 0) {
            vivo = false;
            explotando = true;
            tiempoExplosion = 20;
            sonidoExplotado = false;
        }
    }

    public boolean estaVivo() { return vivo; }
    public boolean isExplotando() { return explotando; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int nx) { x = nx; }
    public void setY(int ny) { y = ny; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public String getTipo() { return tipo; }
    public int getPuntos() { return 10 * (tipo.endsWith("3") ? 3 : tipo.endsWith("2") ? 2 : 1); }
}
