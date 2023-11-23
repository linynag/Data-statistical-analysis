package com.ly.datastatisticalanalysis.machinelearning;

import com.huaban.analysis.jieba.JiebaSegmenter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.*;

public class TFIDFWithJiebaExample {
    public static void main(String[] args) {
        // 文档集合
        String[] documents = {
            "这是第一篇文档。",
            "这篇文档是第二篇文档。",
            "而这是第三篇文档。",
            "这是第一篇文档吗？"
        };

        // 构建词汇表
        Map<String, Integer> vocabulary = buildVocabulary(documents);

        // 构建 TF 矩阵
        double[][] tfMatrix = calculateTF(documents, vocabulary);

        // 构建 IDF 向量
        double[] idfVector = calculateIDF(documents, vocabulary);

        // 构建 TF-IDF 矩阵
        double[][] tfidfMatrix = calculateTFIDF(tfMatrix, idfVector);

        System.out.println("Vocabulary: " + vocabulary.keySet());
        // 输出 TF-IDF 矩阵
        printMatrix(tfidfMatrix);
    }

    // 构建词汇表
     static Map<String, Integer> buildVocabulary(String[] documents) {
        Map<String, Integer> vocabulary = new HashMap<>();
        int index = 0;
        JiebaSegmenter segmenter = new JiebaSegmenter();
        
        for (String document : documents) {
            List<String> words = segmentAndFilter(document, segmenter);

            for (String word : words) {
                if (!vocabulary.containsKey(word)) {
                    vocabulary.put(word, index++);
                }
            }
        }
        return vocabulary;
    }

    // 计算 TF 矩阵
     static double[][] calculateTF(String[] documents, Map<String, Integer> vocabulary) {
        int vocabSize = vocabulary.size();
        double[][] tfMatrix = new double[documents.length][vocabSize];
        JiebaSegmenter segmenter = new JiebaSegmenter();

        for (int i = 0; i < documents.length; i++) {
            List<String> words = segmentAndFilter(documents[i], segmenter);

            for (String word : words) {
                if (vocabulary.containsKey(word)) {
                    tfMatrix[i][vocabulary.get(word)]++;
                }
            }
        }

        // 归一化
        for (int i = 0; i < documents.length; i++) {
            RealVector vector = new ArrayRealVector(tfMatrix[i]);
            tfMatrix[i] = vector.mapDivide(vector.getL1Norm()).toArray();
        }

        return tfMatrix;
    }

    // 计算 IDF 向量
     static double[] calculateIDF(String[] documents, Map<String, Integer> vocabulary) {
        int docCount = documents.length;
        int vocabSize = vocabulary.size();
        double[] idfVector = new double[vocabSize];

        for (String word : vocabulary.keySet()) {
            int docsWithTerm = 0;
            for (String document : documents) {
                if (segmentAndFilter(document, new JiebaSegmenter()).contains(word)) {
                    docsWithTerm++;
                }
            }
            idfVector[vocabulary.get(word)] = Math.log((double) docCount / (1 + docsWithTerm));
        }

        return idfVector;
    }

    // 计算 TF-IDF 矩阵
     static double[][] calculateTFIDF(double[][] tfMatrix, double[] idfVector) {
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

    // 打印矩阵
     static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            for (double value : row) {
                System.out.printf("%.4f\t", value);
            }
            System.out.println();
        }
    }


    // 使用 JiebaSegmenter 进行分词并剔除标点符号
     static List<String> segmentAndFilter(String document, JiebaSegmenter segmenter) {
        List<String> words = segmenter.sentenceProcess(document.replaceAll("[\\pP\\p{Punct}]", ""));
        return words;
    }




}
