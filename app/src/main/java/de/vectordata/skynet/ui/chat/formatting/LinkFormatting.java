package de.vectordata.skynet.ui.chat.formatting;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LinkFormatting implements IMessageFormatting {

    @Override
    public void format(SpannableStringBuilder builder) {
        List<Candidate> linkCandidates = findLinkCandidates(builder);

        for (Candidate candidate : linkCandidates)
            if (isValidLink(candidate))
                builder.setSpan(new URLSpan(formatLink(candidate)), candidate.startIdx, candidate.endIdx, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    private String formatLink(Candidate candidate) {
        if (candidate.content.startsWith("http"))
            return candidate.content;
        else
            return "http://" + candidate.content;
    }

    private boolean isValidLink(Candidate candidate) {
        // Is it a valid URL?
        try {
            new URL(candidate.content);
            return true;
        } catch (MalformedURLException ignored) {
        }

        // Try to extract information about the TLD
        int tldBegin = -1;
        int tldEnd = candidate.content.length() - 1;
        for (int i = 0; i < candidate.content.length(); i++) {
            char chr = candidate.content.charAt(i);
            if (chr == '.')
                tldBegin = i;
            else if (chr == '/') {
                tldEnd = i;
                break;
            }
        }

        // No dots in the URL -> not a valid URL
        if (tldBegin == -1)
            return false;

        // The longest TLD is 24 characters long, longer than that and we ignore it
        if (tldEnd - tldBegin > 24)
            return false;

        // No one- and zero-length TLDs
        if (tldEnd - tldBegin < 2)
            return false;

        // TLDs do not have uppercase characters in them
        for (int i = tldBegin; i <= tldEnd; i++)
            if (Character.isUpperCase(candidate.content.charAt(i)))
                return false;

        return TLD.isValid(candidate.content.substring(tldBegin + 1, tldEnd + 1));
    }

    private List<Candidate> findLinkCandidates(SpannableStringBuilder builder) {
        List<Candidate> candidates = new ArrayList<>();
        StringBuilder candidateBuilder = new StringBuilder();
        int startIdx = 0;

        for (int i = 0; i < builder.length(); i++) {
            char chr = builder.charAt(i);
            if (chr == ' ') {
                candidates.add(new Candidate(startIdx, i, candidateBuilder.toString()));
                candidateBuilder = new StringBuilder();
                startIdx = i + 1; // Skip the whitespace!
            } else
                candidateBuilder.append(chr);
        }
        if (candidateBuilder.length() != 0)
            candidates.add(new Candidate(startIdx, builder.length(), candidateBuilder.toString()));
        return candidates;
    }

    private class Candidate {

        private int startIdx;
        private int endIdx;
        private String content;

        public Candidate(int startIdx, int endIdx, String content) {
            this.startIdx = startIdx;
            this.endIdx = endIdx;
            this.content = content;
        }

    }

}
