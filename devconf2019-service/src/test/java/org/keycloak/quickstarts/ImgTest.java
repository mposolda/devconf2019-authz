package org.keycloak.quickstarts;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.keycloak.common.util.Base64Url;
import org.keycloak.quickstarts.devconf2019.util.ImgUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ImgTest {

    public static void main(String[] args) throws IOException {
        System.out.println("Hello");

        String imgName = "IMG_20190118_082440.jpg";

        String imgStr = ImgUtil.readImage(imgName);

        System.out.println(imgStr);

        writeImage(imgName, imgStr);
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
