package kr.co.core.responsepeople.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CHttpUrlConnection {

    private final String TAG = "ChttpUrlConnection";

    final String boundary = "boundary";

    public String postCall(String url){
        String response = null;
        try{
            URL mUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) mUrl.openConnection();

            //서버로부터 메세지를 받을수 있도록 설정
            con.setDoInput(true);

            //헤더값을 설정
            con.setRequestProperty("Content-Type", "application/x-www-from-urlencoded");

            //전달방식을 설정한다(기본값은GET)
            con.setRequestMethod("GET");

            //서버로 데이터를 전송할 수 있도록 한다. GET방식이면 사용될 일이 없으나, true로 설정하면 자동으로 POST로 설정된다. 기본값은 false이다.
            con.setDoOutput(false);

            int responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                InputStream is = con.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] byteBuffer = new byte[1024];
                byte[] byteData = null;
                int nLength = 0;
                while ((nLength = is.read(byteBuffer,0, byteBuffer.length))!= -1){
                    baos.write(byteBuffer, 0, nLength);
                }
                byteData = baos.toByteArray();
                response = new String(byteData);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }

    public String sendPost(String url, HashMap<String, String> map)
    {
        String response = "";
        try{
            URL mUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)mUrl.openConnection();
            conn.setConnectTimeout(10000);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            if(map != null)
            {
                OutputStream os = conn.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bw.write(getPostString(map));
                bw.flush();
                bw.close();
                os.close();
            }

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                Log.d(TAG,"연결성공");
                String line="";
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while((line = br.readLine()) != null){
                    response += line;
                }
            }else{
                Log.d(TAG,"연결오류"+conn.getResponseCode());
            }
            conn.disconnect();
        }catch(Exception e){
            e.printStackTrace();
        }

        Log.d("TEST@@", "CHECK POINT3 : " + response);
//        Logger.json(response);
        return response;
    }


//    public String sendNodePost(String url, HashMap<String, String> map)
//    {
//        HttpURLConnection con = null;
//        BufferedReader reader = null;
//        try{
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.accumulate("type", "getRoomIs");
//            jsonObject.accumulate("host_user_idx", "5");
//            jsonObject.accumulate("guest_user_idx", "3");
//            jsonObject.accumulate("user_idx", "5");
//
//
//
//            URL mUrl = new URL(url);
//            con = (HttpURLConnection)mUrl.openConnection();
//
//
//            con.setRequestMethod("POST");//POST방식으로 보냄
//
//            con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
//
//            con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
//
//            con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
//            con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
//            con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
//            con.connect();
//            //서버로 보내기위해서 스트림 만듬
//            OutputStream outStream = con.getOutputStream();
//            //버퍼를 생성하고 넣음
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
//            writer.write(jsonObject.toString());
//            writer.flush();
//            writer.close();//버퍼를 받아줌
//            //서버로 부터 데이터를 받음
//            InputStream stream = con.getInputStream();
//            reader = new BufferedReader(new InputStreamReader(stream));
//            StringBuffer buffer = new StringBuffer();
//            String line = "";
//
//            while((line = reader.readLine()) != null){
//                buffer.append(line);
//            }
//
//            Log.d(TAG,"응답 : "+buffer.toString());
//            return buffer.toString();
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }finally {
//            if(con != null){
//                con.disconnect();
//            }
//            try {
//                if(reader != null){
//                    reader.close();//버퍼를 닫아줌
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }


    private String getPostString(HashMap<String, String> map) {
        StringBuilder result = new StringBuilder();
        boolean first = true; // 첫 번째 매개변수 여부

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (first)
                first = false;
            else // 첫 번째 매개변수가 아닌 경우엔 앞에 &를 붙임
                result.append("&");
            try { // UTF-8로 주소에 키와 값을 붙임
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));

            } catch (UnsupportedEncodingException ue) {
                ue.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG,"result : "+result.toString());
        return result.toString();
    }


}
