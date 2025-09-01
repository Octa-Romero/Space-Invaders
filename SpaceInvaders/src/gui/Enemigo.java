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
    private Image imagen; // puede ser nullprivate boolean explotando = false;
    private int tiempoExplosion = 0; // contador de frames
    private Image explosion; // imagen de explosión
    private boolean explotando = false;



    public Enemigo(String tipo, int x, int y) {
        this.tipo = tipo;
        this.x = x;
        this.y = y;

        
        explosion = new ImageIcon(getClass().getResource("/media/explosion.png")).getImage();

        // Asignar vidas segun tipo
        switch(tipo) {
            case "movil1", "disparo1", "kamikaze1" -> vidas = 1;
            case "movil2", "disparo2", "kamikaze2" -> vidas = 2;
            case "movil3", "disparo3", "kamikaze3" -> vidas = 3;
            default -> vidas = 1;
        }
        
        switch(tipo) {
        case "movil1", "movil2", "movil3" -> {
            ancho = 100;  // más grande
            alto = 80;
        }
        case "disparo1", "kamikaze1",
             "disparo2", "kamikaze2",
             "disparo3", "kamikaze3" -> {
            ancho = 60;
            alto = 40;
        }
    }
        // Asignar imagen solo si existe
        String ruta = null;
        switch(tipo) {
            case "movil1" -> ruta = "/media/movil1.png";
            case "movil2" -> ruta = "/media/movil1.png";
            case "movil3" -> ruta = "/media/movil1.png";
            case "disparo1" -> ruta = "/media/movil2.png";
            case "disparo2" -> ruta = "/media/movil2.png";
            case "disparo3" -> ruta = "/media/movil2.png";
            case "kamikaze1" -> ruta = "/media/movil3.png";
            case "kamikaze2" -> ruta = "/media/movil3.png";
            case "kamikaze3" -> ruta = "/media/movil3.png";
        }

        if (ruta != null) {
            try {
                imagen = new ImageIcon(getClass().getResource(ruta)).getImage();
            } catch (Exception e) {
                imagen = null; // fallback seguro
            }
        }
    }

    	public void dibujar(Graphics g) {
        if (vivo) {
            if (imagen != null) {
                g.drawImage(imagen, x, y, ancho, alto, null);
            } else {
                // fallback por si no hay imagen
            	if (tipo.startsWith("movil")) g.setColor(Color.CYAN);
                else if (tipo.startsWith("disparo")) g.setColor(Color.MAGENTA);
                else if (tipo.startsWith("kamikaze")) g.setColor(Color.ORANGE);
                else g.setColor(Color.GRAY);
                g.fillRect(x, y, ancho, alto);
            }
        } else if (explotando) {
            g.drawImage(explosion, x, y, ancho, alto, null);
            tiempoExplosion--;
            if (tiempoExplosion <= 0) {
                explotando = false; // termina la animación
            }
        }
    }


    public void recibirDano(int d) { 
        vidas -= d;
        if (vidas <= 0) {
            vivo = false;
            explotando = true; // activa animación
            tiempoExplosion = 20; // duración de la animación (frames)
        }
    }


    public boolean estaVivo() { return vivo; }
    public boolean isExplotando() {
        return explotando;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int nx) { x = nx; }
    public void setY(int ny) { y = ny; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public String getTipo() { return tipo; }

    public int getPuntos() { 
        return 10 * (tipo.endsWith("3") ? 3 : tipo.endsWith("2") ? 2 : 1); 
    }
}
