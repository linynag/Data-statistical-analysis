package com.ly.datastatisticalanalysis.machinelearning;

import java.util.*;

import static com.ly.datastatisticalanalysis.machinelearning.CosineSimilarityUtils.getSimilarStatements;

/**
 * 推送最接近的语句
 */
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
