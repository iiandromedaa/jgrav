package jgrav.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import jgrav.Body;

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
import java.util.ArrayList;
import java.util.List;

public class JGravGUI {

    public JGravGUI() {
        JFrame frame = new JFrame("JBoids");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JGravCanvas());
        frame.setUndecorated(true);
        frame.setVisible(true);
    }

    private class JGravCanvas extends JPanel implements ActionListener {

        private Timer timer;

        private List<Body> bodies;

        JGravCanvas() {
            bodies = new ArrayList<>();
            timer = new Timer(FRAME_TIME, this);
            timer.start();
            this.setFocusable(true);
            this.requestFocus();
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    // left click
                    if (e.getButton() == MouseEvent.BUTTON1) {  
                        createBody(e.getX(), e.getY());
                    }
                    // right click
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        
                    }
                }
            });
            this.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyChar()) {
                        case ' ':
                            bodies.clear();
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

            for (Body body : bodies) {
                drawBody(body, g);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            for (Body body : bodies) {
                body.updatePosition(bodies);
            }
            repaint();
            Toolkit.getDefaultToolkit().sync();
        }

        private void createBody(int x, int y) {
            Body body = new Body(x, y);
            bodies.add(body);
        }

        private void drawBody(Body body, Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.translate(body.getX(), body.getY());

            g2d.setColor(Color.WHITE);
            g2d.fillOval(-3, -3, 6, 6);

            if (DRAW_LINES) {
                g2d.setColor(Color.WHITE);
                for (Body b : bodies) {
                    g.drawLine((int)body.getX(), (int)body.getY(), (int)b.getX(), (int)b.getY());
                }
            }

            g2d.dispose();
        }

    }
    
}
