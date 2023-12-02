package rage.pitclient.auth;

import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class HWID {
	
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String getHWID() {
    	return bytesToHex(generateHWID());
    }
    
    private static byte[] generateHWID() {
        try {
            MessageDigest hash = MessageDigest.getInstance("MD5");

            String s = getWindowsIdentifier();
            
            return hash.digest(s.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new Error("Algorithm wasn't found.", e);
        }

    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    private static String getWindowsIdentifier() {
		String result = null;
		try {
			String path = System.getenv("SystemRoot") + File.separatorChar +"System32" + File.separatorChar + "wbem" + File.separatorChar + "WMIC.exe";

			Process process = Runtime.getRuntime().exec(new String[] { path, "csproduct", "get", "UUID" });

	        InputStream is = process.getInputStream();
	        Scanner sc = new Scanner(process.getInputStream());
	        try {
	            while (sc.hasNext()) {
	                String next = sc.next();
	                if (next.contains("UUID")) {
	                    result = sc.next().trim();
	                    break;
	                }
	            }
	        } finally {
	            is.close();
	        }
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

        return result;
    }
}
