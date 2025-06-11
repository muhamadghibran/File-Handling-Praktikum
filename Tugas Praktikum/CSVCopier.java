import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class CSVCopier {

    public static void main(String[] args) {
        Scanner inputPengguna = new Scanner(System.in);

        System.out.println("=== PROGRAM PENYALIN FILE CSV ===");
        System.out.println("Program ini akan menyalin data dari satu file CSV ke file CSV lain");
        System.out.println("=====================================================\n");

        try {
            String fileSumber = dapatkanNamaFileValid(inputPengguna, "SUMBER");

            String fileTujuan = dapatkanNamaFileValid(inputPengguna, "TUJUAN");

            if (!apakahFileBisaDibaca(fileSumber)) {
                System.err.println("ERROR: File sumber '" + fileSumber + "' tidak ditemukan atau tidak dapat dibaca!");
                System.err.println("Pastikan file ada dan memiliki permission yang tepat.");
                return;
            }

            if (Files.exists(Paths.get(fileTujuan))) {
                System.out.println("\nPERINGATAN: File tujuan '" + fileTujuan + "' sudah ada!");
                System.out.print("Apakah Anda yakin ingin menimpanya? (ya/tidak): ");
                String konfirmasi = inputPengguna.nextLine().trim().toLowerCase();

                if (!konfirmasi.equals("ya") && !konfirmasi.equals("y")) {
                    System.out.println("Operasi dibatalkan oleh pengguna.");
                    return;
                }
            }

            int modePenyalinan = pilihModePenyalinan(inputPengguna);

            HasilPenyalinan hasil = salinFileCSV(fileSumber, fileTujuan, modePenyalinan);

            if (hasil.berhasil) {
                tampilkanHasilBerhasil(fileSumber, fileTujuan, hasil);
            } else {
                System.err.println("GAGAL: Penyalinan tidak berhasil dilakukan.");
            }

        } catch (Exception kesalahan) {
            System.err.println("ERROR TIDAK TERDUGA: " + kesalahan.getMessage());
            kesalahan.printStackTrace();
        } finally {
            inputPengguna.close();
        }
    }

    private static String dapatkanNamaFileValid(Scanner scanner, String jenisFile) {
        String namaFile;
        while (true) {
            System.out.print("Masukkan nama file " + jenisFile + " (dengan atau tanpa ekstensi .csv): ");
            namaFile = scanner.nextLine().trim();

            if (namaFile.isEmpty()) {
                System.out.println("Nama file tidak boleh kosong! Silakan coba lagi.");
                continue;
            }

            if (!namaFile.toLowerCase().endsWith(".csv")) {
                namaFile += ".csv";
                System.out.println("Info: Ekstensi .csv ditambahkan otomatis -> " + namaFile);
            }

            break;
        }
        return namaFile;
    }

    private static boolean apakahFileBisaDibaca(String namaFile) {
        Path pathFile = Paths.get(namaFile);
        return Files.exists(pathFile) && Files.isReadable(pathFile) && !Files.isDirectory(pathFile);
    }

    private static int pilihModePenyalinan(Scanner scanner) {
        System.out.println("\n=== PILIH MODE PENYALINAN ===");
        System.out.println("1. Salin semua data (termasuk header)");
        System.out.println("2. Salin hanya data (skip header)");
        System.out.println("3. Salin dengan filter validasi");

        int pilihan;
        while (true) {
            try {
                System.out.print("Masukkan pilihan Anda (1-3): ");
                pilihan = scanner.nextInt();
                scanner.nextLine();

                if (pilihan >= 1 && pilihan <= 3) {
                    System.out.println("Mode dipilih: " + getDeskripsiMode(pilihan));
                    break;
                } else {
                    System.out.println("Pilihan harus antara 1-3!");
                }
            } catch (Exception e) {
                System.out.println("Input harus berupa angka!");
                scanner.nextLine();
            }
        }
        return pilihan;
    }

    private static String getDeskripsiMode(int mode) {
        switch (mode) {
            case 1:
                return "Salin Semua Data";
            case 2:
                return "Salin Tanpa Header";
            case 3:
                return "Salin dengan Validasi";
            default:
                return "Mode Tidak Dikenal";
        }
    }

    private static HasilPenyalinan salinFileCSV(String fileSumber, String fileTujuan, int mode) {
        HasilPenyalinan hasil = new HasilPenyalinan();
        boolean adalahBarisHeader = true;

        System.out.println("\n=== MEMULAI PROSES PENYALINAN ===");
        System.out.println("Dari: " + fileSumber);
        System.out.println("Ke: " + fileTujuan);
        System.out.println("Mode: " + getDeskripsiMode(mode));
        System.out.println("================================");

        try (BufferedReader pembaca = new BufferedReader(new FileReader(fileSumber));
                BufferedWriter penulis = new BufferedWriter(new FileWriter(fileTujuan))) {

            String baris;

            while ((baris = pembaca.readLine()) != null) {
                boolean harusSalinBaris = false;

                switch (mode) {
                    case 1:
                        harusSalinBaris = true;
                        break;

                    case 2:
                        if (adalahBarisHeader) {
                            adalahBarisHeader = false;
                            System.out.println("Header diabaikan: " + baris);
                            continue;
                        }
                        harusSalinBaris = true;
                        break;

                    case 3:
                        if (adalahBarisHeader) {
                            harusSalinBaris = true;
                            adalahBarisHeader = false;
                            System.out.println("Header disalin: " + baris);
                        } else {
                            String[] kolom = baris.split(",");
                            if (kolom.length == 4 && !baris.trim().isEmpty()) {
                                boolean semuaKolomValid = true;
                                for (String k : kolom) {
                                    if (k.trim().isEmpty()) {
                                        semuaKolomValid = false;
                                        break;
                                    }
                                }

                                if (semuaKolomValid) {
                                    harusSalinBaris = true;
                                } else {
                                    hasil.barisError++;
                                    System.out.println("Baris dengan kolom kosong diabaikan: " + baris);
                                }
                            } else {
                                hasil.barisError++;
                                System.out.println("Baris dengan format salah diabaikan: " + baris);
                            }
                        }
                        break;
                }

                if (harusSalinBaris) {
                    penulis.write(baris);
                    penulis.newLine();
                    hasil.barisBerhasil++;

                    if (hasil.barisBerhasil % 5 == 0) {
                        System.out.print(".");
                    }
                }
            }

            System.out.println("\n\nProses penyalinan selesai!");
            hasil.berhasil = true;

        } catch (IOException kesalahan) {
            System.err.println("ERROR saat menyalin file: " + kesalahan.getMessage());
            hasil.berhasil = false;
        }

        return hasil;
    }

    private static void tampilkanHasilBerhasil(String fileSumber, String fileTujuan, HasilPenyalinan hasil) {
        System.out.println("\n=== HASIL PENYALINAN ===");
        System.out.println("✓ PENYALINAN BERHASIL!");
        System.out.println("File sumber: " + fileSumber);
        System.out.println("File tujuan: " + fileTujuan);
        System.out.println("Total baris berhasil disalin: " + hasil.barisBerhasil);

        if (hasil.barisError > 0) {
            System.out.println("Total baris error yang diabaikan: " + hasil.barisError);
        }

        tampilkanInfoFile(fileSumber, fileTujuan);
    }

    private static void tampilkanInfoFile(String fileSumber, String fileTujuan) {
        try {
            Path pathSumber = Paths.get(fileSumber);
            Path pathTujuan = Paths.get(fileTujuan);

            long ukuranSumber = Files.size(pathSumber);
            long ukuranTujuan = Files.size(pathTujuan);

            System.out.println("\n=== INFORMASI FILE ===");
            System.out.printf("Ukuran file sumber: %,d bytes%n", ukuranSumber);
            System.out.printf("Ukuran file tujuan: %,d bytes%n", ukuranTujuan);

            if (ukuranSumber == ukuranTujuan) {
                System.out.println("✓ Ukuran file identik - penyalinan sempurna!");
            } else {
                double persentase = ((double) ukuranTujuan / ukuranSumber) * 100;
                System.out.printf("ℹ Ukuran berbeda - file tujuan %.1f%% dari ukuran asli%n", persentase);
            }

        } catch (IOException e) {
            System.out.println("Tidak dapat menampilkan informasi ukuran file.");
        }
    }

    static class HasilPenyalinan {
        boolean berhasil = false;
        int barisBerhasil = 0;
        int barisError = 0;
    }
}