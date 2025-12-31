import java.sql.SQLException;
import java.text.ParseException; // Untuk menangani parsing tanggal
import java.text.SimpleDateFormat; // Untuk format tanggal
import java.util.Date; // Untuk objek tanggal
import java.util.List;
import java.util.Scanner; // Untuk input dari pengguna

public class MainApp { // Kelas utama aplikasi

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd-MM-yyyy"); // Format tanggal

    public static void main(String[] args) { // Metode utama
        try (Scanner scanner = new Scanner(System.in)) { // Scanner untuk input pengguna
             KoneksiDatabase.checkConnection(); // Cek koneksi database
            int pilihan; // Variabel untuk menyimpan pilihan menu
            do { // Loop menu utama
                System.out.println("\n=== MENU SISTEM GAJI KARYAWAN ==="); // Header menu
                System.out.println("1. Tambah Karyawan"); // Opsi tambah karyawan
                System.out.println("2. Tampilkan Data Karyawan"); // Opsi tampilkan data karyawan
                System.out.println("3. Update Gaji Pokok Karyawan"); // Opsi update gaji pokok
                System.out.println("4. Hapus Karyawan"); // Opsi hapus karyawan
                System.out.println("5. Hitung dan Cetak Slip Gaji"); // Opsi hitung dan cetak slip gaji
                System.out.println("6. Keluar"); // Opsi keluar
                System.out.print("Pilih menu: "); // Prompt pilihan menu
                pilihan = safeInt(scanner); // Baca pilihan menu dengan validasi

                switch (pilihan) { // Switch berdasarkan pilihan menu
                    case 1 -> tambahKaryawan(scanner); // Tambah karyawan
                    case 2 -> { // Tampilkan data karyawan
                                System.out.println("Tampilkan berdasarkan:");
                                System.out.println("1. Nama Lengkap");
                                System.out.println("2. Status (Tetap/Kontrak)");
                                System.out.println("3. Semua Karyawan (List)");
                                int pilih = safeInt(scanner);

                                if (pilih == 1) {
                                    System.out.print("Masukkan nama lengkap: ");
                                    String nama = scanner.nextLine().trim();
                                    KaryawanDAO.readKaryawanByNama(nama);
                                } else if (pilih == 2) {
                                    System.out.print("Masukkan status (Tetap/Kontrak): ");
                                    String tipe = scanner.nextLine().trim();
                                    KaryawanDAO.readKaryawanByTipe(tipe);
                                } else if (pilih == 3) {
                                    try {
                                        List<Karyawan> semua = KaryawanDAO.readKaryawan();
                                        System.out.println("Jumlah karyawan: " + semua.size());
                                        System.out.println("=== DAFTAR NAMA KARYAWAN ===");
                                        for (Karyawan k : semua) {
                                        System.out.println("- " + k.getNama());
                                }
                                    } catch (SQLException e) {
                                        System.out.println("Gagal membaca data: " + e.getMessage());
                                    }
                                }
                                }                           

                    case 3 -> updateGaji(scanner); // Update gaji pokok
                    case 4 -> hapusKaryawan(scanner); // Hapus karyawan
                    case 5 -> hitungDanCetakSlip(scanner); // Hitung dan cetak slip gaji
                    case 6 -> System.out.println("Program selesai."); // Keluar program
                    default -> System.out.println("Pilihan tidak valid."); // Pesan pilihan tidak valid
                }
            } while (pilihan != 6); // Ulangi sampai pilihan keluar
        } catch (Exception e) { // Tangani kesalahan umum
            System.out.println("Kesalahan sistem: " + e.getMessage()); // Pesan kesalahan
        }
    }

    private static String getNamaJabatan(int level) { // Mendapatkan nama jabatan berdasarkan level
        return switch (level) { // Switch berdasarkan level
            case 1 -> "Direktur"; // Level 1 = Direktur
            case 2 -> "Manager"; // Level 2 = Manager
            case 3 -> "Supervisor"; // Level 3 = Supervisor
            case 4 -> "Staff Senior"; // Level 4 = Staff Senior
            case 5 -> "Staff Junior"; // Level 5 = Staff Junior
            default -> "Unknown"; // Level tidak dikenal
        };
    }

    private static void tambahKaryawan(Scanner scanner) { // Metode untuk tambah karyawan
        try { // Tangani kesalahan umum
            System.out.print("Nama lengkap: "); // Prompt nama lengkap
            String nama = scanner.nextLine().trim(); // Baca nama lengkap

            System.out.print("Level Jabatan (1 = Direktur, 2 = Manager, 3 = Supervisor, 4=Staff Senior, 5=Staff Junior): "); // Prompt level jabatan
            int level = safeInt(scanner); // Baca level jabatan

            String jabatanNama = getNamaJabatan(level); // Dapatkan nama jabatan berdasarkan level 

            System.out.print("Pendidikan (SMA/D3/S1/S2): "); // Prompt pendidikan
            String pendidikan = scanner.nextLine().trim(); // Baca pendidikan

            System.out.print("Tipe Karyawan (Tetap/Kontrak): "); // Prompt tipe karyawan
            String tipe = scanner.nextLine().trim(); // Baca tipe karyawan

            System.out.print("Gaji Pokok: "); // Prompt gaji pokok
            double gajiPokok = safeDouble(scanner); // Baca gaji pokok

            System.out.print("Tanggal Masuk (dd-mm-yyyy), kosongkan untuk hari ini: "); // Prompt tanggal masuk
            String tglStr = scanner.nextLine().trim(); // Baca tanggal masuk
            Date tanggalMasuk = parseOrToday(tglStr); // Parse tanggal atau gunakan hari ini

            System.out.print("Status Menikah (Menikah/Belum Menikah): "); // Prompt status menikah
            String statusStr = scanner.nextLine().trim(); // Baca status menikah
            Karyawan.StatusMenikah status = Karyawan.StatusMenikah.fromString(statusStr); // Konversi ke enum StatusMenikah

            System.out.print("Jumlah Anak: "); // Prompt jumlah anak
            int jumlahAnak = safeInt(scanner); // Baca jumlah anak

            Date kontrakMulai = null; // Tanggal mulai kontrak
            Date kontrakSelesai = null; // Tanggal selesai kontrak
            if ("Kontrak".equalsIgnoreCase(tipe)) { // Jika tipe karyawan adalah kontrak
                System.out.print("Tanggal Mulai Kontrak (dd-MM-yyyy): "); // Prompt tanggal mulai kontrak
                kontrakMulai = parseOrToday(scanner.nextLine().trim()); // Baca dan parse tanggal mulai kontrak
                System.out.print("Tanggal Selesai Kontrak (dd-MM-yyyy): "); // Prompt tanggal selesai kontrak
                kontrakSelesai = parseOrToday(scanner.nextLine().trim()); // Baca dan parse tanggal selesai kontrak
            
                // Validasi: kontrak selesai tidak boleh sebelum kontrak mulai
                if (kontrakSelesai.before(kontrakMulai)) {
                System.out.println("Error: Tanggal selesai kontrak tidak boleh lebih kecil dari tanggal mulai kontrak.");
                    return; // hentikan proses tambah karyawan
                }
            }
            // Simpan ke DB menggunakan DAO
            KaryawanDAO.createKaryawan(nama, level, jabatanNama, pendidikan, tipe,gajiPokok, tanggalMasuk, kontrakMulai, kontrakSelesai, status, jumlahAnak); // Panggil metode DAO untuk membuat karyawan
        } catch (Exception e) { // Tangani kesalahan umum
            System.out.println("Gagal menambah karyawan: " + e.getMessage()); // Pesan kesalahan
        }
    }

    private static void updateGaji(Scanner scanner) { // Metode untuk update gaji pokok
        try { // Tangani kesalahan umum
            System.out.print("Nama lengkap karyawan yang akan diupdate: "); // Prompt nama karyawan
            String namaUpdate = scanner.nextLine().trim(); // Baca nama karyawan
            System.out.print("Gaji baru: "); // Prompt gaji baru
            double gajiBaru = safeDouble(scanner); // Baca gaji baru
            KaryawanDAO.updateGajiPokok(namaUpdate, gajiBaru); // Panggil metode DAO untuk update gaji pokok
        } catch (Exception e) { // Tangani kesalahan umum
            System.out.println("Gagal update gaji: " + e.getMessage()); // Pesan kesalahan
        }
    }

    private static void hapusKaryawan(Scanner scanner) { // Metode untuk hapus karyawan
        try {
            System.out.print("Nama lengkap karyawan yang akan dihapus: "); // Prompt nama karyawan
            String namaDelete = scanner.nextLine().trim(); // Baca nama karyawan
            KaryawanDAO.deleteKaryawan(namaDelete); // Panggil metode DAO untuk hapus karyawan
        } catch (Exception e) { // Tangani kesalahan umum
            System.out.println("Gagal menghapus karyawan: " + e.getMessage()); // Pesan kesalahan
        }
    }

    private static void hitungDanCetakSlip(Scanner scanner) { // Metode untuk hitung dan cetak slip gaji
        try { // Tangani kesalahan umum
            System.out.print("Nama lengkap karyawan: "); // Prompt nama karyawan
            String namaCari = scanner.nextLine().trim(); // Baca nama karyawan

            System.out.print("Skor Performa (Skala 0 — 100): "); // Prompt performa
            int performa = safeInt(scanner); // Baca performa
            if (performa < 0 || performa > 100) { // Validasi performa
                System.out.println("Performa harus antara 0 dan 100."); // Pesan kesalahan
                return; // Keluar dari metode
            }

            Karyawan k = KaryawanDAO.findKaryawanByName(namaCari); // Cari karyawan berdasarkan nama
            if (k == null) { // Jika karyawan tidak ditemukan
                System.out.println("Karyawan tidak ditemukan."); // Pesan kesalahan
                return; // Keluar dari metode
            }

            tampilkanSlipGaji(k, performa); // Tampilkan slip gaji

        } catch (Exception e) { // Tangani kesalahan umum
            System.out.println("Gagal menghitung/cetak slip: " + e.getMessage()); // Pesan kesalahan
        }
    }

    public static void tampilkanSlipGaji(Karyawan karyawan, int performa) { // Metode untuk menampilkan slip gaji
        System.out.println("----------------------------------------"); // Garis pemisah
        System.out.println("-- PROGRAM HITUNG GAJI KARYAWAN --"); // Header program
        System.out.println("----------------------------------------"); // Garis pemisah
        System.out.println("Nama Karyawan       : " + karyawan.getNama()); // Tampilkan nama karyawan
        System.out.println("Level Jabatan       : " + karyawan.getJabatanLevel()); // Tampilkan level jabatan
        System.out.println("Nama Jabatan        : " + karyawan.getJabatanNama()); // Tampilkan nama jabatan
        System.out.println("Pendidikan          : " + karyawan.getPendidikan()); // Tampilkan pendidikan
        System.out.println("Performa            : " + performa + " / 100"); // Tampilkan performa

        double gajiPokok = karyawan.getGajiPokok(); // Ambil gaji pokok
        double bonusPerforma = performa * 200; // Hitung bonus performa

        double potonganBPJS; // Deklarasi potongan BPJS 
        if (karyawan instanceof KaryawanTetap) { // Jika karyawan tetap
            potonganBPJS = 0.05 * gajiPokok; // Hitung potongan BPJS 5%
        } else { // Jika karyawan kontrak
            potonganBPJS = 0.03 * gajiPokok; // Hitung potongan BPJS 3%
            KaryawanKontrak kontrak = (KaryawanKontrak) karyawan; // Cast ke KaryawanKontrak
            System.out.println("Masa Kontrak        : " + kontrak.getKontrakMulai() + " s/d " + kontrak.getKontrakSelesai()); // Tampilkan masa kontrak
        }
    

    // Hitung tunjangan keluarga langsung
    double tunjanganIstri = (karyawan.getStatusMenikah() == Karyawan.StatusMenikah.MENIKAH) ? 500000 : 0; // Tunjangan istri
    double tunjanganAnak = Math.min(karyawan.getJumlahAnak(), 3) * 250000; // Tunjangan anak

    // Hitung tunjangan jabatan
    double tunjanganJabatan = (karyawan instanceof KaryawanTetap) ? Math.max(0, (6 - karyawan.getJabatanLevel())) * 0.05 * gajiPokok 
                                                                  : Math.max(0, (6 - karyawan.getJabatanLevel())) * 0.03 * gajiPokok; // Menghitung tunjangan jabatan berdasarkan tipe karyawan

    // Hitung tunjangan pendidikan
    double tunjanganPendidikan = switch (karyawan.getPendidikan().toUpperCase()) { // Menghitung tunjangan pendidikan
        case "SMA" -> 30000; // Tunjangan SMA 
        case "D3" -> 60000; // Tunjangan D3 
        case "S1" -> 100000; // Tunjangan S1
        case "S2" -> 150000; // Tunjangan S2
        default -> 0; // Default tidak ada tunjangan
    };

    double totalGaji = karyawan.hitungGajiBersih(performa); // Hitung total gaji bersih

    // Breakdown komponen
    System.out.printf("Gaji Pokok          : Rp %,d%n", (long) gajiPokok); // Tampilkan gaji pokok
    System.out.printf("Tunjangan Jabatan   : Rp %,d%n", (long) tunjanganJabatan); // Tampilkan tunjangan jabatan
    System.out.printf("Tunjangan Pendidikan: Rp %,d%n", (long) tunjanganPendidikan); // Tampilkan tunjangan pendidikan
    System.out.printf("Bonus Performa      : Rp %,d%n", (long) bonusPerforma); // Tampilkan bonus performa
    System.out.printf("Tunjangan Istri     : Rp %,d%n", (long) tunjanganIstri); // Tampilkan tunjangan istri
    System.out.printf("Tunjangan Anak      : Rp %,d%n", (long) tunjanganAnak); // Tampilkan tunjangan anak
    System.out.printf("Potongan BPJS       : Rp %,d%n", (long) potonganBPJS); // Tampilkan potongan BPJS

    System.out.println("----------------------------------------"); // Garis pemisah
    System.out.printf("Total Gaji Bersih   : Rp %,d%n", (long) totalGaji); // Tampilkan total gaji bersih
    System.out.println("----------------------------------------"); // Garis pemisah
}


    private static int safeInt(Scanner scanner) { // Metode untuk membaca integer dengan validasi
        while (true) { // Loop sampai input valid
            try { // Tangani kesalahan parsing
                return Integer.parseInt(scanner.nextLine().trim()); // Baca dan parse input
            } catch (NumberFormatException e) { // Jika parsing gagal
                System.out.print("Masukkan angka yang valid: "); // Prompt ulang
            }
        }
    }

    private static double safeDouble(Scanner scanner) { // Metode untuk membaca double dengan validasi
        while (true) { // Loop sampai input valid
            try { // Tangani kesalahan parsing
                return Double.parseDouble(scanner.nextLine().trim()); // Baca dan parse input
            } catch (NumberFormatException e) { // Jika parsing gagal
                System.out.print("Masukkan angka desimal yang valid: "); // Prompt ulang
            }
        }
    }

    private static Date parseOrToday(String tanggalStr) { // Metode untuk parse tanggal atau gunakan hari ini
        if (tanggalStr == null || tanggalStr.isEmpty()) return new Date(); // Jika kosong, kembalikan tanggal hari ini
        try { // Coba parse tanggal
            return DATE_FMT.parse(tanggalStr); // Parse tanggal
        } catch (ParseException e) { // Jika parsing gagal
            System.out.println("Format tanggal tidak valid, gunakan hari ini."); // Pesan kesalahan
            return new Date(); // Kembalikan tanggal hari ini
        }
    }
}
