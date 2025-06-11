import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class CSVWriter {

  public static void main(String[] args) {
    String namaFileCSV = "data_mahasiswa_baru.csv";
    Scanner inputPengguna = new Scanner(System.in);
    List<String> daftarDataMahasiswa = new ArrayList<>();

    System.out.println("=== PROGRAM INPUT DATA MAHASISWA KE FILE CSV ===");
    System.out.println("File output: " + namaFileCSV);
    System.out.println("===============================================\n");

    System.out.println("PILIH MODE PENULISAN FILE:");
    System.out.println("1. Buat file baru (hapus data lama jika ada)");
    System.out.println("2. Tambahkan ke file yang sudah ada");
    System.out.print("Masukkan pilihan Anda (1 atau 2): ");

    int pilihanMode = 0;
    boolean inputModeValid = false;

    while (!inputModeValid) {
      try {
        pilihanMode = inputPengguna.nextInt();
        inputPengguna.nextLine();

        if (pilihanMode == 1 || pilihanMode == 2) {
          inputModeValid = true;
        } else {
          System.out.print("Pilihan tidak valid! Masukkan 1 atau 2: ");
        }
      } catch (Exception e) {
        System.out.print("Input harus berupa angka! Masukkan 1 atau 2: ");
        inputPengguna.nextLine(); // Bersihkan input yang salah
      }
    }

    boolean modeTambah = (pilihanMode == 2);

    System.out.println("\n=== MULAI INPUT DATA MAHASISWA ===");
    System.out.println("Petunjuk: Ketik 'SELESAI' pada field NIM untuk mengakhiri input");
    System.out.println("==========================================\n");

    int nomorMahasiswa = 1;

    while (true) {
      System.out.println("--- INPUT MAHASISWA KE-" + nomorMahasiswa + " ---");

      String nim = inputDanValidasiTeks(inputPengguna, "NIM", false);
      if (nim.equalsIgnoreCase("SELESAI")) {
        System.out.println("\nProses input dihentikan oleh pengguna.");
        break;
      }

      String nama = inputDanValidasiTeks(inputPengguna, "Nama Lengkap", false);

      int umur = inputDanValidasiAngka(inputPengguna, "Umur", 15, 40);

      String prodi = inputDanValidasiTeks(inputPengguna, "Program Studi (TI/TE/SI/MI)", true);

      System.out.println("\n--- KONFIRMASI DATA ---");
      System.out.println("NIM: " + nim);
      System.out.println("Nama: " + nama);
      System.out.println("Umur: " + umur + " tahun");
      System.out.println("Program Studi: " + prodi);

      System.out.print("Apakah data sudah benar? (y/n): ");
      String konfirmasi = inputPengguna.nextLine().trim().toLowerCase();

      if (konfirmasi.equals("y") || konfirmasi.equals("yes")) {
        String dataCSV = nim + "," + nama + "," + umur + "," + prodi;
        daftarDataMahasiswa.add(dataCSV);

        System.out.println("✓ Data mahasiswa ke-" + nomorMahasiswa + " berhasil disimpan sementara!");
        nomorMahasiswa++;
      } else {
        System.out.println("Data dibatalkan, silakan input ulang.");
      }

      System.out.println();
    }

    if (!daftarDataMahasiswa.isEmpty()) {
      tulisDataKeFile(namaFileCSV, daftarDataMahasiswa, modeTambah);
    } else {
      System.out.println("Tidak ada data yang diinput untuk disimpan.");
    }

    inputPengguna.close();
    System.out.println("\n=== PROGRAM SELESAI ===");
    System.out.println("Terima kasih telah menggunakan program ini!");
  }

  private static String inputDanValidasiTeks(Scanner scanner, String namaField, boolean upperCase) {
    String input;
    while (true) {
      System.out.print("Masukkan " + namaField + ": ");
      input = scanner.nextLine().trim();

      if (!input.isEmpty()) {
        return upperCase ? input.toUpperCase() : input;
      } else {
        System.out.println("Error: " + namaField + " tidak boleh kosong!");
      }
    }
  }

  private static int inputDanValidasiAngka(Scanner scanner, String namaField, int min, int max) {
    int angka;
    while (true) {
      try {
        System.out.print("Masukkan " + namaField + " (" + min + "-" + max + "): ");
        angka = scanner.nextInt();
        scanner.nextLine(); // Bersihkan buffer

        if (angka >= min && angka <= max) {
          return angka;
        } else {
          System.out.println("Error: " + namaField + " harus antara " + min + " dan " + max + "!");
        }
      } catch (Exception e) {
        System.out.println("Error: " + namaField + " harus berupa angka!");
        scanner.nextLine(); // Bersihkan input yang salah
      }
    }
  }

  private static void tulisDataKeFile(String namaFile, List<String> daftarData, boolean modeTambah) {
    System.out.println("\n=== PROSES PENULISAN KE FILE ===");
    System.out.println("Menyimpan " + daftarData.size() + " record ke file: " + namaFile);

    try (BufferedWriter penulisBuffer = new BufferedWriter(new FileWriter(namaFile, modeTambah))) {

      if (!modeTambah) {
        String header = "NIM,NAMA,UMUR,PRODI";
        penulisBuffer.write(header);
        penulisBuffer.newLine();
        System.out.println("✓ Header CSV berhasil ditulis");
      }

      int nomorRecord = 1;
      for (String dataMahasiswa : daftarData) {
        penulisBuffer.write(dataMahasiswa);
        penulisBuffer.newLine();
        System.out.println("✓ Record " + nomorRecord + " ditulis: " + dataMahasiswa);
        nomorRecord++;
      }

      System.out.println("\n=== HASIL PENULISAN ===");
      System.out.println("✓ Berhasil menyimpan " + daftarData.size() + " record mahasiswa");
      System.out.println("✓ File tersimpan di: " + namaFile);
      System.out.println("✓ Mode: " + (modeTambah ? "Menambah ke file lama" : "File baru"));

    } catch (IOException kesalahan) {
      System.err.println("ERROR: Gagal menulis data ke file!");
      System.err.println("Detail kesalahan: " + kesalahan.getMessage());
    }
  }
}