package park.final_exam_monday.controller;

import park.final_exam_monday.entity.FileEntity;
import park.final_exam_monday.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class FileController {
    private final FileService fileService;
    public FileController(FileService fileService) { this.fileService = fileService; }


    @GetMapping("/files")
    public String listFiles(Model model, Authentication auth) {
        List<FileEntity> files = fileService.listAll();
        model.addAttribute("files", files);

        if(auth != null) {
            model.addAttribute("username", auth.getName());
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        }

        return "files";
    }





    @GetMapping("/admin/upload")
    public String uploadForm() { return "upload"; }

    @PostMapping("/admin/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam(value = "description", required=false) String desc,
                         Authentication auth,
                         Model model) {
        try {
            fileService.saveFile(file, desc, auth.getName());
            return "redirect:/files";
        } catch (IOException e) {
            model.addAttribute("error", "업로드 실패: " + e.getMessage());
            return "upload";
        }
    }

    @GetMapping("/files/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        Resource resource = fileService.loadAsResource(id);
        String orig = fileService.listAll().stream().filter(f -> f.getId().equals(id))
                .findFirst().map(FileEntity::getOrigName).orElse("file");
        String encoded = URLEncoder.encode(orig, StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .body(resource);
    }

    @PostMapping("/admin/delete/{id}")
    public String delete(@PathVariable Long id) {
        try { fileService.delete(id); } catch (IOException ignored) {}
        return "redirect:/files";
    }
}
