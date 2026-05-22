package publication_quality_system.services;

import org.springframework.web.multipart.MultipartFile;

public interface S3FileService {
    String uploadFile(MultipartFile file, String folder);

    void deleteFile(String fileKey);
}
