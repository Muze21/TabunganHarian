package tabunganharian;

import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * FormTabungan.java - Main Application
 * All-in-one simple UI untuk pencatatan tabungan harian
 */

public class FormTabungan extends JFrame {

    // ===== FORM FIELDS =====
    private JTextField txtId = new JTextField(10);
    private JTextField txtTanggal = new JTextField(12);
    private JTextField txtSumber = new JTextField(15);
    private JTextField txtNominal = new JTextField(10);
    private JComboBox<String> cmbTipePendapatan;

    // ===== TABLE & CONTROLLER =====
    private JTable tblTabungan;
    private DefaultTableModel tableModel;
    private TabunganController controller = new TabunganController();

    // ===== STATISTICS LABELS =====
    private JLabel lblPemasukan;
    private JLabel lblPengeluaran;
    private JLabel lblSaldo;

    // ===== DATE MANAGEMENT =====
    private Date selectedDate;
    private SimpleDateFormat sdf = new SimpleDateFormat(AppConfig.DATE_FORMAT);
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("d MMM yyyy");
    private DecimalFormat currencyFormat = new DecimalFormat("Rp #,###");

    public FormTabungan() {
        setTitle("Tabungan Harian - Aplikasi Catatan Keuangan");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createStatisticsPanel(), BorderLayout.SOUTH);

        sdf.setLenient(false); // Mematikan toleransi (35/04 akan dianggap error, bukan 05/05)
        reset(); // Set tanggal default ke hari ini saat form pertama kali dibuka
        loadData();
    }

    // ===== FORM PANEL =====
    private JPanel createFormPanel() {
        JPanel main = new JPanel(new GridLayout(4, 1, 5, 5));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Row 1: ID & Tanggal
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        txtId.setEditable(false);
        txtTanggal.setEditable(false);
        row1.add(new JLabel("Tanggal:"));
        row1.add(txtTanggal);
        row1.add(new JLabel("id:"));
        row1.add(txtId);
        main.add(row1);

        // Row 2: Sumber & Nominal
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row2.add(new JLabel("Sumber:"));
        row2.add(txtSumber);
        row2.add(new JLabel("Nominal:"));
        row2.add(txtNominal);
        main.add(row2);

        // Row 3: Tipe Pendapatan
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row3.add(new JLabel("Tipe Pendapatan:"));
        cmbTipePendapatan = new JComboBox<>(AppConfig.TIPE_PENDAPATAN);
        row3.add(cmbTipePendapatan);
        main.add(row3);


        // Row 5: Buttons
        JPanel row5 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JButton btnSimpan = new JButton("Simpan");
        JButton btnUbah = new JButton("Ubah");
        JButton btnHapus = new JButton("Hapus");
        JButton btnReset = new JButton("Reset");
        
        btnSimpan.addActionListener(e -> simpan());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnReset.addActionListener(e -> reset());
        
        row5.add(btnSimpan);
        row5.add(btnUbah);
        row5.add(btnHapus);
        row5.add(btnReset);
        main.add(row5);

        return main;
    }

    // ===== TABLE PANEL =====
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(
                new Object[] { "ID", "Tanggal", "Sumber", "Nominal", "Tipe" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabel hanya bisa dibaca, tidak bisa diketik langsung
            }
        };

        // table clikc
        tblTabungan = new JTable(tableModel);
        tblTabungan.setRowHeight(24);
        tblTabungan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                isiFormDariTabel();
        });

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
             boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                // Warna baris 
                if (!isSelected) {
                    Object tipe = table.getValueAt(row, 4);
                    if (AppConfig.TIPE_PEMASUKAN.equals(tipe)) {
                        setBackground(new Color(235, 255, 235));
                        } else {
                            setBackground(new Color(255, 235, 235));
                            }
                        }
                        setForeground(Color.black

                        );
                // ubah tanggal ke huruf

                if (column == 1 && value instanceof Date) {
                    setText(displayDateFormat.format((Date) value));
                } else if (column == 3 && value instanceof Number) {
                    setText(formatCurrency(((Number) value).longValue()));
                }
                return this;
            }
        };
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            tblTabungan.getColumnModel().getColumn(i).setCellRenderer(renderer);
            
        }
        panel.add(new JScrollPane(tblTabungan), BorderLayout.CENTER);
        return panel;
    }

    // ===== STATISTICS PANEL =====
    private JPanel createStatisticsPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(BorderFactory.createTitledBorder("Ringkasan Keuangan"));

        // Stats row
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 10, 0));
        statsRow.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblPemasukan = new JLabel("Pemasukan: Rp 0");
        lblPengeluaran = new JLabel("Pengeluaran: Rp 0");
        lblSaldo = new JLabel("Saldo: Rp 0");

        statsRow.add(lblPemasukan);
        statsRow.add(lblPengeluaran);
        statsRow.add(lblSaldo);
        main.add(statsRow, BorderLayout.CENTER);

        return main;
    }

    /**
     * Simpan data baru
     */
    private void simpan() {
        try {
            // --- AWAL KODE OPSIONAL: AMBIL TANGGAL DARI TEXT FIELD ---
            try {
                if (txtTanggal.getText().trim().isEmpty()) {
                    selectedDate = new java.sql.Date(System.currentTimeMillis());
                } else {
                    java.util.Date parsed = displayDateFormat.parse(txtTanggal.getText());
                    selectedDate = new java.sql.Date(parsed.getTime());
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Tanggal tidak valid! Pastikan formatnya benar ("
                        + displayDateFormat.toPattern() + ") dan angkanya masuk akal.");
                return;
            }
            // --- AKHIR KODE OPSIONAL ---

            if (txtSumber.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Sumber tidak boleh kosong!");
                return;
            }

            String sumber = txtSumber.getText();
            int nominal = Integer.parseInt(txtNominal.getText());
            String tipePendapatan = (String) cmbTipePendapatan.getSelectedItem();
            
            // Validasi Saldo jika Pengeluaran
            if (tipePendapatan.equals(AppConfig.TIPE_PENGELUARAN)) {
                long currentSaldo = controller.getSaldo();
                if (nominal > currentSaldo) {
                    JOptionPane.showMessageDialog(this, "Saldo tidak cukup! Saldo saat ini: " + currencyFormat.format(currentSaldo));
                    return;
                }
            }

            controller.insert(selectedDate, sumber, nominal, tipePendapatan);
            JOptionPane.showMessageDialog(this, "Data disimpan.");
            loadData();
            updateStatistics();
            reset();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Nominal harus berupa angka!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    /**
     * Ubah data
     */
    private void ubah() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih data yang ingin diubah!");
                return;
            }

            // --- AWAL KODE OPSIONAL: AMBIL TANGGAL DARI TEXT FIELD ---
            try {
                if (txtTanggal.getText().trim().isEmpty()) {
                    selectedDate = new java.sql.Date(System.currentTimeMillis());
                } else {
                    java.util.Date parsed = displayDateFormat.parse(txtTanggal.getText());
                    selectedDate = new java.sql.Date(parsed.getTime());
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Tanggal tidak valid! Pastikan formatnya benar ("
                        + displayDateFormat.toPattern() + ") dan angkanya masuk akal.");
                return;
            }
            // --- AKHIR KODE OPSIONAL ---

            int id = Integer.parseInt(txtId.getText());
            String sumber = txtSumber.getText();
            int nominal = Integer.parseInt(txtNominal.getText());
            String tipePendapatan = (String) cmbTipePendapatan.getSelectedItem();

            // Validasi Saldo jika Pengeluaran (untuk ubah, kita perlu hati-hati karena nominal lama sudah mengurangi saldo)
            // Namun untuk simplenya kita cek saldo saat ini + nominal lama vs nominal baru
            if (tipePendapatan.equals(AppConfig.TIPE_PENGELUARAN)) {
                // Ambil nominal lama dari tabel
                int row = tblTabungan.getSelectedRow();
                int nominalLama = Integer.parseInt(tableModel.getValueAt(row, 3).toString());
                String tipeLama = tableModel.getValueAt(row, 4).toString();
                
                long saldoTanpaItemIni = controller.getSaldo();
                if (tipeLama.equals(AppConfig.TIPE_PENGELUARAN)) {
                    saldoTanpaItemIni += nominalLama;
                } else {
                    saldoTanpaItemIni -= nominalLama;
                }

                if (nominal > saldoTanpaItemIni) {
                    JOptionPane.showMessageDialog(this, "Saldo tidak cukup! Saldo tersedia: " + currencyFormat.format(saldoTanpaItemIni));
                    return;
                }
            }

            controller.update(id, selectedDate, sumber, nominal, tipePendapatan);
            JOptionPane.showMessageDialog(this, "Data diubah.");
            loadData();
            updateStatistics();
            reset();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Nominal harus berupa angka!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    /**
     * Hapus data
     */
    private void hapus() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!");
                return;
            }

            int id = Integer.parseInt(txtId.getText());
            String pesan = String.format("yakin mau menghapus data ini? id=%s", id);
            int confirm = JOptionPane.showConfirmDialog(this, pesan, "konfirmasi hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.delete(id);
                JOptionPane.showMessageDialog(this, "Data dihapus.");
                loadData();
                updateStatistics();
                reset();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    /**
     * Reset form
     */
    private void reset() {
        txtId.setText("");
        //Calendar 
        Calendar cal = Calendar.getInstance();
        selectedDate = new Date(cal.getTimeInMillis());

        txtTanggal.setText(displayDateFormat.format(selectedDate));
        txtSumber.setText("");
        txtNominal.setText("");
        cmbTipePendapatan.setSelectedIndex(0);
        tblTabungan.clearSelection();
    }

    /**
     * Load data dari database
     */
    private void loadData() {
        try {
            controller.loadAll(tableModel);
            updateStatistics(); // Tambahkan ini agar statistik muncul saat startup
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error load data: " + ex.getMessage());
        }
    }
    // jadiin ke RP

    private String formatCurrency(long amount) {
        String formatted = String.format("%,d", amount).replace(',', '.');
        return "RP " + formatted;
    }

    /**
     * Isi form saat row tabel diklik
     */
    private void isiFormDariTabel() {
        int row = tblTabungan.getSelectedRow();
        if (row == -1)
            return;

        try {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            Object tanggalValue = tableModel.getValueAt(row, 1);
            if (tanggalValue instanceof Date) {
                selectedDate = (Date) tanggalValue;
                txtTanggal.setText(displayDateFormat.format(selectedDate));
            } else {
                String tanggalStr = tanggalValue.toString();
                selectedDate = new Date(displayDateFormat.parse(tanggalStr).getTime());
                txtTanggal.setText(displayDateFormat.format(selectedDate));
            }

            txtSumber.setText(tableModel.getValueAt(row, 2).toString());
            txtNominal.setText(tableModel.getValueAt(row, 3).toString());
            String tipe = tableModel.getValueAt(row, 4).toString();
            cmbTipePendapatan.setSelectedItem(tipe);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateStatistics() {
        try {
            long pemasukan = controller.getTotalPemasukan();
            long pengeluaran = controller.getTotalPengeluaran();
            long saldo = controller.getSaldo();

            lblPemasukan.setText("Pemasukan: " + currencyFormat.format(pemasukan));
            lblPengeluaran.setText("Pengeluaran: " + currencyFormat.format(pengeluaran));
            lblSaldo.setText("Saldo: " + currencyFormat.format(saldo));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error load statistics: " + ex.getMessage());
        }
    }

}