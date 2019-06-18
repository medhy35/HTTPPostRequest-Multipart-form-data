
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Launch {

  public static void main(String[] args) {
    System.out.println("Test Upload");

    String charset = "UTF-8";
    File uploadFile = new File("C:/Users/Mahamadoun/Desktop/001.PNG");
    String requestURL = "http://www.mocky.io/v2/5d08e3c234000041ee5d9b5a";

    try {
      MultipartUtility testrep = new MultipartUtility(requestURL, charset);
      testrep.addHeaderField("User-Agent", "Test Quadrica");
      testrep.addHeaderField("Test-Header", "Header-value");

      testrep.addFormField("description", "Cool Img bro");
      testrep.addFormField("Keywords", "upload, java");

      testrep.addFilePart("file", uploadFile);

      List <String> response = testrep.finish();
      System.out.println("le server repond a ma super REQUETE de OUF par : ");
      for (String line : response) {
        System.out.println(line);
      }
    } catch(IOException e){
            System.err.println(e);
    }

  }
}
