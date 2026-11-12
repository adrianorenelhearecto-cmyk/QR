package com.qrlogbook;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

public class Main extends JFrame {
    private static final Color BG = new Color(69, 52, 128);
    private static final Color TITLE_BAR = new Color(255, 103, 201);
    private static final Color PANEL_BG = new Color(236, 236, 236);
    private static final Color FORM_BG = new Color(228, 228, 228);
    private static final Color INPUT_BG = new Color(240, 240, 240);
    private static final Color PRIMARY = new Color(104, 90, 172);
    private static final Color TEXT = new Color(48, 43, 82);
    private static final Color MUTED = new Color(102, 98, 124);
    private static final Color TAB_OFF = new Color(222, 222, 222);
    private static final Color TAB_ON = new Color(166, 169, 216);

    public Main() {
        super("QR Logbook");
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1365, 768);
        setLocationRelativeTo(null);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(buildTitleBar(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(50, 0, 0, 0));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(buildLoginCard(), BorderLayout.CENTER);

        JPanel footerHolder = new JPanel(new BorderLayout());
        footerHolder.setOpaque(false);
        footerHolder.setBorder(new EmptyBorder(0, 0, 0, 80));
        JLabel footer = new JLabel("QR Logbook System • Attendance made simple • 2026");
        footer.setForeground(new Color(255, 255, 255, 210));
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        footer.setHorizontalAlignment(SwingConstants.RIGHT);
        footerHolder.add(footer, BorderLayout.EAST);

        body.add(center, BorderLayout.CENTER);
        body.add(footerHolder, BorderLayout.SOUTH);

        root.add(body, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(TITLE_BAR);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 70, 160)));
        bar.setPreferredSize(new Dimension(0, 32));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        left.setOpaque(false);

        JLabel icon = new JLabel("◫");
        icon.setForeground(new Color(255, 255, 255));
        icon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        left.add(icon);

        JLabel title = new JLabel("QR Logbook — High School for Service and Learning");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        right.setOpaque(false);

        JButton min = new JButton("—");
        JButton max = new JButton("□");
        JButton close = new JButton("✕");
        for (JButton b : new JButton[]{min, max, close}) {
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            b.setForeground(Color.WHITE);
            b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }

        min.addActionListener(e -> setState(JFrame.ICONIFIED));
        close.addActionListener(e -> System.exit(0));

        right.add(min);
        right.add(max);
        right.add(close);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildLoginCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        RoundedPanel card = new RoundedPanel(PANEL_BG, 26);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(510, 600));
        card.setBorder(new EmptyBorder(18, 30, 18, 30));

        card.add(Box.createVerticalStrut(10));

        JPanel logoWrap = new JPanel();
        logoWrap.setOpaque(false);
        logoWrap.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JLabel square = new JLabel();
        square.setPreferredSize(new Dimension(110, 110));
        square.setOpaque(true);
        square.setBackground(new Color(102, 86, 196));
        square.setBorder(BorderFactory.createEmptyBorder());
        square.setHorizontalAlignment(SwingConstants.CENTER);
        square.setIcon(createBadgeIcon(90));
        logoWrap.add(square);
        card.add(logoWrap);
        card.add(Box.createVerticalStrut(10));

        JLabel heading = new JLabel("QR Logbook", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 42));
        heading.setForeground(new Color(71, 60, 124));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(heading);

        JPanel schoolRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        schoolRow.setOpaque(false);
        JLabel schoolBadge = new JLabel();
        schoolBadge.setPreferredSize(new Dimension(38, 38));
        schoolBadge.setOpaque(true);
        schoolBadge.setBackground(new Color(102, 86, 196));
        schoolBadge.setHorizontalAlignment(SwingConstants.CENTER);
        schoolBadge.setIcon(createMiniBadgeIcon(28));
        JLabel schoolText = new JLabel("<html><div style='text-align:center'>High School for<br>Service and Learning</div></html>");
        schoolText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        schoolText.setForeground(TEXT);
        schoolRow.add(schoolBadge);
        schoolRow.add(schoolText);
        card.add(schoolRow);
        card.add(Box.createVerticalStrut(18));

        JLabel welcome = new JLabel("Welcome! Sign in to continue.");
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        welcome.setForeground(TEXT);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(welcome);
        card.add(Box.createVerticalStrut(18));

        JPanel tabs = new JPanel(new GridLayout(1, 3, 4, 0));
        tabs.setOpaque(false);
        tabs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JToggleButton student = new JToggleButton("Student");
        JToggleButton teacher = new JToggleButton("Teacher");
        JToggleButton admin = new JToggleButton("Admin");

        JToggleButton[] tabButtons = {student, teacher, admin};
        for (JToggleButton b : tabButtons) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 15));
            b.setForeground(TEXT);
            b.setFocusPainted(false);
            b.setOpaque(true);
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setBackground(TAB_OFF);
            b.setPreferredSize(new Dimension(120, 46));
        }
        student.setSelected(true);
        student.setBackground(new Color(168, 176, 241));

        student.addActionListener(e -> setActiveTab(student, teacher, admin));
        teacher.addActionListener(e -> setActiveTab(teacher, student, admin));
        admin.addActionListener(e -> setActiveTab(admin, student, teacher));

        tabs.add(student);
        tabs.add(teacher);
        tabs.add(admin);
        card.add(tabs);
        card.add(Box.createVerticalStrut(18));

        JLabel idLabel = new JLabel("ID Number");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        idLabel.setForeground(TEXT);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(idLabel);
        card.add(Box.createVerticalStrut(6));

        JTextField idField = new JTextField();
        idField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        idField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(10, 12, 10, 12)
        ));
        idField.setBackground(INPUT_BG);
        idField.setPreferredSize(new Dimension(0, 42));
        card.add(idField);
        card.add(Box.createVerticalStrut(16));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        passLabel.setForeground(TEXT);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(passLabel);
        card.add(Box.createVerticalStrut(6));

        JPasswordField passField = new JPasswordField();
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(10, 12, 10, 12)
        ));
        passField.setBackground(INPUT_BG);
        passField.setPreferredSize(new Dimension(0, 42));
        card.add(passField);
        card.add(Box.createVerticalStrut(20));

        JButton loginBtn = new JButton("LOG IN");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loginBtn.setBackground(PRIMARY);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(18));

        JButton forgot = new JButton("Forgot Password?");
        forgot.setBackground(null);
        forgot.setForeground(TEXT);
        forgot.setFocusPainted(false);
        forgot.setBorderPainted(false);
        forgot.setContentAreaFilled(false);
        forgot.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        forgot.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(forgot);

        panel.add(card);
        return panel;
    }

    private void setActiveTab(JToggleButton active, JToggleButton left, JToggleButton right) {
        active.setBackground(new Color(166, 169, 216));
        active.setForeground(TEXT);
        left.setBackground(TAB_OFF);
        right.setBackground(TAB_OFF);
    }

    private Icon createBadgeIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(96, 83, 176));
        g2.fill(new Ellipse2D.Double(0, 0, size, size));

        g2.setColor(new Color(216, 181, 92));
        g2.fill(new Ellipse2D.Double(22, 18, 46, 46));

        int[] xs = {size / 2, size / 2 + 12, size / 2 + 28, size / 2 + 18, size / 2 + 30, size / 2, size / 2 - 30, size / 2 - 18, size / 2 - 28, size / 2 - 12};
        int[] ys = {14, 34, 34, 56, 80, 66, 80, 56, 34, 34};
        g2.fillPolygon(xs, ys, xs.length);

        g2.dispose();
        return new ImageIcon(img);
    }

    private Icon createMiniBadgeIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(96, 83, 176));
        g2.fill(new Ellipse2D.Double(0, 0, size, size));

        g2.setColor(new Color(216, 181, 92));
        Path2D.Double book = new Path2D.Double();
        int sx = 4;
        int sy = 8;
        book.moveTo(sx, sy + 18);
        book.lineTo(sx + 8, sy + 6);
        book.lineTo(sx + 20, sy + 6);
        book.lineTo(sx + 12, sy + 18);
        book.closePath();
        g2.fill(book);
        g2.dispose();
        return new ImageIcon(img);
    }

    private static class RoundedPanel extends JPanel {
        private final Color color;
        private final int radius;

        RoundedPanel(Color color, int radius) {
            this.color = color;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}
