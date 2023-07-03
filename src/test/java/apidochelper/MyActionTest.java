// Copyright (C) 2023 Meituan
// All rights reserved
package apidochelper;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.MapDataContext;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.joey.apidochelper.JavaFileParser;
import org.junit.Test;

/**
 * @author wangjunyu
 * @version 1.0
 * Created on 2023/7/3 11:26
 **/
public class MyActionTest extends BasePlatformTestCase {
    @Test
    public void testMyAction() {
        Project project = getProject();
        // 创建 MapDataContext 对象，并设置上下文
        MapDataContext dataContext = new MapDataContext();
        dataContext.put(CommonDataKeys.PROJECT, project);

        // 创建要测试的 Action 实例
        AnAction myAction = new JavaFileParser();
        // 执行 Action
        myAction.actionPerformed(AnActionEvent.createFromDataContext("", null, dataContext));
    }
}
