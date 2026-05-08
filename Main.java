import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Main extends JFrame {
    private Club club;
    private static final String FILE_NAME = "clubData.dat";

    public Main() {
        loadData();
        if (club == null) club = new Club("University Club");

        setTitle("Club Management System");
        setSize(450, 650);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(0, 45, 45)); 
        mainPanel.setLayout(null);
        add(mainPanel);

        JLabel lblTitle = new JLabel("Main Menu", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(50, 30, 350, 50);
        mainPanel.add(lblTitle);

        String[] labels = {"1. Add Committee", "2. Add New Member", "3. Edit Member Info", 
                           "4. View Reports", "5. Manage Events", "6. Save & Exit"};
        
        int yPos = 110;
        for (int i = 0; i < labels.length; i++) {
            JButton btn = new JButton(labels[i]);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setBackground(new Color(230, 230, 230));
            btn.setBounds(50, yPos, 350, 60);
            mainPanel.add(btn);
            yPos += 80;

            final int choice = i + 1;
            btn.addActionListener(e -> {
                if (choice == 1) new AddCommitteeFrame(club).setVisible(true);
                else if (choice == 2) new AddMemberFrame(club).setVisible(true);
                else if (choice == 3) new EditMemberFrame(club).setVisible(true);
                else if (choice == 4) new ReportsDashboardFrame(club).setVisible(true);
                else if (choice == 5) new EventFrame(club).setVisible(true);
                else { saveData(); System.exit(0); }
            });
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
                System.exit(0);
            }
        });
    }

    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            club = (Club) ois.readObject();
        } catch (Exception e) { club = new Club("University Club"); }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(club);
            JOptionPane.showMessageDialog(this, "Data Saved Successfully!");
        } catch (IOException e) { JOptionPane.showMessageDialog(this, "Error Saving Data"); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}


class BaseFrame extends JFrame {
    protected JPanel leftPanel, rightPanel;
    protected JButton actionButton;

    public BaseFrame(String title, String btnText) {
        setTitle(title);
        setSize(850, 550);
        setLayout(null);
        setLocationRelativeTo(null);

        leftPanel = new JPanel();
        leftPanel.setBackground(new Color(0, 45, 45));
        leftPanel.setBounds(0, 0, 350, 550);
        leftPanel.setLayout(null);

        JLabel lblTitle = new JLabel("<html><center>" + title + "</center></html>", SwingConstants.CENTER);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(210, 210, 210));
        lblTitle.setBounds(50, 60, 250, 70);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        leftPanel.add(lblTitle);

        actionButton = new JButton(btnText);
        actionButton.setBounds(60, 400, 230, 50);
        leftPanel.add(actionButton);

        rightPanel = new JPanel();
        rightPanel.setBackground(new Color(230, 230, 230));
        rightPanel.setBounds(350, 0, 500, 550);
        rightPanel.setLayout(null);

        add(leftPanel); add(rightPanel);
    }

    protected void addField(String label, JComponent comp, int y) {
        JLabel lbl = new JLabel(label);
        lbl.setBounds(40, y, 150, 30);
        comp.setBounds(200, y, 240, 30);
        rightPanel.add(lbl);
        rightPanel.add(comp);
    }
}


class ReportsDashboardFrame extends BaseFrame {
    private JTextArea outputArea;

    public ReportsDashboardFrame(Club club) {
        super("System Reports", "Run displayReport()");

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.GREEN); // لون يشبه الـ Terminal
        
        JScrollPane scroll = new JScrollPane(outputArea);
        scroll.setBounds(20, 200, 460, 280);
        rightPanel.add(scroll);

        String[] types = {"Member Report", "Event Report", "All Committee Rewards"};
        JComboBox<String> combo = new JComboBox<>(types);
        JTextField txtComm = new JTextField(), txtSearch = new JTextField();

        addField("Report Type:", combo, 30);
        addField("Committee Name:", txtComm, 80);
        addField("ID / Event Name:", txtSearch, 130);

        actionButton.addActionListener(e -> {
            outputArea.setText("");
            String type = (String) combo.getSelectedItem();
            
           
            PrintStream oldOut = System.out;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.setOut(new PrintStream(baos));

            try {
                if (type.equals("Member Report")) {
                    Committee c = club.findCommittee(txtComm.getText());
                    if (c != null) {
                        Member m = c.searchMember(txtSearch.getText());
                        if (m != null) m.displayReport();
                        else System.out.println("Member not found.");
                    } else System.out.println("Committee not found.");

                } else if (type.equals("Event Report")) {
                    Event ev = club.getEvent(txtSearch.getText());
                    if (ev != null) ev.displayReport();
                    else System.out.println("Event not found.");

                } else if (type.equals("All Committee Rewards")) {
                    Committee c = club.findCommittee(txtComm.getText());
                    if (c != null) {
                        System.out.println("=== Rewards for " + c.getCommName() + " ===");
                        Node<Member> curr = c.getMembers().getFirstNode();
                        while(curr != null) {
                            System.out.println("Member: " + curr.data.getName() + " | Reward: " + curr.data.calculateReward());
                            curr = curr.nextNode;
                        }
                    }
                }
            } finally {
                System.setOut(oldOut);
                outputArea.setText(baos.toString()); 
            }
        });
    }
}


class AddMemberFrame extends BaseFrame {
    public AddMemberFrame(Club club) {
        super("Add Member Form", "ADD Member");

        JTextField txtComm = new JTextField(), txtID = new JTextField(), txtName = new JTextField(), txtYear = new JTextField("2026");
        JCheckBox chkActive = new JCheckBox("Active Member");
        JRadioButton rbVol = new JRadioButton("Volunteer"), rbBoard = new JRadioButton("Board");
        ButtonGroup bg = new ButtonGroup(); bg.add(rbVol); bg.add(rbBoard);

        JLabel lblExtra = new JLabel("Special Info:");
        JComboBox<String> comboPos = new JComboBox<>(new String[]{"Leader", "Assistant", "Coordinator"});
        JTextField txtHours = new JTextField();
        
        lblExtra.setBounds(40, 340, 150, 30);
        comboPos.setBounds(200, 340, 240, 30);
        txtHours.setBounds(200, 340, 240, 30);
        comboPos.setVisible(false); txtHours.setVisible(false); lblExtra.setVisible(false);

        addField("Committee Name:", txtComm, 40); addField("ID:", txtID, 90);
        addField("Name:", txtName, 140); addField("Year:", txtYear, 190);
        addField("Active?", chkActive, 240);

        JPanel p = new JPanel(); p.setBorder(BorderFactory.createTitledBorder("Type"));
        p.setBounds(40, 280, 400, 50); p.add(rbVol); p.add(rbBoard);
        rightPanel.add(p); rightPanel.add(lblExtra); rightPanel.add(comboPos); rightPanel.add(txtHours);

        rbBoard.addActionListener(e -> { comboPos.setVisible(true); txtHours.setVisible(false); lblExtra.setVisible(true); lblExtra.setText("Select Position:"); });
        rbVol.addActionListener(e -> { comboPos.setVisible(false); txtHours.setVisible(true); lblExtra.setVisible(true); lblExtra.setText("Enter Hours:"); });

        actionButton.addActionListener(e -> {
            try {
                Committee c = club.findCommittee(txtComm.getText());
                if (c == null) throw new Exception("Committee Not Found");
                c.checkDuplicateID(txtID.getText());
                Member m;
                if (rbBoard.isSelected()) m = new BoardMember((String)comboPos.getSelectedItem(), Integer.parseInt(txtYear.getText()), chkActive.isSelected(), txtID.getText(), txtName.getText());
                else m = new Volunteer(Integer.parseInt(txtHours.getText()), Integer.parseInt(txtYear.getText()), chkActive.isSelected(), txtID.getText(), txtName.getText());
                if(c.addMember(m)) { JOptionPane.showMessageDialog(this, "Added!"); dispose(); }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });
    }
}


class EditMemberFrame extends BaseFrame {
    private Member mEdit;
    public EditMemberFrame(Club club) {
        super("Edit Member Info", "Save Changes");
        JTextField tC = new JTextField(), tI = new JTextField(), tN = new JTextField(), tSpecial = new JTextField();
        JCheckBox cA = new JCheckBox("Active Status");
        JComboBox<String> cP = new JComboBox<>(new String[]{"Leader", "Assistant", "Coordinator"});
        JLabel lblS = new JLabel("New Value:");
        
        addField("Committee Name:", tC, 40); addField("Member ID:", tI, 90);
        JButton btnS = new JButton("Find Member");
        btnS.setBounds(200, 130, 240, 30); rightPanel.add(btnS);

        addField("New Name:", tN, 180); addField("Status:", cA, 220);
        lblS.setBounds(40, 270, 150, 30); cP.setBounds(200, 270, 240, 30); tSpecial.setBounds(200, 270, 240, 30);
        rightPanel.add(lblS); rightPanel.add(cP); rightPanel.add(tSpecial);
        
        lblS.setVisible(false); cP.setVisible(false); tSpecial.setVisible(false);

        btnS.addActionListener(e -> {
            Committee c = club.findCommittee(tC.getText());
            if (c != null) {
                mEdit = c.searchMember(tI.getText());
                if (mEdit != null) {
                    tN.setText(mEdit.getName()); cA.setSelected(mEdit.isIsActive());
                    lblS.setVisible(true);
                    if (mEdit instanceof BoardMember) { cP.setVisible(true); tSpecial.setVisible(false); cP.setSelectedItem(((BoardMember)mEdit).getPosition()); }
                    else { tSpecial.setVisible(true); cP.setVisible(false); tSpecial.setText(String.valueOf(((Volunteer)mEdit).getVolunteerHours())); }
                }
            }
        });

        actionButton.addActionListener(e -> {
            mEdit.setName(tN.getText()); mEdit.setIsActive(cA.isSelected());
            if (mEdit instanceof BoardMember) ((BoardMember)mEdit).setPosition((String)cP.getSelectedItem());
            else if (mEdit instanceof Volunteer) ((Volunteer)mEdit).setVolunteerHours(Integer.parseInt(tSpecial.getText()));
            JOptionPane.showMessageDialog(this, "Updated!"); dispose();
        });
    }
}


class AddCommitteeFrame extends BaseFrame {
    public AddCommitteeFrame(Club club) {
        super("Add Committee", "Save");
        JTextField t = new JTextField(); addField("Name:", t, 100);
        actionButton.addActionListener(e -> {
            if(club.addCommittee(new Committee(t.getText()))) { JOptionPane.showMessageDialog(this, "Added!"); dispose(); }
        });
    }
}

class EventFrame extends BaseFrame {
    public EventFrame(Club club) {
        super("Manage Events", "Save Event");
        JTextField n = new JTextField(), d = new JTextField(), l = new JTextField();
        addField("Event Name:", n, 50); addField("Date:", d, 100); addField("Location:", l, 150);
        actionButton.addActionListener(e -> {
            if(club.addEvent(new Event(n.getText(), d.getText(), l.getText()))) { JOptionPane.showMessageDialog(this, "Added!"); dispose(); }
        });
    }
}