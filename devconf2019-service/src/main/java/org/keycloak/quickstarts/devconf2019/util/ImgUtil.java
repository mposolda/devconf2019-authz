package org.keycloak.quickstarts.devconf2019.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.keycloak.common.util.Base64Url;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ImgUtil {

    public static String readImage(String imgName) throws IOException {
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
}
