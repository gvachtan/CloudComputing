package com.java.giorgos.elk;

import com.java.giorgos.elk.ElasticSearch.ESManager;
import com.java.giorgos.elk.ElasticSearch.IndexService;
import com.java.giorgos.elk.ElasticSearch.InfoService;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;

@RestController
public class IndexController {

    @Autowired
    ESManager esManager;

    @GetMapping("/index")
    public String getAllIndices() throws IOException {
        IndexService indexService = new IndexService(esManager.client);
        String[] results = indexService.GetIndices();

        JSONObject entity = new JSONObject();
        entity.put("indices", results);
        return entity.toString();
    }

    @GetMapping("/index/{indexName}")
    @ResponseBody
    public String getIndex(@PathVariable String indexName) {
        IndexService indexService = new IndexService(esManager.client);

        JSONObject entity = new JSONObject();
        entity.put("success", true);
        entity.put("data", indexService.ViewIndex(indexName));
        return entity.toString();
    }

    @DeleteMapping("/index/{indexName}")
    @ResponseBody
    public String deleteIndex(@PathVariable String indexName) {
        IndexService indexService = new IndexService(esManager.client);

        try {
            indexService.DeleteIndex(indexName);
            System.out.println("Delete was successful!");
            JSONObject entity = new JSONObject();
            entity.put("success", true);
            return entity.toString();
        } catch (Exception e) {
            System.out.println("Wasn't able to delete!");
            JSONObject entity = new JSONObject();
            entity.put("error", true);
            entity.put("success", false);
            return entity.toString();
        }
    }

    @PostMapping("/index/{indexName}")
    @ResponseBody
    public String createIndex(@PathVariable String indexName, @RequestBody String json) {
        IndexService indexService = new IndexService(esManager.client);
        try {
            indexService.CreateIndex(json, indexName);
            System.out.println("Create was successful!");
            JSONObject entity = new JSONObject();
            entity.put("success", true);
            return entity.toString();
        } catch (Exception e) {
            System.out.println("Wasn't able to create!");
            JSONObject entity = new JSONObject();
            entity.put("error", true);
            entity.put("success", false);
            return entity.toString();
        }
    }

    @PutMapping("/index/{indexName}/{docId}")
    @ResponseBody
    public String updateIndex(@PathVariable String indexName, @PathVariable String docId, @RequestBody String json) {
        IndexService indexService = new IndexService(esManager.client);
        try {
            indexService.UpdateDoc(indexName, docId, json);
            System.out.println("Update was successful!");
            JSONObject entity = new JSONObject();
            entity.put("success", true);
            return entity.toString();
        } catch (Exception e) {
            System.out.println("Update failed!");
            JSONObject entity = new JSONObject();
            entity.put("error", true);
            entity.put("success", false);
            return entity.toString();
        }
    }
}
