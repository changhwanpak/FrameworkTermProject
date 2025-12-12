package park.final_exam_monday.service;

import park.final_exam_monday.entity.FileEntity;
import park.final_exam_monday.repository.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final FileRepository fileRepository;
    public FileService(FileRepository fileRepository) { this.fileRepository = fileRepository; }

    public FileEntity saveFile(MultipartFile multipartFile, String description, String uploadedBy) throws IOException {
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(dir)) Files.createDirectories(dir);

        String origName = multipartFile.getOriginalFilename();
        String ext = "";
        if (origName != null && origName.contains(".")) ext = origName.substring(origName.lastIndexOf("."));
        String stored = UUID.randomUUID().toString() + ext;
        Files.copy(multipartFile.getInputStream(), dir.resolve(stored), StandardCopyOption.REPLACE_EXISTING);

        FileEntity e = new FileEntity();
        e.setFileName(stored);
        e.setOrigName(origName);
        e.setDescription(description);
        e.setUploadedBy(uploadedBy);
        return fileRepository.save(e);
    }

    public List<FileEntity> listAll() { return fileRepository.findAll(); }

    public Resource loadAsResource(Long id) throws IOException {
        FileEntity e = fileRepository.findById(id).orElseThrow();
        Path file = Paths.get(uploadDir).resolve(e.getFileName()).normalize();
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() && resource.isReadable()) return resource;
        else throw new FileNotFoundException("파일을 찾을 수 없음");
    }

    public void delete(Long id) throws IOException {
        FileEntity e = fileRepository.findById(id).orElseThrow();
        Files.deleteIfExists(Paths.get(uploadDir).resolve(e.getFileName()));
        fileRepository.deleteById(id);
    }
}
