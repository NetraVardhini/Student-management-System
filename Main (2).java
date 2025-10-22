import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        new LoginFrame();
    }
}

// ================= LOGIN PAGE =================
class LoginFrame extends JFrame implements ActionListener {
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginBtn, createBtn;
    JLabel statusLabel;

    LoginFrame() {
        setTitle("Login Page");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        JLabel userLabel = new JLabel("Username");
        userLabel.setBounds(100, 200, 80, 25);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(200, 200, 150, 25);
        add(usernameField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setBounds(100, 250, 80, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(200, 250, 150, 25);
        add(passwordField);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(200, 300, 100, 25);
        loginBtn.addActionListener(this);
        add(loginBtn);

        createBtn = new JButton("Create Account");
        createBtn.setBounds(310, 300, 140, 25);
        createBtn.addActionListener(this);
        add(createBtn);

        statusLabel = new JLabel("");
        statusLabel.setBounds(200, 350, 200, 25);
        statusLabel.setForeground(Color.RED);
        add(statusLabel);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String name = checkLogin(username, password);
            if (name != null) {
                dispose();
                new WelcomeWindow(name, username);
            } else {
                statusLabel.setText("Invalid username or password");
            }
        } else if (e.getSource() == createBtn) {
            dispose();
            new CreateAccountFrame();
        }
    }

    private String checkLogin(String username, String password) {
        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT name FROM user WHERE username=? AND password=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

// ================= WELCOME (3 SEC) WINDOW =================
class WelcomeWindow extends JFrame {
    public WelcomeWindow(String name, String username) {
        setTitle("Welcome");
        setSize(400, 200);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel msg = new JLabel("Hello " + name + " (" + username + ")!", SwingConstants.CENTER);
        msg.setFont(new Font("Arial", Font.BOLD, 18));
        msg.setForeground(Color.BLUE);
        add(msg, BorderLayout.CENTER);

        setVisible(true);

        Timer timer = new Timer(3000, e -> {
            dispose();
            new DetailsWindow();
        });
        timer.setRepeats(false);
        timer.start();
    }
}

// ================= CREATE ACCOUNT PAGE =================
class CreateAccountFrame extends JFrame implements ActionListener {
    JTextField nameField, userField;
    JPasswordField passField;
    JButton createBtn, backBtn;
    JLabel msgLabel;

    CreateAccountFrame() {
        setTitle("Create Account");
        setSize(400, 300);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 40, 100, 25);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 40, 180, 25);
        add(nameField);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 80, 100, 25);
        add(userLabel);

        userField = new JTextField();
        userField.setBounds(150, 80, 180, 25);
        add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 120, 100, 25);
        add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(150, 120, 180, 25);
        add(passField);

        createBtn = new JButton("Create");
        createBtn.setBounds(80, 180, 100, 30);
        createBtn.addActionListener(this);
        add(createBtn);

        backBtn = new JButton("Back");
        backBtn.setBounds(200, 180, 100, 30);
        backBtn.addActionListener(this);
        add(backBtn);

        msgLabel = new JLabel("");
        msgLabel.setBounds(100, 220, 250, 25);
        msgLabel.setForeground(Color.BLUE);
        add(msgLabel);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createBtn) {
            String name = nameField.getText();
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                msgLabel.setText("Please fill all fields!");
                return;
            }

            if (createAccount(name, username, password)) {
                JOptionPane.showMessageDialog(this, "Account created successfully!");
                dispose();
                new LoginFrame();
            } else {
                msgLabel.setText("Username already exists!");
            }
        } else if (e.getSource() == backBtn) {
            dispose();
            new LoginFrame();
        }
    }

    private boolean createAccount(String name, String username, String password) {
        try (Connection con = DBConnection.getConnection()) {
            String query = "INSERT INTO user (name, username, password) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}

// ================= DETAILS WINDOW =================
class DetailsWindow extends JFrame {
    public DetailsWindow() {
        JButton studentBtn = new JButton("Student Details");
        studentBtn.setBounds(150, 200, 200, 40);
        studentBtn.addActionListener(e -> new StudentDetailsWindow());

        JPanel panel = new JPanel(null);
        panel.add(studentBtn);

        setTitle("Details Window");
        setSize(500, 500);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        add(panel);
    }
}

// ================= STUDENT DETAILS WINDOW =================
class StudentDetailsWindow extends JFrame {
    public StudentDetailsWindow() {
        JLabel[] labels = {
            new JLabel("Admin No"), new JLabel("Student ID"), new JLabel("First Name"),
            new JLabel("Last Name"), new JLabel("Class"), new JLabel("Section"),
            new JLabel("Gender"), new JLabel("Date Of Birth"), new JLabel("Contact Number"),
            new JLabel("Address"), new JLabel("Blood Group"), new JLabel("Date Of Join")
        };

        JTextField[] fields = new JTextField[12];
        int y = 100;
        JPanel panel = new JPanel(null);

        for (int i = 0; i < 12; i++) {
            labels[i].setBounds(20, y, 120, 25);
            fields[i] = new JTextField();
            fields[i].setBounds(150, y, 150, 25);
            panel.add(labels[i]);
            panel.add(fields[i]);
            y += 35;
        }

        JButton viewBtn = new JButton("View Data");
        viewBtn.setBounds(150, y + 10, 120, 30);
        panel.add(viewBtn);

        JButton addBtn = new JButton("Add");
        addBtn.setBounds(290, y + 10, 80, 30);
        panel.add(addBtn);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBounds(290, y + 50, 80, 30);
        panel.add(deleteBtn);

        // helper for DB operations
        StudentHelper helper = new StudentHelper(fields);

        viewBtn.addActionListener(e -> helper.loadStudentData());
        addBtn.addActionListener(e -> helper.addStudent());
        deleteBtn.addActionListener(e -> helper.deleteStudent());

        setTitle("Student Details");
        setSize(500, 720);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        add(panel);
    }
}

// ================= STUDENT HELPER =================
class StudentHelper {
    JTextField[] f;

    public StudentHelper(JTextField[] f) {
        this.f = f;
    }

    // Load first student record (same behavior as before)
    public void loadStudentData() {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM student LIMIT 1")) {

            if (rs.next()) {
                f[0].setText(rs.getString("admn_no"));
                f[1].setText(String.valueOf(rs.getInt("student_id")));
                f[2].setText(rs.getString("first_name"));
                f[3].setText(rs.getString("last_name"));
                f[4].setText(String.valueOf(rs.getInt("class")));
                f[5].setText(rs.getString("section"));
                f[6].setText(rs.getString("gender"));
                f[7].setText(rs.getString("date_of_birth"));
                f[8].setText(rs.getString("contact"));
                f[9].setText(rs.getString("address"));
                f[10].setText(rs.getString("blood_group"));
                f[11].setText(rs.getString("date_of_join"));
            } else {
                JOptionPane.showMessageDialog(null, "No student records found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading student data: " + e.getMessage());
        }
    }

    // Add new student using values from text fields
    public void addStudent() {
        String admnNo = f[0].getText().trim();
        String studentIdStr = f[1].getText().trim();
        String firstName = f[2].getText().trim();
        String lastName = f[3].getText().trim();
        String classStr = f[4].getText().trim();
        String section = f[5].getText().trim();
        String gender = f[6].getText().trim();
        String dob = f[7].getText().trim();
        String contact = f[8].getText().trim();
        String address = f[9].getText().trim();
        String bloodGroup = f[10].getText().trim();
        String dateOfJoin = f[11].getText().trim();

        if (admnNo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Admin No is required to add a record.");
            return;
        }

        // student_id and class are integers in DB — try to parse, else set 0
        int studentId = 0;
        int classNo = 0;
        try {
            if (!studentIdStr.isEmpty()) studentId = Integer.parseInt(studentIdStr);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Student ID must be a number.");
            return;
        }
        try {
            if (!classStr.isEmpty()) classNo = Integer.parseInt(classStr);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Class must be a number.");
            return;
        }

        String insertSQL = "INSERT INTO student (admn_no, student_id, first_name, last_name, class, section, gender, date_of_birth, contact, address, blood_group, date_of_join) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertSQL)) {

            ps.setString(1, admnNo);
            ps.setInt(2, studentId);
            ps.setString(3, firstName);
            ps.setString(4, lastName);
            ps.setInt(5, classNo);
            ps.setString(6, section);
            ps.setString(7, gender);
            ps.setString(8, dob);
            ps.setString(9, contact);
            ps.setString(10, address);
            ps.setString(11, bloodGroup);
            ps.setString(12, dateOfJoin);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Student added successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add student.");
            }
        } catch (SQLIntegrityConstraintViolationException sicv) {
            JOptionPane.showMessageDialog(null, "Duplicate key or constraint violation: " + sicv.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding student: " + e.getMessage());
        }
    }

    // Delete student by admn_no
    public void deleteStudent() {
        String admnNo = f[0].getText().trim();
        if (admnNo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter Admin No to delete the record.");
            return;
        }

        String deleteSQL = "DELETE FROM student WHERE admn_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(deleteSQL)) {

            ps.setString(1, admnNo);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Student deleted successfully!");
                // clear fields
                for (JTextField tf : f) tf.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "No record found with that Admin No.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting student: " + e.getMessage());
        }
    }
}

// ================= DB CONNECTION =================
class DBConnection {
    static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/ashwath";
        String user = "root"; // your MySQL username
        String pass = "Hoverz.7";     // your MySQL password
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, pass);
    }
}
