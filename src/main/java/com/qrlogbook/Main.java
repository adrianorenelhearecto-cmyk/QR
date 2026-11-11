package com.qrlogbook;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("QR Logbook");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 720);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setContentPane(new MainPanel());
            frame.setVisible(true);
        });
    }

    private static class MainPanel extends JPanel {
        private static final Color PURPLE = new Color(120, 15, 131);
        private static final Color GOLD = new Color(214, 175, 84);
        private static final Color DARK = new Color(12, 12, 12);

        MainPanel() {
            setBackground(DARK);
            setLayout(new BorderLayout());

            JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));
            center.setOpaque(false);
            center.add(new SchoolBadgePanel());
            center.add(new PortraitPanel());

            add(center, BorderLayout.CENTER);
        }
    }

    private static class SchoolBadgePanel extends JPanel {
        SchoolBadgePanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(640, 640));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int radius = Math.min(getWidth(), getHeight()) / 2 - 20;

            // Outer ring
            Ellipse2D outer = new Ellipse2D.Double(cx - radius, cy - radius, radius * 2, radius * 2);
            g2.setColor(new Color(118, 18, 130));
            g2.fill(outer);

            g2.setColor(new Color(214, 175, 84));
            g2.setStroke(new BasicStroke(10f));
            g2.draw(new Ellipse2D.Double(cx - radius + 12, cy - radius + 12, (radius - 12) * 2, (radius - 12) * 2));

            // Inner gold book emblem
            int bookX = cx - 168;
            int bookY = cy - 90;
            int bookW = 336;
            int bookH = 170;

            Path2D book = new Path2D.Double();
            book.moveTo(bookX, bookY + 110);
            book.lineTo(bookX + 68, bookY + 40);
            book.lineTo(bookX + 168, bookY + 40);
            book.lineTo(bookX + 112, bookY + 110);
            book.closePath();

            g2.setColor(new Color(214, 175, 84));
            g2.fill(book);

            Path2D leftPage = new Path2D.Double();
            leftPage.moveTo(bookX + 28, bookY + 122);
            leftPage.lineTo(bookX + 100, bookY + 58);
            leftPage.lineTo(bookX + 150, bookY + 58);
            leftPage.lineTo(bookX + 110, bookY + 122);
            leftPage.closePath();
            g2.fill(leftPage);

            Path2D rightPage = new Path2D.Double();
            rightPage.moveTo(bookX + 168, bookY + 122);
            rightPage.lineTo(bookX + 236, bookY + 58);
            rightPage.lineTo(bookX + 290, bookY + 58);
            rightPage.lineTo(bookX + 220, bookY + 122);
            rightPage.closePath();
            g2.fill(rightPage);

            // Center star
            g2.setColor(new Color(214, 175, 84));
            int[] xs = {cx, cx + 22, cx + 70, cx + 32, cx + 48, cx, cx - 48, cx - 32, cx - 70, cx - 22};
            int[] ys = {cy - 104, cy - 34, cy - 34, cy + 8, cy + 82, cy + 42, cy + 82, cy + 8, cy - 34, cy - 34};
            Polygon star = new Polygon(xs, ys, xs.length);
            g2.fill(star);

            // School text around ring
            g2.setColor(new Color(214, 175, 84));
            g2.setFont(new Font("Serif", Font.BOLD, 22));
            drawArcText(g2, cx, cy, radius - 20, 0, 160, "HIGH SCHOOL FOR SERVICE AND LEARNING");
            drawArcText(g2, cx, cy, radius - 10, 180, 160, "SCHOOL OF SERVICE AND LEARNING");

            g2.setFont(new Font("Serif", Font.BOLD, 42));
            g2.drawString("HSSL", cx - 52, cy + 8);

            g2.setFont(new Font("Serif", Font.PLAIN, 18));
            drawArcText(g2, cx, cy, radius + 18, 30, 120, "TRUTH . SERVICE . LEADERSHIP");
            drawArcText(g2, cx, cy, radius + 14, 214, 120, "DISCIPLINE . CULTURE . CIVIC ENGAGEMENT");

            // Decorative dots
            g2.setColor(new Color(214, 175, 84));
            for (int i = 0; i < 18; i++) {
                double angle = Math.toRadians(360 - i * 20);
                int x = (int) (cx + Math.cos(angle) * (radius - 14));
                int y = (int) (cy + Math.sin(angle) * (radius - 14));
                g2.fillOval(x - 5, y - 5, 10, 10);
            }

            g2.dispose();
        }

        private void drawArcText(Graphics2D g2, int cx, int cy, int radius, int startDegrees, int sweepDegrees, String text) {
            FontMetrics fm = g2.getFontMetrics();
            double angleStep = 0.0;
            double current = Math.toRadians(startDegrees + sweepDegrees);
            double last = Math.toRadians(startDegrees);
            int len = text.length();

            for (int i = 0; i < len; i++) {
                char ch = text.charAt(i);
                int w = fm.charWidth(ch);
                double angle = current - ((double) (len - i) / len) * Math.toRadians(sweepDegrees);
                double x = cx + Math.cos(angle) * radius - (w / 2.0);
                double y = cy + Math.sin(angle) * radius;
                AffineTransform old = g2.getTransform();
                g2.translate(x + w / 2.0, y);
                g2.rotate(angle + Math.PI / 2.0);
                g2.drawString(String.valueOf(ch), 0, 0);
                g2.setTransform(old);
            }
        }
    }

    private static class PortraitPanel extends JPanel {
        PortraitPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(640, 640));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            g2.setColor(new Color(245, 245, 245));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Hair
            g2.setColor(new Color(99, 45, 24));
            g2.fillOval(cx - 175, cy - 250, 350, 320);

            // Face
            g2.setColor(new Color(230, 194, 170));
            g2.fillOval(cx - 145, cy - 180, 290, 290);

            // Ears
            g2.fillOval(cx - 170, cy - 110, 35, 70);
            g2.fillOval(cx + 135, cy - 110, 35, 70);

            // Neck
            g2.fillRoundRect(cx - 45, cy + 80, 90, 120, 35, 35);

            // Suit
            g2.setColor(new Color(15, 15, 15));
            int[] x = {cx - 190, cx + 190, cx + 140, cx - 140};
            int[] y = {cy + 130, cy + 130, cy + 360, cy + 360};
            g2.fillPolygon(x, y, 4);
            g2.setColor(new Color(245, 245, 245));
            g2.fillRoundRect(cx - 170, cy + 150, 340, 240, 25, 25);

            // Eyes
            g2.setColor(new Color(78, 60, 46));
            g2.fillOval(cx - 86, cy - 65, 40, 24);
            g2.fillOval(cx + 46, cy - 65, 40, 24);
            g2.setColor(new Color(255, 255, 255));
            g2.fillOval(cx - 74, cy - 72, 12, 12);
            g2.fillOval(cx + 62, cy - 72, 12, 12);

            // Smile
            g2.setColor(new Color(232, 109, 131));
            g2.draw(new Arc2D.Double(cx - 74, cy + 20, 150, 86, 180, 180, Arc2D.OPEN));

            // Brows
            g2.setColor(new Color(118, 74, 42));
            g2.drawLine(cx - 90, cy - 90, cx - 28, cy - 92);
            g2.drawLine(cx + 28, cy - 92, cx + 90, cy - 90);

            // Nose
            g2.setColor(new Color(225, 181, 154));
            g2.drawLine(cx, cy - 58, cx + 5, cy + 10);

            // Blazer collar
            g2.setColor(new Color(245, 245, 245));
            int[] collarX = {cx - 70, cx, cx + 70};
            int[] collarY = {cy + 160, cy + 100, cy + 160};
            g2.fillPolygon(collarX, collarY, 3);
            g2.setColor(new Color(15, 15, 15));
            g2.drawPolygon(collarX, collarY, 3);

            g2.dispose();
        }
    }
}
