package com.qrlogbook;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

public final class Main extends JFrame {
    private static final Color PURPLE = new Color(90, 80, 181);
    private static final Color DARK_PURPLE = new Color(53, 45, 103);
    private static final Color TEXT = new Color(48, 43, 82);
    private static final Color BG = new Color(243, 240, 255);
    private final AccountStore store = new AccountStore();
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private AccountStore.Account signedIn;
    private String loginRole = "student";

    public Main() {
        super("QR Logbook — High School for Service and Learning");
        setDefaultCloseOperation(EXIT_ON_CLOSE); setMinimumSize(new Dimension(1000, 700));
        setSize(1100, 744); setLocationRelativeTo(null);
        root.add(loginView(), "login"); root.add(recoveryView(), "recovery");
        setContentPane(root); cards.show(root, "login");
    }

    private JPanel loginView() {
        BackgroundPanel screen = new BackgroundPanel("/assets/Assets/_Image_c53utdc53utdc53u.jpg");
        screen.setLayout(new GridBagLayout());
        JPanel card = new RoundedPanel(new Color(255,255,255,236), 26);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); card.setBorder(new EmptyBorder(27, 34, 24, 34));
        card.setPreferredSize(new Dimension(430, 620));
        JLabel logo = new JLabel(image("/assets/Classe365_qrcode-removebg-preview.png", 80, 80)); logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel title = label("QR Logbook", 30, true, PURPLE); title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4)); brand.setOpaque(false);
        brand.add(new JLabel(image("/assets/7057_1566490658_9825_-removebg-preview.png", 48, 48)));
        brand.add(label("High School for\nService and Learning", 13, true, DARK_PURPLE));
        JLabel sub = label("Welcome! Sign in to continue.", 14, false, new Color(98,91,121)); sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel tabs = new JPanel(new GridLayout(1,3,7,0)); tabs.setOpaque(true); tabs.setBackground(new Color(240,237,255));
        ButtonGroup group = new ButtonGroup();
        String[] roles = {"Student", "Teacher", "Admin"};
        for (String role : roles) {
            JToggleButton b = new JToggleButton(role); b.setFocusPainted(false); b.setBorder(new EmptyBorder(10,4,10,4));
            b.setFont(new Font("Segoe UI", Font.BOLD, 12)); b.setBackground(new Color(240,237,255)); b.setForeground(TEXT);
            if (role.equals("Student")) { b.setSelected(true); styleRoleButton(b, true); }
            b.addActionListener(e -> { loginRole = role.toLowerCase(); for (Component c: tabs.getComponents()) if(c instanceof JToggleButton t) styleRoleButton(t,t==b); });
            group.add(b); tabs.add(b);
        }
        JTextField id = styledField("Enter student number"); JPasswordField password = styledPassword();
        JLabel error = label(" ", 12, false, new Color(185,28,28));
        JButton submit = primaryButton("LOG IN");
        submit.addActionListener(e -> {
            char[] pass = password.getPassword();
            if (id.getText().isBlank() || pass.length == 0) { error.setText("Please enter your ID and password."); return; }
            if (!store.authenticate(loginRole, id.getText().trim(), pass)) { error.setText("Invalid login details or account is inactive."); return; }
            signedIn = store.accounts(loginRole).stream().filter(a -> a.id().equalsIgnoreCase(id.getText().trim())).findFirst().orElse(null);
            if (signedIn != null) showDashboard();
        });
        JButton forgot = linkButton("Forgot Password?"); forgot.addActionListener(e -> cards.show(root,"recovery"));
        JPanel links = new JPanel(); links.setOpaque(false); links.add(forgot);
        card.add(logo); card.add(Box.createVerticalStrut(7)); card.add(title); card.add(brand); card.add(sub);
        card.add(Box.createVerticalStrut(18)); card.add(tabs); card.add(Box.createVerticalStrut(11));
        addField(card,"ID Number",id); addField(card,"Password",password); card.add(error); card.add(Box.createVerticalStrut(4)); card.add(submit); card.add(links);
        screen.add(card); screen.add(footer()); return screen;
    }

    private JPanel recoveryView() {
        BackgroundPanel screen = new BackgroundPanel("/assets/Assets/_Image_c53utdc53utdc53u.jpg"); screen.setLayout(new GridBagLayout());
        JPanel card = new RoundedPanel(new Color(255,255,255,240), 24); card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS)); card.setBorder(new EmptyBorder(30,34,26,34)); card.setPreferredSize(new Dimension(440,520));
        JLabel logo = new JLabel(image("/assets/Classe365_qrcode-removebg-preview.png",70,70)); logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel title = label("Account Recovery",26,true,PURPLE); title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel intro = label("Verify your account details to choose a new password.",13,false,TEXT); intro.setAlignmentX(Component.CENTER_ALIGNMENT);
        JComboBox<String> role = new JComboBox<>(new String[]{"Student","Teacher","Admin"}); JTextField id=styledField("Account ID"); JTextField email=styledField("Registered recovery email");
        JPanel fields = new JPanel(); fields.setOpaque(false); fields.setLayout(new BoxLayout(fields,BoxLayout.Y_AXIS));
        fields.add(label("Account role",13,true,TEXT)); fields.add(role); fields.add(label("Student Number / Teacher ID / Username",13,true,TEXT)); fields.add(id); fields.add(label("Registered recovery email",13,true,TEXT)); fields.add(email);
        JPasswordField newPass=styledPassword(), confirm=styledPassword(); JLabel error=label(" ",12,false,new Color(185,28,28));
        JButton verify=primaryButton("VERIFY ACCOUNT");
        verify.addActionListener(e -> {
            String r=((String)role.getSelectedItem()).toLowerCase();
            if(!store.verifyRecovery(r,id.getText().trim(),email.getText().trim())) { error.setText("Those details do not match an account with a saved email."); return; }
            String first=JOptionPane.showInputDialog(this,"Enter a new password (at least 8 characters):","Reset password",JOptionPane.PLAIN_MESSAGE);
            if(first==null)return; String second=JOptionPane.showInputDialog(this,"Confirm your new password:","Reset password",JOptionPane.PLAIN_MESSAGE);
            if(!first.equals(second)||first.length()<8){error.setText("Passwords must match and be at least 8 characters.");return;}
            store.resetPassword(r,id.getText().trim(),first.toCharArray()); JOptionPane.showMessageDialog(this,"Password changed on this device. You can sign in now."); cards.show(root,"login");
        });
        JButton back=linkButton("Back to login"); back.addActionListener(e->cards.show(root,"login"));
        card.add(logo); card.add(title); card.add(intro); card.add(Box.createVerticalStrut(10)); card.add(fields); card.add(error); card.add(verify); card.add(back);
        screen.add(card); screen.add(footer()); return screen;
    }

    private void showDashboard() {
        JPanel app=new JPanel(new BorderLayout()); app.setBackground(BG);
        JPanel side=new JPanel(); side.setPreferredSize(new Dimension(220,0)); side.setBackground(DARK_PURPLE); side.setLayout(new BoxLayout(side,BoxLayout.Y_AXIS)); side.setBorder(new EmptyBorder(22,14,18,14));
        JLabel brand=label("QR Logbook",18,true,Color.WHITE); brand.setBorder(new EmptyBorder(8,8,24,8)); side.add(brand);
        JButton home=navButton("Dashboard"), students=navButton("Student Records"), logout=navButton("Log Out");
        side.add(home); if(signedIn.role().equals("admin")||signedIn.role().equals("teacher"))side.add(students); side.add(Box.createVerticalGlue()); side.add(logout);
        JPanel content=new JPanel(new BorderLayout(0,18)); content.setOpaque(false); content.setBorder(new EmptyBorder(25,28,30,28));
        JPanel top=new JPanel(new BorderLayout()); top.setOpaque(false); top.add(label("Dashboard",26,true,TEXT),BorderLayout.WEST); top.add(label(signedIn.name()+"  •  "+signedIn.role(),13,true,PURPLE),BorderLayout.EAST);
        JPanel pages=new JPanel(new CardLayout()); pages.setOpaque(false);
        pages.add(dashboardPage(),"home"); pages.add(studentPage(),"students");
        home.addActionListener(e->((CardLayout)pages.getLayout()).show(pages,"home")); students.addActionListener(e->((CardLayout)pages.getLayout()).show(pages,"students"));
        logout.addActionListener(e->{signedIn=null;root.removeAll();root.add(loginView(),"login");root.add(recoveryView(),"recovery");cards.show(root,"login");root.revalidate();root.repaint();});
        content.add(top,BorderLayout.NORTH); content.add(pages,BorderLayout.CENTER); app.add(side,BorderLayout.WEST); app.add(content,BorderLayout.CENTER);
        root.add(app,"app"); cards.show(root,"app"); root.revalidate(); root.repaint();
    }

    private JPanel dashboardPage() {
        JPanel p=new JPanel(new BorderLayout(0,16)); p.setOpaque(false);
        JPanel stats=new JPanel(new GridLayout(1,3,14,14)); stats.setOpaque(false);
        stats.add(statCard("Students",String.valueOf(store.count("student")))); stats.add(statCard("Teachers",String.valueOf(store.count("teacher")))); stats.add(statCard("Accounts",String.valueOf(store.count(null))));
        JPanel welcome=new RoundedPanel(Color.WHITE,18); welcome.setLayout(new BoxLayout(welcome,BoxLayout.Y_AXIS)); welcome.setBorder(new EmptyBorder(25,25,25,25));
        welcome.add(label("Welcome to QR Logbook",22,true,TEXT)); welcome.add(Box.createVerticalStrut(8)); welcome.add(label("Attendance made simple for High School for Service and Learning.",14,false,new Color(120,115,145)));
        if(signedIn.role().equals("student")){ welcome.add(Box.createVerticalStrut(22)); welcome.add(label("Student number: "+signedIn.id(),14,true,TEXT)); welcome.add(label("Grade / Section: "+signedIn.grade()+" · "+signedIn.section(),14,false,TEXT)); }
        p.add(stats,BorderLayout.NORTH); p.add(welcome,BorderLayout.CENTER); return p;
    }

    private JPanel studentPage() {
        JPanel panel=new JPanel(new BorderLayout(0,12)); panel.setOpaque(false);
        JPanel head=new JPanel(new BorderLayout()); head.setOpaque(false); head.add(label("Student Records",21,true,TEXT),BorderLayout.WEST);
        JButton add=primaryButton("Add Student"); head.add(add,BorderLayout.EAST);
        String[] cols={"Student Number","Name","Email","Grade","Section"}; javax.swing.table.DefaultTableModel model=new javax.swing.table.DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        JTable table=new JTable(model); table.setRowHeight(32); table.setFillsViewportHeight(true); refreshStudents(model);
        JButton edit=secondaryButton("Edit selected"), delete=secondaryButton("Delete selected"); JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT)); actions.setOpaque(false); actions.add(edit); actions.add(delete);
        add.addActionListener(e->studentDialog(null,model)); edit.addActionListener(e->{int row=table.getSelectedRow();if(row>=0)studentDialog(String.valueOf(model.getValueAt(row,0)),model);});
        delete.addActionListener(e->{int row=table.getSelectedRow();if(row<0)return;String id=String.valueOf(model.getValueAt(row,0));if(JOptionPane.showConfirmDialog(this,"Delete student "+id+"?","Confirm delete",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){store.deleteStudent(id);refreshStudents(model);}});
        panel.add(head,BorderLayout.NORTH); panel.add(new JScrollPane(table),BorderLayout.CENTER); panel.add(actions,BorderLayout.SOUTH); return panel;
    }

    private void studentDialog(String oldId, javax.swing.table.DefaultTableModel model) {
        AccountStore.Account old=oldId==null?null:store.accounts("student").stream().filter(a->a.id().equals(oldId)).findFirst().orElse(null);
        JTextField id=styledField("Student number"),name=styledField("Full name"),email=styledField("Recovery email"),grade=styledField("Grade"),section=styledField("Section");
        if(old!=null){id.setText(old.id());name.setText(old.name());email.setText(old.email());grade.setText(old.grade());section.setText(old.section());}
        JPanel form=new JPanel(new GridLayout(0,2,8,8)); form.add(new JLabel("Student Number"));form.add(id);form.add(new JLabel("Full Name"));form.add(name);form.add(new JLabel("Recovery Email"));form.add(email);form.add(new JLabel("Grade"));form.add(grade);form.add(new JLabel("Section"));form.add(section);
        JPasswordField temp=new JPasswordField(); if(old==null){form.add(new JLabel("Temporary password"));form.add(temp);}
        int result=JOptionPane.showConfirmDialog(this,form,old==null?"Add Student":"Edit Student",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(result!=JOptionPane.OK_OPTION)return;
        try { if(old==null)store.addStudent(id.getText(),name.getText(),email.getText(),grade.getText(),section.getText(),temp.getPassword()); else store.updateStudent(old.id(),id.getText(),name.getText(),email.getText(),grade.getText(),section.getText()); refreshStudents(model); }
        catch(IllegalArgumentException ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Could not save",JOptionPane.ERROR_MESSAGE);}
    }

    private void refreshStudents(javax.swing.table.DefaultTableModel model){model.setRowCount(0);for(AccountStore.Account a:store.accounts("student"))model.addRow(new Object[]{a.id(),a.name(),a.email(),a.grade(),a.section()});}
    private JPanel statCard(String title,String value){JPanel p=new RoundedPanel(Color.WHITE,17);p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));p.setBorder(new EmptyBorder(20,22,20,22));p.add(label(title,13,false,new Color(120,115,145)));p.add(Box.createVerticalStrut(8));p.add(label(value,28,true,PURPLE));return p;}
    private JPanel footer(){JPanel f=new JPanel();f.setOpaque(false);JLabel l=label("QR Logbook System • Attendance made simple • 2026",12,false,Color.WHITE);f.add(l);return f;}
    private static JLabel label(String s,int size,boolean bold,Color color){JLabel l=new JLabel("<html>"+s.replace("\n","<br>")+"</html>");l.setFont(new Font("Segoe UI",bold?Font.BOLD:Font.PLAIN,size));l.setForeground(color);return l;}
    private static JTextField styledField(String hint){JTextField f=new JTextField();f.setFont(new Font("Segoe UI",Font.PLAIN,14));f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(222,215,239)),new EmptyBorder(10,12,10,12)));f.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));if(hint!=null)f.setToolTipText(hint);return f;}
    private static JPasswordField styledPassword(){JPasswordField f=new JPasswordField();f.setFont(new Font("Segoe UI",Font.PLAIN,14));f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(222,215,239)),new EmptyBorder(10,12,10,12)));f.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));return f;}
    private static void addField(JPanel panel,String caption,JComponent input){JLabel l=label(caption,13,true,new Color(64,52,92));l.setAlignmentX(Component.LEFT_ALIGNMENT);input.setAlignmentX(Component.LEFT_ALIGNMENT);panel.add(l);panel.add(Box.createVerticalStrut(5));panel.add(input);panel.add(Box.createVerticalStrut(8));}
    private static JButton primaryButton(String text){JButton b=new JButton(text);b.setAlignmentX(Component.CENTER_ALIGNMENT);b.setFont(new Font("Segoe UI",Font.BOLD,14));b.setForeground(Color.WHITE);b.setBackground(PURPLE);b.setFocusPainted(false);b.setBorder(new EmptyBorder(12,18,12,18));b.setMaximumSize(new Dimension(Integer.MAX_VALUE,48));return b;}
    private static JButton secondaryButton(String text){JButton b=new JButton(text);b.setFont(new Font("Segoe UI",Font.BOLD,13));b.setForeground(PURPLE);b.setBackground(new Color(238,233,255));b.setFocusPainted(false);return b;}
    private static JButton linkButton(String text){JButton b=new JButton(text);b.setAlignmentX(Component.CENTER_ALIGNMENT);b.setForeground(new Color(36,22,83));b.setBorderPainted(false);b.setContentAreaFilled(false);b.setFocusPainted(false);return b;}
    private static JButton navButton(String text){JButton b=new JButton(text);b.setAlignmentX(Component.LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));b.setForeground(new Color(230,224,255));b.setBackground(new Color(255,255,255,0));b.setBorder(new EmptyBorder(12,10,12,10));b.setHorizontalAlignment(SwingConstants.LEFT);b.setFocusPainted(false);return b;}
    private static void styleRoleButton(JToggleButton b,boolean selected){b.setBackground(selected?PURPLE:new Color(240,237,255));b.setForeground(selected?Color.WHITE:TEXT);}
    private static ImageIcon image(String path,int w,int h){try{URL u=Main.class.getResource(path);if(u!=null){Image img=Toolkit.getDefaultToolkit().getImage(u).getScaledInstance(w,h,Image.SCALE_SMOOTH);return new ImageIcon(img);}}catch(Exception ignored){}BufferedImage blank=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);Graphics2D g=blank.createGraphics();g.setColor(new Color(90,80,181));g.fillRoundRect(2,2,w-4,h-4,18,18);g.dispose();return new ImageIcon(blank);}
    private static class RoundedPanel extends JPanel{private final Color fill;private final int radius;RoundedPanel(Color fill,int radius){this.fill=fill;this.radius=radius;setOpaque(false);}protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(fill);g2.fillRoundRect(0,0,getWidth(),getHeight(),radius,radius);g2.dispose();super.paintComponent(g);}}
    private static class BackgroundPanel extends JPanel{private final Image image;BackgroundPanel(String path){setOpaque(true);URL u=Main.class.getResource(path);image=u==null?null:Toolkit.getDefaultToolkit().getImage(u);}protected void paintComponent(Graphics g){super.paintComponent(g);Graphics2D g2=(Graphics2D)g.create();if(image!=null)g2.drawImage(image,0,0,getWidth(),getHeight(),this);else{g2.setColor(new Color(57,48,116));g2.fillRect(0,0,getWidth(),getHeight());}g2.setColor(new Color(67,49,135,125));g2.fillRect(0,0,getWidth(),getHeight());g2.dispose();}}
    public static void main(String[] args){SwingUtilities.invokeLater(()->new Main().setVisible(true));}
}
