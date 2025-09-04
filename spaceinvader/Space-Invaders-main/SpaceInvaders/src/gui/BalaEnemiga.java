package gui;

import java.awt.*;

public class BalaEnemiga {
    private int x, y;
    private int ancho = 8, alto = 8;
    private int velocidad = 10;
    private boolean activa = true;
    private int limiteY;

    public BalaEnemiga(int x, int y, int limiteY) {
        this.x = x;
        this.y = y;
        this.limiteY = limiteY;
    }

    public void dibujar(Graphics g) {
        if (!activa) return;
        g.setColor(Color.RED);
        g.fillRect(x, y, ancho, alto);
    }

    public void mover() {
        y += velocidad;
        if (y > limiteY) activa = false;
    }

    public boolean colisiona(Jugador j) {
        return j.estaVivo() && x < j.getX() + j.getAncho() && x + ancho > j.getX() &&
               y < j.getY() + j.getAlto() && y + alto > j.getY();
    }

    public boolean estaActiva() { return activa; }
    public void setActiva(boolean a) { activa = a; }
}
