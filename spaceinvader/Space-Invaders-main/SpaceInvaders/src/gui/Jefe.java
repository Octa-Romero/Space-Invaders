package gui;

import java.awt.*;
import javax.swing.*;

public class Jefe {
    private int x, y;
    private int ancho = 140, alto = 120;
    private int vidas;
    private boolean vivo = true;
    private int velocidadX = 4;
    private boolean explotando = false;
    private int contadorExplosion = 0;
    private Image imagenExplosion;

    private Image imagen; // imagen del jefe

    // Constructor nuevo que recibe la ruta de la imagen
 // Constructor nuevo que recibe la ruta de la imagen
    public Jefe(int x, int y, int vidas, String rutaImagen) {
        this.x = x;
        this.y = y;
        this.vidas = vidas;
        if (rutaImagen != null) {
            this.imagen = new ImageIcon(getClass().getResource("/media/jefeFinal.png")).getImage();
        }
        this.imagenExplosion = new ImageIcon(getClass().getResource("/media/explosionJefe.png")).getImage();

    }


    public void dibujar(Graphics g) {
        if (explotando) {
            g.drawImage(imagenExplosion, x, y, ancho, alto, null);
            contadorExplosion++;
            if (contadorExplosion > 20) { // duración de la explosión
                explotando = false;
                vivo = false;
            }
            return; // no dibujar vida ni imagen normal mientras explota
        }

        if(!vivo) return;

        if (imagen != null) {
            g.drawImage(imagen, x, y, ancho, alto, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, ancho, alto);
        }

        // Barra de vida
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 15, ancho, 10);
        g.setColor(Color.GREEN);
        int vidaActual = (int) ((vidas / 20.0) * ancho);
        g.fillRect(x, y - 15, vidaActual, 10);
    }

    public void mover(int anchoPantalla) {
        x += velocidadX;
        if(x <= 0 || x + ancho >= anchoPantalla) velocidadX *= -1;
    }

    public void recibirDano(int d) {
        vidas -= d;
        if (vidas <= 0 && !explotando) {
            explotando = true; // activa la animación
            contadorExplosion = 0;
        }
    }


    public boolean estaVivo() { return vivo; }
    public boolean isExplotando() {
        return explotando;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public int getPuntos() { return 100; }
}