package com.example.demo.serviceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.UrlResource;
import com.example.demo.config.FileUploadConfig;
import com.example.demo.entities.FileData;
import com.example.demo.entities.User;
import com.example.demo.repository.FiledataRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FileDataService;

import org.springframework.core.io.Resource;

@Service
public class FileDataServiceImpl implements FileDataService {
	
	
	@Autowired
	FiledataRepository fileDataRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	FileUploadConfig fileUploadConfig;

	@Override
	public FileData uploadFile(String name, MultipartFile multipartFile) throws IOException {
		// TODO Auto-generated method stub
		String uploadDir = fileUploadConfig.getUploadDir();
		String uuid = UUID.randomUUID().toString();
		String uploadDate = new SimpleDateFormat("yyy-MM-dd").format(new Date());
		Path subPath = createDirectory(uploadDir, uploadDate);
		
		Files.copy(multipartFile.getInputStream(), subPath.resolve(uuid)); 

	    FileData saved = createFileData(multipartFile, uuid, subPath);
		saveFileData(saved);
		
		User user = userRepository.findByName(name);
        user.getSharedFiles().add(saved);
        userRepository.save(user);

        return saved;
	}

	@Override
	public Resource load(String uuid, String username) throws Exception {
		// TODO Auto-generated method stub
		Optional<FileData> fileDataOptional = fileDataRepository.findById(uuid);
        if (fileDataOptional.isPresent()) {
            FileData filedata = fileDataOptional.get();
            User user = userRepository.findByName(username);
            
            if (filedata.getSharedWithUsers().contains(user)) {
                Path filePath = Paths.get(filedata.getFilePath());
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists() || resource.isReadable()) {
                    return resource;
                } else {
                    throw new RuntimeException("Could not read file: " + uuid);
                }
            } else {
                throw new RuntimeException("You don't have permission to access this file.");
            }
        } else {
            throw new RuntimeException("File not found.");
        }
    }


	@Override
	public void shareFile(String fileId, String fromUser, String toUser) {
		// TODO Auto-generated method stub
		User fromUserEntity = userRepository.findByName(fromUser);
        User toUserEntity = userRepository.findByName(toUser);
        Optional<FileData> fileDataOptional = fileDataRepository.findById(fileId);

        if (fileDataOptional.isPresent()) {
            FileData fileData = fileDataOptional.get();
            if (fromUserEntity.getSharedFiles().contains(fileData)) {
                toUserEntity.getSharedFiles().add(fileData);
                userRepository.save(toUserEntity);
            } else {
                throw new RuntimeException("You don't have permission to share this file.");
            }
        } else {
            throw new RuntimeException("File not found.");
        }
		
	}
	
    @Override
    public Optional<FileData> getFileData(String fileId) {
        return fileDataRepository.findById(fileId);
    }

	
	// save tool
	public FileData saveFileData(FileData fileData) {
        return fileDataRepository.save(fileData);
    }
	
	
	// createDirectory tool
	private Path createDirectory(String uploadDir, String uploadDate) throws IOException {
        Path subPath = Paths.get(uploadDir).resolve(uploadDate);
        if (!Files.exists(subPath)) {
            Files.createDirectories(subPath);
            System.out.println("Folder create success");
        } else {
            System.out.println("Folder is already exist");
        }
        return subPath;
    }
	
	// createFileData tool
	 private FileData createFileData(MultipartFile multipartFile, String uuid, Path subPath) {
	        FileData fileData = new FileData();
	        fileData.setUuid(uuid);
	        fileData.setFilePath(subPath.resolve(uuid).toString());
	        fileData.setFileName(multipartFile.getOriginalFilename());
	        fileData.setFileSize(multipartFile.getSize());
	        fileData.setContentType(multipartFile.getContentType());
	        return fileData;
	    }




}
