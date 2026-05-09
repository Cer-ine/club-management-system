import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Main extends JFrame {
    private Club club;
    private static final String FILE_NAME = "clubData.dat";

    public Main() {
        loadData(); // Load existing data from the file when the app starts
        if (club == null) club = new Club("University Club");

        setTitle("Club Management System");
        setSize(450, 650);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on screen

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(0, 45, 45)); 
        mainPanel.setLayout(null);
        add(mainPanel);
// Menu Title 
        JLabel lblTitle = new JLabel("Main Menu", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(50, 30, 350, 50);
        mainPanel.add(lblTitle);
// Create Menu Buttons  
        String[] labels = {"1. Add Committee", "2. Add New Member", "3. Edit Member Info", 
                           "4. View Reports", "5. Manage Events", "6. Save & Exit"};
        // to add carfly Buttons 
        int yPos = 110;
        for (int i = 0; i < labels.length; i++) {
            JButton btn = new JButton(labels[i]);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setBackground(new Color(230, 230, 230));
            btn.setBounds(50, yPos, 350, 60);
            mainPanel.add(btn);
            yPos += 80;
           // clicks for each button to open the correct window 
           int choice = i + 1;
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
                saveData(); // Auto-save when the user clicks 'X'
                System.exit(0);
            }
        });
    }
     // File Operations ***
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            club = (Club) ois.readObject(); // Read the club object from the file
        } catch (FileNotFoundException e) {
        
        JOptionPane.showMessageDialog(this," No previous data found. Starting a new system ");
        club = new Club("University Club");

    } catch (IOException | ClassNotFoundException e) {
        
        JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        club = new Club("University Club"); 
    }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(club); // Write the entire club object to the file
            JOptionPane.showMessageDialog(this, "Data Saved Successfully!");
        } catch (IOException e) { JOptionPane.showMessageDialog(this, "Error Saving Data"); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}

//creat basefream to make all frame extend it
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
    //helper method to add a lable and text field easily
    protected void addField(String label, JComponent comp, int y) {
        JLabel lbl = new JLabel(label);
        lbl.setBounds(40, y, 150, 30);
        comp.setBounds(200, y, 240, 30);
        rightPanel.add(lbl);
        rightPanel.add(comp);
    }
}


class EditMemberFrame extends BaseFrame {
    private Member mEdit;

    public EditMemberFrame(Club club) {
        super("Edit Member Info", "Save Changes");
        JTextField tC = new JTextField(), tI = new JTextField(), tN = new JTextField(), tHours = new JTextField();
        JCheckBox cA = new JCheckBox("Active Status");
        JComboBox<String> cP = new JComboBox<>(new String[]{"Leader", "Assistant", "Coordinator"});
        JLabel lblS = new JLabel("Value:");

        addField("Committee Name:", tC, 40); addField("Member ID:", tI, 90);
        JButton btnS = new JButton("Find Member");
        btnS.setBounds(200, 130, 240, 30); rightPanel.add(btnS);

        addField("Update Name:", tN, 180); addField("Status:", cA, 220);
        lblS.setBounds(40, 270, 150, 30); cP.setBounds(200, 270, 240, 30); tHours.setBounds(200, 270, 240, 30);
        rightPanel.add(lblS); rightPanel.add(cP); rightPanel.add(tHours);
        
        lblS.setVisible(false); cP.setVisible(false); tHours.setVisible(false); actionButton.setEnabled(false);

       //search action //handled action
        btnS.addActionListener(e -> {
            Committee c = club.findCommittee(tC.getText());  //find committe
            if (c != null) {
                mEdit = c.searchMember(tI.getText()); //find ID 
                if (mEdit != null) {
                    tN.setText(mEdit.getName()); cA.setSelected(mEdit.isIsActive());//to show currenr name
                    lblS.setVisible(true); actionButton.setEnabled(true); //to show current status
                    // polymorphism check
                    if (mEdit instanceof BoardMember) {
                        cP.setVisible(true); tHours.setVisible(false); lblS.setText("New Position:");
                        cP.setSelectedItem(((BoardMember)mEdit).getPosition());
                    } else {
                        tHours.setVisible(true); cP.setVisible(false); lblS.setText("Hours to ADD:"); //to show hours text field
                        tHours.setText(""); 
                    }
                } else JOptionPane.showMessageDialog(this, "Member Not Found!");
            } else JOptionPane.showMessageDialog(this, "Committee Not Found!");
        });

        // to save changes action //handled action
        actionButton.addActionListener(e -> {
            try {
                mEdit.setName(tN.getText()); //update name
                mEdit.setIsActive(cA.isSelected()); //updute status
                
                if (mEdit instanceof BoardMember) {
                    ((BoardMember)mEdit).setPosition((String)cP.getSelectedItem()); //cast and updut position
                } else if (mEdit instanceof Volunteer) {
                    
                    int hoursToAdd = Integer.parseInt(tHours.getText());
                    ((Volunteer)mEdit).addHours(hoursToAdd); //cast and add hours
                }
                
                JOptionPane.showMessageDialog(this, "Updated Successfully!");
                dispose(); //close window
            } catch (IllegalArgumentException ex) { //catch if hours<0
                
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            } 
        });
    }
}


class ReportsDashboardFrame extends BaseFrame {
    public ReportsDashboardFrame(Club club) {
        super("System Reports", "Run Report");
        
        // i add lebal to count active member
        JLabel lblActiveCount = new JLabel("Active Members: 0");
        lblActiveCount.setFont(new Font("Arial", Font.BOLD, 15));
        lblActiveCount.setForeground(new Color(0, 102, 0)); 
        lblActiveCount.setBounds(200, 10, 250, 30); 
        rightPanel.add(lblActiveCount);

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        outputArea.setBackground(Color.BLACK); 
        outputArea.setForeground(Color.GREEN);
        JScrollPane scroll = new JScrollPane(outputArea); 
        scroll.setBounds(20, 200, 460, 280);
        rightPanel.add(scroll);

        String[] types = {"Member Report", "Event Report", "All Committee Rewards"};
        JComboBox<String> combo = new JComboBox<>(types);
        JTextField tC = new JTextField(), tS = new JTextField();
        
        addField("Type:", combo, 50); 
        addField("Committee:", tC, 100); 
        addField("ID/Event:", tS, 150);
        //handled action 
        actionButton.addActionListener(e -> {
            outputArea.setText(""); // to clean
            
            // find committe and count member
            Committee currentComm = club.findCommittee(tC.getText());
            if (currentComm != null) {
                lblActiveCount.setText("Active Members (" + currentComm.getCommName() + "): " + currentComm.countActiveMembers());
            } else {
                lblActiveCount.setText("Active Members: 0");
            }

            // for displayReport
            if (combo.getSelectedIndex() == 0) {
                if (currentComm != null) { 
                    Member m = currentComm.searchMember(tS.getText()); 
                    if (m != null) outputArea.setText(m.displayReport()); // استدعاء ميثود الـ String
                    else outputArea.setText("Member Not Found!");
                } else {
                    outputArea.setText("Committee Not Found!");
                }
            } 
            else if (combo.getSelectedIndex() == 1) {
                Event ev = club.getEvent(tS.getText()); 
                if (ev != null) outputArea.setText(ev.displayReport()); // استدعاء ميثود الـ String
                else outputArea.setText("Event Not Found!");
            } 
            else {
                //for count Rewards
                if (currentComm != null) {
                    String report = "=== Rewards for " + currentComm.getCommName() + " ===\n";
                    
                    Node<Member> curr = currentComm.getMembers().getFirstNode();
                    while(curr != null) {
                        report += "Member: " + curr.data.getName() + " | Reward: " + curr.data.calculateReward() + "\n";
                        curr = curr.nextNode;
                    }
                    outputArea.setText(report); 
                } else {
                    outputArea.setText("Committee Not Found!");
                }
            }
        });
    }
}

//
class AddMemberFrame extends BaseFrame {
    public AddMemberFrame(Club club) {
        super("Add Member Form", "ADD Member");

        JTextField txtComm = new JTextField(), txtID = new JTextField(), txtName = new JTextField(), txtYear = new JTextField("2026");
        JCheckBox chkActive = new JCheckBox("Active Member");
        
        JRadioButton rbVol = new JRadioButton("Volunteer"), rbBoard = new JRadioButton("Board");
        ButtonGroup bg = new ButtonGroup(); bg.add(rbVol); bg.add(rbBoard);

        
        JLabel lblSpecial = new JLabel("Special Info:");
        JComboBox<String> comboPos = new JComboBox<>(new String[]{"Leader", "Assistant", "Coordinator"});
        JTextField txtHours = new JTextField();
        
        lblSpecial.setBounds(40, 340, 150, 30);
        comboPos.setBounds(200, 340, 240, 30);
        txtHours.setBounds(200, 340, 240, 30);
        
        comboPos.setVisible(false); txtHours.setVisible(false); lblSpecial.setVisible(false);

        addField("Committee Name:", txtComm, 40);
        addField("ID:", txtID, 90);
        addField("Name:", txtName, 140);
        addField("Join Year:", txtYear, 190);
        addField("Active?", chkActive, 240);

        JPanel pnlType = new JPanel();
        pnlType.setBorder(BorderFactory.createTitledBorder("Type of Member"));
        pnlType.setBounds(40, 280, 400, 50);
        pnlType.add(rbVol); pnlType.add(rbBoard);
        rightPanel.add(pnlType);
        rightPanel.add(lblSpecial); rightPanel.add(comboPos); rightPanel.add(txtHours);

        rbBoard.addActionListener(e -> { comboPos.setVisible(true); txtHours.setVisible(false); lblSpecial.setVisible(true); lblSpecial.setText("Choose Position:"); });
        rbVol.addActionListener(e -> { comboPos.setVisible(false); txtHours.setVisible(true); lblSpecial.setVisible(true); lblSpecial.setText("Enter Hours:"); });

        //handled action
        actionButton.addActionListener(e -> {
            try {
                Committee c = club.findCommittee(txtComm.getText());
                if (c == null) throw new Exception("Committee Not Found!"); //throw new catch if committe not found
                c.checkDuplicateID(txtID.getText()); //doublicteIdException

                Member m;
                if (rbBoard.isSelected()) 
                    m = new BoardMember((String)comboPos.getSelectedItem(), Integer.parseInt(txtYear.getText()), chkActive.isSelected(), txtID.getText(), txtName.getText());
                else if (rbVol.isSelected())
                    m = new Volunteer(Integer.parseInt(txtHours.getText()), Integer.parseInt(txtYear.getText()), chkActive.isSelected(), txtID.getText(), txtName.getText());
                else throw new Exception("Please select member type!");

                if(c.addMember(m)) { JOptionPane.showMessageDialog(this, "Member Added Successfully!"); dispose(); }
            } catch (DoublicateIdException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); //catchers
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });
    }
}
class AddCommitteeFrame extends BaseFrame {
    public AddCommitteeFrame(Club club) {
        super("Add Committee", "Save");
        JTextField t = new JTextField(); addField("Name:", t, 100);
        //handied actiion
        actionButton.addActionListener(e -> {
            if(club.addCommittee(new Committee(t.getText()))) { JOptionPane.showMessageDialog(this, "Added!"); dispose(); }
        });
    }
}

class EventFrame extends BaseFrame {
    public EventFrame(Club club) {
        super("Manage Events", "Save");
        JTextField n = new JTextField(), d = new JTextField(), l = new JTextField();
        addField("Name:", n, 50); addField("Date:", d, 100); addField("Loc:", l, 150);
        //handled action
        actionButton.addActionListener(e -> {
            if(club.addEvent(new Event(n.getText(), d.getText(), l.getText()))) { JOptionPane.showMessageDialog(this, "Added!"); dispose(); }
        });
    }
}