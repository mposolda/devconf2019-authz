package org.keycloak.quickstarts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.keycloak.common.util.Base64Url;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ImgTest {

    public static void main(String[] args) throws IOException {
        System.out.println("Hello");

        String imgName = "IMG_20190118_082440.jpg";

        String imgStr = readImage(imgName);

        System.out.println(imgStr);

        writeImage(imgName, imgStr);
    }

    private static String readImage(String imgName) throws IOException {
        InputStream is = null;
        ByteArrayOutputStream buffer = null;
        try {
            File f = new File("devconf2019-service/images/" + imgName);
            System.out.println(f.getAbsolutePath() + " " + f.exists());

            is = new FileInputStream(f);
            buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            byte[] imgBytes = buffer.toByteArray();
            return Base64Url.encode(imgBytes);
        } finally {
            if (is != null) {
                is.close();
            }
            if (buffer != null) {
                buffer.close();
            }
        }
    }


    private static void writeImage(String imgName, String imgStr) throws IOException {
        ByteArrayInputStream bis = null;
        FileOutputStream fos = null;
        try {
            File outputFile = new File("/tmp/" + imgName);
            if (outputFile.exists()) {
                System.out.println("Deleting output file " + outputFile.getAbsolutePath() + " as it already exists.");
                outputFile.delete();
            }

            byte[] imgBytes = Base64Url.decode(imgStr);
            bis = new ByteArrayInputStream(imgBytes);

            fos = new FileOutputStream(outputFile);

            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = bis.read(data, 0, data.length)) != -1) {
                fos.write(data, 0, nRead);
            }

        } finally {
            if (bis != null) {
                bis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }
}
