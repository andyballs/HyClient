package rage.pitclient.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class WebUtils {

	public static ArrayList<String> readFromWebPage(String StringUrl) {
	    ArrayList<String> list = new ArrayList<String>();
		try {
			URL url = new URL(StringUrl);
			trustAllHosts();
	        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {

	            String line;
	            while ((line = br.readLine()) != null) {
	            	list.add(line);
	            }
	        } catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
		}
		return list;
	}
	
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException
            {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
