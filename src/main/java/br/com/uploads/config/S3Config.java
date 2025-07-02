package br.com.uploads.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

  @Value("${aws.region}")
  private String awsRegion;

  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
      .region(Region.of(awsRegion))
      .credentialsProvider(DefaultCredentialsProvider.builder().build())
      .build();
  }

  @Bean
  public S3AsyncClient s3AsyncClient() {
    return S3AsyncClient.builder()
      .region(Region.of(awsRegion))
      .credentialsProvider(DefaultCredentialsProvider.builder().build())
      .build();
  }

}
