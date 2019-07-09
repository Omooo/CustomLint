package top.omooo.lint_library.detector;

import static com.android.SdkConstants.ATTR_SRC;
import com.android.resources.ResourceFolderType;
import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Context;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Detector.UastScanner;
import com.android.tools.lint.detector.api.Detector.XmlScanner;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.XmlContext;
import com.intellij.psi.PsiMethod;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.util.UastExpressionUtils;
import org.w3c.dom.Attr;

/**
 * Author: Omooo
 * Date: 2019/7/5
 * Desc: Avoid to use png in layout or java file
 */
@SuppressWarnings("UnstableApiUsage")
public class PngDetector extends Detector
        implements XmlScanner, UastScanner {

    private static final String LINT_EXPLANATION =
            "WebP can provide better compression than PNG. ";

    private static final String LINT_MSG = "\u21E2 Please use webp instead of png.";

    public static final Issue ISSUE_PNG_IN_XML = Issue.create(
            "PngUseInXml",
            "Png Usage",
            LINT_EXPLANATION,
            Category.CORRECTNESS,
            5,
            Severity.WARNING,
            new Implementation(PngDetector.class, Scope.ALL_RESOURCES_SCOPE)
    );

    public static final Issue ISSUE_PNG_IN_CODE = Issue.create(
            "PngUseInCode",
            "Png Usage",
            LINT_EXPLANATION,
            Category.CORRECTNESS,
            5,
            Severity.WARNING,
            new Implementation(PngDetector.class, Scope.JAVA_FILE_SCOPE)
    );

    @Nullable
    @Override
    public Collection<String> getApplicableAttributes() {
        return Collections.singletonList(ATTR_SRC);
    }

    @Override
    public void visitAttribute(@NotNull XmlContext context, @NotNull Attr attribute) {
        String srcValue = attribute.getValue();
        if (check(srcValue.substring(10),
                context.getMainProject().getResourceFolders())) {
            context.report(
                    ISSUE_PNG_IN_XML,
                    attribute,
                    context.getLocation(attribute),
                    LINT_MSG
            );
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
                if (!UastExpressionUtils.isMethodCall(node)
                        || !"setImageResource".equals(node.getMethodName())
                        || node.getValueArgumentCount() != 1) {
                    return;
                }
                PsiMethod psiMethod = node.resolve();
                boolean b = context.getEvaluator().isMemberInClass(psiMethod, "android.widget.ImageView");
                if (b) {
                    if (check(String.valueOf(node.getValueArguments().get(0)).substring(11),
                            context.getMainProject().getResourceFolders())) {
                        context.report(
                                ISSUE_PNG_IN_CODE,
                                node,
                                context.getLocation(node),
                                LINT_MSG
                        );
                    }
                }
            }
        };
    }

    private boolean check(String imageName, List<File> resFolders) {
        File resFolder = null;
        for (File file : resFolders) {
            if ("res".equals(file.getName())) {
                resFolder = new File(file.getPath());
                break;
            }
        }
        if (resFolder == null) {
            return false;
        }
        File drawableFolder = new File(resFolder.getPath(), "drawable");
        if (!drawableFolder.exists() && !drawableFolder.isDirectory()) {
            return false;
        }
        // TODO: 2019/7/8 还需处理 drawable 目录下还有目录的情况
        String[] filesName = drawableFolder.list();
        if (filesName == null || filesName.length == 0) {
            return false;
        }

        for (String fileName : filesName) {
            if (fileName.substring(0, fileName.indexOf("."))
                    .equals(imageName)) {
                if (fileName.endsWith(".png")) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    @Override
    public boolean appliesTo(@NotNull ResourceFolderType folderType) {
        return folderType == ResourceFolderType.LAYOUT;
    }

    @Override
    public void run(@NotNull Context context) {
        assert false;
    }
}
