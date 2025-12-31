import java.util.Date; // Import kelas Date

public abstract class Karyawan { // Kelas abstrak Karyawan
    public enum StatusMenikah { // Enum untuk status menikah
        MENIKAH("Menikah"), // Status menikah
        BELUM_MENIKAH("Belum Menikah"); // Status belum menikah

        private final String label; // Label status
        StatusMenikah(String label) { this.label = label; } // Konstruktor
        public String getLabel() { return label; } // Getter label

        public static StatusMenikah fromString(String str) { // Konversi dari string ke enum
            if (str == null) return BELUM_MENIKAH; // Default jika null
            return str.equalsIgnoreCase("Menikah") ? MENIKAH : BELUM_MENIKAH; // Cek string
        }
    }

    protected String nama; // Nama karyawan, protected agar bisa diakses subclass
    protected int jabatanLevel; // Level jabatan (1-5)
    protected String jabatanNama;   // Keterangan jabatan (Direktur, Manager, dll)
    protected String pendidikan;    // SMA, D3, S1, S2
    protected double gajiPokok; // Gaji pokok
    protected Date tanggalMasuk; // Tanggal masuk kerja
    protected StatusMenikah statusMenikah; // Status menikah
    protected int jumlahAnak; // Jumlah anak

    public Karyawan(String nama, int jabatanLevel, String jabatanNama,
                    String pendidikan, double gajiPokok, Date tanggalMasuk,
                    StatusMenikah statusMenikah, int jumlahAnak) { // Konstruktor
        // Inisialisasi atribut
        this.nama = nama;
        this.jabatanLevel = jabatanLevel;
        this.jabatanNama = jabatanNama; 
        this.pendidikan = pendidikan;
        this.gajiPokok = gajiPokok;
        this.tanggalMasuk = tanggalMasuk;
        this.statusMenikah = statusMenikah;
        this.jumlahAnak = jumlahAnak;
    }

    // method abstrak untuk perhitungan gaji bersih
    public abstract double hitungGajiBersih(int performa);

    // getter
    public String getNama() { return nama; }
    public int getJabatanLevel() { return jabatanLevel; }
    public String getJabatanNama() { return jabatanNama; }
    public String getPendidikan() { return pendidikan; }
    public double getGajiPokok() { return gajiPokok; }
    public Date getTanggalMasuk() { return tanggalMasuk; }
    public StatusMenikah getStatusMenikah() { return statusMenikah; }
    public int getJumlahAnak() { return jumlahAnak; }
}
