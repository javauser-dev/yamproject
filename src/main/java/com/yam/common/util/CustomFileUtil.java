package com.yam.common.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomFileUtil {
	@Value("${com.yam.upload.path}")
	private String uploadPath;

	@PostConstruct
	public void init() {
		File tempFolder = new File(uploadPath);

		if (tempFolder.exists() == false) {
			tempFolder.mkdir();
		}
		uploadPath = tempFolder.getAbsolutePath();

		log.info("-------------------------------------");
		log.info(uploadPath);
	}

	/*
	 * 파일 업로드 public String saveFile(MultipartFile file) throws RuntimeException {
	 * if (file == null) { return null;}
	 * 
	 * String savedName = UUID.randomUUID().toString() + "_" +
	 * file.getOriginalFilename(); Path savePath = Paths.get(uploadPath, savedName);
	 * 
	 * try { Files.copy(file.getInputStream(), savePath); } catch (IOException e) {
	 * throw new RuntimeException(e.getMessage()); } return savedName; }
	 */

	/* 썸네일 이미지 및 파일 업로드 */
	public String saveFile(MultipartFile file) throws RuntimeException {
		if (file == null) {
			return null;
		}

		String savedName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		Path savePath = Paths.get(uploadPath, savedName);

		try {
			Files.copy(file.getInputStream(), savePath);
			String contentType = file.getContentType();

			// 이미지여부 확인
			if (contentType != null && contentType.startsWith("image")) {
				Path thumbnailPath = Paths.get(uploadPath, "s_" + savedName);
				Thumbnails.of(savePath.toFile()).size(200, 133).toFile(thumbnailPath.toFile());
			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		return savedName;
	}

	public ResponseEntity<Resource> getFile(String fileName) {
		Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

		if (!resource.exists()) {
			resource = new FileSystemResource(uploadPath + File.separator + "default.jpg");
		}

		HttpHeaders headers = new HttpHeaders();
		try {
			headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
		return ResponseEntity.ok().headers(headers).body(resource);
	}

	/* 썸네일 파일 및 파일 삭제 */
	public void deleteFile(String fileName) {
		if (fileName == null) {
			return;
		}
		// 썸네일이 있는지 확인하고 삭제
		String thumbnailFileName = "s_" + fileName;
		Path thumbnailPath = Paths.get(uploadPath, thumbnailFileName);
		Path filePath = Paths.get(uploadPath, fileName);

		try {
			Files.deleteIfExists(filePath);
			Files.deleteIfExists(thumbnailPath);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

}