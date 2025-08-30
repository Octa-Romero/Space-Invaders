package gui;

import java.awt.*;

public class Enemigo {
    private int x, y;
    private int ancho = 40, alto = 20;
    private int vidas;
    private boolean vivo = true;
    public boolean bajo = false;
    private String tipo;

    public Enemigo(String tipo, int x, int y) {
        this.tipo = tipo;
        this.x = x;
        this.y = y;

        switch(tipo) {
            case "movil1", "disparo1" -> vidas = 1;
            case "movil2", "disparo2" -> vidas = 2;
            case "movil3", "disparo3" -> vidas = 3;
            case "kamikaze1" -> vidas = 1;
            case "kamikaze2" -> vidas = 2;
            case "kamikaze3" -> vidas = 3;
            default -> vidas = 1;
        }
    }

    public void dibujar(Graphics g) {
        if(!vivo) return;
        if(tipo.startsWith("movil")) g.setColor(Color.CYAN);
        else if(tipo.startsWith("disparo")) g.setColor(Color.MAGENTA);
        else if(tipo.startsWith("kamikaze")) g.setColor(Color.ORANGE);
        g.fillRect(x, y, ancho, alto);
    }

    public void recibirDano(int d) { 
        vidas -= d;
        if(vidas <= 0) vivo = false;
    }

    public boolean estaVivo() { return vivo; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int nx) { x = nx; }
    public void setY(int ny) { y = ny; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public String getTipo() { return tipo; }
    public int getPuntos() { return 10 * (tipo.endsWith("3") ? 3 : tipo.endsWith("2") ? 2 : 1); }
}