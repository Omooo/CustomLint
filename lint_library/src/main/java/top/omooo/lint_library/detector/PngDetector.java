package top.omooo.lint_library.detector;

import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.ULiteralExpression;

/**
 * Author: Omooo
 * Date: 2019/7/5
 * Version: 
 * Desc: 
 */
@SuppressWarnings("UnstableApiUsage")
public class PngDetector extends Detector implements Detector.UastScanner {

    public static final Issue ISSUE = Issue.create(
            "PngUsage",
            "Png Usage",
            "Png is garbage",
            Category.CORRECTNESS,
            5,
            Severity.WARNING,
            new Implementation(PngDetector.class, Scope.JAVA_FILE_SCOPE)
    );

    @Nullable
    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {
        return Collections.singletonList(ULiteralExpression.class);
    }

    @Nullable
    @Override
    public UElementHandler createUastHandler(@NotNull JavaContext context) {
        return super.createUastHandler(context);
    }
}
