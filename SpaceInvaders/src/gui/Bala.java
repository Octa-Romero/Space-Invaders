package gui;

import java.awt.*;

public class Bala {
    private int x, y;
    private int ancho = 4, alto = 10;
    private int velocidad = 15;
    private boolean activa = true;

    public Bala(int x, int y) { this.x = x; this.y = y; }

    public void dibujar(Graphics g) {
        if(!activa) return;
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, ancho, alto);
    }

    public void mover() { y -= velocidad; if(y<0) activa = false; }

    public boolean colisiona(Enemigo e) {
        return e.estaVivo() && x < e.getX() + e.getAncho() && x + ancho > e.getX() &&
               y < e.getY() + e.getAlto() && y + alto > e.getY();
    }

    public boolean colisiona(Jefe j) {
        return j.estaVivo() && x < j.getX() + j.getAncho() && x + ancho > j.getX() &&
               y < j.getY() + j.getAlto() && y + alto > j.getY();
    }

    public boolean estaActiva() { return activa; }
    public void setActiva(boolean a) { activa = a; }
}