package gui;

import java.awt.*;
import javax.swing.ImageIcon;

public class Jugador {
    private int x, y;
    private int ancho = 60, alto = 40;
    private int vidas = 3;
    private boolean invulnerable = false;
    private int invulnerableTimer = 0;
    private Image imagen;

    // Animación propulsión
    private Image[] propulsion;
    private int framePropulsion = 0;
    private boolean moviendo = false; // indica si la nave se mueve

    public Jugador(int x, int y, String rutaImagen) {
        this.x = x;
        this.y = y;
        if (rutaImagen != null && getClass().getResource(rutaImagen) != null) {
            imagen = new ImageIcon(getClass().getResource(rutaImagen)).getImage();
        }


        // Cargar frames de propulsion correctamente
        propulsion = new Image[3];
        for (int i = 0; i < propulsion.length; i++) {
            String ruta = "/media/propulsion" + i + ".png";
            Image img = new ImageIcon(getClass().getResource(ruta)).getImage();
            propulsion[i] = img;
        }
    }

    public void moverIzquierda() { 
        x -= 10; 
        if (x < 0) x = 0;
        moviendo = true;
    }

    public void moverDerecha(int anchoPantalla) { 
        x += 10; 
        if (x + ancho > anchoPantalla) x = anchoPantalla - ancho;
        moviendo = true;
    }

    public void dibujar(Graphics g) {
        if (invulnerable && invulnerableTimer % 4 < 2) return;

        // Dibujar la nave
        if (imagen != null) {
            g.drawImage(imagen, x, y, ancho, alto, null);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(x, y, ancho, alto);
        }

        // Dibujar propulsión debajo de la nave
        if (moviendo && propulsion[framePropulsion] != null) {
            g.drawImage(propulsion[framePropulsion], x, y + alto, ancho, 15, null);
            framePropulsion++;
            if (framePropulsion >= propulsion.length) framePropulsion = 0;
        }

        moviendo = false; // reiniciar para el siguiente frame
    }
    
    public void reiniciarVidas()
    {
    	vidas=3;
    }

    public void perderVida() { 
        vidas--; 
        invulnerable = true;
        invulnerableTimer = 50; 
        respawn(380, 700);
    }

    public void actualizarInvulnerabilidad() {
        if (invulnerable) {
            invulnerableTimer--;
            if (invulnerableTimer <= 0) invulnerable = false;
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