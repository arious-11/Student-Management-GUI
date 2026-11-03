import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentForm extends JFrame {

    JTextField trn, tfn, tln;
    JTextField tcs, tmaths, tphysics;

    JComboBox<String> jsem;
    JRadioButton jmale, jfemale;
    ButtonGroup gender;

    JTable table;
    DefaultTableModel model;
    Connection con;

    // Constructor
    StudentForm() {
        setTitle("Student Record Management System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        connectDB();

        // Form panel
        JPanel form = new JPanel(new GridLayout(8, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder(
                "Student Information"));

        trn = new JTextField();
        tfn = new JTextField();
        tln = new JTextField();

        tcs = new JTextField();
        tmaths = new JTextField();
        tphysics = new JTextField();

        jsem = new JComboBox<>(new String[]{
            "--Choose Semester--",
            "1st Semester", "2nd Semester",
            "3rd Semester", "4th Semester",
            "5th Semester", "6th Semester",
            "7th Semester", "8th Semester"
        });

        // Gender
        jmale = new JRadioButton("Male");
        jfemale = new JRadioButton("Female");

        gender = new ButtonGroup();
        gender.add(jmale);
        gender.add(jfemale);

        JPanel genderPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT));
        genderPanel.add(jmale);
        genderPanel.add(jfemale);

        // Add form fields
        form.add(new JLabel("Roll No:"));
        form.add(trn);

        form.add(new JLabel("First Name:"));
        form.add(tfn);

        form.add(new JLabel("Last Name:"));
        form.add(tln);

        form.add(new JLabel("Gender:"));
        form.add(genderPanel);

        form.add(new JLabel("Semester:"));
        form.add(jsem);

        form.add(new JLabel("Computer Science Marks:"));
        form.add(tcs);

        form.add(new JLabel("Maths Marks:"));
        form.add(tmaths);

        form.add(new JLabel("Physics Marks:"));
        form.add(tphysics);

        // Buttons
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton delete = new JButton("Delete");
        JButton display = new JButton("Display");
        JButton clear = new JButton("Clear");

        JPanel buttons = new JPanel();
        buttons.add(add);
        buttons.add(edit);
        buttons.add(delete);
        buttons.add(display);
        buttons.add(clear);

        // Table
        model = new DefaultTableModel(
                new String[]{
                    "Roll No", "First Name", "Last Name",
                    "Gender", "Semester",
                    "Computer Science", "Maths", "Physics"
                }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        scroll.setBorder(BorderFactory.createTitledBorder(
                "Student Records"));

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Add button using anonymous class
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        // Edit button
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editStudent();
            }
        });

        // Delete button
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteStudent();
            }
        });

        // Display button
        display.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayStudents();
            }
        });

        // Clear button
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        // Table row selection using anonymous class
        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            public void valueChanged(
                    javax.swing.event.ListSelectionEvent e) {

                if (!e.getValueIsAdjusting()) {
                    int row = table.getSelectedRow();

                    if (row >= 0) {
                        trn.setText(model.getValueAt(row, 0).toString());
                        tfn.setText(model.getValueAt(row, 1).toString());
                        tln.setText(model.getValueAt(row, 2).toString());

                        String g = model.getValueAt(row, 3).toString();
                        jmale.setSelected("Male".equals(g));
                        jfemale.setSelected("Female".equals(g));

                        jsem.setSelectedItem(
                                model.getValueAt(row, 4).toString());

                        tcs.setText(model.getValueAt(row, 5).toString());
                        tmaths.setText(model.getValueAt(row, 6).toString());
                        tphysics.setText(model.getValueAt(row, 7).toString());
                    }
                }
            }
        });

        displayStudents();
        setVisible(true);
    }

    // Database connection
    void connectDB() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:sqlite:student_db.db");

            String sql = "CREATE TABLE IF NOT EXISTS students ("
                    + "roll_no TEXT PRIMARY KEY, "
                    + "first_name TEXT NOT NULL, "
                    + "last_name TEXT NOT NULL, "
                    + "gender TEXT NOT NULL, "
                    + "semester TEXT NOT NULL, "
                    + "computer_science REAL NOT NULL, "
                    + "maths REAL NOT NULL, "
                    + "physics REAL NOT NULL)";

            Statement st = con.createStatement();
            st.executeUpdate(sql);
            st.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database connection failed: "
                            + ex.getMessage());
        }
    }

    // Get gender
    String getGender() {
        if (jmale.isSelected()) {
            return "Male";
        }

        if (jfemale.isSelected()) {
            return "Female";
        }

        return "";
    }

    // Validate form
    boolean validForm() {
        if (trn.getText().trim().isEmpty()
                || tfn.getText().trim().isEmpty()
                || tln.getText().trim().isEmpty()
                || getGender().isEmpty()
                || jsem.getSelectedIndex() == 0
                || tcs.getText().trim().isEmpty()
                || tmaths.getText().trim().isEmpty()
                || tphysics.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Please fill in all student details.");
            return false;
        }

        try {
            double cs = Double.parseDouble(tcs.getText());
            double maths = Double.parseDouble(tmaths.getText());
            double physics = Double.parseDouble(tphysics.getText());

            if (cs < 0 || cs > 100
                    || maths < 0 || maths > 100
                    || physics < 0 || physics > 100) {

                JOptionPane.showMessageDialog(this,
                        "Marks must be between 0 and 100.");
                return false;
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Enter valid numeric marks.");
            return false;
        }

        return true;
    }

    // Add student
    void addStudent() {
        if (con == null || !validForm()) {
            return;
        }

        String sql = "INSERT INTO students VALUES "
                + "(?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, trn.getText().trim());
            ps.setString(2, tfn.getText().trim());
            ps.setString(3, tln.getText().trim());
            ps.setString(4, getGender());
            ps.setString(5, jsem.getSelectedItem().toString());
            ps.setDouble(6, Double.parseDouble(tcs.getText()));
            ps.setDouble(7, Double.parseDouble(tmaths.getText()));
            ps.setDouble(8, Double.parseDouble(tphysics.getText()));

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(this,
                    "Student added successfully.");

            displayStudents();
            clearForm();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not add student: " + ex.getMessage());
        }
    }

    // Edit student
    void editStudent() {
        if (con == null || !validForm()) {
            return;
        }

        String sql = "UPDATE students SET "
                + "first_name=?, last_name=?, gender=?, semester=?, "
                + "computer_science=?, maths=?, physics=? "
                + "WHERE roll_no=?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, tfn.getText().trim());
            ps.setString(2, tln.getText().trim());
            ps.setString(3, getGender());
            ps.setString(4, jsem.getSelectedItem().toString());
            ps.setDouble(5, Double.parseDouble(tcs.getText()));
            ps.setDouble(6, Double.parseDouble(tmaths.getText()));
            ps.setDouble(7, Double.parseDouble(tphysics.getText()));
            ps.setString(8, trn.getText().trim());

            int count = ps.executeUpdate();
            ps.close();

            if (count > 0) {
                JOptionPane.showMessageDialog(this,
                        "Student updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Roll number not found.");
            }

            displayStudents();
            clearForm();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not update student: " + ex.getMessage());
        }
    }

    // Delete student
    void deleteStudent() {
        if (con == null) {
            return;
        }

        String roll = trn.getText().trim();

        if (roll.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter or select a roll number first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete student with roll number " + roll + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM students WHERE roll_no=?");

            ps.setString(1, roll);
            int count = ps.executeUpdate();
            ps.close();

            if (count > 0) {
                JOptionPane.showMessageDialog(this,
                        "Student deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Roll number not found.");
            }

            displayStudents();
            clearForm();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not delete student: " + ex.getMessage());
        }
    }

    // Display students
    void displayStudents() {
        if (con == null || model == null) {
            return;
        }

        model.setRowCount(0);

        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT * FROM students ORDER BY roll_no");

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("roll_no"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("gender"),
                    rs.getString("semester"),
                    rs.getDouble("computer_science"),
                    rs.getDouble("maths"),
                    rs.getDouble("physics")
                });
            }

            rs.close();
            st.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not display records: " + ex.getMessage());
        }
    }

    // Clear form
    void clearForm() {
        trn.setText("");
        tfn.setText("");
        tln.setText("");
        tcs.setText("");
        tmaths.setText("");
        tphysics.setText("");

        gender.clearSelection();
        jsem.setSelectedIndex(0);
        table.clearSelection();
    }

    // Main method
    public static void main(String[] args) {
        new StudentForm();
    }
}
