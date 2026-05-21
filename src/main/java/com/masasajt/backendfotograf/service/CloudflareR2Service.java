package com.masasajt.backendfotograf.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CloudflareR2Service {
    private final S3Client s3Client;
    private final String bucketName;
    private final String publicURL;

    public CloudflareR2Service(S3Client s3Client,
                               @Value("${cloudflare.r2.bucket-name}") String bucketName,
                               @Value("${cloudflare.r2.public-url}") String publicURL){
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.publicURL = publicURL;
    }


    public String uploadImage(MultipartFile file){
        if(file.isEmpty()){
            throw new IllegalArgumentException("Fajl ne moze da bude prazan!");
        }
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName != null && originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf(".")) : "";

        String uniqueFileName = UUID.randomUUID().toString() + extension;

        try{
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName)
                    .key(uniqueFileName).contentType(file.getContentType()).build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Umesto ovoga:
            // return publicURL + "/" + uniqueFileName;

            // Možeš staviti sigurniju proveru:
            String cleanPublicUrl = publicURL.endsWith("/") ? publicURL : publicURL + "/";
            return cleanPublicUrl + uniqueFileName;
        }catch (IOException e){
            throw new RuntimeException("Greska pri ucitavanju ulaznog fajla: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Greska pri uploadu na Cloudflare R2: " + e);
        }
    }

    // DODAJ OVU METODU U CloudflareR2Service.java
    public List<String> uploadMultipleImages(MultipartFile[] files) {
        List<String> uploadedUrls = new ArrayList<>();

        if (files == null || files.length == 0) {
            return uploadedUrls;
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String url = uploadImage(file); // Koristimo tvoju staru metodu za svaku sliku
                uploadedUrls.add(url);
            }
        }

        return uploadedUrls;
    }

    public void deleteImageByUrl(String fullUrl) {
        if (fullUrl == null || fullUrl.trim().isEmpty()) {
            System.out.println("URL je prazan, preskačem brisanje sa R2.");
            return;
        }

        try {
            // Izvlačimo jedinstveno ime fajla iz URL-a (sve posle poslednjeg karaktera '/')
            String fileKey = fullUrl.substring(fullUrl.lastIndexOf("/") + 1);

            software.amazon.awssdk.services.s3.model.DeleteObjectRequest deleteObjectRequest =
                    software.amazon.awssdk.services.s3.model.DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileKey)
                            .build();

            s3Client.deleteObject(deleteObjectRequest);
            System.out.println("Uspešno obrisan fajl sa Cloudflare R2. Key: " + fileKey);
        } catch (Exception e) {
            // Logujemo grešku, ali ne prekidamo izvršavanje da bi aplikacija obrisala bazu
            System.err.println("Greška prilikom brisanja fajla sa R2 za URL [" + fullUrl + "]: " + e.getMessage());
        }
    }
}
