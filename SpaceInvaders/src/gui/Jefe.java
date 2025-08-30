package gui;

import java.awt.*;

public class Jefe {
    private int x, y;
    private int ancho = 120, alto = 60;
    private int vidas;
    private boolean vivo = true;
    private int velocidadX = 4;

    public Jefe(int x, int y, int vidas) { this.x = x; this.y = y; this.vidas = vidas; }

    public void dibujar(Graphics g) {
        if(!vivo) return;
        g.setColor(Color.RED);
        g.fillRect(x, y, ancho, alto);
    }

    public void mover(int anchoPantalla) {
        x += velocidadX;
        if(x <=0 || x + ancho >= anchoPantalla) velocidadX*=-1;
    }

    public void recibirDano(int d) {
        vidas -= d;
        if(vidas <=0) vivo = false;
    }

    public boolean estaVivo() { return vivo; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public int getPuntos() { return 100; }
}