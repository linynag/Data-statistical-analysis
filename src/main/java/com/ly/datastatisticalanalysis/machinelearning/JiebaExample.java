package com.ly.datastatisticalanalysis.machinelearning;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JiebaExample {
    public static void main(String[] args) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        String sentence = "服务站现场检查排插绝缘，发现电池高压箱MSD破损导致进水绝缘，已联系亿纬赵工现场判断，同时从另外台车拆卸对调MSD保险后，车辆恢复正常。";
        log.info("分词结果：");
        for (SegToken token : segmenter.process(sentence, JiebaSegmenter.SegMode.INDEX)) {
            log.info(token.word);
        }
    }

    // 分词函数
    static List<String> segmentText(String text) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<String> words = new ArrayList<>();
        for (SegToken token : segmenter.process(text, JiebaSegmenter.SegMode.INDEX)) {
            words.add(token.word);
        }
        return words;
    }
}