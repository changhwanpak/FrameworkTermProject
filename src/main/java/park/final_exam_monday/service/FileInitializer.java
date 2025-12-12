package park.final_exam_monday.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import park.final_exam_monday.entity.FileEntity;
import park.final_exam_monday.repository.FileRepository;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FileInitializer {
    private final FileRepository fileRepository;
    private final String uploadDir = "uploads"; // application.properties와 동일하게

    public FileInitializer(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @PostConstruct
    public void init() throws IOException {
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(dir)) Files.createDirectories(dir);

        // uploads 폴더의 모든 파일을 DB에 등록
        Files.list(dir).forEach(path -> {
            if (!Files.isDirectory(path)) {
                String storedName = path.getFileName().toString();
                boolean exists = fileRepository.findAll().stream()
                        .anyMatch(f -> f.getFileName().equals(storedName));
                if (!exists) {
                    FileEntity e = new FileEntity();
                    e.setFileName(storedName);
                    e.setOrigName(storedName); // 원본명 정보가 없으면 storedName으로
                    e.setDescription("자동 등록"); // 기본 설명
                    e.setUploadedBy("SYSTEM"); // 미리 있는 파일은 SYSTEM 등록
                    e.setUploadTime(LocalDateTime.now());
                    fileRepository.save(e);
                }
            }
        });
    }
}


