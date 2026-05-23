package publication_quality_system.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import publication_quality_system.base.BaseController;
import publication_quality_system.base.BaseResponse;
import publication_quality_system.services.S3FileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lab-members/files")
public class S3FileController extends BaseController {

    private final S3FileService s3FileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('FILE_UPLOAD')")
    public ResponseEntity<BaseResponse<String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "uploads") String folder) {
        String fileKey = s3FileService.uploadFile(file, folder);
        return success(fileKey, "File uploaded successfully");
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('FILE_DELETE')")
    public ResponseEntity<BaseResponse<Void>> deleteFile(@RequestParam("fileKey") String fileKey) {
        s3FileService.deleteFile(fileKey);
        return success(null, "File deleted successfully");
    }
}
