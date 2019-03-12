package lucene.lucene_core.analysis;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;

import java.io.IOException;
import java.util.*;

/**
 * <pre>
 *
 *  File: TokenStreamToECharts.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  可以将TokenStream绘制成图形，在ECharts中可以展示。
 *
 *  生成的json参数放到<a href="">http://echarts.baidu.com/examples/editor.html?c=graph-simple</a> 执行
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/3/11				    zouxiang				Initial.
 *
 * </pre>
 */
public class TokenStreamToECharts {
    public static void getEchartsJson(TokenStream ts) {
        int currentPosition = -1; // 当前 position
        Map<Pair<Integer, Integer>, List<String>> arcMap = new LinkedHashMap<>();
        CharTermAttribute charTermAttribute = ts.addAttribute(CharTermAttribute.class);
        PositionIncrementAttribute positionIncrementAttribute = ts.addAttribute(PositionIncrementAttribute.class);
        PositionLengthAttribute positionLengthAttribute = ts.addAttribute(PositionLengthAttribute.class);
        TermToBytesRefAttribute byteRefAttribute = ts.addAttribute(TermToBytesRefAttribute.class);
        try {
            ts.reset();
            while (ts.incrementToken()) {
                String term = charTermAttribute.toString();
                if (term == null || term.trim().length() == 0) {
                    term = byteRefAttribute.getBytesRef().utf8ToString();
                }
                int positionIncrement = positionIncrementAttribute.getPositionIncrement();
                int positionLength = positionLengthAttribute.getPositionLength();
                currentPosition += positionIncrement;
                int to = currentPosition + positionLength;
                Pair<Integer, Integer> pair = new Pair<>(currentPosition, to);
                if (!arcMap.containsKey(pair)) {
                    arcMap.put(pair, new ArrayList<String>());
                }
                arcMap.get(pair).add(term);
            }
            ts.end();
            for (Map.Entry<Pair<Integer, Integer>, List<String>> entry : arcMap.entrySet()) {
                String str = String.join(",", entry.getValue());
                System.out.println(entry.getKey().getLeft() + "\t" + entry.getKey().getRight() + "\t" + str);
            }
            String jsonStr = toJson(arcMap);
            System.out.println("option=" + jsonStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void getEchartsJson(Analyzer analyzer, String text) {
        TokenStream ts = analyzer.tokenStream("", text);
        getEchartsJson(ts);
    }

    private static String toJson(Map<Pair<Integer, Integer>, List<String>> arcMap) {
        int maxPosition = -1;
        for (Map.Entry<Pair<Integer, Integer>, List<String>> entry : arcMap.entrySet()) {
            Integer to = entry.getKey().getRight();
            if (to > maxPosition) {
                maxPosition = to;
            }
        }
        JSONObject result = new JSONObject();
        JSONArray series = new JSONArray();
        JSONObject s0 = new JSONObject();
        s0.put("type", "graph");
        s0.put("layout", "none");
        s0.put("symbolSize", 50);
        JSONObject label = new JSONObject();
        JSONObject normal = new JSONObject();
        normal.put("show", true);
        label.put("normal", normal);
        s0.put("label", label);
        s0.put("edgeSymbol", Arrays.asList("circle"));
        s0.put("edgeSymbolSize", Arrays.asList(4, 10));
        JSONObject textStyle = new JSONObject();
        textStyle.put("fontSize", 20);
        s0.put("edgeLabel", new JSONObject().put("normal", new JSONObject().put("textStyle", textStyle)));
        JSONArray data = new JSONArray();
        for (int i = 0; i <= maxPosition; i++) {
            JSONObject obj = new JSONObject();
            obj.put("name", i);
            obj.put("x", i * 100);
            obj.put("y", 300);
            data.add(obj);
        }
        s0.put("data", data);
        JSONArray links = new JSONArray();
        for (Map.Entry<Pair<Integer, Integer>, List<String>> entry : arcMap.entrySet()) {
            boolean isFirstWord = true;
            for (String word : entry.getValue()) {
                if (isFirstWord) {
                    isFirstWord = false;
                    JSONObject obj = new JSONObject();
                    int source = entry.getKey().getLeft();
                    int target = entry.getKey().getRight();
                    obj.put("source", source);
                    obj.put("target", target);
                    JSONObject lab = new JSONObject();
                    lab.put("show", true);
                    lab.put("formatter", word);
                    obj.put("label", lab);
                    float curveness = 0;
                    if (source + 1 != target) {
                        curveness = 0.5f;
                    }
                    JSONObject lineStyle = new JSONObject();
                    lineStyle.put("curveness", curveness);
                    obj.put("lineStyle", lineStyle);
                    links.add(obj);
                } else {
                    JSONObject obj = new JSONObject();
                    int source = entry.getKey().getLeft();
                    int target = entry.getKey().getRight();
                    obj.put("source", target);   // 这里取反!!!!!!
                    obj.put("target", source);
                    JSONObject lab = new JSONObject();
                    lab.put("show", true);
                    lab.put("formatter", word);
                    obj.put("label", lab);
                    float curveness = 0;
                    curveness = 0.5f;
                    JSONObject lineStyle = new JSONObject();
                    lineStyle.put("curveness", curveness);
                    obj.put("lineStyle", lineStyle);
                    links.add(obj);
                }
            }
        }
        s0.put("links", links);
        series.add(s0);
        result.put("series", series);
        return result.toJSONString();
    }
}
