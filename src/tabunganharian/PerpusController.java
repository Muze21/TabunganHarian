package tabunganharian;

import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class PerpusController {

    public void insert(String judul, String penulis, int tahunTerbit) throws SQLException {
        validasi(judul, penulis, tahunTerbit);
        String sql = "INSERT INTO buku (judul, penulis, tahun_terbit) VALUES (?, ?, ?)";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, judul);
            ps.setString(2, penulis);
            ps.setInt(3, tahunTerbit);
            ps.executeUpdate();
        }
    }

    public void update(int id, String judul, String penulis, int tahunTerbit) throws SQLException {
        validasi(judul, penulis, tahunTerbit);
        String sql = "UPDATE buku SET judul=?, penulis=?, tahun_terbit=? WHERE id=?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, judul);
            ps.setString(2, penulis);
            ps.setInt(3, tahunTerbit);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM buku WHERE id=?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void loadAll(DefaultTableModel model) throws SQLException {
        model.setRowCount(0);
        String sql = "SELECT * FROM buku ORDER BY id ASC";
        try (Connection conn = Koneksi.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("judul"),
                    rs.getString("penulis"),
                    rs.getInt("tahun_terbit")
                });
            }
        }
    }

    public int getTotalBuku() throws SQLException {
        String sql = "SELECT COUNT(*) FROM buku";
        try (Connection conn = Koneksi.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private void validasi(String judul, String penulis, int tahun) {
        if (judul == null || judul.trim().isEmpty())
            throw new IllegalArgumentException("Judul tidak boleh kosong!");
        if (penulis == null || penulis.trim().isEmpty())
            throw new IllegalArgumentException("Penulis tidak boleh kosong!");
        if (tahun < 1000 || tahun > 9999)
            throw new IllegalArgumentException("Tahun terbit harus 4 digit!");
    }
}
