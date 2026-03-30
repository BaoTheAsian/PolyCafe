package utils;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;

public class FileUtil {

    public static String upload(Part part, String uploadDir) throws IOException {
        String fileName = part.getSubmittedFileName();
        if (fileName == null || fileName.trim().isEmpty()) {
            return null;
        }
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // Thêm timestamp để tránh trùng tên
        String uniqueName = System.currentTimeMillis() + "_" + fileName;
        part.write(uploadDir + File.separator + uniqueName);
        return uniqueName;
    }

    public static boolean delete(String fileName, String uploadDir) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }
        File file = new File(uploadDir + File.separator + fileName);
        return file.exists() && file.delete();
    }
}
