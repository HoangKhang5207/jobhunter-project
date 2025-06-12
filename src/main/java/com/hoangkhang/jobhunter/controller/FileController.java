package com.hoangkhang.jobhunter.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hoangkhang.jobhunter.domain.response.file.ResUploadFileDTO;
import com.hoangkhang.jobhunter.exception.custom.StorageException;
import com.hoangkhang.jobhunter.service.FileService;
import com.hoangkhang.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    @Value("${khang.upload-file.base-uri}")
    private String baseURI;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {
        // validate file
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty, please select a file to upload");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = List.of("jpg", "png", "jpeg", "pdf", "doc", "docx");
        boolean isValidExtension = allowedExtensions.stream()
                .anyMatch(ext -> fileName != null && fileName.toLowerCase().endsWith(ext));

        if (!isValidExtension) {
            throw new StorageException("Invalid file type, please select a valid file in " + allowedExtensions);
        }

        // create a directory if it does not exist
        this.fileService.createDirectory(baseURI + folder);

        // store file
        String uploadFile = this.fileService.storeFile(file, folder);

        ResUploadFileDTO res = new ResUploadFileDTO(uploadFile, Instant.now());

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/files")
    @ApiMessage("Download single file")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder)
            throws URISyntaxException, FileNotFoundException, StorageException {
        if (fileName == null || folder == null) {
            throw new StorageException("File name or folder is empty, please select a file to download");
        }

        // check file exists (and not directory)
        long fileLength = this.fileService.getFileLength(fileName, folder);
        if (fileLength == 0) {
            throw new StorageException(
                    "File with name = " + fileName + " not found, please check the file name and folder");
        }

        // download file
        InputStreamResource resource = this.fileService.getResource(fileName, folder);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
