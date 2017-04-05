// Import required java libraries

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UploadServlet extends HttpServlet {

    private boolean isMultipart;
    private String filePath;
    private int maxFileSize = 50 * 1024;
    private int maxMemSize = 4 * 1024;
    private File file;

    private static final Logger logger = LogManager.getLogger("UploadServlet");

    public void init() {
        // Get the file location where it would be stored.
        filePath = getServletContext().getInitParameter("file-upload");
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
            throws ServletException, java.io.IOException {

        Pattern filePattern = Pattern.compile("([^\\s]+(\\.(?i)(|txt|))$)");
        boolean isTXT = false;

        // Check that we have a file upload request
        isMultipart = ServletFileUpload.isMultipartContent(request);
        response.setContentType("text/html");
        java.io.PrintWriter out = response.getWriter();
        if (!isMultipart) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet upload</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<p>No file uploaded</p>");
            out.println("</body>");
            out.println("</html>");
            return;
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // maximum size that will be stored in memory
        factory.setSizeThreshold(maxMemSize);
        // Location to save data that is larger than maxMemSize.

        factory.setRepository(new File("c:\\temp"));

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        // maximum file size to be uploaded.
        upload.setSizeMax(maxFileSize);


        try {
            // Parse the request to get file items.
            List fileItems = upload.parseRequest(request);

            // Process the uploaded file items
            Iterator i = fileItems.iterator();

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet upload</title>");
            out.println("</head>");
            out.println("<body>");
            while (i.hasNext()) {
                FileItem fi = (FileItem) i.next();
                Matcher m = filePattern.matcher(fi.getName());
                if (m.matches()) {
                    isTXT = true;
                    if (!fi.isFormField()) {
                        // Get the uploaded file parameters
                        String fieldName = fi.getFieldName();
                        String fileName = fi.getName();
                        String contentType = fi.getContentType();
                        boolean isInMemory = fi.isInMemory();
                        long sizeInBytes = fi.getSize();


                        UUID uuid = UUID.randomUUID();
                        String randomUUIDString = uuid.toString();

                        file = new File(filePath + "\\" +  "data.txt");


                        fi.write(file);
                        out.println("Uploaded Filename: " + fileName + "<br>");

                    }
                } else {
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>Servlet upload</title>");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<p>Wrong File uploaded</p>");
                    out.println("</body>");
                    out.println("</html>");
                }

            }
            out.println("</body>");
            out.println("</html>");


        } catch (Exception ex) {
            appendToFile(ex);


        }


        File xmlFile = new File("D:\\Random\\settings\\settings.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            NodeList NL;
            Element EL;

            NL = doc.getElementsByTagName("functionToUse");

            for (int a = 0; a < NL.getLength(); a++) {

                Node readNode = NL.item(a);

                if (readNode.getNodeType() == Node.ELEMENT_NODE) {
                    EL = (Element) readNode;

                    String value = EL.getTextContent();

                    if (value.equals("toText")) {
                        logger.info("just uploading the file {}", file);

                    } else if (value.equals("toXML")) {
                        try {
                            new ToXML().changeToXML();
                            file.delete();


                        } catch (Exception ex) {
                            logger.catching(ex);

                        }
                    } else {
                        logger.error("The function to use is not specified");
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, java.io.IOException {

        throw new ServletException("GET method used with " +
                getClass().getName() + ": POST method required.");
    }

    public static void appendToFile(Exception e) {
        try {
            FileWriter fstream = new FileWriter("d:\\errorLog.txt", true);
            BufferedWriter out = new BufferedWriter(fstream);
//            PrintWriter pWriter = new PrintWriter(out, true);

            out.write(e.toString());
            out.close();
        } catch (Exception ie) {
            throw new RuntimeException("Could not write Exception to file", ie);
        }
    }

}