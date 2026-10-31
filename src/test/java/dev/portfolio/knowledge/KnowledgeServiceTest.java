package dev.portfolio.knowledge;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class KnowledgeServiceTest {
  @Test void identicalVectorsHaveSimilarityOne() {
    assertEquals(1.0, KnowledgeService.cosine(new float[]{2,3}, new float[]{2,3}), 1e-9);
  }
  @Test void orthogonalVectorsHaveZeroSimilarity() {
    assertEquals(0, KnowledgeService.cosine(new float[]{1,0}, new float[]{0,1}), 1e-9);
  }
  @Test void dimensionMismatchIsRejected() {
    assertThrows(IllegalArgumentException.class,
      () -> KnowledgeService.cosine(new float[]{1}, new float[]{1,2}));
  }
}
