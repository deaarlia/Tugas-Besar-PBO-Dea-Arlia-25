public class UMRConfig {
    public static final double UMR = 2994193; // UMR Padang 2025

    public static double validasiUMR(double gaji) { // Validasi gaji terhadap UMR
        return gaji < UMR ? UMR : gaji; // Kembalikan UMR jika gaji di bawah UMR
    }
}
