package top.omooo.lint_library.detector;

import static com.android.SdkConstants.ATTR_SRC;
import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector.UastScanner;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.LayoutDetector;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.XmlContext;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UReferenceExpression;
import org.jetbrains.uast.util.UastExpressionUtils;
import org.w3c.dom.Attr;

/**
 * Author: Omooo
 * Date: 2019/7/5
 * Version: 
 * Desc: 
 */
@SuppressWarnings("UnstableApiUsage")
public class PngDetector extends LayoutDetector
        implements UastScanner {

    public static final Issue ISSUE = Issue.create(
            "PngUsage",
            "Png Usage",
            "Png is garbage",
            Category.CORRECTNESS,
            5,
            Severity.WARNING,
            new Implementation(PngDetector.class, Scope.ALL_RESOURCES_SCOPE)
    );

    private String srcValue;
    private String[] resFilesName;

    @Nullable
    @Override
    public Collection<String> getApplicableAttributes() {
        return Collections.singletonList(ATTR_SRC);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void visitAttribute(@NotNull XmlContext context, @NotNull Attr attribute) {
        srcValue = attribute.getValue();
        File resFolder = null;
        for (File file : context.getMainProject().getResourceFolders()) {
            if ("res".equals(file.getName())) {
                resFolder = new File(file.getPath());
                break;
            }
        }
        if (resFolder == null) {
            return;
        }
        File drawableFolder = new File(resFolder.getPath(), "drawable");
        if (!drawableFolder.exists() && !drawableFolder.isDirectory()) {
            return;
        }
        // TODO: 2019/7/8 还需处理 drawable 目录下还有目录的情况
        String[] filesName = drawableFolder.list();
        if (filesName == null || filesName.length == 0) {
            return;
        }

        for (String fileName : filesName) {
            if (fileName.substring(0, fileName.indexOf("."))
                    .equals(srcValue.substring(10))) {
                if (fileName.endsWith(".png")) {
                    context.report(
                            ISSUE,
                            attribute,
                            context.getLocation(attribute),
                            "Src Usage"
                    );
                }
                break;
            }
        }

        //仅仅用于调试输出
        try {
            File file = new File("detector.txt");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.append("/******** All files through use 'src' in xml ********/");
            fileWriter.append("\n");
            fileWriter.append(attribute.getName());
            fileWriter.append(": ");
            fileWriter.append(attribute.getValue());
            fileWriter.append("\n\n");
            fileWriter.append("/******** All files in 'src/drawable folder'  ********/");
            fileWriter.append("\n");
            for (String s : filesName) {
                fileWriter.append(s);
                fileWriter.append("\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {
        return Collections.singletonList(UCallExpression.class);
    }

    @Nullable
    @Override
    public UElementHandler createUastHandler(@NotNull JavaContext context) {
        return new UElementHandler() {
            @Override
            public void visitCallExpression(@NotNull UCallExpression node) {
//                if (!UastExpressionUtils.isMethodCall(node)
//                        || !"setImageResource".equals(node.getMethodName())
//                        || !node.getTypeArguments().get(0).equalsToText("int")) {
//                    return;
//                }
//                UReferenceExpression classReference = node.getClassReference();
//                if (classReference == null) {
//                    return;
//                }
//                PsiElement element = classReference.resolve();
//                if (!(element instanceof PsiClass)) {
//                    return;
//                }
//                String classType = ((PsiClass) element).getQualifiedName();
//                if ("android.widget.ImageView".equals(classType)) {
//                    check((String) node.getValueArguments().get(0).evaluate(),
//                            context.getMainProject().getResourceFolders());
//                }
            }
        };
    }

    private void check(String imageName, List<File> resFolders) {
        try {
            FileWriter fileWriter = new FileWriter("detector.txt", true);
            fileWriter.append("------------" + imageName);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
