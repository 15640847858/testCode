import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FolderToZip {

    // 创建文件夹结构
    public static void createFolders(String caseNumber, List<String> receptionNumbers, Map<String, List<String>> filesMap) throws IOException {
        // 创建主文件夹（案件番号）
        File caseDir = new File(caseNumber);
        if (!caseDir.exists()) {
            caseDir.mkdir();
        }

        // 创建多个子文件夹（受付番号）
        for (String receptionNumber : receptionNumbers) {
            File receptionDir = new File(caseDir, receptionNumber);
            if (!receptionDir.exists()) {
                receptionDir.mkdir();
            }

            // 在子文件夹中创建多个文件（添付文件）
            List<String> fileNames = filesMap.get(receptionNumber);
            if (fileNames != null) {
                for (String fileName : fileNames) {
                    File file = new File(receptionDir, fileName);
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write("This is a dummy content for file " + fileName);
                    }
                }
            }
        }
    }

    // 压缩文件夹到ZIP
    public static void zipDirectory(File folder, File zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zipFolder(folder, folder.getName(), zos);
        }
    }

    private static void zipFolder(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                // 递归处理文件夹
                zipFolder(file, parentFolder + "/" + file.getName(), zos);
            } else {
                // 压缩文件
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(parentFolder + "/" + file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }

                    zos.closeEntry();
                }
            }
        }
    }

    // 获取当前时间字符串
    public static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    public static void main(String[] args) throws IOException {
        // 输入：案件番号、受付番号、添付文件列表
        String caseNumber = "Case123";
        List<String> receptionNumbers = Arrays.asList("Reception001", "Reception002");

        // 每个受付番号对应的文件名列表
        Map<String, List<String>> filesMap = new HashMap<>();
        filesMap.put("Reception001", Arrays.asList("file1.txt", "file2.txt"));
        filesMap.put("Reception002", Arrays.asList("file3.txt", "file4.txt"));

        // 创建文件夹和文件
        createFolders(caseNumber, receptionNumbers, filesMap);

        // 获取当前时间并创建ZIP文件名
        String zipFileName = getCurrentTimestamp() + ".zip";
        File zipFile = new File(zipFileName);

        // 压缩主文件夹
        zipDirectory(new File(caseNumber), zipFile);

        System.out.println("ZIP file created: " + zipFile.getAbsolutePath());
    }
}
