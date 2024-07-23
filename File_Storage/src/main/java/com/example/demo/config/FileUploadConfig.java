package com.example.demo.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class FileUploadConfig {

	private String uploadDir;
	
	@PostConstruct
	public void init() {
		try{
			File tempDir = Files.createTempDirectory("uploads").toFile();
            uploadDir = tempDir.getAbsolutePath();
		}catch(IOException e){
			throw new RuntimeException("Could not create upload directory", e);
		}
	}
	
	public String getUploadDir() {
	        return uploadDir;
	}
}
