import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Data Access Object untuk Karyawan
public class KaryawanDAO {

    // Cek apakah jabatan boleh diisi kontrak
    public static boolean cekBolehKontrak(int jabatanLevel) throws SQLException {
        try (Connection conn = KoneksiDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT bolehKontrak FROM jabatan WHERE level=?")) { // Ambil data bolehKontrak dari tabel jabatan
            ps.setInt(1, jabatanLevel); // Set parameter level
            ResultSet rs = ps.executeQuery(); // Eksekusi query
            if (rs.next()) { // Jika data ditemukan
                return rs.getBoolean("bolehKontrak"); // Mengembalikan nilai bolehKontrak
            }
        }
        return false; // Default false jika tidak ditemukan
    }

    // CREATE 
    public static void createKaryawan(String nama, int jabatanLevel, String jabatanNama, String pendidikan, String tipe, double gajiPokok, Date tanggalMasuk,
                                      Date kontrakMulai, Date kontrakSelesai, Karyawan.StatusMenikah statusMenikah, int jumlahAnak) throws SQLException {
        if ("Kontrak".equalsIgnoreCase(tipe) && !cekBolehKontrak(jabatanLevel)) { // Cek apakah jabatan boleh diisi kontrak
            System.out.println("Jabatan level " + jabatanLevel + " (" + jabatanNama + ") tidak boleh diisi tenaga kontrak."); // Menampilkan pesan kesalahan
            return; // Keluar dari method tanpa menambah data
        }

        String sql = "INSERT INTO karyawan (id, nama, jabatanLevel, jabatanNama, pendidikan, tipe, gajiPokok, tanggalMasuk, kontrakMulai, kontrakSelesai, statusMenikah, jumlahAnak) " +
                     "VALUES (UUID(),?,?,?,?,?,?,?,?,?,?,?)"; // UUID untuk id unik, ? untuk prepared statement

        try (Connection conn = KoneksiDatabase.getConnection(); // Mengambil koneksi database
             PreparedStatement ps = conn.prepareStatement(sql)) { // Mempersiapkan statement
            ps.setString(1, nama); // Set parameter nama
            ps.setInt(2, jabatanLevel); // Set parameter jabatanLevel
            ps.setString(3, jabatanNama); // Set parameter jabatanNama
            ps.setString(4, pendidikan); // Set parameter pendidikan
            ps.setString(5, tipe); // Set parameter tipe
            ps.setDouble(6, gajiPokok); // Set parameter gajiPokok
            ps.setDate(7, new java.sql.Date(tanggalMasuk.getTime())); // Set parameter tanggalMasuk

            if ("Kontrak".equalsIgnoreCase(tipe)) { // Jika tipe kontrak, set tanggal kontrak
                ps.setDate(8, new java.sql.Date(kontrakMulai.getTime())); // Set parameter kontrakMulai
                ps.setDate(9, new java.sql.Date(kontrakSelesai.getTime())); // Set parameter kontrakSelesai
            } else { // Jika tipe tetap, set null untuk tanggal kontrak
                ps.setNull(8, Types.DATE); // Set parameter kontrakMulai bernilai null
                ps.setNull(9, Types.DATE); // Set parameter kontrakSelesai bernilai null
            }

            ps.setString(10, statusMenikah.getLabel()); // Set parameter statusMenikah
            ps.setInt(11, jumlahAnak); // Set parameter jumlahAnak

            ps.executeUpdate(); // Eksekusi pemasukan data
            System.out.println("Data karyawan berhasil ditambahkan."); // Pesan sukses
        } catch (SQLException e) { // Tangani kesalahan SQL
            System.out.println("Gagal menambahkan karyawan: " + e.getMessage()); // Pesan kesalahan
        }
    }

    // READ ALL
    public static List<Karyawan> readKaryawan() throws SQLException { // Mengembalikan daftar semua karyawan
        List<Karyawan> daftar = new ArrayList<>(); // Inisialisasi list karyawan
        try (Connection conn = KoneksiDatabase.getConnection(); Statement stmt = conn.createStatement(); // Membuat statement
             ResultSet rs = stmt.executeQuery("SELECT * FROM karyawan")) { // Eksekusi query untuk mengambil semua data karyawan
            System.out.println("\n=== DATA KARYAWAN ==="); // Header data karyawan
            while (rs.next()) { // Iterasi setiap baris hasil query
                tampilkanData(rs); // Tampilkan data karyawan

                int jabatanLevel = rs.getInt("jabatanLevel"); // Ambil data jabatanLevel
                String jabatanNama = rs.getString("jabatanNama"); // Ambil data jabatanNama
                String pendidikan = rs.getString("pendidikan"); // Ambil data pendidikan   
                String tipe = rs.getString("tipe"); // Ambil data tipe
                double gajiPokok = rs.getDouble("gajiPokok"); // Ambil data gajiPokok
                Date tanggalMasuk = rs.getDate("tanggalMasuk"); // Ambil data tanggalMasuk
                Date kontrakMulai = rs.getDate("kontrakMulai"); // Ambil data kontrakMulai
                Date kontrakSelesai = rs.getDate("kontrakSelesai"); // Ambil data kontrakSelesai

                String statusStr = rs.getString("statusMenikah"); // Ambil data statusMenikah
                Karyawan.StatusMenikah status = Karyawan.StatusMenikah.fromString(statusStr); // Konversi string ke enum StatusMenikah
                int jumlahAnak = rs.getInt("jumlahAnak"); // Ambil data jumlahAnak

                if ("Tetap".equalsIgnoreCase(tipe)) { // Jika tipe tetap, buat objek KaryawanTetap
                    daftar.add(new KaryawanTetap(rs.getString("nama"), jabatanLevel, jabatanNama,
                                             pendidikan, gajiPokok, tanggalMasuk, status, jumlahAnak)); // Tambah ke daftar
                } else { // Jika tipe kontrak, buat objek KaryawanKontrak
                    daftar.add(new KaryawanKontrak(rs.getString("nama"), jabatanLevel, jabatanNama, 
                                               pendidikan, gajiPokok, tanggalMasuk,
                                               kontrakMulai, kontrakSelesai, status, jumlahAnak)); // Tambah ke daftar
                }
            }
        } catch (SQLException e) { // Tangani kesalahan SQL
            System.out.println("Gagal membaca data karyawan: " + e.getMessage()); // Pesan kesalahan
        }
        return daftar; // Kembalikan daftar karyawan
    }

    // READ BY NAMA
    public static void readKaryawanByNama(String nama) throws SQLException { // Mencari karyawan berdasarkan nama
        try (Connection conn = KoneksiDatabase.getConnection(); // Mengambil koneksi database
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM karyawan WHERE nama=?")) { // Mempersiapkan statement dengan parameter nama
            ps.setString(1, nama); // Set parameter nama
            ResultSet rs = ps.executeQuery(); // Eksekusi query
            if (rs.next()) { // Jika data ditemukan
                tampilkanData(rs); // Tampilkan data karyawan
            } else { // Jika data tidak ditemukan
                System.out.println("Karyawan tidak ditemukan."); // Pesan tidak ditemukan
            }
        } catch (SQLException e) { // Tangani kesalahan SQL
            System.out.println("Gagal membaca data karyawan: " + e.getMessage()); // Pesan kesalahan
        }
    }

    // READ BY TIPE
    public static void readKaryawanByTipe(String tipe) throws SQLException { // Mencari karyawan berdasarkan tipe
        try (Connection conn = KoneksiDatabase.getConnection(); // Mengambil koneksi database
            PreparedStatement ps = conn.prepareStatement("SELECT nama FROM karyawan WHERE tipe=?")) { // Mempersiapkan statement dengan parameter tipe
            ps.setString(1, tipe); // Set parameter tipe
            ResultSet rs = ps.executeQuery(); // Eksekusi query
            System.out.println("\n=== DAFTAR KARYAWAN " + tipe.toUpperCase() + " ==="); // Header daftar karyawan berdasarkan tipe
            boolean ada = false; // Flag untuk mengecek ada tidaknya data
            while (rs.next()) { // Iterasi setiap baris hasil query
                System.out.println("- " + rs.getString("nama")); // Tampilkan nama karyawan
                ada = true; // Set flag ada menjadi true
            }
            if (!ada) { // Jika tidak ada data
                System.out.println("Tidak ada karyawan dengan tipe " + tipe); // Menampilkan pesan jika tidak ada data
            }
        } catch (SQLException e) { // Tangani kesalahan SQL
            System.out.println("Gagal membaca data karyawan: " + e.getMessage()); // Pesan kesalahan
        }
    }


    // Helper tampilkan data
    private static void tampilkanData(ResultSet rs) throws SQLException {
        String tipe = rs.getString("tipe"); // Ambil data tipe
        System.out.println("=== DATA KARYAWAN ==="); // Header data karyawan
        System.out.println("Nama              : " + rs.getString("nama")); // Tampilkan nama
        System.out.println("Level Jabatan     : " + rs.getInt("jabatanLevel")); // Tampilkan level jabatan
        System.out.println("Nama Jabatan      : " + rs.getString("jabatanNama")); // Tampilkan nama jabatan
        System.out.println("Pendidikan        : " + rs.getString("pendidikan")); // Tampilkan pendidikan
        System.out.println("Tipe              : " + tipe); // Tampilkan tipe
        System.out.println("Gaji Pokok        : Rp " + rs.getDouble("gajiPokok")); // Tampilkan gaji pokok
        System.out.println("Tanggal Masuk     : " + rs.getDate("tanggalMasuk")); // Tampilkan tanggal masuk
        if ("Kontrak".equalsIgnoreCase(tipe)) { // Jika tipe kontrak, tampilkan masa kontrak
            System.out.println("Masa Kontrak      : " + rs.getDate("kontrakMulai") + 
                               " s/d " + rs.getDate("kontrakSelesai")); // Tampilkan masa kontrak
        }
        System.out.println("Status Menikah    : " + rs.getString("statusMenikah")); // Tampilkan status menikah
        System.out.println("Jumlah Anak       : " + rs.getInt("jumlahAnak")); // Tampilkan jumlah anak
        System.out.println("------------------------------"); // Separator
    }

    // UPDATE gaji pokok
    public static void updateGajiPokok(String nama, double gajiBaru) throws SQLException { // Update gaji pokok berdasarkan nama
        try (Connection conn = KoneksiDatabase.getConnection(); // Mengambil koneksi database
             PreparedStatement ps = conn.prepareStatement("UPDATE karyawan SET gajiPokok=? WHERE nama=?")) { // Mempersiapkan statement dengan parameter gajiPokok dan nama
            ps.setDouble(1, gajiBaru); // Set parameter gajiPokok
            ps.setString(2, nama); // Set parameter nama
            int rows = ps.executeUpdate(); // Eksekusi update
            if (rows > 0) { // Jika ada baris yang terupdate
                System.out.println("Gaji pokok karyawan berhasil diupdate."); // Pesan sukses
            } else { // Jika tidak ada baris yang terupdate
                System.out.println("Karyawan tidak ditemukan."); // Pesan tidak ditemukan
            }
        } catch (SQLException e) { // Tangani kesalahan SQL
            System.out.println("Gagal mengupdate gaji pokok: " + e.getMessage()); // Pesan kesalahan
        }
    }

    // UPDATE status keluarga
    public static void updateStatusKeluarga(String nama, Karyawan.StatusMenikah status, int jumlahAnak) throws SQLException { // Update status keluarga berdasarkan nama
        try (Connection conn = KoneksiDatabase.getConnection(); // Mengambil koneksi database
             PreparedStatement ps = conn.prepareStatement("UPDATE karyawan SET statusMenikah=?, jumlahAnak=? WHERE nama=?")) { // Mempersiapkan statement dengan parameter statusMenikah, jumlahAnak, dan nama
            ps.setString(1, status.getLabel()); // Set parameter statusMenikah
            ps.setInt(2, jumlahAnak); // Set parameter jumlahAnak
            ps.setString(3, nama); // Set parameter nama
            int rows = ps.executeUpdate(); // Eksekusi update
            if (rows > 0) { // Jika ada baris yang terupdate
                System.out.println("Status keluarga berhasil diupdate."); // Pesan sukses
            } else { // Jika tidak ada baris yang terupdate
                System.out.println("Karyawan tidak ditemukan."); // Pesan tidak ditemukan
            }
        }
    }

    // DELETE
    public static void deleteKaryawan(String nama) throws SQLException { // Hapus karyawan berdasarkan nama
        try (Connection conn = KoneksiDatabase.getConnection(); // Mengambil koneksi database
             PreparedStatement ps = conn.prepareStatement("DELETE FROM karyawan WHERE nama=?")) { // Mempersiapkan statement dengan parameter nama
            ps.setString(1, nama); // Set parameter nama
            int rows = ps.executeUpdate(); // Eksekusi delete
            if (rows > 0) { // Jika ada baris yang terhapus
                System.out.println("Data karyawan berhasil dihapus."); // Pesan sukses
            } else { // Jika tidak ada baris yang terhapus
                System.out.println("Karyawan tidak ditemukan."); // Pesan tidak ditemukan
            }
        } catch (SQLException e) { // Tangani kesalahan SQL
            System.out.println("Gagal menghapus karyawan: " + e.getMessage()); // Pesan kesalahan
        }
    }

    // FIND BY NAME -> return object Karyawan
    public static Karyawan findKaryawanByName(String nama) throws SQLException { // Mencari karyawan berdasarkan nama dan mengembalikan objek Karyawan
        try (Connection conn = KoneksiDatabase.getConnection(); // Mengambil koneksi database
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM karyawan WHERE LOWER(nama) LIKE ?")) { // Mempersiapkan statement dengan parameter nama (case insensitive)
            ps.setString(1, "%" + nama.toLowerCase().trim() + "%"); // Set parameter nama dengan wildcard untuk pencarian sebagian
            ResultSet rs = ps.executeQuery(); // Eksekusi query
            if (rs.next()) { // Jika data ditemukan
                int jabatanLevel = rs.getInt("jabatanLevel"); // Ambil data jabatanLevel
                String jabatanNama = rs.getString("jabatanNama"); // Ambil data jabatanNama
                String pendidikan = rs.getString("pendidikan"); // Ambil data pendidikan
                String tipe = rs.getString("tipe"); // Ambil data tipe
                double gajiPokok = rs.getDouble("gajiPokok"); // Ambil data gajiPokok
                Date tanggalMasuk = rs.getDate("tanggalMasuk"); // Ambil data tanggalMasuk
                Date kontrakMulai = rs.getDate("kontrakMulai"); // Ambil data kontrakMulai
                Date kontrakSelesai = rs.getDate("kontrakSelesai"); // Ambil data kontrakSelesai

                String statusStr = rs.getString("statusMenikah"); // Ambil data statusMenikah
                Karyawan.StatusMenikah status = Karyawan.StatusMenikah.fromString(statusStr); // Konversi string ke enum StatusMenikah
                int jumlahAnak = rs.getInt("jumlahAnak"); // Ambil data jumlahAnak

                if ("Tetap".equalsIgnoreCase(tipe)) { // Jika tipe tetap, buat objek KaryawanTetap
                    return new KaryawanTetap(nama, jabatanLevel, jabatanNama, pendidikan, gajiPokok, tanggalMasuk, status, jumlahAnak); // Kembalikan objek KaryawanTetap
                } else { // Jika tipe kontrak, buat objek KaryawanKontrak
                    return new KaryawanKontrak(nama, jabatanLevel, jabatanNama, pendidikan,gajiPokok, tanggalMasuk, kontrakMulai, kontrakSelesai, status, jumlahAnak); // Kembalikan objek KaryawanKontrak
                }
            }
        } catch (SQLException e) { // Tangani kesalahan SQL
            System.out.println("Gagal mencari karyawan: " + e.getMessage()); // Pesan kesalahan
        }
        return null; // Kembalikan null jika tidak ditemukan
    }
}