package com.itniuma.bitinn.service.search;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhrasePrefixQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhraseQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import com.itniuma.bitinn.pojo.Result;
import com.itniuma.bitinn.pojo.document.ArticleDocument;
import com.itniuma.bitinn.utils.RedisCacheHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ArticleSearchService {

    private final ElasticsearchOperations esOps;
    private final RedisCacheHelper redisCache;

    private static final String SEARCH_KEYWORD_ZSET = "search:keywords";
    private static final String SEARCH_KEYWORD_DAILY_ZSET = "search:keywords:daily:";
    private static final long KEYWORD_TTL_DAYS = 7;

    public ArticleSearchService(ElasticsearchOperations esOps, RedisCacheHelper redisCache) {
        this.esOps = esOps;
        this.redisCache = redisCache;
    }

    public Result search(String keyword, Integer pageNum, Integer pageSize, String sortType) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.error("搜索关键词不能为空");
        }
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        keyword = keyword.trim();
        recordKeyword(keyword);

        final String kw = keyword;

        MultiMatchQuery multiMatch = MultiMatchQuery.of(m -> m
                .fields("title^3", "excerpt^2", "content", "tags^2", "categoryName^1.5", "authorName^1.5")
                .query(kw)
                .fuzziness("AUTO")
        );

        MatchPhraseQuery matchPhrase = MatchPhraseQuery.of(m -> m
                .field("title")
                .query(kw)
                .boost(5.0f)
        );

        BoolQuery boolQuery = BoolQuery.of(b -> b
                .should(List.of(multiMatch._toQuery(), matchPhrase._toQuery()))
        );

        HighlightParameters highlightParams = HighlightParameters.builder()
                .withPreTags("<em class='highlight'>")
                .withPostTags("</em>")
                .build();

        List<HighlightField> highlightFields = List.of(
                new HighlightField("title"),
                new HighlightField("excerpt"),
                new HighlightField("content")
        );

        Highlight highlight = new Highlight(highlightParams, highlightFields);

        NativeQuery query = NativeQuery.builder()
                .withQuery(boolQuery._toQuery())
                .withHighlightQuery(new HighlightQuery(highlight, ArticleDocument.class))
                .withPageable(PageRequest.of(pageNum - 1, pageSize))
                .withSort(buildSort(sortType))
                .build();

        SearchHits<ArticleDocument> hits = esOps.search(query, ArticleDocument.class, IndexCoordinates.of("bitinn-article"));

        List<Map<String, Object>> articles = new ArrayList<>();
        for (SearchHit<ArticleDocument> hit : hits.getSearchHits()) {
            ArticleDocument doc = hit.getContent();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", doc.getId());
            item.put("title", getFirstHighlight(hit, "title", doc.getTitle()));
            item.put("excerpt", getFirstHighlight(hit, "excerpt", doc.getExcerpt()));
            item.put("contentHighlights", getHighlights(hit, "content"));
            item.put("coverImg", doc.getCoverImg());
            item.put("categoryName", doc.getCategoryName());
            item.put("tags", doc.getTags());
            item.put("authorName", doc.getAuthorName());
            item.put("authorAvatar", doc.getAuthorAvatar());
            item.put("likeCount", doc.getLikeCount());
            item.put("commentCount", doc.getCommentCount());
            item.put("favoriteCount", doc.getFavoriteCount());
            item.put("viewCount", doc.getViewCount());
            item.put("hotScore", doc.getHotScore());
            item.put("createTime", doc.getCreateTime());
            item.put("score", hit.getScore());
            articles.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", hits.getTotalHits());
        result.put("items", articles);
        result.put("keyword", keyword);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);

        return Result.success(result);
    }

    public Result suggest(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return Result.success(List.of());
        }
        prefix = prefix.trim();

        final String pf = prefix;

        MatchPhrasePrefixQuery prefixQuery = MatchPhrasePrefixQuery.of(m -> m
                .field("title")
                .query(pf)
        );

        NativeQuery titleSuggest = NativeQuery.builder()
                .withQuery(prefixQuery._toQuery())
                .withMaxResults(5)
                .withFields(List.of("title"))
                .build();

        SearchHits<ArticleDocument> titleHits = esOps.search(titleSuggest, ArticleDocument.class, IndexCoordinates.of("bitinn-article"));

        List<String> suggestions = new ArrayList<>();
        for (SearchHit<ArticleDocument> hit : titleHits.getSearchHits()) {
            String title = hit.getContent().getTitle();
            if (title != null && !suggestions.contains(title)) {
                suggestions.add(title);
            }
        }

        return Result.success(suggestions);
    }

    public Result getHotKeywords(int topN) {
        if (topN < 1) topN = 10;

        Set<ZSetOperations.TypedTuple<String>> tuples = redisCache.zReverseRangeWithScore(SEARCH_KEYWORD_ZSET, 0, topN - 1);

        List<Map<String, Object>> keywords = new ArrayList<>();
        if (tuples != null) {
            for (ZSetOperations.TypedTuple<String> tuple : tuples) {
                if (tuple.getValue() != null) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("keyword", tuple.getValue());
                    item.put("count", tuple.getScore() != null ? tuple.getScore().longValue() : 0);
                    keywords.add(item);
                }
            }
        }

        Long totalArticles = redisCache.zCard(SEARCH_KEYWORD_ZSET);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("keywords", keywords);
        result.put("totalKeywords", totalArticles != null ? totalArticles : 0);

        return Result.success(result);
    }

    private void recordKeyword(String keyword) {
        try {
            redisCache.zIncrementScore(SEARCH_KEYWORD_ZSET, keyword, 1);

            String dailyKey = SEARCH_KEYWORD_DAILY_ZSET + java.time.LocalDate.now().toString();
            redisCache.zIncrementScore(dailyKey, keyword, 1);
            redisCache.expire(dailyKey, KEYWORD_TTL_DAYS, TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("[ES搜索] 记录搜索关键词失败: {}", keyword, e);
        }
    }

    private Sort buildSort(String sortType) {
        if ("hot".equals(sortType)) {
            return Sort.by(Sort.Order.desc("hotScore"), Sort.Order.desc("createTime"));
        } else if ("latest".equals(sortType)) {
            return Sort.by(Sort.Order.desc("createTime"));
        }
        return Sort.unsorted();
    }

    private String getFirstHighlight(SearchHit<ArticleDocument> hit, String field, String fallback) {
        List<String> highlights = hit.getHighlightField(field);
        return (highlights != null && !highlights.isEmpty()) ? highlights.get(0) : fallback;
    }

    private List<String> getHighlights(SearchHit<ArticleDocument> hit, String field) {
        List<String> highlights = hit.getHighlightField(field);
        return highlights != null ? highlights : List.of();
    }
}
