import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FolderToZip {

    // フォルダ構造を作成する
    public static void createFolders(String baseDir, String caseNumber, List<String> receptionNumbers, Map<String, List<String>> filesMap) throws IOException {
        // 主フォルダ（案件番号）を作成
        File caseDir = new File(baseDir, caseNumber);
        if (!caseDir.exists()) {
            caseDir.mkdirs();  // 必要な親フォルダも作成
        }

        // 複数のサブフォルダ（受付番号）を作成
        for (String receptionNumber : receptionNumbers) {
            File receptionDir = new File(caseDir, receptionNumber);
            if (!receptionDir.exists()) {
                receptionDir.mkdirs();
            }

            // サブフォルダに複数のファイル（添付ファイル）を作成
            List<String> fileNames = filesMap.get(receptionNumber);
            if (fileNames != null) {
                for (String fileName : fileNames) {
                    // 旧ファイルのパス
                    File sourceFile = new File("D:\\tmg\\ebd\\temp\\" + fileName);
                    
                    // 新しいフォルダにコピーする先のファイルパス
                    File destinationFile = new File(receptionDir, fileName);

                    Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    // フォルダをZIPに圧縮する
    public static void zipDirectory(File folder, File zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zipFolder(folder, folder.getName(), zos);
        }
    }

    private static void zipFolder(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                // フォルダを再帰的に処理する
                zipFolder(file, parentFolder + "/" + file.getName(), zos);
            } else {
                // ファイルを圧縮する
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

    // 指定されたフォルダとその中身を削除する
    public static void deleteDirectory(File folder) throws IOException {
        File[] files = folder.listFiles();
        if (files != null) { // 空のフォルダではない場合
            for (File file : files) {
                if (file.isDirectory()) {
                    // サブフォルダを再帰的に削除
                    deleteDirectory(file);
                } else {
                    // ファイルを削除
                    file.delete();
                }
            }
        }
        folder.delete();  // 最後にフォルダ自体を削除
    }

    // 現在の日時の文字列を取得する
    public static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    public static void main(String[] args) throws IOException {
        // 基準ディレクトリ
        String baseDir = "D:\\tmg\\ebd\\temp";

        // 入力: 案件番号、受付番号、添付ファイルリスト
        String caseNumber = "Case123";
        List<String> receptionNumbers = Arrays.asList("Reception001", "Reception002");

        // 各受付番号に対応するファイル名リスト
        Map<String, List<String>> filesMap = new HashMap<>();
        filesMap.put("Reception001", Arrays.asList("file1.txt", "file2.txt"));
        filesMap.put("Reception002", Arrays.asList("file3.txt", "file4.txt"));

        // フォルダとファイルを作成
        createFolders(baseDir, caseNumber, receptionNumbers, filesMap);

        // 現在の日時を取得してZIPファイル名を作成
        String zipFileName = getCurrentTimestamp() + ".zip";
        File zipFile = new File(baseDir, zipFileName);

        // 主フォルダを圧縮
        zipDirectory(new File(baseDir, caseNumber), zipFile);

        // 圧縮後、元のフォルダとその中身を削除
        deleteDirectory(new File(baseDir, caseNumber));

        System.out.println("ZIPファイルが作成され、元のフォルダは削除されました: " + zipFile.getAbsolutePath());
    }
}
