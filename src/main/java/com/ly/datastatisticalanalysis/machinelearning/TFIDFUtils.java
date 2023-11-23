package com.ly.datastatisticalanalysis.machinelearning;

import com.huaban.analysis.jieba.JiebaSegmenter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TFIDFUtils {

    // 构建词汇表
    public static Map<String, Integer> buildVocabulary(List<String> documents) {
        Map<String, Integer> vocabulary = new HashMap<>();
        int index = 0;

        for (String document : documents) {
            List<String> words = segmentAndFilter(document);

            for (String word : words) {
                if (!vocabulary.containsKey(word)) {
                    vocabulary.put(word, index++);
                }
            }

        }
        return vocabulary;
    }

    // 计算 TF 矩阵
    public static double[][] calculateTF(List<String> documents, Map<String, Integer> vocabulary) {
        int vocabSize = vocabulary.size();
        double[][] tfMatrix = new double[documents.size()][vocabSize];

        for (int i = 0; i < documents.size(); i++) {
            List<String> words = segmentAndFilter(documents.get(i));

            for (String word : words) {
                if (vocabulary.containsKey(word)) {
                    tfMatrix[i][vocabulary.get(word)]++;
                }
            }
        }

        // 归一化
        for (int i = 0; i < documents.size(); i++) {
            RealVector vector = new ArrayRealVector(tfMatrix[i]);
            tfMatrix[i] = vector.mapDivide(vector.getL1Norm()).toArray();
        }

        return tfMatrix;
    }

    // 计算 IDF 向量
    public static double[] calculateIDF(List<String> documents, Map<String, Integer> vocabulary) {
        int docCount = documents.size();
        int vocabSize = vocabulary.size();
        double[] idfVector = new double[vocabSize];

        for (String word : vocabulary.keySet()) {
            int docsWithTerm = 0;
            for (String document : documents) {
                if (segmentAndFilter(document).contains(word)) {
                    docsWithTerm++;
                }
            }
            idfVector[vocabulary.get(word)] = Math.log((double) docCount / (1 + docsWithTerm));
        }

        return idfVector;
    }

    // 计算 TF-IDF 矩阵
    public static double[][] calculateTFIDF(double[][] tfMatrix, double[] idfVector) {
        int rowCount = tfMatrix.length;
        int colCount = tfMatrix[0].length;

        double[][] tfidfMatrix = new double[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                tfidfMatrix[i][j] = tfMatrix[i][j] * idfVector[j];
            }
        }

        return tfidfMatrix;
    }

    // 计算 TF-IDF 向量
    public static double[] calculateTFIDFVector(List<String> words, Map<String, Integer> vocabulary, double[][] tfMatrix, double[] idfVector) {
        int vocabSize = vocabulary.size();
        double[] tfidfVector = new double[vocabSize];

        for (String word : words) {
            if (vocabulary.containsKey(word)) {
                int wordIndex = vocabulary.get(word);
                double tf = tfMatrix[0][wordIndex];  // 假设tfMatrix是单个文档的TF矩阵
                tfidfVector[wordIndex] = calculateTFIDF(tf, idfVector[wordIndex]);
            }
        }

        return tfidfVector;
    }

    // 计算 TF-IDF
    public static double calculateTFIDF(double tf, double idf) {
        return tf * idf;
    }

    // 分词并过滤
    public static List<String> segmentAndFilter(String document) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<String> words = segmenter.sentenceProcess(document.replaceAll("[\\pP\\p{Punct}]", ""));
        return words;
    }

    // 打印矩阵
    public static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            for (double value : row) {
                System.out.printf("%.4f\t", value);
            }
            System.out.println();
        }
    }
}
