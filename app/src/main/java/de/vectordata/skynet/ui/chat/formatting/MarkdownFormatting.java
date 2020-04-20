package de.vectordata.skynet.ui.chat.formatting;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;

import java.util.HashMap;
import java.util.Map;

public class MarkdownFormatting implements IMessageFormatting {

    @Override
    public void format(SpannableStringBuilder builder) {
        Map<Formatting, FormatState> formatStates = new HashMap<>();
        for (Formatting formatting : Formatting.values())
            formatStates.put(formatting, new FormatState(false, 0));

        for (int i = 0; i < builder.length(); i++) {
            char chr = builder.charAt(i);

            Map.Entry<Formatting, FormatState> state = getState(formatStates, chr);
            if (state == null) // Not a formatting-related char
                continue;

            if (state.getValue().isOpen()) {
                // Close it
                builder.setSpan(state.getKey().toSpanObject(), state.getValue().getStartIndex(), i, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                state.getValue().end();
            } else {
                // Open it
                state.getValue().begin(i);
            }
        }
    }

    private Map.Entry<Formatting, FormatState> getState(Map<Formatting, FormatState> states, char chr) {
        for (Map.Entry<Formatting, FormatState> state : states.entrySet()) {
            if (state.getKey().getFlagChar() == chr) {
                return state;
            }
        }
        return null;
    }

    private enum Formatting {
        Bold('*'),
        Italic('_'),
        Strikeout('~'),
        SourceCode('`');

        private char flagChar;

        Formatting(char flagChar) {
            this.flagChar = flagChar;
        }

        public char getFlagChar() {
            return flagChar;
        }

        public Object toSpanObject() {
            if (this == Bold)
                return new StyleSpan(Typeface.BOLD);
            else if (this == Italic)
                return new StyleSpan(Typeface.ITALIC);
            else if (this == Strikeout)
                return new StrikethroughSpan();
            else if (this == SourceCode)
                return new TypefaceSpan("monospace");

            throw new IllegalStateException("Unknown formatting type " + this.name());
        }

    }

    private static class FormatState {
        private boolean isOpen;
        private int startIndex;

        FormatState(boolean isOpen, int startIndex) {
            this.isOpen = isOpen;
            this.startIndex = startIndex;
        }

        boolean isOpen() {
            return isOpen;
        }

        int getStartIndex() {
            return startIndex;
        }

        void begin(int idx) {
            isOpen = true;
            startIndex = idx;
        }

        void end() {
            isOpen = false;
            startIndex = 0;
        }
    }

}
