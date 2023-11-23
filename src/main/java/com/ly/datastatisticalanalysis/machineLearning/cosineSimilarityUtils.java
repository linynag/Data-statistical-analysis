package com.ly.datastatisticalanalysis.machineLearning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ly.datastatisticalanalysis.machineLearning.TFIDFUtils.*;

public class cosineSimilarityUtils {
    private static final double EPSILON = 1e-10;

    // 计算余弦相似度
    private static double cosineSimilarity(double[] vector1, double[] vector2) {
        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;

        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += Math.pow(vector1[i], 2);
            norm2 += Math.pow(vector2[i], 2);
        }

        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);

        if (norm1 * norm2 < EPSILON) {
            return 0.0;
        } else {
            return dotProduct / (norm1 * norm2);
        }
    }

    // 输入新语句，返回与文档中相似度最高的语句
    public static List<String> getSimilarStatements(String newStatement, List<String> documents, int topN) {
        // 分词并过滤
        List<String> newWords = segmentAndFilter(newStatement);

        // 构建词汇表
        Map<String, Integer> vocabulary = buildVocabulary(documents);

        // 计算 TF 矩阵
        double[][] tfMatrix = calculateTF(documents, vocabulary);

        // 计算 IDF 向量
        double[] idfVector = calculateIDF(documents, vocabulary);

        // 计算 TF-IDF 矩阵
        double[][] tfidfMatrix = calculateTFIDF(tfMatrix, idfVector);

        // 计算新语句的 TF-IDF 向量
        double[] newTFIDF = calculateTFIDFVector(newWords, vocabulary, tfMatrix, idfVector);

        // 计算新语句与文档中每个语句的余弦相似度
        List<Double> similarities = new ArrayList<>();
        for (double[] docVector : tfidfMatrix) {
            double similarity = cosineSimilarity(newTFIDF, docVector);
            similarities.add(similarity);
        }

        // 获取相似度最高的前 topN 个语句
        List<String> result = new ArrayList<>();
        for (int i : getTopNIndices(similarities, topN)) {
            result.add(documents.get(i));
        }

        return result;
    }

    // 获取相似度最高的前 topN 个索引
    private static List<Integer> getTopNIndices(List<Double> list, int topN) {
        Map<Integer, Double> indexToValue = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            indexToValue.put(i, list.get(i));
        }

        List<Integer> indices = new ArrayList<>(indexToValue.keySet());
        indices.sort((i1, i2) -> Double.compare(indexToValue.get(i2), indexToValue.get(i1)));

        return indices.subList(0, Math.min(topN, indices.size()));
    }
}
