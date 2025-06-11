package com.yixuan.yh.live.repository;

import com.yixuan.yh.live.document.LiveDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LiveRepository extends ElasticsearchRepository<LiveDocument, Long> {
}
