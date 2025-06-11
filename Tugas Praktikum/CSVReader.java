import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {

  public static void main(String[] args) {
    String namaFileCSV = "students.csv";
    String baris = "";
    String pemisahCSV = ",";
    int totalBarisData = 0;
    int totalBarisKosong = 0;
    boolean adalahBarisHeader = true;
    System.out.println("=== PROGRAM PEMBACA DAN PENGHITUNG BARIS CSV ===");
    System.out.println("File yang dibaca: " + namaFileCSV);
    System.out.println("===============================================\n");

    try (BufferedReader pembacaBuffer = new BufferedReader(new FileReader(namaFileCSV))) {

      System.out.println("STATUS: Memulai pembacaan file...\n");

      while ((baris = pembacaBuffer.readLine()) != null) {
        if (baris.trim().isEmpty()) {
          totalBarisKosong++;
          System.out.println("Ditemukan baris kosong pada baris ke-" + (totalBarisData + totalBarisKosong + 1));
          continue;
        }

        if (adalahBarisHeader) {
          String[] kolomHeader = baris.split(pemisahCSV);
          System.out.println("HEADER DITEMUKAN:");
          System.out.printf("%-8s %-20s %-8s %-12s%n",
              kolomHeader[0], kolomHeader[1], kolomHeader[2], kolomHeader[3]);
          System.out.println("================================================");
          adalahBarisHeader = false;
          continue;
        }

        String[] dataMahasiswa = baris.split(pemisahCSV);

        if (dataMahasiswa.length == 4) {
          System.out.printf("%-8s %-20s %-8s %-12s%n",
              dataMahasiswa[0].trim(),
              dataMahasiswa[1].trim(),
              dataMahasiswa[2].trim(),
              dataMahasiswa[3].trim());

          totalBarisData++;
        } else {
          System.out.println("PERINGATAN: Baris dengan format salah diabaikan - " + baris);
        }
      }

      System.out.println("\n================================================");
      System.out.println("=== RINGKASAN HASIL PEMBACAAN ===");
      System.out.println("Total baris data mahasiswa yang valid: " + totalBarisData);
      System.out.println("Total baris kosong yang ditemukan: " + totalBarisKosong);
      System.out.println("Total baris keseluruhan (termasuk header): " + (totalBarisData + totalBarisKosong + 1));

      if (totalBarisData > 0) {
        System.out.println("\nSTATUS: File berhasil dibaca!");
        System.out.println("Ditemukan " + totalBarisData + " record mahasiswa yang valid.");

        if (totalBarisKosong > 0) {
          System.out.println("Catatan: Terdapat " + totalBarisKosong + " baris kosong dalam file.");
        }
      } else {
        System.out.println("\nSTATUS: Tidak ada data mahasiswa yang valid!");
      }

    } catch (IOException kesalahan) {
      System.err.println("ERROR: Terjadi kesalahan saat membaca file CSV!");
      System.err.println("Detail kesalahan: " + kesalahan.getMessage());
      System.err.println("\nSOLUSI YANG DISARANKAN:");
      System.err.println("1. Pastikan file '" + namaFileCSV + "' ada di direktori yang sama");
      System.err.println("2. Periksa permission file (pastikan file bisa dibaca)");
      System.err.println("3. Pastikan format file adalah CSV yang valid");
    }

    System.out.println("\n=== PROGRAM SELESAI ===");
  }
}