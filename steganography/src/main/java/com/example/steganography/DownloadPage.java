package com.example.steganography;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class DownloadPage {

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("imagePath") String filename) throws Exception {
        // Assuming files are stored in this folder (change as needed)
        Path file = Paths.get("path/to/generated/files").resolve(filename).normalize();

        Resource resource = new UrlResource(file.toUri());
        if (!resource.exists()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                     .header("X-Message", "No data found")
                     .build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
