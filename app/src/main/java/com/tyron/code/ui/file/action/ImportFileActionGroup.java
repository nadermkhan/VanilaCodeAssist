package com.tyron.code.ui.file.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.tyron.actions.ActionGroup;
import com.tyron.actions.AnAction;
import com.tyron.actions.AnActionEvent;
import com.tyron.actions.CommonDataKeys;
import com.tyron.actions.Presentation;
import com.tyron.code.R;
import com.tyron.code.ui.file.CommonFileKeys;
import com.tyron.code.ui.file.action.file.ImportDirectoryAction;
import com.tyron.code.ui.file.action.file.ImportFileAction;
import com.tyron.code.ui.file.tree.TreeFileManagerFragment;
import com.tyron.code.ui.file.tree.model.TreeFile;
import com.tyron.ui.treeview.TreeNode;
import com.tyron.ui.treeview.TreeView;
import java.io.File;

public class ImportFileActionGroup extends ActionGroup {

  public static final String ID = "fileManagerImportGroup";

  @Override
  public void update(@NonNull AnActionEvent event) {
    Presentation presentation = event.getPresentation();
    presentation.setVisible(false);

    TreeNode<TreeFile> data = event.getData(CommonFileKeys.TREE_NODE);
    if (data == null) {
      return;
    }
    TreeFileManagerFragment fragment =
        (TreeFileManagerFragment) event.getRequiredData(CommonDataKeys.FRAGMENT);
    TreeView<TreeFile> treeView = fragment.getTreeView();
    TreeNode<TreeFile> currentNode = event.getRequiredData(CommonFileKeys.TREE_NODE);

    File currentFile = currentNode.getValue().getFile();
    if (currentFile.isDirectory()) {
      presentation.setVisible(true);
    }
    presentation.setText(event.getDataContext().getString(R.string.menu_import));
  }

  @Override
  public AnAction[] getChildren(@Nullable AnActionEvent e) {
    return new AnAction[] {new ImportFileAction(), new ImportDirectoryAction()};
  }
}
