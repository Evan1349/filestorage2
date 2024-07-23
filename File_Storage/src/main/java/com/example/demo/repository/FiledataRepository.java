package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.FileData;

public interface FiledataRepository extends JpaRepository<FileData, String> {
	
	FileData findByUuid(String Uuid);
	
}
