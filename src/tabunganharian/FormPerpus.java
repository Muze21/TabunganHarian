package tabunganharian;

import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FormPerpus extends JFrame {

    private JTextField txtId      = new JTextField(8);
    private JTextField txtJudul   = new JTextField(20);
    private JTextField txtPenulis = new JTextField(18);
    private JTextField txtTahun   = new JTextField(6);

    private JTable tblBuku;
    private DefaultTableModel tableModel;
    private PerpusController controller = new PerpusController();
    private JLabel lblTotal;

    public FormPerpus() {
        setTitle("Sistem Perpustakaan - CRUD Data Buku");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);

        txtId.setEditable(false);
        loadData();
    }

    private JPanel createFormPanel() {
        JPanel main = new JPanel(new GridLayout(3, 1, 3, 3));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JPanel r1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        r1.add(new JLabel("ID:")); r1.add(txtId);
        r1.add(new JLabel("Judul:")); r1.add(txtJudul);
        main.add(r1);

        JPanel r2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        r2.add(new JLabel("Penulis:")); r2.add(txtPenulis);
        r2.add(new JLabel("Tahun Terbit:")); r2.add(txtTahun);
        main.add(r2);

        JPanel r3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        JButton btnSimpan = new JButton("Simpan");
        JButton btnUbah   = new JButton("Ubah");
        JButton btnHapus  = new JButton("Hapus");
        JButton btnReset  = new JButton("Reset");
        btnSimpan.addActionListener(e -> simpan());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnReset.addActionListener(e -> reset());
        r3.add(btnSimpan); r3.add(btnUbah); r3.add(btnHapus); r3.add(btnReset);
        main.add(r3);

        return main;
    }

    private JPanel createTablePanel() {
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Judul", "Penulis", "Tahun Terbit"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBuku = new JTable(tableModel);
        tblBuku.setRowHeight(22);
        tblBuku.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { isiFormDariTabel(); }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tblBuku), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        lblTotal = new JLabel("Total Buku: 0");
        panel.add(lblTotal);
        return panel;
    }

    private void simpan() {
        try {
            if (txtJudul.getText().trim().isEmpty() || txtPenulis.getText().trim().isEmpty() || txtTahun.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Judul, Penulis, dan Tahun Terbit tidak boleh kosong!");
                return;
            }
            controller.insert(txtJudul.getText().trim(), txtPenulis.getText().trim(), Integer.parseInt(txtTahun.getText().trim()));
            JOptionPane.showMessageDialog(this, "Buku berhasil disimpan.");
            loadData(); reset();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Tahun Terbit harus berupa angka!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void ubah() {
        try {
            if (txtId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Pilih buku dari tabel!"); return; }
            controller.update(Integer.parseInt(txtId.getText()), txtJudul.getText().trim(), txtPenulis.getText().trim(), Integer.parseInt(txtTahun.getText().trim()));
            JOptionPane.showMessageDialog(this, "Data buku berhasil diubah.");
            loadData(); reset();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Tahun Terbit harus berupa angka!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void hapus() {
        try {
            if (txtId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Pilih buku dari tabel!"); return; }
            int id = Integer.parseInt(txtId.getText());
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Yakin hapus buku \"" + txtJudul.getText() + "\"?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.delete(id);
                JOptionPane.showMessageDialog(this, "Buku berhasil dihapus.");
                loadData(); reset();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void reset() {
        txtId.setText(""); txtJudul.setText(""); txtPenulis.setText(""); txtTahun.setText("");
        tblBuku.clearSelection();
    }

    private void loadData() {
        try { controller.loadAll(tableModel); updateStatus(); }
        catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error load: " + ex.getMessage()); }
    }

    private void isiFormDariTabel() {
        int row = tblBuku.getSelectedRow();
        if (row == -1) return;
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtJudul.setText(tableModel.getValueAt(row, 1).toString());
        txtPenulis.setText(tableModel.getValueAt(row, 2).toString());
        txtTahun.setText(tableModel.getValueAt(row, 3).toString());
    }

    private void updateStatus() {
        try { lblTotal.setText("Total Buku: " + controller.getTotalBuku()); }
        catch (SQLException ex) { lblTotal.setText("Total Buku: -"); }
    }
}
