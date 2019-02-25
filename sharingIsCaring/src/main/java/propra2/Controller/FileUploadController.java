package propra2.Controller;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import propra2.database.Product;
import propra2.repositories.ProductRepository;
import propra2.storage.StorageFileNotFoundException;
import propra2.storage.StorageService;

@Controller
@RequestMapping("/upload")
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{productId}/{filename}")
    @ResponseBody
    public Resource serveFile(@PathVariable String filename, @PathVariable Long productId) {
		System.out.println(filename + " " + productId + "serveFile");
        Resource file = storageService.loadAsResource(filename, productId);
        /*return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);*/
        return file;
    }

    @PostMapping("/")
    public String handleFileUpload(Model model, @RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName,
								   @RequestParam("productId") String productId, RedirectAttributes redirectAttributes) {

        String originalFilename = file.getOriginalFilename();
        if(originalFilename.length() >= 4) {
			storageService.store(file, fileName + originalFilename.substring(originalFilename.length() - 4), productId);
			redirectAttributes.addFlashAttribute("message",
					"You successfully uploaded " + file.getOriginalFilename() + "!");
		}

        Product product = productRepository.findById(Long.parseLong(productId)).get();
        model.addAttribute(product);
        return "editProductImage";
    }

    @PostMapping("/{productId}/deleteCurrentImage")
    public String deleteCurrentImage(@PathVariable Long productId, Model model){
        storageService.deleteFile(productId);
        Product product = productRepository.findById(productId).get();
        model.addAttribute(product);
        return "editProductImage";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
