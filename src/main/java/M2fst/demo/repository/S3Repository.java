package M2fst.demo.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.auth.aws.signer.RegionSet;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

@Repository
public class S3Repository {
    private static S3Repository instance;

    private final String accessKeyId;
    private final String secretAccessKey;
    private final String url;
    private final S3Client s3Client;

    public S3Repository(@Value("${s3.accessKeyId}") String accessKeyId, @Value("${s3.secretAccessKey}") String secretAccessKey, @Value("${s3.url}") String endpointOverride) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.url = endpointOverride;
        this.s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.builder()
                                .accessKeyId(accessKeyId)
                                .secretAccessKey(secretAccessKey)
                                .build()))
                .endpointOverride(URI.create(url))
                .region(Region.US_EAST_1)
                .sigv4aSigningRegionSet(RegionSet.GLOBAL)
                .forcePathStyle(true)
                .build();
    }

    public String getUploadFileUrl(String key, String bucket) {
        try (var s3Presigner = S3Presigner.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .endpointOverride(URI.create(url))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build()) {

            PutObjectPresignRequest request = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build())
                    .build();

            return s3Presigner.presignPutObject(request)
                    .url()
                    .toExternalForm()
                    .replace("%2F", "/");
        }
    }

    public String getDownloadFileUrl(String key, String bucket) {
        try (var s3Presigner = S3Presigner.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .endpointOverride(URI.create(url))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build()) {

            GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build())
                    .build();

            return s3Presigner.presignGetObject(request)
                    .url()
                    .toExternalForm()
                    .replace("%2F", "/");
        }
    }


}
