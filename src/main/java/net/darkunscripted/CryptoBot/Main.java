package net.darkunscripted.CryptoBot;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    public static void main(String[] args) throws Exception {

        Main obj = new Main();

        try {
            System.out.println("Testing 1 - Send Http GET request");
            obj.sendGet();
        } finally {
            obj.close();
        }
    }

    private void close() throws IOException {
        httpClient.close();
    }

    private void sendGet() throws Exception {

        HttpGet request = new HttpGet("https://api.kraken.com/0/public/Ticker?pair=BTCUSD,ETHUSD,DOGEUSD");

        // add request headers
        request.addHeader("Accept", "application/json");
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Accept-Charset", "utf-8");
//        request.addHeader("pair", "AAVEAUD");
//        request.addHeader(HttpHeaders.USER_AGENT, "Googlebot");

        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();

            if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString(entity);
                JSONArray exchanges = new JSONObject(result).getJSONObject("result").names();
                for(int i=0; i<exchanges.length(); i++) {
                    JSONArray bidArray = new JSONObject(result).getJSONObject("result").getJSONObject(exchanges.get(i).toString()).getJSONArray("b");
                    HttpGet httpRequest = new HttpGet("https://api.kraken.com/0/public/Assets?asset=" + exchanges.get(i).toString().substring(0, (exchanges.get(i).toString().length() / 2)));

                    // add request headers
                    request.addHeader("Accept", "application/json");
                    request.addHeader("Content-Type", "application/json");
                    request.addHeader("Accept-Charset", "utf-8");

                    try (CloseableHttpResponse resp = httpClient.execute(httpRequest)) {

                        // Get HttpResponse Status
                        HttpEntity httpEntity = resp.getEntity();
                        Header header = httpEntity.getContentType();

                        if (httpEntity != null) {
                            // return it as a String
                            String results = EntityUtils.toString(httpEntity);
                            JSONObject resultObject = new JSONObject(results);
                            String name = exchanges.get(i).toString().substring(0, (exchanges.get(i).toString().length() / 2));
                            while (name.length() < 4){
                                name = "X" + name;
                            }
                            System.out.println(resultObject.getJSONObject("result").getJSONObject(name).getString("altname") + ": " + bidArray.getBigDecimal(0));
                        }
                    }
                }
            }

        }

    }

    private void sendPost() throws Exception {

        HttpPost post = new HttpPost("https://httpbin.org/post");

        // add request parameter, form parameters
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("username", "abc"));
        urlParameters.add(new BasicNameValuePair("password", "123"));
        urlParameters.add(new BasicNameValuePair("custom", "secret"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

            System.out.println(EntityUtils.toString(response.getEntity()));
        }

    }

}
