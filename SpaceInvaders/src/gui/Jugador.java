package gui;

import java.awt.*;

public class Jugador {
    private int x, y;
    private int ancho = 40, alto = 20;
    private int vidas = 3;
    private boolean invulnerable = false;
    private int invulnerableTimer = 0;

    public Jugador(int x, int y) { this.x = x; this.y = y; }

    public void moverIzquierda() { x -= 10; if(x<0)x=0; }
    public void moverDerecha(int anchoPantalla) { x += 10; if(x+ancho>anchoPantalla)x=anchoPantalla-ancho; }

    public void dibujar(Graphics g) {
        if(invulnerable && invulnerableTimer % 4 < 2) return;
        g.setColor(Color.GREEN);
        g.fillRect(x, y, ancho, alto);
    }

    public void perderVida() { 
        vidas--; 
        invulnerable = true;
        invulnerableTimer = 50; 
        respawn(380, 700);
    }

    public void actualizarInvulnerabilidad() {
        if(invulnerable) {
            invulnerableTimer--;
            if(invulnerableTimer <= 0) invulnerable = false;
        }
    }

    public void respawn(int nx, int ny) { x = nx; y = ny; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public int getVidas() { return vidas; }
    public boolean estaVivo() { return vidas > 0; }
    public boolean estaInvulnerable() { return invulnerable; }
}