import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Main extends JFrame {
    private Club club;
    private static final String FILE_NAME = "clubData.dat";

    static final Color DARK_TEAL    = new Color(0, 45, 45);
    static final Color DARK_TEAL2   = new Color(0, 58, 58);
    static final Color ACCENT_GREEN = new Color(29, 158, 117);
    static final Color ACCENT_DARK  = new Color(15, 110, 86);
    static final Color BG_LIGHT     = new Color(245, 245, 243);
    static final Color BG_WHITE     = Color.WHITE;
    static final Color TEXT_MUTED   = new Color(160, 196, 184);
    static final Color BORDER_COLOR = new Color(220, 220, 218);

    private JButton[] navButtons;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // ── Custom dark dialog ────────────────────────────────────────────
    /**
     * Replaces JOptionPane.showMessageDialog everywhere in the app.
     * Renders a fully themed dialog — no white, no system defaults.
     */
    static void showMsg(Component parent, String message) {
        Frame owner = null;
        if (parent instanceof Frame) {
            owner = (Frame) parent;
        } else if (parent != null) {
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        JDialog dlg = new JDialog(owner, true);
        dlg.setUndecorated(true);
        dlg.setBackground(new Color(0, 0, 0, 0));

        // ── Outer card with rounded corners ──────────────────────────
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DARK_TEAL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 0, 24, 0));

        // ── Green accent bar at the top ───────────────────────────────
        JPanel topBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_GREEN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // flatten the bottom two corners
                g2.fillRect(0, 7, getWidth(), getHeight() - 7);
                g2.dispose();
            }
        };
        topBar.setOpaque(false);
        topBar.setPreferredSize(new Dimension(0, 5));

        // ── Message ───────────────────────────────────────────────────
        JLabel msgLabel = new JLabel(
            "<html><body style='width:220px; padding:0;'>" + message + "</body></html>"
        );
        msgLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        msgLabel.setForeground(new Color(220, 240, 235));
        msgLabel.setBorder(new EmptyBorder(20, 28, 20, 28));

        // ── OK button ─────────────────────────────────────────────────
        JButton okBtn = new JButton("OK");
        okBtn.setFont(new Font("Arial", Font.BOLD, 13));
        okBtn.setBackground(ACCENT_GREEN);
        okBtn.setForeground(Color.WHITE);
        okBtn.setFocusPainted(false);
        okBtn.setBorderPainted(false);
        okBtn.setOpaque(true);
        okBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        okBtn.setBorder(new EmptyBorder(9, 36, 9, 36));
        okBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { okBtn.setBackground(ACCENT_DARK); }
            public void mouseExited(MouseEvent e)  { okBtn.setBackground(ACCENT_GREEN); }
        });
        okBtn.addActionListener(e -> dlg.dispose());

        // Close on Escape / Enter
        dlg.getRootPane().setDefaultButton(okBtn);
        dlg.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc");
        dlg.getRootPane().getActionMap().put("esc", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { dlg.dispose(); }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 28, 0));
        btnRow.setOpaque(false);
        btnRow.add(okBtn);

        card.add(topBar,    BorderLayout.NORTH);
        card.add(msgLabel,  BorderLayout.CENTER);
        card.add(btnRow,    BorderLayout.SOUTH);

        // Transparent wrapper so the OS composites correctly
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        root.add(card);

        dlg.setContentPane(root);
        dlg.pack();
        dlg.setLocationRelativeTo(owner);
        dlg.setVisible(true);
    }

    public Main() {
        loadData();
        if (club == null) club = new Club("University Club");

        setTitle("Club Management System");
        setSize(900, 600);
        setMinimumSize(new Dimension(800, 550));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(DARK_TEAL);
        setContentPane(root);

        root.add(buildSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(BG_LIGHT);
        root.add(cardPanel, BorderLayout.CENTER);

        cardPanel.add(new AddCommitteePanel(club), "committee");
        cardPanel.add(new AddMemberPanel(club),    "member");
        cardPanel.add(new EditMemberPanel(club),   "edit");
        cardPanel.add(new ReportsPanel(club),      "reports");
        cardPanel.add(new EventsPanel(club),       "events");

        activateNav(0);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { saveData(); System.exit(0); }
        });
    }

    // ── Sidebar ───────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(DARK_TEAL);
        sidebar.setPreferredSize(new Dimension(240, 600));

        JPanel header = new JPanel();
        header.setBackground(DARK_TEAL);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(24, 20, 16, 20));

        JLabel appLabel = new JLabel("CLUB MANAGEMENT");
        appLabel.setFont(new Font("Arial", Font.BOLD, 10));
        appLabel.setForeground(TEXT_MUTED);
        appLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("<html>University<br>Club</html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(6, 0, 0, 0));

        header.add(appLabel);
        header.add(titleLabel);

        String[][] navItems = {
            {"1", "Add Committee",  "committee"},
            {"2", "Add Member",     "member"},
            {"3", "Edit Member",    "edit"},
            {"4", "View Reports",   "reports"},
            {"5", "Manage Events",  "events"},
        };

        JPanel navPanel = new JPanel();
        navPanel.setBackground(DARK_TEAL);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        navButtons = new JButton[navItems.length];
        for (int i = 0; i < navItems.length; i++) {
            final int idx  = i;
            final String c = navItems[i][2];
            JButton btn = createNavButton(navItems[i][0], navItems[i][1]);
            btn.addActionListener(e -> { cardLayout.show(cardPanel, c); activateNav(idx); });
            navButtons[i] = btn;
            navPanel.add(btn);
            navPanel.add(Box.createVerticalStrut(3));
        }

        JPanel footer = new JPanel();
        footer.setBackground(DARK_TEAL);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(new EmptyBorder(10, 10, 18, 10));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 25));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JButton saveBtn = new JButton("💾  Save & Exit");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 13));
        saveBtn.setBackground(ACCENT_GREEN);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> { saveData(); System.exit(0); });
        saveBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { saveBtn.setBackground(ACCENT_DARK); }
            public void mouseExited(MouseEvent e)  { saveBtn.setBackground(ACCENT_GREEN); }
        });

        footer.add(sep);
        footer.add(Box.createVerticalStrut(10));
        footer.add(saveBtn);

        sidebar.add(header,   BorderLayout.NORTH);
        sidebar.add(navPanel, BorderLayout.CENTER);
        sidebar.add(footer,   BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton createNavButton(String num, String label) {
        JButton btn = new JButton("<html><b style='color:#9ab'>" + num + "</b>  " + label + "</html>");
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setForeground(new Color(192, 216, 212));
        btn.setBackground(DARK_TEAL);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 14, 10, 14));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!btn.getBackground().equals(ACCENT_DARK))
                    btn.setBackground(new Color(255, 255, 255, 20));
            }
            public void mouseExited(MouseEvent e) {
                if (!btn.getBackground().equals(ACCENT_DARK))
                    btn.setBackground(DARK_TEAL);
            }
        });
        return btn;
    }

    private void activateNav(int active) {
        for (int i = 0; i < navButtons.length; i++) {
            navButtons[i].setBackground(i == active ? ACCENT_DARK : DARK_TEAL);
            navButtons[i].setForeground(i == active ? Color.WHITE : new Color(192, 216, 212));
        }
    }

    // ── File Operations ───────────────────────────────────────────────
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            club = (Club) ois.readObject();
        } catch (FileNotFoundException e) {
            club = new Club("University Club");
            showMsg(this, "No previous data found. Starting a new system.");
        } catch (IOException | ClassNotFoundException e) {
            club = new Club("University Club");
            showMsg(this, "Error loading data: " + e.getMessage());
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(club);
            showMsg(this, "Data saved successfully!");
        } catch (IOException e) {
            showMsg(this, "Error saving data.");
        }
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            UIManager.getLookAndFeelDefaults().put("control",               new Color(245, 245, 243));
            UIManager.getLookAndFeelDefaults().put("nimbusBase",            new Color(0, 45, 45));
            UIManager.getLookAndFeelDefaults().put("nimbusBlueGrey",        new Color(180, 200, 195));
            UIManager.getLookAndFeelDefaults().put("nimbusFocus",           new Color(29, 158, 117));
            UIManager.getLookAndFeelDefaults().put("text",                  new Color(30, 30, 30));
            UIManager.getLookAndFeelDefaults().put("TextField.background",  Color.WHITE);
            UIManager.getLookAndFeelDefaults().put("ComboBox.background",   Color.WHITE);
            UIManager.getLookAndFeelDefaults().put("ScrollPane.background", new Color(245, 245, 243));
            UIManager.getLookAndFeelDefaults().put("Panel.background",      new Color(245, 245, 243));
        } catch (Exception e) {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
        }
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}

// ════════════════════════════════════════════════════════════════════
// Base Panel
// ════════════════════════════════════════════════════════════════════
abstract class BasePanel extends JPanel {
    protected JPanel formPanel;
    protected JButton actionButton;

    static final Color DARK_TEAL    = Main.DARK_TEAL;
    static final Color ACCENT_GREEN = Main.ACCENT_GREEN;
    static final Color ACCENT_DARK  = Main.ACCENT_DARK;
    static final Color BG_LIGHT     = Main.BG_LIGHT;
    static final Color BG_WHITE     = Main.BG_WHITE;
    static final Color BORDER_COLOR = Main.BORDER_COLOR;

    protected static void msg(String text) { Main.showMsg(null, text); }

    public BasePanel(String title, String subtitle, String btnText) {
        setLayout(new BorderLayout());
        setBackground(BG_LIGHT);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(18, 24, 14, 24)
        ));

        JLabel lTitle = new JLabel(title);
        lTitle.setFont(new Font("Arial", Font.BOLD, 17));
        lTitle.setForeground(new Color(30, 30, 30));

        JLabel lSub = new JLabel(subtitle);
        lSub.setFont(new Font("Arial", Font.PLAIN, 12));
        lSub.setForeground(new Color(120, 120, 118));

        JPanel titleBox = new JPanel();
        titleBox.setBackground(BG_WHITE);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(lTitle);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(lSub);

        header.add(titleBox, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        formPanel = new JPanel();
        formPanel.setBackground(BG_LIGHT);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(18, 24, 18, 24));

        JScrollPane scroll = new JScrollPane(formPanel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_LIGHT);
        add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footer.setBackground(BG_WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(12, 24, 12, 24)
        ));

        actionButton = new JButton(btnText);
        actionButton.setFont(new Font("Arial", Font.BOLD, 13));
        actionButton.setBackground(DARK_TEAL);
        actionButton.setForeground(Color.WHITE);
        actionButton.setFocusPainted(false);
        actionButton.setBorderPainted(false);
        actionButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        actionButton.setBorder(new EmptyBorder(9, 20, 9, 20));
        actionButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { actionButton.setBackground(new Color(0, 58, 58)); }
            public void mouseExited(MouseEvent e)  { actionButton.setBackground(DARK_TEAL); }
        });

        footer.add(actionButton);
        add(footer, BorderLayout.SOUTH);
    }

    protected JTextField addField(String label, String placeholder) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(BG_LIGHT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        row.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(new Color(100, 100, 98));
        lbl.setPreferredSize(new Dimension(160, 20));

        PlaceholderTextField tf = new PlaceholderTextField(placeholder);
        tf.setFont(new Font("Arial", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(7, 10, 7, 10)
        ));
        tf.setForeground(new Color(50, 50, 50));
        tf.setBackground(BG_WHITE);

        JPanel inner = new JPanel(new BorderLayout());
        inner.setBackground(BG_LIGHT);
        inner.add(lbl, BorderLayout.WEST);
        inner.add(tf,  BorderLayout.CENTER);

        row.add(inner, BorderLayout.CENTER);
        formPanel.add(row);
        return tf;
    }

    protected <T> JComboBox<T> addCombo(String label, T[] items) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(BG_LIGHT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        row.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(new Color(100, 100, 98));
        lbl.setPreferredSize(new Dimension(160, 20));

        JComboBox<T> cb = new JComboBox<>(items);
        cb.setFont(new Font("Arial", Font.PLAIN, 13));
        cb.setBackground(BG_WHITE);

        JPanel inner = new JPanel(new BorderLayout());
        inner.setBackground(BG_LIGHT);
        inner.add(lbl, BorderLayout.WEST);
        inner.add(cb,  BorderLayout.CENTER);

        row.add(inner, BorderLayout.CENTER);
        formPanel.add(row);
        return cb;
    }

    protected JCheckBox addCheckBox(String label) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(BG_LIGHT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        row.setBorder(new EmptyBorder(0, 0, 10, 0));

        JCheckBox cb = new JCheckBox(label);
        cb.setFont(new Font("Arial", Font.PLAIN, 13));
        cb.setForeground(new Color(50, 50, 50));
        cb.setBackground(BG_LIGHT);

        row.add(cb);
        formPanel.add(row);
        return cb;
    }

    protected void addSectionLabel(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(new Font("Arial", Font.BOLD, 10));
        lbl.setForeground(new Color(100, 100, 98));
        lbl.setBorder(new EmptyBorder(8, 0, 6, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(lbl);
    }
}

// ════════════════════════════════════════════════════════════════════
// Add Committee Panel
// ════════════════════════════════════════════════════════════════════
class AddCommitteePanel extends BasePanel {
    public AddCommitteePanel(Club club) {
        super("Add Committee", "Create a new committee to organise club members", "Save Committee");
        JTextField tName = addField("Committee Name:", "e.g. Technical Committee");
        actionButton.addActionListener(e -> {
            if (club.addCommittee(new Committee(tName.getText()))) {
                msg("Committee added successfully!");
                tName.setText("");
            }
        });
    }
}

// ════════════════════════════════════════════════════════════════════
// Add Member Panel
// ════════════════════════════════════════════════════════════════════
class AddMemberPanel extends BasePanel {
    public AddMemberPanel(Club club) {
        super("Add New Member", "Register a new volunteer or board member", "Add Member");

        JTextField tComm  = addField("Committee Name:", "Enter committee name");
        JTextField tID    = addField("Member ID:", "e.g. M001");
        JTextField tName  = addField("Full Name:", "Member's full name");
        JTextField tYear  = addField("Join Year:", "2026");
        JCheckBox  chkAct = addCheckBox("Active Member");

        addSectionLabel("Type of Member");

        JPanel typeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        typeRow.setBackground(BG_LIGHT);
        typeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        typeRow.setBorder(new EmptyBorder(0, 0, 10, 0));
        JRadioButton rbVol   = new JRadioButton("Volunteer");
        JRadioButton rbBoard = new JRadioButton("Board Member");
        rbVol.setBackground(BG_LIGHT);
        rbBoard.setBackground(BG_LIGHT);
        rbVol.setFont(new Font("Arial", Font.PLAIN, 13));
        rbBoard.setFont(new Font("Arial", Font.PLAIN, 13));
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbVol); bg.add(rbBoard);
        typeRow.add(rbVol); typeRow.add(Box.createHorizontalStrut(16)); typeRow.add(rbBoard);
        formPanel.add(typeRow);

        JTextField        tHours = addField("Volunteer Hours:", "0");
        JComboBox<String> cbPos  = addCombo("Position:", new String[]{"Leader", "Assistant", "Coordinator"});

        tHours.getParent().setVisible(false);
        cbPos.getParent().setVisible(false);

        rbVol.addActionListener(e -> {
            tHours.getParent().setVisible(true);
            cbPos.getParent().setVisible(false);
            formPanel.revalidate();
        });
        rbBoard.addActionListener(e -> {
            tHours.getParent().setVisible(false);
            cbPos.getParent().setVisible(true);
            formPanel.revalidate();
        });

        actionButton.addActionListener(e -> {
            try {
                Committee c = club.findCommittee(tComm.getText());
                if (c == null) throw new Exception("Committee not found!");
                c.checkDuplicateID(tID.getText());

                Member m;
                if (rbBoard.isSelected())
                    m = new BoardMember((String) cbPos.getSelectedItem(),
                        Integer.parseInt(tYear.getText()), chkAct.isSelected(),
                        tID.getText(), tName.getText());
                else if (rbVol.isSelected())
                    m = new Volunteer(Integer.parseInt(tHours.getText()),
                        Integer.parseInt(tYear.getText()), chkAct.isSelected(),
                        tID.getText(), tName.getText());
                else throw new Exception("Please select a member type!");

                if (c.addMember(m)) msg("Member added successfully!");
            } catch (DoublicateIdException ex) {
                msg(ex.getMessage());
            } catch (Exception ex) {
                msg("Error: " + ex.getMessage());
            }
        });
    }
}

// ════════════════════════════════════════════════════════════════════
// Edit Member Panel
// ════════════════════════════════════════════════════════════════════
class EditMemberPanel extends BasePanel {
    private Member mEdit;

    public EditMemberPanel(Club club) {
        super("Edit Member Info", "Search for a member and update their details", "Save Changes");

        addSectionLabel("Search Member");
        JTextField tComm = addField("Committee Name:", "Enter committee name");
        JTextField tID   = addField("Member ID:", "e.g. M001");

        JButton btnFind = new JButton("Find Member");
        btnFind.setFont(new Font("Arial", Font.BOLD, 12));
        btnFind.setBackground(ACCENT_GREEN);
        btnFind.setForeground(Color.WHITE);
        btnFind.setFocusPainted(false);
        btnFind.setBorderPainted(false);
        btnFind.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnFind.setBorder(new EmptyBorder(8, 16, 8, 16));
        btnFind.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(btnFind);
        formPanel.add(Box.createVerticalStrut(14));

        addSectionLabel("Update Details");
        JTextField        tName  = addField("Full Name:", "Updated name");
        JCheckBox         chkAct = addCheckBox("Active Status");
        JTextField        tHours = addField("Hours to Add:", "");
        JComboBox<String> cbPos  = addCombo("New Position:", new String[]{"Leader", "Assistant", "Coordinator"});

        tName.getParent().setVisible(false);
        chkAct.getParent().setVisible(false);
        tHours.getParent().setVisible(false);
        cbPos.getParent().setVisible(false);
        actionButton.setEnabled(false);

        btnFind.addActionListener(e -> {
            Committee c = club.findCommittee(tComm.getText());
            if (c != null) {
                mEdit = c.searchMember(tID.getText());
                if (mEdit != null) {
                    tName.setText(mEdit.getName());
                    chkAct.setSelected(mEdit.isIsActive());
                    tName.getParent().setVisible(true);
                    chkAct.getParent().setVisible(true);
                    actionButton.setEnabled(true);
                    if (mEdit instanceof BoardMember) {
                        cbPos.setSelectedItem(((BoardMember) mEdit).getPosition());
                        cbPos.getParent().setVisible(true);
                        tHours.getParent().setVisible(false);
                    } else {
                        tHours.setText("");
                        tHours.getParent().setVisible(true);
                        cbPos.getParent().setVisible(false);
                    }
                    formPanel.revalidate();
                } else msg("Member not found!");
            } else msg("Committee not found!");
        });

        actionButton.addActionListener(e -> {
            try {
                mEdit.setName(tName.getText());
                mEdit.setIsActive(chkAct.isSelected());
                if (mEdit instanceof BoardMember)
                    ((BoardMember) mEdit).setPosition((String) cbPos.getSelectedItem());
                else if (mEdit instanceof Volunteer)
                    ((Volunteer) mEdit).addHours(Integer.parseInt(tHours.getText()));
                msg("Updated successfully!");
            } catch (IllegalArgumentException ex) {
                msg("Error: " + ex.getMessage());
            }
        });
    }
}

// ════════════════════════════════════════════════════════════════════
// Reports Panel
// ════════════════════════════════════════════════════════════════════
class ReportsPanel extends BasePanel {
    public ReportsPanel(Club club) {
        super("System Reports", "View member, event, and committee reward reports", "Run Report");

        JPanel statsRow = new JPanel(new GridLayout(1, 3, 12, 0));
        statsRow.setBackground(BG_LIGHT);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        statsRow.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel lblActive = addStatCard(statsRow, "0", "Active Members");
        formPanel.add(statsRow);

        JComboBox<String> cbType = addCombo("Report Type:",
            new String[]{"Member Report", "Event Report", "All Committee Rewards"});
        JTextField tComm = addField("Committee Name:", "Enter committee");
        JTextField tID   = addField("ID / Event Name:", "Search term");

        addSectionLabel("Output");
        JTextArea output = new JTextArea(8, 30);
        output.setEditable(false);
        output.setFont(new Font("Monospaced", Font.PLAIN, 13));
        output.setBackground(new Color(13, 31, 26));
        output.setForeground(new Color(93, 202, 158));
        output.setBorder(new EmptyBorder(12, 12, 12, 12));
        output.setText("► Ready — enter fields above and click Run Report.");

        JScrollPane sp = new JScrollPane(output);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(sp);

        actionButton.addActionListener(e -> {
            output.setText("");
            Committee comm = club.findCommittee(tComm.getText());
            lblActive.setText(comm != null ? String.valueOf(comm.countActiveMembers()) : "0");

            int sel = cbType.getSelectedIndex();
            if (sel == 0) {
                if (comm != null) {
                    Member m = comm.searchMember(tID.getText());
                    output.setText(m != null ? m.displayReport() : "Member not found.");
                } else output.setText("Committee not found.");
            } else if (sel == 1) {
                Event ev = club.getEvent(tID.getText());
                output.setText(ev != null ? ev.displayReport() : "Event not found.");
            } else {
                if (comm != null) {
                    StringBuilder sb = new StringBuilder("=== Rewards for " + comm.getCommName() + " ===\n\n");
                    Node<Member> cur = comm.getMembers().getFirstNode();
                    while (cur != null) {
                        sb.append("Member: ").append(cur.data.getName())
                          .append("  |  Reward: ").append(cur.data.calculateReward()).append("\n");
                        cur = cur.nextNode;
                    }
                    output.setText(sb.toString());
                } else output.setText("Committee not found.");
            }
        });
    }

    private JLabel addStatCard(JPanel parent, String val, String lbl) {
        JPanel card = new JPanel();
        card.setBackground(Main.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Main.BORDER_COLOR),
            new EmptyBorder(10, 14, 10, 14)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel valLabel = new JLabel(val);
        valLabel.setFont(new Font("Arial", Font.BOLD, 22));
        valLabel.setForeground(new Color(15, 110, 86));

        JLabel lblLabel = new JLabel(lbl);
        lblLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        lblLabel.setForeground(new Color(120, 120, 118));

        card.add(valLabel);
        card.add(lblLabel);
        parent.add(card);
        return valLabel;
    }
}

// ════════════════════════════════════════════════════════════════════
// Events Panel
// ════════════════════════════════════════════════════════════════════
class EventsPanel extends BasePanel {
    public EventsPanel(Club club) {
        super("Manage Events", "Add and track club events", "Save Event");

        addSectionLabel("New Event");
        JTextField tName = addField("Event Name:", "e.g. Annual Gala");
        JTextField tDate = addField("Date:", "e.g. 2026-06-01");
        JTextField tLoc  = addField("Location:", "Venue or link");

        actionButton.addActionListener(e -> {
            if (club.addEvent(new Event(tName.getText(), tDate.getText(), tLoc.getText()))) {
                msg("Event added successfully!");
                tName.setText(""); tDate.setText(""); tLoc.setText("");
            }
        });
    }
}

// ════════════════════════════════════════════════════════════════════
// PlaceholderTextField
// ════════════════════════════════════════════════════════════════════
class PlaceholderTextField extends JTextField {
    private final String placeholder;
    private boolean showingPlaceholder = false;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
        super.setText(placeholder);
        setForeground(new Color(160, 160, 155));
        showingPlaceholder = true;

        addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (showingPlaceholder) {
                    PlaceholderTextField.super.setText("");
                    setForeground(new Color(30, 30, 30));
                    showingPlaceholder = false;
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (PlaceholderTextField.super.getText().isEmpty()) {
                    PlaceholderTextField.super.setText(placeholder);
                    setForeground(new Color(160, 160, 155));
                    showingPlaceholder = true;
                }
            }
        });
    }

    @Override public String getText() { return showingPlaceholder ? "" : super.getText(); }

    @Override public void setText(String t) {
        if (t == null || t.isEmpty()) {
            if (!isFocusOwner()) {
                super.setText(placeholder);
                setForeground(new Color(160, 160, 155));
                showingPlaceholder = true;
            } else {
                super.setText("");
                setForeground(new Color(30, 30, 30));
                showingPlaceholder = false;
            }
        } else {
            super.setText(t);
            setForeground(new Color(30, 30, 30));
            showingPlaceholder = false;
        }
    }
}