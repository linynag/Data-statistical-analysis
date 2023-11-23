package com.ly.datastatisticalanalysis.machineLearning;

import com.huaban.analysis.jieba.JiebaSegmenter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.*;

import static com.ly.datastatisticalanalysis.machineLearning.TFIDFUtils.*;
import static com.ly.datastatisticalanalysis.machineLearning.cosineSimilarityUtils.getSimilarStatements;

public class SimilarityCalculator {

    public static void main(String[] args) {
        // 文档集合
        List<String> documents = Arrays.asList(
                "这是第一篇文档。",
                "这篇文档是第二篇文档。",
                "而这是第三篇文档。",
                "这是第一篇文档吗？"
        );

        String newStatement = "这是第四篇文档。";
        int topN = 3;

        List<String> similarStatements = getSimilarStatements(newStatement, documents, topN);

        System.out.println("输入新语句：\n" + newStatement);
        System.out.println("\n相似度最高的前 " + topN + " 个语句：");
        for (String statement : similarStatements) {
            System.out.println(statement);
        }
    }
}
