package jgrav.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import jgrav.Body;
import jgrav.Simulation;
import static jgrav.Settings.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JGravGUI {

    Simulation sim = Simulation.getInstance();

    public JGravGUI() {
        JFrame frame = new JFrame("JBoids");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JGravCanvas());
        frame.setUndecorated(true);
        frame.setVisible(true);
    }

    private class JGravCanvas extends JPanel implements ActionListener {
        private Timer timer;
        private int frameCount;
        private MouseHandler mouseHandler;

        JGravCanvas() {
            timer = new Timer(FRAME_TIME, this);
            timer.start();
            mouseHandler = new MouseHandler();
            // setFocusable(true);
            requestFocus();
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyChar()) {
                        case ' ':
                            sim.getBodies().clear();
                        default:
                            break;
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            for (Body body : sim.getBodies()) {
                drawBody(body, g);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            frameCount++;
            for (Body body : sim.getBodies()) {
                body.updatePosition(sim.getBodies());
            }
            System.out.printf("time: %.3f sec\n", frameCount/TIME_SCALE);
            repaint();
            Toolkit.getDefaultToolkit().sync();
        }

        private void drawBody(Body body, Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.translate(body.getX(), body.getY());

            g2d.setColor(Color.WHITE);
            g2d.fillOval(-3, -3, 6, 6);

            if (DRAW_LINES) {
                g2d.setColor(Color.WHITE);
                for (Body b : sim.getBodies()) {
                    g.drawLine((int)body.getX(), (int)body.getY(), (int)b.getX(), (int)b.getY());
                }
            }

            g2d.dispose();
        }

    }

    private class MouseHandler extends MouseAdapter {
        private int pressX, pressY;
        private boolean mouseDown;

        @Override
        public void mousePressed(MouseEvent e) {
            // left click
            if (e.getButton() == MouseEvent.BUTTON1) {  
                pressX = e.getX();
                pressY = e.getY();
                mouseDown = true;
            }
            // right click
            if (e.getButton() == MouseEvent.BUTTON3) {
                
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            System.out.println(e.getSource());
            if (e.getButton() == MouseEvent.BUTTON1) {
                mouseDown = false;
                Simulation.getInstance().createBody(pressX, pressY, 
                    e.getX() - pressX/SPACE_SCALE, e.getY() - pressY/SPACE_SCALE);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1)
                System.out.println((pressX - e.getX()) + ", " + (pressY - e.getY()));
        }
    }
    
}
