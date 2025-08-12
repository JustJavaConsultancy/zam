package tech.justjava.zam.gallery;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient (name = "file-service", url = "http://localhost:8089", configuration = FeignConfig.class)
public interface FileFeignClient {
        @GetMapping("/download/{id}")
        ResponseEntity<Resource > downloadFile(@PathVariable("id") String id);
        @DeleteMapping("/delete/{id}")
        ResponseEntity<String> deleteFile(@PathVariable("id") String id);
        @PostMapping(value = "/uploadWithMetaData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        String uploadWithMetaData(@RequestPart("file") MultipartFile file, @RequestPart("metadata")
        String metadata);

}
