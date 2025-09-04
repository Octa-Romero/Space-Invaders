package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.*;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {

    private Jugador jugador;
    private Enemigo[] enemigos;
    private Jefe jefe;
    private Bala[] balas = new Bala[1000];
    private BalaEnemiga[] balasEnemigas = new BalaEnemiga[50];
    private int cantidadBalas = 0, cantidadBalasEnemigas = 0;

    private boolean izquierdaPresionada = false, derechaPresionada = false;
    private Timer timer;
    private int velocidadMovil = 3, direccionBloque = 1;
    private int nivelActual = 1;
    private int puntaje = 0;
    private boolean inicioPantalla = true, gameOver = false, youWin = false;
    private Image fondo;
    private Image fondoInicio;
    private boolean visible = true;
    private int contadorParpadeo = 0;



    // Kamikaze
    private int olaKamikaze = 0;

    // Música de pantalla de inicio
    private Clip musicaInicio;

    public SpaceInvaders() {
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(20, this);
        timer.start();

        fondo = new ImageIcon(getClass().getResource("/media/fondito.jpg")).getImage();
        jugador = new Jugador(375, 700, "/media/jugador.png");
        fondoInicio = new ImageIcon(getClass().getResource("/media/space invaders.png")).getImage();


        // Reproducir música de inicio
        reproducirMusicaInicio("/media/musica inicio.wav");
    }

    //sonido
    public void reproducirSonido(String ruta, float volumenDB) {
        new Thread(() -> {
            try (InputStream is = getClass().getResourceAsStream(ruta)) {
                if (is == null) return;
                BufferedInputStream bis = new BufferedInputStream(is);
                AudioInputStream ais = AudioSystem.getAudioInputStream(bis);

                AudioFormat baseFormat = ais.getFormat();
                AudioFormat decodedFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(),
                        16,
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2,
                        baseFormat.getSampleRate(),
                        false
                );

                try (AudioInputStream dais = AudioSystem.getAudioInputStream(decodedFormat, ais)) {
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
                    try (SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                        line.open(decodedFormat);
                        line.start();
                        FloatControl control = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                        control.setValue(volumenDB); // negativo = mas bajo, 0 = volumen maximo
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = dais.read(buffer)) != -1)
                            line.write(buffer, 0, bytesRead);
                        line.drain();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
 // Sobrecarga para mantener llamadas antiguas
    public void reproducirSonido(String ruta) {
        reproducirSonido(ruta, 0.0f); // 0 = volumen máximo por defecto
    }


    // musica inicio
    public void reproducirMusicaInicio(String ruta) {
        new Thread(() -> {
            try (InputStream is = getClass().getResourceAsStream(ruta)) {
                if (is == null) return;
                BufferedInputStream bis = new BufferedInputStream(is);
                AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
                musicaInicio = AudioSystem.getClip();
                musicaInicio.open(ais);

                // Controlar volumen
                FloatControl volumen = (FloatControl) musicaInicio.getControl(FloatControl.Type.MASTER_GAIN);
                volumen.setValue(-10.0f); // Valor en decibelios, negativo = más bajo, 0 = volumen máximo

                musicaInicio.loop(Clip.LOOP_CONTINUOUSLY); // bucle infinito
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    //

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (inicioPantalla) {
            g.drawImage(fondoInicio, 0, 0, getWidth(), getHeight(), this);
            g.setColor(Color.YELLOW);
            if (visible) {
                g.setFont(new Font("Arial", Font.PLAIN, 24));
                g.drawString("Presione cualquier tecla para jugar", getWidth()/2 - 180, getHeight()/2+90);
            }
        } else if (gameOver) {
        	g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("GAME OVER", getWidth()/2 - 150, getHeight()/2 - 50);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.drawString("Puntaje final: " + puntaje, getWidth()/2 - 120, getHeight()/2 + 10);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 25));
            g.drawString("Presione R para reiniciar", getWidth()/2 - 140, getHeight()/2 + 60);
        } 
        else if (youWin) {
        	g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("YOU WIN", getWidth()/2 - 120, getHeight()/2 - 50);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.drawString("Puntaje total: " + puntaje, getWidth()/2 - 135, getHeight()/2 + 10);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 25));
            g.drawString("Presione R para volver a jugar", getWidth()/2 - 183, getHeight()/2 + 65);
        }else {
            // Juego normal
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);

            jugador.dibujar(g);

            for (int i = 0; i < balas.length; i++)
                if (balas[i] != null) balas[i].dibujar(g);

            for (int i = 0; i < balasEnemigas.length; i++)
                if (balasEnemigas[i] != null) balasEnemigas[i].dibujar(g);

            if (enemigos != null)
                for (Enemigo e : enemigos) if (e != null) e.dibujar(g);

            if (jefe != null) jefe.dibujar(g);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Puntaje: " + puntaje, 10, getHeight() - 10);
            g.drawString("Nivel: " + nivelActual, getWidth() - 100, getHeight() - 10);
            g.drawString("Vidas: " + jugador.getVidas(), 10, 30);
        }
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if (!inicioPantalla && !gameOver && !youWin) {
            if (izquierdaPresionada) jugador.moverIzquierda();
            if (derechaPresionada) jugador.moverDerecha(getWidth());

            jugador.actualizarInvulnerabilidad();
            moverBalasJugador();
            moverBalasEnemigas();
            moverEnemigos();
            disparoEnemigos();

            if (jefe != null && jefe.estaVivo()) jefe.mover(getWidth());

            moverKamikaze();
            colisiones();
            verificarFinNivel();
        } else if (inicioPantalla) {
            // --- Parpadeo del texto arcade ---
            contadorParpadeo++;
            if (contadorParpadeo > 30) { // cada 30 ciclos 
                visible = !visible;
                contadorParpadeo = 0;
            }
        }
        repaint();
    }

    // Movimientos
    private void moverEnemigos() {
        boolean rebote = false;
        if (enemigos != null) {
            for (Enemigo e : enemigos) {
                if (e != null && e.getTipo().startsWith("movil")) {
                    e.setX(e.getX() + velocidadMovil * direccionBloque);
                    if (e.getX() <= 0 || e.getX() + e.getAncho() >= getWidth()) rebote = true;
                }
            }
            if (rebote) {
                direccionBloque *= -1;
                for (Enemigo e : enemigos)
                    if (e != null && e.getTipo().startsWith("movil")) e.setY(e.getY() + 20);
            }
        }
    }

    private void moverBalasJugador() {
        for (int i = 0; i < balas.length; i++)
            if (balas[i] != null && balas[i].estaActiva()) balas[i].mover();
    }

    private void moverBalasEnemigas() {
        for (int i = 0; i < balasEnemigas.length; i++) {
            BalaEnemiga bala = balasEnemigas[i];
            if (bala != null && bala.estaActiva()) {
                bala.mover();
            }
        }
    }


    private void disparoEnemigos() {
        	if (enemigos != null) {
        	    for (Enemigo e : enemigos) {
        	        if (e != null && e.estaVivo() && e.getTipo().startsWith("disparo")) {
        	            e.contadorDisparo++;
        	            if (e.contadorDisparo >= e.intervaloDisparo) {
        	                dispararEnemigo(e.getX() + e.getAncho() / 2, e.getY() + e.getAlto());
        	                e.contadorDisparo = 0;
        	                e.intervaloDisparo = 50 + (int)(Math.random() * 100); // nuevo intervalo aleatorio
        	            }
        	        }
        	    }
            
            if (jefe != null && jefe.estaVivo())
                dispararJefe();
        }
    }

    private void dispararJugador() {
        balas[cantidadBalas % balas.length] = 
            new Bala(jugador.getX() + jugador.getAncho()/2 - 4, jugador.getY() - 10);
        cantidadBalas++;
        reproducirSonido("/media/disparo.wav"); // sonido disparo jugador
    }
    
    private void crearBalaEnemiga(int x, int y) {
        for (int i = 0; i < balasEnemigas.length; i++) {
            if (balasEnemigas[i] == null || !balasEnemigas[i].estaActiva()) {
                balasEnemigas[i] = new BalaEnemiga(x, y, this.getHeight());
                return;
            }
        }
    }
    private long ultimoDisparoJefe = 0;

    private void dispararEnemigo(int x, int y) {
        for (int i = 0; i < balasEnemigas.length; i++) {
            if (balasEnemigas[i] == null || !balasEnemigas[i].estaActiva()) {
                balasEnemigas[i] = new BalaEnemiga(x, y, this.getHeight());
                cantidadBalasEnemigas++;
                reproducirSonido("/media/disparoEnemigo.wav", -10.0f); // sonido menos saturado
                return;
            }
        }
    }


    private void dispararJefe() {
        if (jefe != null && jefe.estaVivo()) {
            long ahora = System.currentTimeMillis();
            if (ahora - ultimoDisparoJefe < 500) return; // 0.5s entre disparos
            ultimoDisparoJefe = ahora;

            crearBalaEnemiga(jefe.getX() + jefe.getAncho()/2 - 35, jefe.getY() + jefe.getAlto());
            crearBalaEnemiga(jefe.getX() + jefe.getAncho()/2 + 35, jefe.getY() + jefe.getAlto());

            reproducirSonido("/media/disparo Jefe.wav", -10.0f); // volumen más bajo
        }
    }

    private void crearBalaJefe(int x, int y) {
        for (int i = 0; i < balasEnemigas.length; i++) {
            if (balasEnemigas[i] == null || !balasEnemigas[i].estaActiva()) {
                balasEnemigas[i] = new BalaEnemiga(x, y, this.getHeight());
                break;
            }
        }
    }


    private void colisiones() {
        // Balas jugador
        for (int i = 0; i < cantidadBalas; i++) {
            Bala bala = balas[i];
            if (bala != null && bala.estaActiva()) {
                if (enemigos != null)
                    for (Enemigo enemigo : enemigos)
                        if (enemigo != null && enemigo.estaVivo() && bala.colisiona(enemigo)) {
                            enemigo.recibirDano(1);
                            bala.setActiva(false);
                            if (!enemigo.estaVivo()) puntaje += enemigo.getPuntos();
                        }

                if (jefe != null && jefe.estaVivo() && bala.colisiona(jefe)) {
                    jefe.recibirDano(1);
                    bala.setActiva(false);
                }

                if (jefe != null && !jefe.estaVivo() && !jefe.isExplotando()) {
                    puntaje += jefe.getPuntos();
                    youWin = true;
                }
            }
        }

        // Balas enemigas
        for (BalaEnemiga bala : balasEnemigas) {
            if (bala != null && bala.estaActiva() && jugador.estaVivo() && !jugador.estaInvulnerable() && bala.colisiona(jugador)) {
                bala.setActiva(false);
                jugador.perderVida();
                reproducirSonido("/media/daño.wav");
            }
        }


        for(Enemigo e : enemigos)
        {
        if(e != null && e.estaVivo())
        if (jugador.estaVivo() && !jugador.estaInvulnerable() &&
        	    e.getX() < jugador.getX() + jugador.getAncho() &&
        	    e.getX() + e.getAncho() > jugador.getX() &&
        	    e.getY() < jugador.getY() + jugador.getAlto() &&
        	    e.getY() + e.getAlto() > jugador.getY()) {

        	    jugador.perderVida();
        	    reproducirSonido("/media/daño.wav");
        }
        }
        
        // Verificar si el jugador murio despues de colisiones
        if (!jugador.estaVivo()) {
            gameOver = true;
        }
    }


    private void moverKamikaze() {
        if (nivelActual == 3 || nivelActual == 6 || nivelActual == 9) {
            if (enemigos != null) {
                for (Enemigo e : enemigos) {
                    if (e != null && e.estaVivo()) {
                        
                        // Si aun no entró en modo kamikaze (después de 3 segundos)
                    	if (!e.modoKamikaze && !e.timerKamikazeIniciado) {
                    	    e.timerKamikazeIniciado = true; // evitar múltiples timers
                    	    Timer inicioKamikaze = new Timer(3000, et -> {
                    	        e.modoKamikaze = true;
                    	    });
                    	    inicioKamikaze.setRepeats(false); // importante: solo se ejecuta una vez
                    	    inicioKamikaze.start();
                    	}

                            // Movimiento horizontal (como "movil")
                            if (!e.alineado && !e.modoKamikaze) {
                                if (e.getX() + e.getAncho()/2 < jugador.getX() + jugador.getAncho()/2) {
                                    e.setX(e.getX() + 3); // mueve derecha
                                } else if (e.getX() + e.getAncho()/2 > jugador.getX() + jugador.getAncho()/2) {
                                    e.setX(e.getX() - 3); // mueve izquierda
                                } else {
                                    e.alineado = true;
                                }
                            } else {
                            // En modo kamikaze: baja en linea recta
                            e.setY(e.getY() + 6);
                            if(e.getY() + e.getAlto() >= getHeight())
                            {
                            	e.YKamikaze+=40;
                            	e.setY(e.YKamikaze); 
                            	e.alineado=false;
                            	e.modoKamikaze=false;
                            	e.timerKamikazeIniciado = false;
                            }
                        }

                        // Colision con jugador
                        if (jugador.estaVivo() && !jugador.estaInvulnerable() &&
                            e.getX() < jugador.getX() + jugador.getAncho() &&
                            e.getX() + e.getAncho() > jugador.getX() &&
                            e.getY() < jugador.getY() + jugador.getAlto() &&
                            e.getY() + e.getAlto() > jugador.getY()) {

                            jugador.perderVida();
                            reproducirSonido("/media/daño.wav");

                            e.YKamikaze+=40;
                            e.setY(e.YKamikaze); // Reset y cada vez se acerca mas al jugador
                            e.bajo = false;
                            e.modoKamikaze = false;
                            e.alineado=false;
                            e.timerKamikazeIniciado = false;
                        }
                    }
                }
            }

            // Verifica si la ola termino
            boolean olaTerminada = true;
            if (enemigos != null)
                for (Enemigo e : enemigos)
                    if (e != null && e.estaVivo()) olaTerminada = false;
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
        int x = 0;
        if (numeroOla == 1) x = getWidth() - 50;
        if (numeroOla == 2) x = getWidth()/2 - 25;
        enemigos = new Enemigo[]{new Enemigo("kamikaze" + (nivelActual/3), x, 50, this)};
    }

    private void verificarFinNivel() {
        boolean nivelTerminado = true;
        if (enemigos != null)
            for (Enemigo e : enemigos) if (e != null && (e.estaVivo() || e.isExplotando())) nivelTerminado = false;
        if (nivelTerminado && nivelActual < 10) avanzarNivel();
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
        velocidadMovil = 2 + nivelActual;

        switch(nivelActual) {
            case 1,2,4,5,7,8 -> {
                String tipo;
                if (nivelActual == 1) tipo = "movil1";
                else if (nivelActual == 2) tipo = "disparo1";
                else if (nivelActual == 4) tipo = "movil2";
                else if (nivelActual == 5) tipo = "disparo2";
                else if (nivelActual == 7) tipo = "movil3";
                else tipo = "disparo3";

                int filas = 2 + nivelActual/3;
                int columnas = 5;
                if(nivelActual == 2 || nivelActual == 5 || nivelActual == 8) {
                	columnas = 7;
                }
                enemigos = new Enemigo[filas * columnas];

                for (int fila = 0; fila < filas; fila++) {
                    for (int col = 0; col < columnas; col++) {
                        enemigos[fila * columnas + col] = new Enemigo(tipo, 50 + col * 100, 50 + fila * 60, this);
                    }
                }
            }
            case 3,6,9 -> iniciarOlaKamikaze(0);
            case 10 -> {
                // Jefe final en el centro
                jefe = new Jefe(getWidth()/2 - 60, 50, 30, "/media/jefeFinal.png");

                // enemigos acompañantes (dos filas, 6 en total)
                int filas = 2;
                int columnas = 3;
                enemigos = new Enemigo[filas * columnas];

                for (int fila = 0; fila < filas; fila++) {
                    for (int col = 0; col < columnas; col++) {
                        String tipo;
                        if (fila == 0) {
                            tipo = "disparo3"; // fila superior dispara
                        } else {
                            tipo = "movil3";   // fila inferior solo se mueve
                        }
                        enemigos[fila * columnas + col] =
                            new Enemigo(tipo, 150 + col * 150, 200 + fila * 80, this);
                    }
                }
            }


        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (inicioPantalla) {
            inicioPantalla = false;

            // Detener música de inicio
            if (musicaInicio != null && musicaInicio.isRunning()) {
                musicaInicio.stop();
                musicaInicio.close();
            }

            iniciarNivel();
        } else if (!gameOver && !youWin) {
            switch(e.getKeyCode()) {
            	case KeyEvent.VK_A -> izquierdaPresionada = true;
            	case KeyEvent.VK_D -> derechaPresionada = true;
                case KeyEvent.VK_LEFT -> izquierdaPresionada = true;
                case KeyEvent.VK_RIGHT -> derechaPresionada = true;
                case KeyEvent.VK_W -> dispararJugador();
                case KeyEvent.VK_UP -> dispararJugador();
            }
        }
        
        if ((gameOver || youWin) && e.getKeyCode() == KeyEvent.VK_R) {
            puntaje = 0;
            nivelActual = 1;
            jugador.reiniciarVidas();
            gameOver = false;
            youWin = false; 
            inicioPantalla = false; 
            iniciarNivel(); 
        }
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
        	case KeyEvent.VK_A -> izquierdaPresionada = false;
        	case KeyEvent.VK_D -> derechaPresionada = false;
        	case KeyEvent.VK_LEFT -> izquierdaPresionada = false;
            case KeyEvent.VK_RIGHT -> derechaPresionada = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}