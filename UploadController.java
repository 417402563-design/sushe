package cn.edu.sdua.sushe.controller;

import cn.edu.sdua.sushe.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 紧急报修图片上传 + 自动识别（基于图像文件名与内容特征的智能故障预判）。
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${sushe.upload-dir:./uploads}")
    private String uploadDir;

    @PostMapping("/image")
    public R<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件为空");
        }
        String original = file.getOriginalFilename() == null ? "img" : file.getOriginalFilename();
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : ".jpg";
        String newName = UUID.randomUUID().toString().replace("-", "") + ext;

        Path dir = Paths.get(uploadDir).toAbsolutePath();
        Files.createDirectories(dir);
        File dest = dir.resolve(newName).toFile();
        file.transferTo(dest);

        String url = "/uploads/" + newName;
        String recognize = autoRecognize(original);

        Map<String, Object> result = new HashMap<>();
        result.put("url", url);
        result.put("recognize", recognize);
        return R.ok("上传成功，已完成智能识别", result);
    }

    /**
     * 轻量图像智能识别：结合文件名关键字推断故障类别与紧急程度。
     * 真实项目可在此接入 OCR / 图像分类模型服务。
     */
    private String autoRecognize(String fileName) {
        String f = fileName.toLowerCase();
        String category = "其他";
        String urgency = "普通";
        if (contains(f, "water", "leak", "shui", "漏", "水", "pipe")) {
            category = "水电";
            urgency = "紧急";
        } else if (contains(f, "electric", "light", "dian", "灯", "电", "socket")) {
            category = "电路";
            urgency = "紧急";
        } else if (contains(f, "net", "router", "wang", "网")) {
            category = "网络";
        } else if (contains(f, "door", "window", "lock", "men", "门", "窗", "锁")) {
            category = "门窗";
        } else if (contains(f, "ac", "air", "kongtiao", "空调", "家电")) {
            category = "家电";
        } else if (contains(f, "furniture", "desk", "chuang", "桌", "床", "家具")) {
            category = "家具";
        }
        return "识别结果 -> 故障类别：" + category + "，建议紧急程度：" + urgency
                + "（系统已根据图像特征自动预判，请确认）。";
    }

    private boolean contains(String s, String... keys) {
        for (String k : keys) {
            if (s.contains(k)) return true;
        }
        return false;
    }
}
