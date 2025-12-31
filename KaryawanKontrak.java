import java.util.Date; // Import kelas Date

public class KaryawanKontrak extends Karyawan { // Kelas KaryawanKontrak
    private Date kontrakMulai; // Tanggal mulai kontrak
    private Date kontrakSelesai; // Tanggal selesai kontrak

    public KaryawanKontrak(String nama, int jabatanLevel, String jabatanNama,
                           String pendidikan, double gajiPokok, Date tanggalMasuk,
                           Date kontrakMulai, Date kontrakSelesai,
                           StatusMenikah statusMenikah, int jumlahAnak) { // Konstruktor
        super(nama, jabatanLevel, jabatanNama, pendidikan, gajiPokok, tanggalMasuk, 
              statusMenikah, jumlahAnak); // Panggil konstruktor superclass
        this.kontrakMulai = kontrakMulai; // Inisialisasi kontrak mulai
        this.kontrakSelesai = kontrakSelesai; // Inisialisasi kontrak selesai
    }

    @Override
    public double hitungGajiBersih(int performa) { // Implementasi metode hitungGajiBersih
        double tunjanganJabatan = Math.max(0, (6 - jabatanLevel)) * 0.03 * gajiPokok; // Tunjangan jabatan

        double tunjanganPendidikan = switch (pendidikan.toUpperCase()) { // Tunjangan pendidikan
            case "SMA" -> 30000; // Tunjangan untuk SMA
            case "D3" -> 60000; // Tunjangan untuk D3
            case "S1" -> 100000; // Tunjangan untuk S1
            case "S2" -> 150000; // Tunjangan untuk S2
            default -> 0;
        };

        double bonusPerforma = performa * 200; // Bonus performa berdasarkan performa
        double potonganBPJS = 0.03 * gajiPokok; // Potongan BPJS 3%

        // Tambahan tunjangan keluarga
        double tunjanganPasangan = (statusMenikah == StatusMenikah.MENIKAH) ? 500000 : 0; // Tunjangan suami/istri
        double tunjanganAnak = Math.min(jumlahAnak, 3) * 250000; // Tunjangan anak maksimal 3 anak

        double total = gajiPokok + tunjanganJabatan + tunjanganPendidikan + bonusPerforma // Total gaji bersih 
                       + tunjanganPasangan + tunjanganAnak - potonganBPJS; // Hitung total
        return UMRConfig.validasiUMR(total); // Validasi terhadap UMR
    }

    public Date getKontrakMulai() { return kontrakMulai; } // Getter kontrak mulai
    public Date getKontrakSelesai() { return kontrakSelesai; } // Getter kontrak selesai
}
