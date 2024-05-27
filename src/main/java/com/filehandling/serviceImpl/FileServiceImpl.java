package com.filehandling.serviceImpl;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.filehandling.model.UploadedFile;
import com.filehandling.repository.UploadedFileRepository;
import com.filehandling.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileServiceImpl implements FileService {

    private final Path rootLocation = Paths.get("uploaded-files");

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    public FileServiceImpl() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }


    public String storeFile(MultipartFile file) {
        String filename = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        try {
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            UploadedFile upload=new UploadedFile();
            upload.setFilename(filename);
            upload.setUploadTime(LocalDateTime.now());
            uploadedFileRepository.save(upload);

            return filename;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public Resource loadFile(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteOldFiles() {
        try (Stream<Path> files = Files.list(rootLocation)) {
            files.forEach(file -> {
                try {
                    FileTime fileTime = Files.getLastModifiedTime(file);
                    if (fileTime.toMillis() < System.currentTimeMillis() - 10 * 60 * 1000) { // 10 minutes
                        Files.delete(file);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
