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
import com.tyron.code.ui.file.action.file.CreateDirectoryAction;
import com.tyron.code.ui.file.action.file.CreateFileAction;
import com.tyron.code.ui.file.action.java.CreateClassAction;
import com.tyron.code.ui.file.action.kotlin.CreateKotlinClassAction;
import com.tyron.code.ui.file.action.xml.CreateLayoutAction;
import com.tyron.code.ui.file.tree.TreeFileManagerFragment;
import com.tyron.code.ui.file.tree.model.TreeFile;
import com.tyron.ui.treeview.TreeNode;
import com.tyron.ui.treeview.TreeView;
import java.io.File;

public class NewFileActionGroup extends ActionGroup {

  public static final String ID = "fileManagerNewGroup";

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
    presentation.setText(event.getDataContext().getString(R.string.menu_new));
  }

  @Override
  public AnAction[] getChildren(@Nullable AnActionEvent e) {
    return new AnAction[] {
      new CreateFileAction(),
      new CreateClassAction(),
      new CreateKotlinClassAction(),
      new CreateLayoutAction(),
      new CreateDirectoryAction()
    };
  }
}
