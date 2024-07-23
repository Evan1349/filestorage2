package com.example.demo.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entities.FileData;

import org.springframework.core.io.Resource;

public interface FileDataService {

    FileData uploadFile(String username, MultipartFile multipartFile) throws IOException;

    public Resource load(String uuid, String username) throws Exception;

    void shareFile(String fileId, String fromUser, String toUser);

    Optional<FileData> getFileData(String fileId);

}
