package com.example.steganography.controller;

import com.example.steganography.service.SteganographyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

// @RestController
// @RequestMapping("/api/steganography")
@Controller
public class SteganographyController {

    @Autowired
    private SteganographyService stegoService;

    @PostMapping("/embed")
    public String embedFile(
            Model model,
            @RequestParam(value="image", required=false) MultipartFile coverImage,
            @RequestParam("file") MultipartFile fileToHide
    ) throws Exception {
        File tempFile = File.createTempFile("secret", ".dat");
        File output = new File("src/main/resources/static/output-stego.png");
        
        // Load if default image is enabled
        File tempImage;
        if (coverImage != null) {
            tempImage = File.createTempFile("cover", ".png");
            coverImage.transferTo(tempImage);
        } else {
            tempImage = new File("src/main/resources/static/cutecat.png");
        }

        fileToHide.transferTo(tempFile);

        stegoService.embedFile(tempImage, tempFile, output);
        model.addAttribute("filePath", output.getAbsolutePath());
        return "downloadpage";
    }

    @PostMapping("/extract")
    public String extractFile(
            Model model,
            @RequestParam("image") MultipartFile stegoImage
    ) throws Exception {
        File tempImage = File.createTempFile("stego", ".png");
        File extracted = new File("src/main/resources/static/extracted.dat");

        stegoImage.transferTo(tempImage);
        stegoService.extractFile(tempImage, extracted);
        model.addAttribute("filePath", extracted.getAbsolutePath());
        return "downloadpage";
    }
}
