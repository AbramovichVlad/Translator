package com.pet.app.translator.translator;

import android.os.Handler;
import android.os.Looper;
import android.text.Html;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class TranslatorText {

    private String codeLanguageTo = "en";

    public TranslatorText(String codeLanguageTo) {
        this.codeLanguageTo = codeLanguageTo;
    }

    public interface Callback {
        public void onResult(String translatedText, String codeLang);
    }

    private String parseAnswer(String str) {
        JSONArray jSONArray;
        try {
            jSONArray = (JSONArray) new JSONArray(str).get(0);
        } catch (JSONException e) {
            e.printStackTrace();
            jSONArray = null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < jSONArray.length(); i++) {
            try {
                sb.append(((JSONArray) jSONArray.get(i)).get(0).toString());
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        if (sb.length() > 100) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public void translateText(final String textForTranslate, Callback callback) {
        final AtomicReference<String>[] resultText = new AtomicReference[]{new AtomicReference<>("")};

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String keyTranslate = "trans";
            String keyTerms = "terms";
            String keySentences = "sentences";
            String keyDict = "dict";

            StringBuilder sb = new StringBuilder(textForTranslate);
            sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
            String textLower = sb.toString();

            String textEncode = textLower;
            try {
                textEncode = URLEncoder.encode(textLower, UTF_VALUE);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String format = String.format(byteArrayToString(stringToByteArray("aHR0cHM6Ly90cmFuc2xhdGUuZ29vZ2xlLmNvbS90cmFuc2xhdGVfYS90P2NsaWVudD1hdCZzYz0xJnY9Mi4wJnNsPSVzJnRsPSVzJmhsPW5sJmllPVVURi04Jm9lPVVURi04JnRleHQ9JXM=")), CODE_LANG_FROM, codeLanguageTo, textEncode);

            try {
                DefaultHttpClient defaultHttpClient = getHttpClient();
                String readLine = new BufferedReader(new InputStreamReader(defaultHttpClient.execute(new HttpPost(format)).getEntity().getContent(), StandardCharsets.UTF_8), 8).readLine();
                if (readLine != null) {
                    if (readLine.length() > 0) {
                        JSONObject jSONObject = new JSONObject(readLine);
                        StringBuilder stringBuilderResult = new StringBuilder();
                        jSONObject.getJSONArray(keySentences);
                        for (int i = 0; i < jSONObject.getJSONArray(keySentences).length(); i++) {
                            if (jSONObject.getJSONArray(keySentences).getJSONObject(i).has(keyTranslate)) {
                                stringBuilderResult.append(jSONObject.getJSONArray(keySentences).getJSONObject(i).getString(keyTranslate));
                            }
                        }
                        if (jSONObject.has(keyDict)) {
                            stringBuilderResult.append("\n\n###dict");
                            for (int i2 = 0; i2 < jSONObject.getJSONArray(keyDict).length(); i2++) {
                                jSONObject.getJSONArray(keyDict).getJSONObject(i2).getJSONArray(keyTerms);
                                for (int i3 = 0; i3 < jSONObject.getJSONArray(keyDict).getJSONObject(i2).getJSONArray(keyTerms).length(); i3++) {
                                    String string = jSONObject.getJSONArray(keyDict).getJSONObject(i2).getJSONArray(keyTerms).getString(i3);
                                    if (!stringBuilderResult.toString().toLowerCase(Locale.getDefault()).contains(string.toLowerCase(Locale.getDefault()))) {
                                        stringBuilderResult.append(string);
                                        stringBuilderResult.append("\n");
                                    }
                                }
                            }
                        }
                        if (stringBuilderResult.length() <= 0) {
                            resultText[0].set(translateMozilla(textEncode));
                        }
                        if (stringBuilderResult.length() > 100) {
                            stringBuilderResult.setLength(stringBuilderResult.length() - 1);
                        }
                        if (resultText[0].get().isEmpty())
                            resultText[0].set(stringBuilderResult.toString());
                        return;
                    }
                }
                if (resultText[0].get().isEmpty()) resultText[0].set(translateMozilla(textEncode));
            } catch (Exception unused) {
                if (resultText[0].get().isEmpty()) resultText[0].set(translateMozilla(textEncode));
            }
            handler.post(() -> {
                try {
                    resultText[0].set(resultText[0].get().substring(0,1).toUpperCase() + resultText[0].get().substring(1));
                    resultText[0].set(resultText[0].get().replaceAll("\\+", " "));
                    resultText[0].set(resultText[0].get().replaceAll("%2C", ","));
                    resultText[0].set(resultText[0].get().replaceAll("%27t", "'"));
                    resultText[0].set(resultText[0].get().replaceAll("%27s", "'"));
                    resultText[0].set(resultText[0].get().replaceAll("%27", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callback.onResult(resultText[0].get(), codeLanguageTo);
            });
        });


    }

    private static String byteArrayToString(byte[] bArr) {
        return new String(Base64.decodeBase64(bArr));
    }

    private String translateMozilla(String textForTranslate) {
        String str4 = CharEncoding.UTF_8;
        try {
            URLConnection openConnection = new URL(String.format(formatMozilla, CODE_LANG_FROM, codeLanguageTo, URLEncoder.encode(textForTranslate, str4))).openConnection();
            openConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openConnection.getInputStream(), str4));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    sb.append(readLine);
                } else {
                    bufferedReader.close();
                    return parseAnswer(sb.toString());
                }
            }
        } catch (Exception unused) {
            return translateWindows(textForTranslate);
        }
    }

    private static byte[] stringToByteArray(String str) {
        return str.getBytes();
    }

    private String translateWindows(String textForTranslate) {
        String resultStr = "";
        try {
            if (textForTranslate.split(" ").length == 1) {
                textForTranslate = textForTranslate.toLowerCase(Locale.getDefault());
            }
            URLConnection openConnection = new URL(String.format(formatWindow, URLEncoder.encode(textForTranslate, UTF_VALUE), CODE_LANG_FROM, codeLanguageTo)).openConnection();
            openConnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openConnection.getInputStream(), UTF_VALUE));
            StringBuilder stringBuilder = new StringBuilder(100000);
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                stringBuilder.append(readLine);
            }
            bufferedReader.close();
            if (stringBuilder.length() != 0 && stringBuilder.toString().contains(KEY_TRANSLATE)) {
                resultStr = StringEscapeUtils.unescapeXml(Html.fromHtml(stringBuilder.substring(stringBuilder.indexOf(KEY_TRANSLATE) + 18, stringBuilder.indexOf("\",\""))).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultStr;
    }

    private DefaultHttpClient getHttpClient() {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpParams params = defaultHttpClient.getParams();
        params.setParameter(KEY_CONNECTION_TIME_OUT, VALUE_CONNECTION_TIME_OUT);
        params.setParameter(KEY_SOCKET_TIME_OUT, VALUE_SOCKET_TIME_OUT);
        params.setParameter(KEY_PROTOCOL_CONTENT, UTF_VALUE);
        HttpProtocolParams.setUserAgent(params, USER_AGENT);
        return defaultHttpClient;
    }

    private static final String UTF_VALUE = "UTF-8";
    private static final String KEY_CONNECTION_TIME_OUT = "http.connection.timeout";
    private static final Integer VALUE_CONNECTION_TIME_OUT = 10000;
    private static final String KEY_SOCKET_TIME_OUT = "http.socket.timeout";
    private static final String CODE_LANG_FROM = "en";
    private static final Integer VALUE_SOCKET_TIME_OUT = 10000;
    private static final String KEY_PROTOCOL_CONTENT = "http.protocol.content-charset";
    private static final String USER_AGENT = "AndroidTranslate/2.5.3 2.5.3 (gzip)";

    private final String formatWindow = byteArrayToString(stringToByteArray("aHR0cDovL215bWVtb3J5LnRyYW5zbGF0ZWQubmV0L2FwaS9nZXQ/cT0lcyZsYW5ncGFpcj0lc3wlcw=="));
    private final String formatMozilla = byteArrayToString(stringToByteArray("aHR0cHM6Ly90cmFuc2xhdGUuZ29vZ2xlYXBpcy5jb20vdHJhbnNsYXRlX2Evc2luZ2xlP2NsaWVudD1ndHgmc2w9JXMmdGw9JXMmZHQ9dCZxPSVz"));

    private final String KEY_TRANSLATE = "\"translatedText\":\"";


}
