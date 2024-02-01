package com.tyron.completion.xml.providers;

import com.tyron.builder.project.api.AndroidModule;
import com.tyron.completion.CompletionParameters;
import com.tyron.completion.CompletionProvider;
import com.tyron.completion.model.CompletionList;
import com.tyron.completion.xml.XmlRepository;
import java.io.File;
import java.io.IOException;
import kotlin.io.FilesKt;

/** Only used to listen to updates to files. */
public class EmptyCompletionProvider extends CompletionProvider {
  @Override
  public boolean accept(File file) {
    return "xml".equals(FilesKt.getExtension(file));
  }

  @Override
  public CompletionList complete(CompletionParameters parameters) {
    try {
      doRefresh(parameters);
    } catch (IOException e) {
      // ignored
    }
    return null;
  }

  private void doRefresh(CompletionParameters parameters) throws IOException {
    File file = parameters.getFile();
    XmlRepository repository =
        XmlRepository.getRepository(
            parameters.getProject(), (AndroidModule) parameters.getModule());
    repository.getRepository().updateFile(file, parameters.getContents());
  }
}
