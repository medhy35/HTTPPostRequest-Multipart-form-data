

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Sending Multipart HTTP POST request to a web server
 * @author Medhy
 */

public class MultipartUtility {

  private final String boundary;
  private static final String LINE_FEED="\r\n";

  private HttpURLConnection httpConn;
  private String charset;
  private OutputStream outputStream;
  private PrintWriter writer;
  private String fieldName;

  /**
   * HTTP POST request with content type is set
   * to multipart/form-data
   *
   * @param requestURL
   * @param charset
   * @throws IOException
   */

  public MultipartUtility (String requestURL,String charset) throws IOException {

    this.charset = charset;
    boundary = "==="+System.currentTimeMillis()+ "===";
    URL url = new URL(requestURL);
    httpConn = (HttpURLConnection) url.openConnection();
    httpConn.setUseCaches(false);
    httpConn.setDoOutput(true); // indicates POST method
    httpConn.setDoInput(true);

    httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
    httpConn.setRequestProperty("User-Agent", "web client Agent");
    httpConn.setRequestProperty("Test","Bonjour");
    outputStream=httpConn.getOutputStream();
    writer = new PrintWriter(new OutputStreamWriter(outputStream,this.charset),true);
  }

  /**
   * Add a form to the request
   * @param name
   * @param value
   */

  public void addFormField(String name,String value) {
    writer.append("--"+boundary).append(LINE_FEED);
    writer.append("Content-Disposition:form-data; name=\""+name+"\"").append(LINE_FEED);
    writer.append(LINE_FEED);
    writer.append(value).append(LINE_FEED);
    writer.flush();
  }

  /**
   * Add a upload file section to the request
   * @param fielName  correspond a <input type="file" name="..."/>
   * @param uploadFile a file to be uploaded
   * @throws IOException
   */

  public void addFilePart(String fielName, File uploadFile) throws IOException {
    String fileName = uploadFile.getName();
    writer.append("--"+boundary).append(LINE_FEED);
    writer.append("Content-Disposition:form-data; name=\"" + fieldName + "\"; filename=\"" + fielName + "\"")
        .append(LINE_FEED);
    writer.append("Content-Type:"+ URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
    writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
    writer.flush();
    FileInputStream inputStream = new FileInputStream(uploadFile);
    byte[] buffer = new byte[4096];
    int bytesRead = -1;
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      outputStream.write(buffer, 0, bytesRead);
    }
    outputStream.flush();
    inputStream.close();
    writer.append(LINE_FEED);
    writer.flush();
  }

  /**
   * Adds a header field to the request
   * @param name
   * @param value
   */
  public void addHeaderField(String name,String value) {
    writer.append(name+": "+value).append(LINE_FEED);
    writer.flush();
  }

/**
 *  Completes the request and receives response from th server.
 * @return a list of Strings as response in case the server returned
 * status OK, otherwise an exception is thrown
 * @throws IOException
 */

  public List<String> finish() throws IOException {
    List<String> response = new ArrayList<String>();
    writer.append(LINE_FEED).flush();
    writer.append("--"+boundary+"--").append(LINE_FEED);
    writer.close();

    int status = httpConn.getResponseCode();
    if (status==HttpURLConnection.HTTP_OK){
      BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
      String line = null;
      while ((line = reader.readLine()) != null){
        response.add(line);
      }
      reader.close();
      httpConn.disconnect();
    } else {
      throw new IOException("Server returned non-OK Status "+ status);
    }
    return response;
  }

}
