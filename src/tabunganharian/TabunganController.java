package tabunganharian;
// src/tabunganharian/TabunganController.java

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TabunganController {

    private SimpleDateFormat sdf = new SimpleDateFormat(AppConfig.DATE_FORMAT);

    public TabunganController() {
        sdf.setLenient(false); // Validasi tanggal harus tepat
    }

    // ==================== INSERT ====================
    public void insert(Date tanggal, String sumber, int nominal, 
                      String tipePendapatan) 
                      throws SQLException {
        validasiInput(tanggal, sumber, nominal, tipePendapatan);
        
        String sql = "INSERT INTO tabungan_harian (tanggal, sumber, nominal, tipe_pendapatan) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, tanggal);
            ps.setString(2, sumber);
            ps.setInt(3, nominal);
            ps.setString(4, tipePendapatan);
            ps.executeUpdate();
        }
    }

    // ==================== UPDATE ====================
    public void update(int id, Date tanggal, String sumber, int nominal,
                      String tipePendapatan) 
                      throws SQLException {
        validasiInput(tanggal, sumber, nominal, tipePendapatan);
        
        String sql = "UPDATE tabungan_harian SET tanggal=?, sumber=?, nominal=?, " +
                    "tipe_pendapatan=? WHERE id=?";

        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, tanggal);
            ps.setString(2, sumber);
            ps.setInt(3, nominal);
            ps.setString(4, tipePendapatan);
            ps.setInt(5, id);
            ps.executeUpdate();
        }
    }

    // ==================== DELETE ====================
    /**
     * Hapus data
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM tabungan_harian WHERE id=?";
        
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ==================== LOAD / READ ====================
    /**
     * Load semua data ke tabel (dengan 2 kolom baru)
     */
    public void loadAll(DefaultTableModel model) throws SQLException {
        model.setRowCount(0);
        String sql = "SELECT * FROM tabungan_harian ORDER BY id ASC";
        
        try (Connection conn = Koneksi.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                Date dbTanggal = rs.getDate("tanggal");
                String tanggalFormat = dbTanggal != null ? sdf.format(dbTanggal) : "";
                String sumber = rs.getString("sumber");
                int nominal = rs.getInt("nominal");
                String tipePendapatan = rs.getString("tipe_pendapatan");
                
                model.addRow(new Object[]{id, tanggalFormat, sumber, nominal, tipePendapatan});
            }
        }
    }

    // ==================== STATISTIK ====================
    /**
     * Hitung total PEMASUKAN
     */
    public long getTotalPemasukan() throws SQLException {
        String sql = "SELECT SUM(nominal) as total FROM tabungan_harian WHERE tipe_pendapatan = ?";
        
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, AppConfig.TIPE_PEMASUKAN);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getLong("total");
            }
        }
        return 0;
    }

    /**
     * Hitung total PENGELUARAN
     */
    public long getTotalPengeluaran() throws SQLException {
        String sql = "SELECT SUM(nominal) as total FROM tabungan_harian WHERE tipe_pendapatan = ?";
        
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, AppConfig.TIPE_PENGELUARAN);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getLong("total");
            }
        }
        return 0;
    }

    /**
     * Hitung SALDO = Pemasukan - Pengeluaran
     */
    public long getSaldo() throws SQLException {
        return getTotalPemasukan() - getTotalPengeluaran();
    }

    // ==================== STATISTIK DENGAN DATE RANGE ====================
    /**
     * Hitung total PEMASUKAN dalam range tanggal
     */
    public long getTotalPemasukanRange(Date startDate, Date endDate) throws SQLException {
        String sql = "SELECT SUM(nominal) as total FROM tabungan_harian " +
                    "WHERE tipe_pendapatan = ? AND tanggal BETWEEN ? AND ?";
        
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, AppConfig.TIPE_PEMASUKAN);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getLong("total");
            }
        }
        return 0;
    }

    /**
     * Hitung total PENGELUARAN dalam range tanggal
     */
    public long getTotalPengeluaranRange(Date startDate, Date endDate) throws SQLException {
        String sql = "SELECT SUM(nominal) as total FROM tabungan_harian " +
                    "WHERE tipe_pendapatan = ? AND tanggal BETWEEN ? AND ?";
        
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, AppConfig.TIPE_PENGELUARAN);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getLong("total");
            }
        }
        return 0;
    }

    /**
     * Hitung SALDO dalam range tanggal
     */
    public long getSaldoRange(Date startDate, Date endDate) throws SQLException {
        return getTotalPemasukanRange(startDate, endDate) - getTotalPengeluaranRange(startDate, endDate);
    }



    // ==================== VALIDASI ====================
    /**
     * Validasi semua input sebelum insert/update
     */
    private void validasiInput(Date tanggal, String sumber, int nominal, 
                              String tipePendapatan) {
        // Cek tanggal - lebih simple karena sudah Date object
        if (tanggal == null) {
            throw new IllegalArgumentException("Tanggal tidak boleh kosong!");
        }
        
        // Cek sumber
        if (sumber == null || sumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Sumber tidak boleh kosong!");
        }
        
        // Cek nominal
        if (nominal <= 0) {
            throw new IllegalArgumentException("Nominal harus lebih dari 0!");
        }
        
        // Cek tipe pendapatan
        if (!AppConfig.isValidTipePendapatan(tipePendapatan)) {
            throw new IllegalArgumentException("Tipe pendapatan tidak valid!");
        }
    }
}