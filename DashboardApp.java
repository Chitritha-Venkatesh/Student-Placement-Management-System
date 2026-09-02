import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class DashboardApp extends JFrame {

    private static final String DATA_FILE = "data/students.csv";

    private final PlacementManager manager = new PlacementManager();
    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JLabel totalValue = cardValue();
    private final JLabel eligibleValue = cardValue();
    private final JLabel avgCgpaValue = cardValue();
    private final JLabel avgScoreValue = cardValue();
    private final JLabel statusLabel = new JLabel("Ready");

    private final Color NAVY = new Color(24, 32, 48);
    private final Color BLUE = new Color(55, 120, 240);
    private final Color BG = new Color(245, 247, 251);
    private final Color CARD = Color.WHITE;
    private final Color TEXT = new Color(35, 42, 55);
    private final Color MUTED = new Color(105, 115, 130);

    public DashboardApp() {
        super("Student Placement Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1400, 800);
        setMinimumSize(new Dimension(1150, 700));
        setLocationRelativeTo(null);

        try {
            manager.loadDataFromFile(DATA_FILE);
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load student data: " + ex.getMessage(),
                    "Data Load Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(buildSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(0, 18));
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(24, 26, 18, 26));

        main.add(buildHeader(), BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"USN", "Student", "Branch", "CGPA", "Certs", "Internships", "Eligibility", "Score", "Recommended Companies"},
                0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        configureTable();
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    int viewRow = table.getSelectedRow();
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    String usn = String.valueOf(tableModel.getValueAt(modelRow, 0));
                    try { showStudentDetails(manager.searchStudentByUsn(usn)); }
                    catch (Exception ignored) { }
                }
            }
        });

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        center.add(buildCards(), BorderLayout.NORTH);
        center.add(buildStudentTable(), BorderLayout.CENTER);

        main.add(center, BorderLayout.CENTER);
        main.add(buildStatusBar(), BorderLayout.SOUTH);

        root.add(main, BorderLayout.CENTER);
        setContentPane(root);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                saveData();
                dispose();
            }
        });

        refreshDashboard();
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(240, 0));
        side.setBackground(NAVY);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(28, 18, 20, 18));

        JLabel brand = new JLabel("<html><b>PLACEMENT</b><br><font color='#8FA8D8'>MANAGEMENT SYSTEM</font></html>");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.PLAIN, 19));
        brand.setBorder(new EmptyBorder(0, 8, 30, 8));
        side.add(brand);

        side.add(navButton("Dashboard", e -> refreshDashboard()));
        side.add(Box.createVerticalStrut(8));
        side.add(navButton("Add Student", e -> showAddStudentDialog()));
        side.add(Box.createVerticalStrut(8));
        side.add(navButton("Search Student", e -> showSearchDialog()));
        side.add(Box.createVerticalStrut(8));
        side.add(navButton("Placement Analysis", e -> showAnalysisDialog()));

        side.add(Box.createVerticalGlue());

        JButton save = navButton("Save Data", e -> saveData());
        side.add(save);
        side.add(Box.createVerticalStrut(8));

        JButton exit = navButton("Save & Exit", e -> {
            saveData();
            dispose();
        });
        side.add(exit);

        return side;
    }

    private JButton navButton(String text, ActionListener listener) {
        JButton b = new JButton(text);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(0, 16, 0, 10));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setForeground(new Color(225, 232, 245));
        b.setBackground(NAVY);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(listener);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(45, 60, 88)); }
            public void mouseExited(MouseEvent e) { b.setBackground(NAVY); }
        });
        return b;
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel title = new JPanel();
        title.setOpaque(false);
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));

        JLabel h = new JLabel("Placement Dashboard");
        h.setFont(new Font("Segoe UI", Font.BOLD, 29));
        h.setForeground(TEXT);

        JLabel sub = new JLabel("Monitor student profiles, eligibility and placement readiness");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(MUTED);

        title.add(h);
        title.add(Box.createVerticalStrut(4));
        title.add(sub);

        JButton add = new JButton("+ Add Student");
        stylePrimary(add);
        add.addActionListener(e -> showAddStudentDialog());

        p.add(title, BorderLayout.WEST);
        p.add(add, BorderLayout.EAST);
        return p;
    }

    private JPanel buildCards() {
        JPanel p = new JPanel(new GridLayout(1, 4, 14, 0));
        p.setOpaque(false);
        p.add(statCard("TOTAL STUDENTS", totalValue, "Profiles in system"));
        p.add(statCard("ELIGIBLE", eligibleValue, "CGPA ≥ 7.5"));
        p.add(statCard("AVERAGE CGPA", avgCgpaValue, "Academic average"));
        p.add(statCard("AVERAGE SCORE", avgScoreValue, "Placement readiness"));
        return p;
    }

    private JPanel statCard(String title, JLabel value, String note) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 230, 238)),
                new EmptyBorder(16, 18, 14, 18)
        ));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 11));
        t.setForeground(MUTED);

        value.setForeground(TEXT);

        JLabel n = new JLabel(note);
        n.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        n.setForeground(MUTED);

        p.add(t, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        p.add(n, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildStudentTable() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 230, 238)),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("Student Placement Overview  •  Double-click a student for full profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(TEXT);

        JButton search = new JButton("Search by USN");
        styleSecondary(search);
        search.addActionListener(e -> showSearchDialog());

        top.add(title, BorderLayout.WEST);
        top.add(search, BorderLayout.EAST);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        p.add(top, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildStatusBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(MUTED);
        p.add(statusLabel, BorderLayout.WEST);

        JLabel hint = new JLabel("Data source: data/students.csv");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hint.setForeground(MUTED);
        p.add(hint, BorderLayout.EAST);
        return p;
    }

    private void configureTable() {
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setGridColor(new Color(235, 238, 244));
        table.setSelectionBackground(new Color(225, 235, 255));
        table.setSelectionForeground(TEXT);
        table.setAutoCreateRowSorter(true);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setForeground(MUTED);
        table.getTableHeader().setBackground(new Color(248, 249, 252));
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i : new int[]{0, 2, 3, 4, 5, 6, 7}) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        DefaultTableCellRenderer recommendation = new DefaultTableCellRenderer();
        recommendation.setForeground(TEXT);
        recommendation.setHorizontalAlignment(SwingConstants.LEFT);
        table.getColumnModel().getColumn(8).setCellRenderer(recommendation);

        DefaultTableCellRenderer eligibility = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focused, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focused, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!selected) setForeground("Eligible".equals(value) ? new Color(25, 125, 75) : new Color(190, 65, 65));
                return c;
            }
        };
        table.getColumnModel().getColumn(6).setCellRenderer(eligibility);

        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(75);
        table.getColumnModel().getColumn(8).setPreferredWidth(280);
    }

    private void refreshDashboard() {
        List<Student> students = manager.getAllStudents();

        tableModel.setRowCount(0);

        int eligible = 0;
        double cgpaTotal = 0;
        double scoreTotal = 0;

        for (Student s : students) {
            boolean isEligible = manager.checkEligibility(s);
            double score = manager.calculatePlacementScore(s);

            if (isEligible) eligible++;
            cgpaTotal += s.getCgpa();
            scoreTotal += score;

            tableModel.addRow(new Object[]{
                    s.getUsn(),
                    s.getName(),
                    s.getBranch(),
                    String.format("%.2f", s.getCgpa()),
                    s.getCertificationsCount(),
                    s.getInternshipsCount(),
                    isEligible ? "Eligible" : "Not Eligible",
                    String.format("%.1f", score),
                    String.join(", ", manager.getCompanyRecommendations(s))
            });
        }

        totalValue.setText(String.valueOf(students.size()));
        eligibleValue.setText(String.valueOf(eligible));
        avgCgpaValue.setText(students.isEmpty() ? "0.00" : String.format("%.2f", cgpaTotal / students.size()));
        avgScoreValue.setText(students.isEmpty() ? "0.0" : String.format("%.1f", scoreTotal / students.size()));

        statusLabel.setText("Loaded " + students.size() + " student profile(s)");
    }

    private void showAddStudentDialog() {
        JDialog d = new JDialog(this, "Add Student", true);
        d.setSize(500, 520);
        d.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel title = new JLabel("Add Student Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setOpaque(false);

        JTextField usn = field();
        JTextField name = field();
        JTextField branch = field();
        JTextField cgpa = field();
        JTextField certs = field();
        JTextField internships = field();
        JTextField skills = field();

        addField(form, "USN", usn);
        addField(form, "Name", name);
        addField(form, "Branch", branch);
        addField(form, "CGPA", cgpa);
        addField(form, "Certifications", certs);
        addField(form, "Internships", internships);
        addField(form, "Skills", skills);

        root.add(form, BorderLayout.CENTER);

        JButton cancel = new JButton("Cancel");
        styleSecondary(cancel);
        cancel.addActionListener(e -> d.dispose());

        JButton add = new JButton("Add Student");
        stylePrimary(add);
        add.addActionListener(e -> {
            try {
                double c = Double.parseDouble(cgpa.getText().trim());
                int ce = Integer.parseInt(certs.getText().trim());
                int in = Integer.parseInt(internships.getText().trim());

                Student s = new Student(
                        name.getText().trim(),
                        usn.getText().trim(),
                        c,
                        branch.getText().trim(),
                        ce,
                        in,
                        parseSkills(skills.getText().trim())
                );

                manager.addStudent(s);
                saveData();
                refreshDashboard();
                JOptionPane.showMessageDialog(d, "Student added successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                d.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(d, "CGPA, certifications and internships must be valid numbers.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, ex.getMessage(),
                        "Unable to Add Student", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        buttons.add(cancel);
        buttons.add(add);
        root.add(buttons, BorderLayout.SOUTH);

        d.setContentPane(root);
        d.setVisible(true);
    }


    private List<String> parseSkills(String text) {
        List<String> result = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) return result;
        for (String skill : text.split(",")) {
            if (!skill.trim().isEmpty()) result.add(skill.trim());
        }
        return result;
    }

    private void showSearchDialog() {
        String usn = JOptionPane.showInputDialog(this, "Enter student USN:", "Search Student",
                JOptionPane.QUESTION_MESSAGE);

        if (usn == null || usn.trim().isEmpty()) return;

        try {
            Student s = manager.searchStudentByUsn(usn.trim());
            showStudentDetails(s);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Student not found.",
                    "Search Result", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showStudentDetails(Student s) {
        boolean eligible = manager.checkEligibility(s);
        double score = manager.calculatePlacementScore(s);
        List<String> recommendations = manager.getCompanyRecommendations(s);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setForeground(TEXT);
        area.setBackground(Color.WHITE);
        area.setBorder(new EmptyBorder(15, 15, 15, 15));

        area.setText(
                "STUDENT PROFILE\n\n" +
                "Name: " + s.getName() + "\n" +
                "USN: " + s.getUsn() + "\n" +
                "Branch: " + s.getBranch() + "\n" +
                "CGPA: " + String.format("%.2f", s.getCgpa()) + "\n" +
                "Certifications: " + s.getCertificationsCount() + "\n" +
                "Internships: " + s.getInternshipsCount() + "\n" +
                "Skills: " + s.getSkills() + "\n\n" +
                "PLACEMENT ANALYSIS\n\n" +
                "Eligibility: " + (eligible ? "ELIGIBLE" : "NOT ELIGIBLE") + "\n" +
                "Placement Score: " + String.format("%.1f / 100", score) + "\n\n" +
                "COMPANY RECOMMENDATIONS\n\n" +
                String.join("\n", recommendations)
        );

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(600, 430));

        JOptionPane.showMessageDialog(this, scroll,
                "Student Details — " + s.getName(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAnalysisDialog() {
        List<Student> students = manager.getAllStudents();
        int eligible = 0, high = 0, medium = 0, improve = 0;
        for (Student s : students) {
            if (manager.checkEligibility(s)) eligible++;
            double score = manager.calculatePlacementScore(s);
            if (score > 80) high++; else if (score > 60) medium++; else improve++;
        }

        JDialog d = new JDialog(this, "Placement Analysis", true);
        d.setSize(620, 500);
        d.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("Placement Readiness Analysis");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT);
        panel.add(title, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(4, 1, 0, 12));
        stats.setOpaque(false);
        stats.add(analysisBar("Eligible Students", eligible, students.size(), new Color(55,120,240)));
        stats.add(analysisBar("Strong Profile  •  Score > 80", high, students.size(), new Color(35,155,95)));
        stats.add(analysisBar("Competitive Profile  •  61–80", medium, students.size(), new Color(220,150,45)));
        stats.add(analysisBar("Skill Improvement  •  ≤ 60", improve, students.size(), new Color(205,75,75)));
        panel.add(stats, BorderLayout.CENTER);

        JLabel footer = new JLabel("Total students: " + students.size() + "   |   Double-click students on the dashboard for detailed recommendations.");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.setForeground(MUTED);
        panel.add(footer, BorderLayout.SOUTH);

        d.setContentPane(panel);
        d.setVisible(true);
    }

    private JPanel analysisBar(String label, int value, int total, Color fill) {
        JPanel p = new JPanel(new BorderLayout(10, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(label + "  —  " + value);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(TEXT);
        JProgressBar bar = new JProgressBar(0, Math.max(1, total));
        bar.setValue(value);
        bar.setStringPainted(true);
        bar.setString(total == 0 ? "0%" : String.format("%.0f%%", value * 100.0 / total));
        bar.setForeground(fill);
        bar.setBackground(new Color(230,233,240));
        bar.setBorderPainted(false);
        p.add(l, BorderLayout.NORTH);
        p.add(bar, BorderLayout.CENTER);
        return p;
    }

    private void saveData() {
        try {
            manager.saveDataToFile(DATA_FILE);
            statusLabel.setText("Saved successfully to " + DATA_FILE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not save data: " + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static JLabel cardValue() {
        JLabel l = new JLabel("0");
        l.setFont(new Font("Segoe UI", Font.BOLD, 28));
        return l;
    }

    private JTextField field() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225)),
                new EmptyBorder(7, 9, 7, 9)
        ));
        return f;
    }

    private void addField(JPanel panel, String label, JTextField field) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(TEXT);
        panel.add(l);
        panel.add(field);
    }

    private void stylePrimary(JButton b) {
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setForeground(Color.WHITE);
        b.setBackground(BLUE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(new EmptyBorder(9, 15, 9, 15));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondary(JButton b) {
        b.setFocusPainted(false);
        b.setForeground(TEXT);
        b.setBackground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225)),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new DashboardApp().setVisible(true);
        });
    }
}
