//package com.amingge.repository.es;
//
//import com.amingge.pojo.EsBlog;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//
//public interface EsBlogRepository extends ElasticsearchRepository<EsBlog,String> {
//    Page<EsBlog> findDistinctEsBlogByTitleContainingOrSummaryContainingOrContentContainingOrTagsContaining(String title, String summary, String content, String tags,Pageable pageable);
//    EsBlog findByBlogId(Long blogId);
//}
//
