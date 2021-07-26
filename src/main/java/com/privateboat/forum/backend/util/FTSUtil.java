package com.privateboat.forum.backend.util;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FTSUtil {
    private static final Pattern CHINESE_PUNCTUATION = Pattern.compile("[！｜|，。（）《》“”？：；【】]");

    public static List<String> segment(String text) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<SegToken> tokens = segmenter.process(text, JiebaSegmenter.SegMode.SEARCH);

        return tokens.stream()
                .map(token -> token.word)
                .filter(lexeme -> lexeme != null && !lexeme.isBlank() && !CHINESE_PUNCTUATION.matcher(lexeme).find())
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
