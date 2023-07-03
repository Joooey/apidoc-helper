// Copyright (C) 2023 Meituan
// All rights reserved
package com.joey.apidochelper;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.javadoc.PsiDocComment;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * @author wangjunyu
 * @version 1.0
 * Created on 2023/7/2 16:36
 **/
public class JavaFileParser extends AnAction {

    String MD_FILE_NAME = "api-doc.md";

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
//        String fileName = "/Users/wangjunyu/code/forex-pay-tradecore/forexpay-trade-client/src/main/java/com/sankuai/forexpay/trade/client/dto/PayFundInfo.java"; // 指定文件名
//        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(fileName);
        final VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (virtualFile != null) {
            PsiManager psiManager = PsiManager.getInstance(project);
            PsiFile psiFile = psiManager.findFile(virtualFile);
            if (psiFile != null) {
                // 处理 PsiFile 对象
                if (psiFile instanceof PsiJavaFile) {
                    ApplicationManager.getApplication().runWriteAction(() -> {
                        // 在Idea的写操作上下文中执行写操作
                        try {
                            // 创建一个新的md文件，将内容写入该文件
                            VirtualFile newFile = virtualFile.getParent().createChildData(this, MD_FILE_NAME);
                            OutputStream outputStream = newFile.getOutputStream(this);
                            PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
                            PsiClass[] psiClasses = psiJavaFile.getClasses();

                            for (PsiClass psiClass : psiClasses) {
                                outputStream.write((psiClass.getName() + "\n").getBytes());
                                // 处理 PsiClass 对象
                                PsiField[] psiFields = psiClass.getFields();
                                outputStream.write("| 字段 | 类型 | 说明 | 备注 |\n".getBytes());
                                outputStream.write("| -- | -- | -- | -- |\n".getBytes());
                                for (PsiField psiField : psiFields) {
                                    StringBuilder sb = new StringBuilder();
                                    // 获取字段名称和简单类型
                                    sb.append("|").append(psiField.getName()).append("|").append(psiField.getType().getPresentableText()).append("|");
                                    // 获取字段注释
                                    PsiDocComment docComment = psiField.getDocComment();
                                    if (docComment != null) {
                                        String filteredStr = docComment.getText().replaceAll("[/*\\s]", "");
                                        sb.append(filteredStr).append("|");
                                    } else {
                                        PsiAnnotation[] annotations = psiField.getAnnotations();
                                        for (PsiAnnotation annotation : annotations) {
                                            if (Objects.requireNonNull(annotation.getQualifiedName()).contains("FieldDoc")) {
                                                sb.append(annotation.findAttributeValue("description").getText().replaceAll("\"", "")).append("|");
                                            }
                                        }
                                    }
                                    sb.append("|\n");
                                    outputStream.write(sb.toString().getBytes());
                                }
                            }
                            outputStream.close();
                            // 执行系统命令，打开md文件
                            String command = "open " + newFile.getPath();
                            Runtime runtime = Runtime.getRuntime();
                            Process process = runtime.exec(command);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });

                } else {
                    // 处理非 Java 文件的情况
                    System.out.println("文件类型有误");
                }
            } else {
                // 处理文件不存在的情况
                System.out.println("文件不存在");
            }
        }
    }
}