package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {
    private Jugador jugador;
    private Enemigo[] enemigos;
    private Jefe jefe;
    private Bala[] balas = new Bala[50];
    private BalaEnemiga[] balasEnemigas = new BalaEnemiga[50];
    private int cantidadBalas = 0, cantidadBalasEnemigas = 0;
    private boolean izquierdaPresionada = false, derechaPresionada = false;
    private Timer timer;
    private int velocidadMovil = 3, direccionBloque = 1;
    private int contadorDisparoEnemigos = 0;
    private int nivelActual = 1;
    private int puntaje = 0;

    private boolean inicioPantalla = true;
    private boolean gameOver = false;
    private boolean youWin = false;

    private Image fondo;

    // kamikaze
    private int olaKamikaze = 0;
    private boolean olaActiva = false;

    public SpaceInvaders() {
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(20, this);
        timer.start();

        // fondo
        fondo = new ImageIcon(getClass().getResource("/media/fondo.png")).getImage();

        jugador = new Jugador(375, 700);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);

        if (inicioPantalla) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("SPACE INVADERS", getWidth()/2 - 150, getHeight()/2 - 50);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Presione cualquier tecla para jugar", getWidth()/2 - 180, getHeight()/2);
        } else if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("GAME OVER", getWidth()/2 - 150, getHeight()/2);
        } else if (youWin) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("YOU WIN", getWidth()/2 - 120, getHeight()/2);
        } else {
            // jugador
            jugador.dibujar(g);

            // balas jugador
            for (int i = 0; i < cantidadBalas; i++)
                if (balas[i] != null) balas[i].dibujar(g);

            // balas enemigas
            for (int i = 0; i < cantidadBalasEnemigas; i++)
                if (balasEnemigas[i] != null) balasEnemigas[i].dibujar(g);

            // enemigos
            for (Enemigo e : enemigos) if (e != null) e.dibujar(g);

            // jefe
            if (jefe != null) jefe.dibujar(g);

            // puntaje y nivel
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Puntaje: " + puntaje, 10, getHeight() - 10);
            g.drawString("Nivel: " + nivelActual, getWidth() - 100, getHeight() - 10);

            // vidas jugador
            g.drawString("Vidas: " + jugador.getVidas(), 10, 30);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!inicioPantalla && !gameOver && !youWin) {
            // movimiento jugador
            if (izquierdaPresionada) jugador.moverIzquierda();
            if (derechaPresionada) jugador.moverDerecha(getWidth());

            // actualizar invulnerabilidad
            jugador.actualizarInvulnerabilidad();

            // mover balas
            moverBalasJugador();
            moverBalasEnemigas();

            // mover enemigos móviles
            moverEnemigos();

            // disparos enemigos individuales
            disparoEnemigos();

            // mover jefe
            if (jefe != null && jefe.estaVivo()) jefe.mover(getWidth());

            // kamikaze
            moverKamikaze();

            // colisiones
            colisiones();

            // verificar fin de nivel
            verificarFinNivel();
        }
        repaint();
    }

    private void moverEnemigos() {
        boolean rebote = false;
        for (Enemigo e : enemigos) {
            if (e != null && e.getTipo().startsWith("movil")) {
                e.setX(e.getX() + velocidadMovil * direccionBloque);
                if (e.getX() <= 0 || e.getX() + e.getAncho() >= getWidth()) rebote = true;
            }
        }
        if (rebote) {
            direccionBloque *= -1;
            for (Enemigo e : enemigos) if (e != null && e.getTipo().startsWith("movil")) e.setY(e.getY() + 20);
        }
    }

    private void moverBalasJugador() {
        for (int i = 0; i < cantidadBalas; i++)
            if (balas[i] != null && balas[i].estaActiva()) balas[i].mover();
    }

    private void moverBalasEnemigas() {
        for (int i = 0; i < cantidadBalasEnemigas; i++)
            if (balasEnemigas[i] != null && balasEnemigas[i].estaActiva()) balasEnemigas[i].mover();
    }

    private void disparoEnemigos() {
        contadorDisparoEnemigos++;
        if (contadorDisparoEnemigos >= 50) {
            for (Enemigo e : enemigos) {
                if (e != null && e.estaVivo() && e.getTipo().startsWith("disparo")) {
                    dispararEnemigo(e.getX() + e.getAncho()/2, e.getY() + e.getAlto());
                }
            }
            if (jefe != null && jefe.estaVivo()) dispararEnemigo(jefe.getX() + jefe.getAncho()/2, jefe.getY() + jefe.getAlto());
            contadorDisparoEnemigos = 0;
        }
    }

    private void dispararJugador() {
        if (cantidadBalas < balas.length) balas[cantidadBalas++] = new Bala(jugador.getX() + jugador.getAncho()/2 - 4, jugador.getY() - 10);
    }

    private void dispararEnemigo(int x, int y) {
        if (cantidadBalasEnemigas < balasEnemigas.length) balasEnemigas[cantidadBalasEnemigas++] = new BalaEnemiga(x, y);
    }

    private void colisiones() {
        // balas jugador vs enemigos y jefe
        for (int i = 0; i < cantidadBalas; i++) {
            Bala bala = balas[i];
            if (bala != null && bala.estaActiva()) {
                for (int j = 0; j < enemigos.length; j++) {
                    Enemigo enemigo = enemigos[j];
                    if (enemigo != null && enemigo.estaVivo() && bala.colisiona(enemigo)) {
                        enemigo.recibirDano(1);
                        bala.setActiva(false);
                        if (!enemigo.estaVivo()) puntaje += enemigo.getPuntos();
                    }
                }
                if (jefe != null && jefe.estaVivo() && bala.colisiona(jefe)) {
                    jefe.recibirDano(1);
                    bala.setActiva(false);
                    if (!jefe.estaVivo()) {
                        puntaje += jefe.getPuntos();
                        youWin = true;
                        Timer timerYOUWIN = new Timer(5000, ev -> {
                            youWin = false;
                            nivelActual = 1;
                            inicioPantalla = true;
                            iniciarNivel();
                        });
                        timerYOUWIN.setRepeats(false);
                        timerYOUWIN.start();
                    }
                }
            }
        }

        // balas enemigas vs jugador
        for (int i = 0; i < cantidadBalasEnemigas; i++) {
            BalaEnemiga bala = balasEnemigas[i];
            if (bala != null && bala.estaActiva() && jugador.estaVivo() && !jugador.estaInvulnerable() && bala.colisiona(jugador)) {
                bala.setActiva(false);
                jugador.perderVida();
            }
        }
    }

    private void moverKamikaze() {
        if (nivelActual == 3 || nivelActual == 6 || nivelActual == 9) {
            for (Enemigo e : enemigos) {
                if (e != null && e.estaVivo()) {
                    if (!e.bajo) {
                        e.setY(e.getY() + 5);
                        if (e.getY() >= jugador.getY() - e.getAlto()) e.bajo = true;
                    } else {
                        e.setY(e.getY() - 5);
                    }

                    // colision jugador
                    if (jugador.estaVivo() && !jugador.estaInvulnerable() &&
                        e.getX() < jugador.getX() + jugador.getAncho() &&
                        e.getX() + e.getAncho() > jugador.getX() &&
                        e.getY() < jugador.getY() + jugador.getAlto() &&
                        e.getY() + e.getAlto() > jugador.getY()) 
                    {
                        jugador.perderVida();
                        e.recibirDano(999);
                    }
                }
            }

            // verificar si ola termino
            boolean olaTerminada = true;
            for (Enemigo e : enemigos) if (e != null && e.estaVivo()) olaTerminada = false;
            if (olaTerminada) avanzarSiguienteOlaKamikaze();
        }
    }

    private void avanzarSiguienteOlaKamikaze() {
        olaKamikaze++;
        if (olaKamikaze > 2) {
            olaKamikaze = 0;
            nivelActual++;
            iniciarNivel();
        } else {
            iniciarOlaKamikaze(olaKamikaze);
        }
    }

    private void iniciarOlaKamikaze(int numeroOla) {
        // crear enemigos segun ola (izquierda, derecha, medio)
        int x = 0;
        if (numeroOla == 0) x = 0;
        if (numeroOla == 1) x = getWidth() - 50;
        if (numeroOla == 2) x = getWidth()/2 - 25;
        enemigos = new Enemigo[]{new Enemigo("kamikaze" + (nivelActual/3), x, 50)};
    }

    private void verificarFinNivel() {
        boolean nivelTerminado = true;
        if (nivelActual != 3 && nivelActual != 6 && nivelActual != 9 && nivelActual != 10) {
            for (Enemigo e : enemigos) if (e != null && e.estaVivo()) nivelTerminado = false;
            if (nivelTerminado) avanzarNivel();
        }
    }

    private void avanzarNivel() {
        nivelActual++;
        iniciarNivel();
    }

    private void iniciarNivel() {
        jugador.respawn(getWidth()/2 - jugador.getAncho()/2, 700);
        cantidadBalas = 0;
        cantidadBalasEnemigas = 0;
        jefe = null;
        enemigos = null;

        switch(nivelActual) {
            case 1: // moviles 1
                enemigos = new Enemigo[]{
                    new Enemigo("movil1", 50, 50),
                    new Enemigo("movil1", 150, 50),
                    new Enemigo("movil1", 250, 50),
                };
                break;
            case 2: // disparadores 1
                enemigos = new Enemigo[]{
                    new Enemigo("disparo1", 50, 50),
                    new Enemigo("disparo1", 150, 50),
                    new Enemigo("disparo1", 250, 50),
                };
                break;
            case 3: // kamikazes 1
                olaKamikaze = 0;
                iniciarOlaKamikaze(0);
                break;
            case 4: // moviles 2
                enemigos = new Enemigo[]{
                    new Enemigo("movil2", 50, 50),
                    new Enemigo("movil2", 150, 50),
                    new Enemigo("movil2", 250, 50),
                };
                break;
            case 5: // disparadores 2
                enemigos = new Enemigo[]{
                    new Enemigo("disparo2", 50, 50),
                    new Enemigo("disparo2", 150, 50),
                    new Enemigo("disparo2", 250, 50),
                };
                break;
            case 6: // kamikazes 2
                olaKamikaze = 0;
                iniciarOlaKamikaze(0);
                break;
            case 7: // moviles 3
                enemigos = new Enemigo[]{
                    new Enemigo("movil3", 50, 50),
                    new Enemigo("movil3", 150, 50),
                    new Enemigo("movil3", 250, 50),
                };
                break;
            case 8: // disparadores 3
                enemigos = new Enemigo[]{
                    new Enemigo("disparo3", 50, 50),
                    new Enemigo("disparo3", 150, 50),
                    new Enemigo("disparo3", 250, 50),
                };
                break;
            case 9: // kamikazes 3
                olaKamikaze = 0;
                iniciarOlaKamikaze(0);
                break;
            case 10: // boss
                jefe = new Jefe(getWidth()/2 - 60, 50, 30);
                enemigos = new Enemigo[0];
                break;
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (inicioPantalla) {
            inicioPantalla = false;
            iniciarNivel();
        } else if (!gameOver && !youWin) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_A -> izquierdaPresionada = true;
                case KeyEvent.VK_D -> derechaPresionada = true;
                case KeyEvent.VK_UP -> dispararJugador();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_A -> izquierdaPresionada = false;
            case KeyEvent.VK_D -> derechaPresionada = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}