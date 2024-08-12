package com.vincent.aws;


import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.config.aws.s3.S3Service;
import com.vincent.exception.handler.ErrorHandler;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {
    private static final String BUCKET_NAME = "test-bucket";
    private static final String FILE_NAME = "test-file.txt";
    private static final String BASE_DIR_BUILDING = "building";
    private static final String BASE_DIR_FLOOR = "floor";
    private static final String UPLOAD_URL = "https://test-bucket.s3.amazonaws.com/";
    @Mock
    private AmazonS3Client amazonS3Client;
    @Mock
    private MultipartFile multipartFile;
    @InjectMocks
    private S3Service s3Service;

    @Test
    void 이미지_업로드_성공_빌딩() throws IOException {
        // Given
        s3Service.bucket = BUCKET_NAME;
        File file = new File(System.getProperty("user.dir") + "/" + FILE_NAME);

        // When
        when(multipartFile.getOriginalFilename()).thenReturn(FILE_NAME);
        when(multipartFile.getBytes()).thenReturn("file-content".getBytes());
        when(amazonS3Client.getUrl(eq(BUCKET_NAME), any(String.class)))
            .thenReturn(new java.net.URL(UPLOAD_URL + BASE_DIR_BUILDING + "/" + UUID.randomUUID() + "_" + FILE_NAME));

        String resultUrl = s3Service.upload(multipartFile, "Building");

        // Then
        ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(amazonS3Client, times(1)).putObject(putObjectRequestCaptor.capture());
        Assertions.assertThat(putObjectRequestCaptor.getValue().getBucketName()).isEqualTo(BUCKET_NAME);
        Assertions.assertThat(putObjectRequestCaptor.getValue().getFile().getName()).isEqualTo(FILE_NAME);
        Assertions.assertThat(resultUrl).startsWith(UPLOAD_URL + BASE_DIR_BUILDING);
    }

    @Test
    void 이미지_업로드_성공_층() throws IOException {
        // Given
        s3Service.bucket = BUCKET_NAME;
        File file = new File(System.getProperty("user.dir") + "/" + FILE_NAME);

        // When
        when(multipartFile.getOriginalFilename()).thenReturn(FILE_NAME);
        when(multipartFile.getBytes()).thenReturn("file-content".getBytes());
        when(amazonS3Client.getUrl(eq(BUCKET_NAME), any(String.class)))
            .thenReturn(new java.net.URL(UPLOAD_URL + BASE_DIR_FLOOR + "/" + UUID.randomUUID() + "_" + FILE_NAME));

        String resultUrl = s3Service.upload(multipartFile, "Floor");

        // Then
        ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(amazonS3Client, times(1)).putObject(putObjectRequestCaptor.capture());
        Assertions.assertThat(putObjectRequestCaptor.getValue().getBucketName()).isEqualTo(BUCKET_NAME);
        Assertions.assertThat(putObjectRequestCaptor.getValue().getFile().getName()).isEqualTo(FILE_NAME);
        Assertions.assertThat(resultUrl).startsWith(UPLOAD_URL + BASE_DIR_FLOOR);
    }

}
