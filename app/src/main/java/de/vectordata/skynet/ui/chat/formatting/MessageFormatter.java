package de.vectordata.skynet.ui.chat.formatting;

import android.text.Spannable;
import android.text.SpannableStringBuilder;

public class MessageFormatter {

    private static final IMessageFormatting[] formattingSteps = new IMessageFormatting[]{
            new MarkdownFormatting(),
            new LinkFormatting()
    };

    public static Spannable format(CharSequence message) {
        if (message == null)
            message = "";

        SpannableStringBuilder builder = new SpannableStringBuilder(message);
        for (IMessageFormatting step : formattingSteps)
            step.format(builder);
        return builder;
    }

}
