package dev.portfolio.knowledge;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class KnowledgeController {
  private final KnowledgeService service;
  public KnowledgeController(KnowledgeService service) { this.service = service; }
  public record DocumentInput(@NotBlank @Size(max=120) String source, @NotBlank @Size(max=20000) String text) {}
  public record QuestionInput(@NotBlank @Size(max=2000) String question) {}
  @PostMapping("/documents")
  public Map<String,Object> ingest(@Valid @RequestBody DocumentInput input) {
    return Map.of("chunksIndexed", service.ingest(input.source(), input.text()));
  }
  @PostMapping("/ask")
  public KnowledgeService.Answer ask(@Valid @RequestBody QuestionInput input) {
    return service.ask(input.question());
  }
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String,String>> badInput(IllegalArgumentException e) {
    return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
  }
}
