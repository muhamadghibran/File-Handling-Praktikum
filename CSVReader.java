import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {
   public static void main(String[] args) {
      String csvFile = "./data/student.csv";
      String line;
      String csvSplitBy = ",";
      int indeks = 0;
      System.out.println("NIM, NAMA, UMUR, PRODI");
      try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
         while ((line = br.readLine()) != null) {
            indeks++;
            if(indeks>1){
               String[] student = line.split(csvSplitBy);
               System.err.println(student[0] + ", " + student[1] + ", ");
            
            } 
         }
      } catch (IOException e) {
         System.err.println("Error reading file: " + e.getMessage());
      }
   }
}
