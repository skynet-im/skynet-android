package de.vectordata.skynet.ui.chat.views;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.vanniktech.emoji.EmojiTextView;

/**
 * An emoji text view that automatically scales up
 * emoji-only messages. Used in message items.
 */
public class VariableSizeEmojiTextView extends EmojiTextView {

    private float defaultEmojiSize;

    public VariableSizeEmojiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
        defaultEmojiSize = fontMetrics.descent - fontMetrics.ascent;
    }

    @Override
    public void setText(CharSequence rawText, BufferType type) {
        if (hasAsciiChars(rawText))
            setEmojiSize((int) defaultEmojiSize, false);
        else
            setEmojiSize((int) (defaultEmojiSize * 2.25), false);

        super.setText(rawText, type);
    }

    private boolean hasAsciiChars(CharSequence rawText) {
        for (int i = 0; i < rawText.length(); i++) {
            char chr = rawText.charAt(i);
            if (Character.isLetterOrDigit(chr) || Character.isWhitespace(chr))
                return true;
        }
        return false;
    }

}
