package com.example.demo.client;

import com.example.demo.dto.GithubFileItem;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.ObjectSummary;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.bmc.objectstorage.responses.ListObjectsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OciObjectStorageClient {

    @Value("${oci.object-storage.namespace}")
    private String namespace;

    @Value("${oci.object-storage.bucket-name}")
    private String bucketName;

    @Value("${oci.object-storage.config-file}")
    private String configFile;

    private final ObjectMapper objectMapper;

    // OCI ObjectStorage 클라이언트 생성
    private ObjectStorage buildClient() throws IOException {
        ConfigFileReader.ConfigFile config =
                ConfigFileReader.parse(configFile, "DEFAULT");

        AuthenticationDetailsProvider provider =
                new ConfigFileAuthenticationDetailsProvider(config);

        return ObjectStorageClient.builder()
                .build(provider);
    }

    // 버킷 내 JSON 파일 목록 조회
    public List<String> listJsonFiles() throws Exception {
        try (ObjectStorage client = buildClient()) {
            ListObjectsRequest request = ListObjectsRequest.builder()
                    .namespaceName(namespace)
                    .bucketName(bucketName)
                    .prefix("github/")          // github/ 폴더 아래 파일만
                    .build();

            ListObjectsResponse response = client.listObjects(request);

            return response.getListObjects().getObjects().stream()
                    .map(ObjectSummary::getName)
                    .filter(name -> name.endsWith(".json"))
                    .toList();
        }
    }

    // OCI에서 JSON 파일 읽기 → List<GithubFileItem> 변환
    public List<GithubFileItem> readFromOci(String objectName) throws Exception {
        try (ObjectStorage client = buildClient()) {
            GetObjectRequest request = GetObjectRequest.builder()
                    .namespaceName(namespace)
                    .bucketName(bucketName)
                    .objectName(objectName)
                    .build();

            GetObjectResponse response = client.getObject(request);

            return Arrays.asList(objectMapper.readValue(response.getInputStream(), GithubFileItem[].class));
        }
    }
}
