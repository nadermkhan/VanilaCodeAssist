package com.tyron.xml.completion.repository.parser;

import com.google.common.collect.ImmutableList;
import com.tyron.builder.compiler.manifest.SdkConstants;
import com.tyron.builder.compiler.manifest.resources.ResourceType;
import com.tyron.xml.completion.repository.api.LayoutInfo;
import com.tyron.xml.completion.repository.api.LayoutResourceValueImpl;
import com.tyron.xml.completion.repository.api.ResourceNamespace;
import com.tyron.xml.completion.repository.api.ResourceReference;
import com.tyron.xml.completion.repository.api.ResourceValue;
import com.tyron.xml.completion.repository.api.ResourceValueImpl;
import com.tyron.xml.completion.util.DOMUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kotlin.io.FilesKt;
import org.eclipse.lemminx.dom.DOMAttr;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMParser;
import org.eclipse.lemminx.dom.DOMProcessingInstruction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LayoutXmlParser implements ResourceParser {

  private static class Result {
    public LayoutInfo layoutInfo;
    public List<ResourceValue> additionalValues;
  }

  @Override
  public List<ResourceValue> parse(
      @NotNull File file,
      @Nullable String contents,
      @NotNull ResourceNamespace namespace,
      @Nullable String libraryName)
      throws IOException {
    if (!"xml".equals(FilesKt.getExtension(file)) || contents == null) {
      return Collections.emptyList();
    }

    DOMDocument document = DOMParser.getInstance().parse(contents, "", null);
    List<DOMNode> roots = document.getRoots();
    for (DOMNode root : roots) {
      if (root instanceof DOMProcessingInstruction) {
        continue;
      }

      return parseRoot(file, root, namespace, libraryName);
    }
    return ImmutableList.of();
  }

  private List<ResourceValue> parseRoot(
      File file, DOMNode root, ResourceNamespace namespace, String libraryName) {
    List<ResourceValue> values = new ArrayList<>();
    Result result = parseLayout(root, namespace, libraryName);
    ResourceReference resourceReference =
        new ResourceReference(
            namespace, ResourceType.LAYOUT, FilesKt.getNameWithoutExtension(file));
    LayoutResourceValueImpl layoutValue =
        new LayoutResourceValueImpl(resourceReference, null, libraryName, result.layoutInfo);
    values.add(layoutValue);
    if (result.additionalValues != null) {
      values.addAll(result.additionalValues);
    }
    return values;
  }

  private Result parseLayout(DOMNode root, ResourceNamespace namespace, String libraryName) {
    String tag = root.getLocalName();
    if (tag == null) {
      tag = "";
    }

    Result result = new Result();
    LayoutInfo layoutInfo = new LayoutInfo(tag);
    result.layoutInfo = layoutInfo;
    result.additionalValues = new ArrayList<>();

    parseAttributes(root, namespace, libraryName, result);

    List<DOMNode> children = root.getChildren();
    if (children != null) {
      for (DOMNode child : children) {
        Result childResult = parseLayout(child, namespace, libraryName);
        if (childResult.layoutInfo != null) {
          layoutInfo.addChild(childResult.layoutInfo);
        }

        if (childResult.additionalValues != null) {
          result.additionalValues.addAll(childResult.additionalValues);
        }
      }
    }
    return result;
  }

  private void parseAttributes(
      DOMNode node, ResourceNamespace namespace, String libraryName, Result result) {
    ResourceNamespace.Resolver resolver = DOMUtils.getNamespaceResolver(node.getOwnerDocument());
    String prefix = resolver.uriToPrefix(ResourceNamespace.ANDROID.getXmlNamespaceUri());
    if (prefix != null) {
      DOMAttr idAttr = node.getAttributeNode(prefix, "id");
      if (idAttr != null) {
        String value = idAttr.getValue();
        ResourceValue resourceValue = parseIdValue(value, namespace, libraryName);
        if (resourceValue != null) {
          result.additionalValues.add(resourceValue);
        }
      }
    }

    List<DOMAttr> attributeNodes = node.getAttributeNodes();
    if (attributeNodes != null) {
      for (DOMAttr attrNode : attributeNodes) {
        result.layoutInfo.addAttribute(attrNode.getName(), attrNode.getValue());
      }
    }
  }

  private ResourceValue parseIdValue(
      String value, ResourceNamespace namespace, String libraryName) {
    if (value == null) {
      return null;
    }
    if (!value.startsWith(SdkConstants.NEW_ID_PREFIX)) {
      return null;
    }
    String name = value.substring(SdkConstants.NEW_ID_PREFIX.length());
    if (name.isEmpty()) {
      return null;
    }
    try {
      ResourceReference reference = new ResourceReference(namespace, ResourceType.ID, name);
      return new ResourceValueImpl(reference, null, libraryName);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
