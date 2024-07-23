package com.example.demo.entities;

import java.util.Set;

import com.example.demo.utils.UuidUtil;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name="T_File")
public class FileData {
	
	@Id
	private String uuid;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String contentType;
    
    @ManyToMany(mappedBy = "sharedFiles")
    private Set<User> sharedWithUsers;
    
    @PrePersist
    public void generateUUID() {
        if (this.uuid == null || this.uuid.isEmpty()) {
            this.uuid = UuidUtil.getUUID(); 
        }
    }
}
