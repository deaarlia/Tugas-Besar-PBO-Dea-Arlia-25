import java.util.Date; // Import kelas Date

public class KaryawanTetap extends Karyawan { // Kelas KaryawanTetap
    public KaryawanTetap(String nama, int jabatanLevel, String jabatanNama,
                         String pendidikan, double gajiPokok, Date tanggalMasuk,
                         StatusMenikah statusMenikah, int jumlahAnak) { // Konstruktor
        super(nama, jabatanLevel, jabatanNama, pendidikan, gajiPokok, tanggalMasuk,
              statusMenikah, jumlahAnak); // Panggil konstruktor superclass
    }

    @Override
    public double hitungGajiBersih(int performa) { // Implementasi metode hitungGajiBersih
        double tunjanganJabatan = Math.max(0, (6 - jabatanLevel)) * 0.05 * gajiPokok; // Tunjangan jabatan

        double tunjanganPendidikan = switch (pendidikan.toUpperCase()) { // Tunjangan pendidikan
            case "SMA" -> 30000; // Tunjangan untuk SMA
            case "D3" -> 60000; // Tunjangan untuk D3
            case "S1" -> 100000; // Tunjangan untuk S1
            case "S2" -> 150000; // Tunjangan untuk S2
            default -> 0; // Default tunjangan 0
        };

        double honorLembur = performa * 200; // Honor lembur berdasarkan performa
        double potonganBPJS = 0.05 * gajiPokok; // Potongan BPJS 5%

        // Tambahan tunjangan keluarga
        double tunjanganPasangan = (statusMenikah == StatusMenikah.MENIKAH) ? 500000 : 0; // Tunjangan suami/istri
        double tunjanganAnak = Math.min(jumlahAnak, 3) * 250000; // Tunjangan anak maksimal 3 anak

        double total = gajiPokok + tunjanganJabatan + tunjanganPendidikan + honorLembur // Total gaji bersih
                       + tunjanganPasangan + tunjanganAnak - potonganBPJS; // Hitung total

        return UMRConfig.validasiUMR(total); // Validasi terhadap UMR
    }
}
