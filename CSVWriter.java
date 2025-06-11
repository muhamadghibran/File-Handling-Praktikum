import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter{
    public static void main(String[] args) {
        String csvFile = "./data/student.csv";
        String[] data = {
            "4,David,23",
            "5,Eva,22",
            "6,Ferdi,21"
        };
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
            for (String line : data) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}