package com.jiaruiblog.justforonce.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CsvFileParser
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/7/17 10:29 下午
 * @Version 1.0
 **/
public class CsvFileParser {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    /**
     * @Author luojiarui
     * @Description 读取csv文件的第一行
     * @Date 10:34 下午 2022/7/17
     * @Param [bufferedReader]
     * @return java.util.List<java.lang.String>
     **/
    public static List<String> parseFirstLine(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        if ( line == null || "".equals(line)) {
            return null;
        }
        return parseLine(line);
    }

    /**
     * @Author luojiarui
     * @Description 读取csv的多行数据
     * @Date 10:36 下午 2022/7/17
     * @Param [bufferedReader]
     * @return java.util.List<java.lang.String>
     **/
    public static List<String> parseCsvLines(BufferedReader bufferedReader) throws IOException {
        String line;
        List<List<String>> cases = new ArrayList<>();
        //
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\r\n");
        }
        return parseLine(stringBuilder.toString());
    }

    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {
        List<String> result = new ArrayList<>();
        // if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }
        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }
        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }
        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;
        char[] chars = cvsLine.toCharArray();
        for (char c : chars) {
            if (inQuotes) {
                startCollectChar = true;
                if (c == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {
                    // Fixed : allow "" in custom quote enclosed
                    if (c == '"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(c);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(c);
                    }
                }
            } else {
                if (c == customQuote) {
                    inQuotes = true;
                    // Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }
                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }
                } else if (c == separators) {
                    result.add(curVal.toString());
                    curVal = new StringBuffer();
                    startCollectChar = false;
                } else if (c == '\r') {
                    // ignore LF characters
                    continue;
                } else if (c == '\n') {
                    // the end, break!
                    // break;
                    // modified by luojiarui
                    result.add(curVal.toString());
                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else {
                    curVal.append(c);
                }
            }
        }
        result.add(curVal.toString());
        return result;
    }

    public static void main(String[] args) throws Exception {

    }
}