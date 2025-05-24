package com.example.steganography.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;

@Service
public class SteganographyService {

    public void embedFile(File coverImage, File fileToHide, File outputImage) throws IOException {
        BufferedImage image = ImageIO.read(coverImage);
        byte[] fileBytes = Files.readAllBytes(fileToHide.toPath());
        byte[] lengthBytes = ByteBuffer.allocate(4).putInt(fileBytes.length).array();

        byte[] data = new byte[lengthBytes.length + fileBytes.length];
        System.arraycopy(lengthBytes, 0, data, 0, lengthBytes.length);
        System.arraycopy(fileBytes, 0, data, lengthBytes.length, fileBytes.length);

        int dataIndex = 0;
        outer:
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                if (dataIndex < data.length * 8) {
                    int byteIndex = dataIndex / 8;
                    int bitIndex = 7 - (dataIndex % 8);
                    int bit = (data[byteIndex] >> bitIndex) & 1;

                    int newRgb = (rgb & 0xFFFFFFFE) | bit; // overwrite LSB
                    image.setRGB(x, y, newRgb);
                    dataIndex++;
                } else {
                    break outer;
                }
            }
        }
        ImageIO.write(image, "png", outputImage);
    }

    public void extractFile(File stegoImage, File outputFile) throws IOException {
        BufferedImage image = ImageIO.read(stegoImage);
        ByteArrayOutputStream dataOut = new ByteArrayOutputStream();

        int bitCount = 0;
        int length = -1;
        int byteVal = 0;

        outer:
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int bit = image.getRGB(x, y) & 1;
                byteVal = (byteVal << 1) | bit;
                bitCount++;

                if (bitCount == 8) {
                    dataOut.write(byteVal);
                    if (dataOut.size() == 4 && length == -1) {
                        byte[] lenBytes = dataOut.toByteArray();
                        length = ByteBuffer.wrap(lenBytes).getInt();
                        dataOut.reset();
                    } else if (length != -1 && dataOut.size() == length) {
                        break outer;
                    }
                    bitCount = 0;
                    byteVal = 0;
                }
            }
        }
        Files.write(outputFile.toPath(), dataOut.toByteArray());
    }
}
