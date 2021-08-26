package com.privateboat.forum.backend.util;

import com.privateboat.forum.backend.configuration.DataSourceConfig;
import com.privateboat.forum.backend.enumerate.PreferenceDegree;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Term;
import org.ansj.library.StopLibrary;
import org.ansj.splitWord.Analysis;
import org.apache.mahout.cf.taste.impl.model.jdbc.PostgreSQLJDBCDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RecommendUtil<T extends Analysis> {

    private static final Map<String, Double> POS_SCORE = new HashMap<>();
    public static final Map<PreferenceDegree, Integer> DEGREE2VALUE = new HashMap<>();
    public static final Map<Integer, PreferenceDegree> VALUE2DEGREE = new HashMap<>();

    private static final String PREFERENCE_POST_TABLE = "preference_post";
    private static final String USER_ID_COLUMN = "user_id";
    private static final String POST_ID_COLUMN = "post_id";
    private static final String PREFERENCE_COLUMN = "preference_degree";
    private static final String TIMESTAMP_COLUMN = "browse_time";

    private T analysisType;

    @Autowired
    DataSourceConfig dataSourceConfig;

    public void setAnalysisType(T analysisType) {
        this.analysisType = analysisType;
    }


    public PostgreSQLJDBCDataModel getDataSource(){
//        ConnectionPoolDataSource connectionPoolDataSource = new ConnectionPoolDataSource(dataSourceConfig.getDataSource());
        return new PostgreSQLJDBCDataModel(dataSourceConfig.getDataSource(), PREFERENCE_POST_TABLE, USER_ID_COLUMN, POST_ID_COLUMN, PREFERENCE_COLUMN, TIMESTAMP_COLUMN);
    }

    public List<Keyword> computeArticleTfidf(String title, String content, int nKeyword) {
        Map<String, Keyword> tm = new HashMap<>();

        List<Term> parse = this.analysisType.parseStr(title + '\t' + content).recognition(StopLibrary.get()).getTerms();

        for (Term term : parse) {
            double weight = this.getWeight(term, content.length(), title.length());
            if (weight != 0.0D) {
                Keyword keyword = (Keyword) tm.get(term.getName());
                if (keyword == null) {
                    keyword = new Keyword(term.getName(), term.termNatures().allFreq, weight);
                    tm.put(term.getName(), keyword);
                } else {
                    keyword.updateWeight(1);
                }
            }
        }

        TreeSet<Keyword> treeSet = new TreeSet<>(tm.values());
        ArrayList<Keyword> arrayList = new ArrayList<>(treeSet);
        if (treeSet.size() <= nKeyword) {
            return arrayList;
        } else {
            return arrayList.subList(0, nKeyword);
        }
    }

    private double getWeight(Term term, int length, int titleLength) {
        if (term.getName().trim().length() < 2) {
            return 0.0D;
        } else {
            String pos = term.natrue().natureStr;
            Double posScore = (Double)POS_SCORE.get(pos);
            if (posScore == null) {
                posScore = 1.0D;
            } else if (posScore == 0.0D) {
                return 0.0D;
            }

            return titleLength > term.getOffe() ? 5.0D * posScore : (double)(length - term.getOffe()) * posScore / (double)length;
        }
    }

    static {
        // begin initiate POS_SCORE
        POS_SCORE.put("null", 0.0D);
        POS_SCORE.put("w", 0.0D);
        POS_SCORE.put("en", 0.0D);
        POS_SCORE.put("m", 0.0D);
        POS_SCORE.put("num", 0.0D);
        POS_SCORE.put("nr", 3.0D);
        POS_SCORE.put("nrf", 3.0D);
        POS_SCORE.put("nw", 3.0D);
        POS_SCORE.put("nt", 3.0D);
        POS_SCORE.put("l", 0.2D);
        POS_SCORE.put("a", 0.2D);
        POS_SCORE.put("nz", 3.0D);
        POS_SCORE.put("v", 0.2D);
        POS_SCORE.put("kw", 6.0D);

        // begin initiate PRE2VALUE convert map
        DEGREE2VALUE.put(PreferenceDegree.BROWSE, 1);
        DEGREE2VALUE.put(PreferenceDegree.REPLY, 2);
        DEGREE2VALUE.put(PreferenceDegree.STAR, 3);

        VALUE2DEGREE.put(1, PreferenceDegree.BROWSE);
        VALUE2DEGREE.put(2, PreferenceDegree.REPLY);
        VALUE2DEGREE.put(3, PreferenceDegree.STAR);
    }
}
