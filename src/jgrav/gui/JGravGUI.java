package jgrav.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import jgrav.Body;
import jgrav.Simulation;
import jgrav.Vec2d;

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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class JGravGUI {

    private Simulation sim = Simulation.getInstance();

    public JGravGUI() {
        JFrame frame = new JFrame("JBoids");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JGravCanvas());
        frame.setUndecorated(true);
        frame.setVisible(true);
    }

    private class JGravCanvas extends JPanel implements ActionListener {
        private Timer timer;
        private MouseHandler mouseHandler;

        private double t;

        JGravCanvas() {
            timer = new Timer(FRAME_TIME, this);
            timer.start();
            mouseHandler = new MouseHandler();
            setFocusable(true);
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
            addMouseWheelListener(mouseHandler);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyChar()) {
                        case ' ':
                            sim.getBodies().clear();
                            break;
                        case 'p':
                            sim.togglePause();
                            break;
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
            g.setColor(Color.WHITE);

            if (mouseHandler.mouseDown) {
                g.drawLine(mouseHandler.pressX, mouseHandler.pressY, 
                    mouseHandler.curX, mouseHandler.curY);
            }

            if (mouseHandler.mouse2Down || ALWAYS_LABEL) {
                int curX = mouseHandler.curX; int curY  = mouseHandler.curY;
                g.drawString(String.format("%.3f sec", sim.getSteps()/TIME_SCALE), 
                    25, getHeight()-25);
                drawBody(new Body(
                    curX, curY, mouseHandler.selectedMass*MASS_MULTIPLIER,
                    Vec2d.fromComponent(
                        (mouseHandler.mouseDown) ? (curX - mouseHandler.pressX)/SPACE_SCALE : 0, 
                        (mouseHandler.mouseDown) ? (curY - mouseHandler.pressY)/SPACE_SCALE : 0
                    )), g, false);
            }

            for (Body body : sim.getBodies()) {
                drawBody(body, g, true);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            sim.step();
            sim.updateBodies();
            repaint();
            Toolkit.getDefaultToolkit().sync();
        }

        private void drawBody(Body body, Graphics g, boolean fill) {
            Graphics2D g2d = (Graphics2D) g.create();
            int r = (int)(RADIUS_MULT * Math.pow(body.getMass(), RADIUS_POWER) + 10);
            g2d.translate(body.getX(), body.getY());

            g2d.setColor(Color.WHITE);
            if (fill)
                g2d.fillOval(-r/2, -r/2, r, r);
            else
                g2d.drawOval(-r/2, -r/2, r, r);

            if (DRAW_FORCES) {
                for (Body b : sim.getBodies()) {
                    g.drawLine((int)body.getX(), (int)body.getY(), (int)b.getX(), (int)b.getY());
                }
            }

            if (mouseHandler.mouse2Down || ALWAYS_LABEL) {
                drawLabel(0, 0, body.getMass(), body.getVelocity(), r, g2d);
            }

            g2d.dispose();
        }

        /**
         * 
         * @param x
         * @param y
         * @param mass final mass, after mass multiplier is applied
         * @param r radius of body
         * @param g awt graphics
         */
        private void drawLabel(int x, int y, double mass, 
            Vec2d velocity, int r, Graphics g) {
            g.drawString(String.format("%.3g kg", mass), x+r/2, y-r/2);
            g.drawString(String.format("%.1f m/s", velocity.getLength()), x+r/2, y+r/2);
        }

    }

    private class MouseHandler extends MouseAdapter {
        private int pressX, pressY;
        private int curX, curY;
        private boolean mouseDown;
        private boolean mouse2Down;
        private double selectedMass = 1;

        @Override
        public void mousePressed(MouseEvent e) {
            // left click
            if (e.getButton() == MouseEvent.BUTTON1) {  
                pressX = curX = e.getX();
                pressY = curY = e.getY();
                mouseDown = true;
            }
            // right click
            if (e.getButton() == MouseEvent.BUTTON3) {
                mouse2Down = !mouse2Down;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                mouseDown = false;
                sim.createBody(pressX, pressY, selectedMass * MASS_MULTIPLIER,
                    sim.pxFrToMSec(Vec2d.fromComponent(
                        (e.getX() - pressX)/(TIME_SCALE*TIME_MULTIPLIER),
                        (e.getY() - pressY)/(TIME_SCALE*TIME_MULTIPLIER)
                    ))
                );
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (mouseDown) {
                curX = e.getX(); 
                curY = e.getY();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            curX = e.getX(); 
            curY = e.getY();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (mouse2Down) {
                selectedMass += -selectedMass*0.1*e.getPreciseWheelRotation();
                if (selectedMass < 1)
                    selectedMass = 1;
            }
        }
    }
    
}
