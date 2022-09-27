package com.java.giorgos.elk.ElasticSearch;

import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.core.Nullable;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.java.giorgos.elk.ElasticSearch.utils.ConsoleColors;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.xcontent.XContentType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class IndexService {

    static RestHighLevelClient client;

    public IndexService(RestHighLevelClient client) {
        IndexService.client = client;
    }

    public void CreateIndex(String json, String indexName) {
        try {
            IndexRequest req = new IndexRequest(indexName);
            req.source(json, XContentType.JSON);
            IndexResponse response = client.index(req, RequestOptions.DEFAULT);
            // TODO : check if index already exists
            System.out.println(ConsoleColors.BLUE + "Creating Index       ---->" + ConsoleColors.RESET + " name : "
                    + indexName + " | result : " + ConsoleColors.RED + response.getResult().toString()
                    + ConsoleColors.RESET);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void UpdateDoc(String indexName, String docId, String jsonString) {
        try {
            UpdateRequest request = new UpdateRequest(indexName, docId);
            request.doc(jsonString, XContentType.JSON);

            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);

            System.out.println(ConsoleColors.BLUE + "Creating Index       ---->" + ConsoleColors.RESET + " name : "
                    + indexName + " | result : " + ConsoleColors.RED + response.getResult().toString()
                    + ConsoleColors.RESET);

            System.out.println(response.toString());


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void DeleteIndex(String indexName) {
        try {
            DeleteIndexRequest req = new DeleteIndexRequest(indexName);
            AcknowledgedResponse response = client.indices().delete(req, RequestOptions.DEFAULT);
            // TODO : check if index exists
            System.out.println(ConsoleColors.BLUE + "Deleting Index       ---->" + ConsoleColors.RESET + " name : "
            + indexName + " | Acknowledged : " + ConsoleColors.RED + response.isAcknowledged()
            + ConsoleColors.RESET);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Nullable
    public String[] GetIndices() {
        try {
            GetIndexRequest request = new GetIndexRequest("*");
            GetIndexResponse response = client.indices().get(request, RequestOptions.DEFAULT);
            String[] indices = response.getIndices();
            System.out.println(indices);
            return indices;
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }
    }

    public Object[] ViewIndex(String indexName) {
        SearchRequest searchRequest = new SearchRequest(indexName);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		//searchSourceBuilder.size(1000);
//		searchSourceBuilder.sort("_id");
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		searchRequest.source(searchSourceBuilder);
		try {
			SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			SearchHit[] values = searchResponse.getHits().getHits();
			if(values.length > 0) {
				for(SearchHit s : values) {
					System.out.println(s.getSourceAsString());
				}
                return Arrays.stream(values).map(x -> {
                    x.getSourceAsMap().put("index", x.getIndex());
                    x.getSourceAsMap().put("docId", x.getId());
                    return x.getSourceAsMap();
                }).toArray();
			} else {
				System.out.println("No results found!");
                return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
            return null;
		}
    }
}

