package com.tyron.completion.java.provider;

import static com.google.common.truth.Truth.assertThat;

import com.tyron.completion.model.CompletionItem;
import com.tyron.completion.model.CompletionList;
import java.util.stream.Collectors;
import org.junit.Test;

public class CompleteScopeTest extends CompletionBase {

  @Test
  public void testScope() {
    CompletionList list = completeInsertHandle("Scope.java", "shouldNotBe");
    assertThat(list.items.stream().map(CompletionItem::toString).collect(Collectors.toList()))
        .doesNotContain("shouldNotBeVisible");
  }
}
