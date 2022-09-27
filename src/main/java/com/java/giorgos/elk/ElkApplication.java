package com.java.giorgos.elk;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import com.java.giorgos.elk.ElasticSearch.ESManager;
//import com.java.giorgos.elk.ElasticSearch.InfoService;
import com.java.giorgos.elk.ElasticSearch.InfoService;
import com.java.giorgos.elk.ElasticSearch.utils.ConsoleColors;
import com.java.giorgos.elk.ElasticSearch.utils.SpringBootEnv;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class ElkApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElkApplication.class, args);

        try {
            ESManager esManager = new ESManager();


            InfoService infoService = new InfoService(esManager.client);
            infoService.ShowClusterInfo();

            System.out.println(ConsoleColors.BLUE_BRIGHT + "Number of Indexes    ----> " + ConsoleColors.RESET
                + infoService.CountIndexes());

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
	}
}
