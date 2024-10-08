package com.vincent.config.aws.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.exception.handler.ErrorHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷

    public String BUILDING_IMG_DIR = "building";
    public String FLOOR_IMG_DIR = "floor";
    public String SPACE_IMG_DIR = "space";
    public String SOCKET_IMG_DIR = "socket";

    // S3 파일 업로드
    public String upload(MultipartFile multipartFile, String type) throws IOException {
        File convertFile = convert(multipartFile)
            .orElseThrow(
                () -> new ErrorHandler(ErrorStatus.IMAGE_CONVERT_ERROR)); // 파일을 변환할 수 없으면 에러

        String baseDir = switch (type) {
            case "Building" -> BUILDING_IMG_DIR;
            case "Floor" -> FLOOR_IMG_DIR;
            case "Space" -> SPACE_IMG_DIR;
            case "Socket" -> SOCKET_IMG_DIR;
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        };

        String fileName = baseDir + "/" + UUID.randomUUID() + "_" + convertFile.getName();

        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, convertFile));

        String uploadImageUrl = amazonS3Client.getUrl(bucket, fileName).toString();

        convertFile.delete();
        return uploadImageUrl;
    }

    public void uploadLog(String logPath, String keyName) {
        File file = new File(logPath);
        if (file.exists()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, keyName, file));
        } else {
            System.out.println("파일을 찾을 수 없습니다: " + logPath);
        }
    }

    public String findByUrl(String url) {
        return amazonS3Client.getUrl(bucket, url).toString();
    }

    // S3 파일 삭제
    public void delete(String path) {
        amazonS3Client.deleteObject(bucket, path);
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(
            System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(
                convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

}
