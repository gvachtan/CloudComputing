package com.java.giorgos.elk.ElasticSearch;

import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import javax.net.ssl.SSLContext;

import com.java.giorgos.elk.ElasticSearch.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class ESManager {
    public RestHighLevelClient client;

    public ESManager() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        client = CreateHighLevelClient();
    }

    public static RestHighLevelClient CreateHighLevelClient()
            throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, KeyManagementException {

        boolean useSSL = true;
        boolean useCrt = true;

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(Project_Properties.Elasticsearch_username, Project_Properties.Elasticsearch_password));

        RestClientBuilder lowLevelClientBuilder = RestClient.builder(
                new HttpHost("localhost", 9200, "https")
        );

        if (!useSSL) {  // Without TLS
            lowLevelClientBuilder.setHttpClientConfigCallback(
                new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                }
            );
        } else if (useCrt) {
            Path caCertificatePath = Paths.get(Project_Properties.Elasticsearch_http_crt);
            CertificateFactory factory =
                    CertificateFactory.getInstance("X.509");
            Certificate trustedCa;
            try (InputStream is = Files.newInputStream(caCertificatePath)) {
                trustedCa = factory.generateCertificate(is);
            }
            KeyStore trustStore = KeyStore.getInstance("pkcs12");
            trustStore.load(null, null);
            trustStore.setCertificateEntry("ca", trustedCa);
            SSLContextBuilder sslContextBuilder = SSLContexts.custom()
                    .loadTrustMaterial(trustStore, null);
            final SSLContext sslContext = sslContextBuilder.build();
            lowLevelClientBuilder
                    .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                        @Override
                        public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                            return httpClientBuilder
                                    .setSSLContext(sslContext)
                                    .setDefaultCredentialsProvider(credentialsProvider);
                        }
                    });
        } else {

            Path trustStorePath = Paths.get(Project_Properties.trustStorePath);
            Path keyStorePath = Paths.get(Project_Properties.keyStorePath);
            KeyStore trustStore = KeyStore.getInstance("pkcs12");
            KeyStore keyStore = KeyStore.getInstance("pkcs12");
            String trustStorePass="";
            String keyStorePass="";

            try (InputStream is = Files.newInputStream(trustStorePath)) {

                trustStore.load(is, trustStorePass.toCharArray());
            }
            try (InputStream is = Files.newInputStream(keyStorePath)) {

                keyStore.load(is, keyStorePass.toCharArray());
            }

            try {
            final SSLContext sslcontext = SSLContextBuilder.create().loadTrustMaterial(keyStore, new
                 TrustSelfSignedStrategy()).build();

                 lowLevelClientBuilder.setHttpClientConfigCallback(new
                 RestClientBuilder.HttpClientConfigCallback() {
                   @Override
                   public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                     return httpClientBuilder.setSSLContext(sslcontext)
                                  .setDefaultCredentialsProvider(credentialsProvider);
                     }
                 });
                }
            catch (Exception e) {
                System.out.println(ConsoleColors.RED+e.getMessage()+ConsoleColors.RESET);
            }
        }

        RestHighLevelClient highLevelClient = new RestHighLevelClient(lowLevelClientBuilder);
        return highLevelClient;
    }
      
    
    public void CloseHighLevelCLient(RestHighLevelClient HighClient) throws IOException {
        System.out.println(ConsoleColors.WHITE+"Shutting down HighLevel Connection....."+ConsoleColors.RED+"OK");
        HighClient.close();
    }
    public void CloseLowLevelCLient(RestClient LowLevelClient) throws IOException {
        System.out.println(ConsoleColors.WHITE+"Shutting down LowLevel Connection....."+ConsoleColors.RED+"OK");
        LowLevelClient.close();
    }

    public void CloseAllCLients(RestHighLevelClient HighClient, RestClient LowLevelClient) throws IOException {

        HighClient.close();
        LowLevelClient.close();
    }
        
                
                
        
}
