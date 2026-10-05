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

public class JGravGUI {

    private Simulation sim = Simulation.getInstance();

    public JGravGUI() {
        JFrame frame = new JFrame("JGrav");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JGravCanvas());
        frame.setUndecorated(true);
        frame.setVisible(true);
    }

    private class JGravCanvas extends JPanel implements ActionListener {
        private Timer timer;
        private MouseHandler mouseHandler;

        //TODO refactor so this is the canvas' responsibility
        private double zoom = 1;
        private int panX = 0, panY = 0;

        JGravCanvas() {
            timer = new Timer(FRAME_TIME, this);
            timer.start();
            mouseHandler = new MouseHandler(this);
            setFocusable(true);
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
            addMouseWheelListener(mouseHandler);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyChar()) {
                        case ' ':
                            sim.clearBodies();
                            break;
                        case 'p':
                            sim.togglePause();
                            break;
                        case 'i':
                            sim.accelerate();
                            break;
                        case 'k':
                            sim.decelerate();
                            break;
                        case 'n':
                            mouseHandler.selectedMass = -mouseHandler.selectedMass;
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(Color.WHITE);

            if (mouseHandler.mouseDown && mouseHandler.widgetOpen) {
                g2d.drawLine(mouseHandler.pressX, mouseHandler.pressY, 
                    mouseHandler.curX, mouseHandler.curY);
            }

            if (mouseHandler.widgetOpen || ALWAYS_LABEL) {
                // sim info
                String secs = String.format("%.3f sec", sim.getSimTime());
                String speed = String.format("%.3fx", TIME_MULTIPLIER);
                String scale = String.format("%.1fm : 1px", SPACE_SCALE);
                drawSimInfo(g2d, false, secs, speed, scale);
                // camera info
                String zoom = String.format("Zoom: %.3fx", mouseHandler.zoom);
                String pan = String.format("Pan: %d, %d", mouseHandler.camX, mouseHandler.camY);
                drawSimInfo(g2d, true, zoom, pan);
            }

            if (mouseHandler.widgetOpen && !mouseHandler.middleDown) {
                int curX = mouseHandler.curX; int curY  = mouseHandler.curY;
                int pressX = mouseHandler.pressX; int pressY = mouseHandler.pressY;
                // placing widget
                drawBody(new Body(
                    curX*SPACE_SCALE, curY*SPACE_SCALE, mouseHandler.selectedMass*MASS_MULTIPLIER,
                    sim.pxFrToMSec(
                        (mouseHandler.mouseDown) ? 
                            createScreenScaledVelocity
                                (curX-pressX, curY-pressY) : Vec2d.fromZero()
                    )), g2d, false);
            }

            g2d.translate(-mouseHandler.camX, -mouseHandler.camY);
            g2d.scale(mouseHandler.zoom, mouseHandler.zoom);

            for (Body body : sim.getBodies()) {
                drawBody(body, g2d, true);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            sim.step(FRAME_TIME*0.001);
            repaint();
            Toolkit.getDefaultToolkit().sync();
        }

        private void drawBody(Body body, Graphics g, boolean fill) {
            Graphics2D g2d = (Graphics2D) g.create();
            int r = body.getScreenRadius();
            int scrx = (int) (body.getX()/SPACE_SCALE);
            int scry = (int) (body.getY()/SPACE_SCALE);

            g2d.setColor(Color.WHITE);
            if (fill)
                g2d.fillOval(scrx-r, scry-r, 2*r, 2*r);
            else
                g2d.drawOval(scrx-r, scry-r, 2*r, 2*r);

            if (DRAW_FORCES) {
                for (Body b : sim.getBodies()) {
                    g.drawLine(scrx, scry, 
                        (int)(b.getX()), (int)(b.getY()));
                }
            }

            if (mouseHandler.widgetOpen || ALWAYS_LABEL) {
                drawLabel(scrx, scry, body.getMass(), body.getVelocity(), r, g2d);
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
            g.drawString(String.format("%.3g kg", mass), x+r, y-r);
            String vel = String.format("%.1f m/s", velocity.getLength());
            g.drawString(vel, x-r-g.getFontMetrics().stringWidth(vel), y+r);
        }

        private void drawSimInfo(Graphics2D g2d, boolean rightAlign, String... strings) {
            int displacement = 25;
            if (rightAlign) {
                for (int i = 0; i < strings.length; i++) {
                    displacement += g2d.getFontMetrics().stringWidth(strings[i]);
                    g2d.drawString(strings[i], getWidth()-displacement, getHeight()-25);
                    displacement += 25;
                }
            } else {
                for (int i = 0; i < strings.length; i++) {
                    g2d.drawString(strings[i], displacement, getHeight()-25);
                    displacement += g2d.getFontMetrics().stringWidth(strings[i]) + 25;
                }
            }
        }

        private static Vec2d createScreenScaledVelocity(int dx, int dy) {
            return Vec2d.fromComponent(dx/(FRAME_TIME*FRAME_TIME*TIME_MULTIPLIER),
                dy/(FRAME_TIME*FRAME_TIME*TIME_MULTIPLIER));
        }

    }

    private class MouseHandler extends MouseAdapter {
        private JGravCanvas jGravCanvas;
        private int pressX, pressY;
        private int curX, curY;
        private boolean mouseDown;
        private boolean widgetOpen;
        private boolean middleDown;
        private double selectedMass = 1;
        private double zoom = 1;

        private int prevMmbX;
        private int prevMmbY;
        private int camX = 0;
        private int camY = 0;

        private MouseHandler(JGravCanvas jGravCanvas) {
            this.jGravCanvas = jGravCanvas;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // left click
            if (e.getButton() == MouseEvent.BUTTON1 && !middleDown) {  
                pressX = curX = e.getX();
                pressY = curY = e.getY();
                mouseDown = true;
            }
            // right click
            if (e.getButton() == MouseEvent.BUTTON3) {
                widgetOpen = !widgetOpen;
            }
            // mmb
            if (e.getButton() == MouseEvent.BUTTON2 && !mouseDown) {
                prevMmbX = e.getX();
                prevMmbY = e.getY();
                middleDown = true;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                mouseDown = false;
                if (!middleDown && widgetOpen) {
                    sim.createBody((int)((pressX+camX)*SPACE_SCALE), (int)((pressY+camY)*SPACE_SCALE), 
                        selectedMass * MASS_MULTIPLIER,
                        sim.pxFrToMSec(JGravCanvas.createScreenScaledVelocity
                            (e.getX() - pressX, e.getY() - pressY)
                        )
                    );
                }
            }
            if (e.getButton() == MouseEvent.BUTTON2 && !mouseDown) {
                middleDown = false;
                curX = e.getX(); 
                curY = e.getY();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (mouseDown) {
                curX = e.getX(); 
                curY = e.getY();
            }
            if (middleDown) {
                int dx = e.getX() - prevMmbX;
                int dy = e.getY() - prevMmbY;
                camX -= dx/zoom;
                camY -= dy/zoom;
                prevMmbX = e.getX();
                prevMmbY = e.getY();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            curX = e.getX(); 
            curY = e.getY();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (widgetOpen) {
                selectedMass *= Math.pow(1.33, -e.getPreciseWheelRotation());
                if (selectedMass > 0 && selectedMass < 1)
                    selectedMass = 1;
                if (selectedMass < 0 && selectedMass > -1)
                    selectedMass = -1;
            } else {
                // zoom *= Math.pow(1.1, -e.getPreciseWheelRotation());
            }
        }
    }
    
}
