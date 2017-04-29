package lab3.utem.my.mydailyexpenses;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class WebServiceCall {
    JSONObject jsonObject;
    String strURL = "";

    public WebServiceCall() {
        jsonObject = null;
        strURL = "http://192.168.1.108/global.php";
    }

    public String fnGetURL() {
        return strURL;
    }

    public JSONObject makeHTTPRequest(String url, String method, List<NameValuePair> params) {
        InputStream is = null;
        String json = "";
        JSONObject jObj = null;


        try {
            if (method == "POST") {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null) {
                sb.append(line+"\n");
            }
            is.close();
            json = sb.toString();
            jObj = new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jObj;
    }
}
