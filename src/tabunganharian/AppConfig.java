package tabunganharian;

public class AppConfig {
    
    // ==================== DATE FORMAT ====================
    /**
     * Format tanggal yang digunakan di seluruh aplikasi
     * Format: dd-MM-yyyy
     * Bisa diubah ke: MM-dd-yyyy, yyyy-MM-dd, dll
     */
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    
    
    // ==================== TIPE PENDAPATAN ====================
    /**
     * Opsi tipe pendapatan
     * - PEMASUKAN: uang masuk
     * - PENGELUARAN: uang keluar
     */
    public static final String[] TIPE_PENDAPATAN = {
        "PEMASUKAN",
        "PENGELUARAN"
    };
    
    public static final String TIPE_PEMASUKAN = "PEMASUKAN";
    public static final String TIPE_PENGELUARAN = "PENGELUARAN";

    
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Validasi apakah tipe pendapatan valid
     */
    public static boolean isValidTipePendapatan(String tipe) {
        for (String t : TIPE_PENDAPATAN) {
            if (t.equalsIgnoreCase(tipe)) {
                return true;
            }
        }
        return false;
    }
}

