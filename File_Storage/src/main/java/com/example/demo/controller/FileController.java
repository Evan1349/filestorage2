package com.example.demo.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entities.FileData;
import com.example.demo.service.FileDataService;

@RestController
@RequestMapping("/files")
public class FileController {

	@Autowired
	FileDataService fileDataService;
	
	
	@PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("username") String username) {
        try {
            FileData fileData = fileDataService.uploadFile(username, file);
            return ResponseEntity.ok("File uploaded successfully: " + fileData.getFileName());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }
	
	@GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId, @RequestParam("username") String username) {
        Optional<FileData> fileDataOptional = fileDataService.getFileData(fileId);
        if (fileDataOptional.isPresent()) {
            FileData fileData = fileDataOptional.get();
            try {
                Resource resource = fileDataService.load(fileData.getUuid(), username);
                if (resource.exists()) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                            .header(HttpHeaders.CONTENT_TYPE, fileData.getContentType())
                            .body(resource);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body(null);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body(null);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/share")
    public ResponseEntity<String> shareFile(@RequestParam("fileId") String fileId, @RequestParam("fromUser") String fromUser, @RequestParam("toUser") String toUser) {
        try {
            fileDataService.shareFile(fileId, fromUser, toUser);
            return ResponseEntity.ok("File shared successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
