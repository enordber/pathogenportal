package edu.vt.vbi.ci.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.portlet.RenderRequest;

public class PathPortUtilities {
	private static boolean debug = false;
	private static final String NEWLINE = "\n";

	public static void main(String[] args) {
		System.out.println("");
		
		
	}

	public static String contactServlet(String urlString, HashMap parameters) {
		String r = null;
		try {
			urlString = getURLString(urlString, parameters);
			URL servletURL = new URL(urlString);
			URLConnection servletConnection = servletURL.openConnection();

			servletConnection.setDoInput(true);
			servletConnection.setDoOutput(true);

			servletConnection.connect();

			InputStream servletInputStream = servletConnection.getInputStream();
			BufferedReader servletInputReader = 
				new BufferedReader(new InputStreamReader(servletInputStream));

			String line = servletInputReader.readLine();

			StringBuffer sb = new StringBuffer();
			while(line != null) {
				sb.append(line);
				sb.append(NEWLINE);
				line = servletInputReader.readLine();
			}
			r = sb.toString();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return r;
	}

	public static String getURLString(String base, HashMap parameters) {
		String r = null;
		StringBuffer sb = new StringBuffer(base);
		if(parameters != null) {
			//append parameters to url string
			Set keys = parameters.keySet();
			Iterator keyIterator = keys.iterator();
			sb.append("?");
			try {
				while(keyIterator.hasNext()) {
					String nextKey = (String)keyIterator.next();
					String nextValue = (String)parameters.get(nextKey);
					sb.append(URLEncoder.encode(nextKey, "UTF-8"));
					sb.append("=");
					sb.append(URLEncoder.encode(nextValue,
					"UTF-8"));
					if(keyIterator.hasNext()) {
						sb.append("&");
					}
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		r = sb.toString();
		return r;
	}

	/**
	 * @param url
	 * @return
	 */
	public static String getContentsOfURL(String url) {
		if(debug) {
			System.out.println("PathPortUtilities.getContentsFromURL() " + url);
		}
		String       r             = null;
		String       lineDelimiter = "\n";
		StringBuffer sb            = new StringBuffer();

		try {
			URL    urlToGet   = new URL(url);
			Object urlContent = urlToGet.getContent();

			if (urlContent instanceof InputStream) {
				InputStreamReader isReader =
					new InputStreamReader((InputStream) urlContent, "UTF-8");
				BufferedReader    br       = new BufferedReader(isReader);
				String            line;

				line = br.readLine();
				if(line != null) {
					sb.append(line);
				}

				while ((line = br.readLine()) != null) {

					//System.out.println(line);
					sb.append(lineDelimiter);
					sb.append(line);
				}
			}
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			//ioe.printStackTrace();
		}

		r = sb.toString();

		return r;
	}

	public static void downloadURLToFile(String url, String fileName) {

		String       r             = null;
		String       lineDelimiter = "\n";

		try {
			FileWriter fw = new FileWriter(fileName);
			URL    urlToGet   = new URL(url);
			Object urlContent = urlToGet.getContent();

			if (urlContent instanceof InputStream) {
				InputStream urlIS = (InputStream)urlContent;
				InputStreamReader isReader =
				new InputStreamReader((InputStream) urlContent, "UTF-8");
				BufferedReader    br       = new BufferedReader(isReader);
				String            line;

				line = br.readLine();
				if(line != null) {
					fw.write(line);
				}

				while ((line = br.readLine()) != null) {
					fw.write(lineDelimiter);
					fw.write(line);
				}
			}

			fw.flush();
			fw.close();
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void downloadDataURLToFile(String url, String fileName) {

		String       r             = null;
		String       lineDelimiter = "\n";

		try {
			FileWriter fw = new FileWriter(fileName);
			URL    urlToGet   = new URL(url);
			Object urlContent = urlToGet.getContent();

			if (urlContent instanceof InputStream) {
				InputStream urlIS = (InputStream)urlContent;
				OutputStream out = new FileOutputStream(fileName);
				int read = 0;
				byte[] bytes = new byte[1024];
			 
				while ((read = urlIS.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
			 
				urlIS.close();
				out.flush();
				out.close();
			}				
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Retrieves and returns the contents of the specified URL.
	 * method should be either HandyConstants.POST or HandyConstants.GET.
	 * 
	 * @param url
	 * @param properties
	 * @param method
	 * @return
	 */
	public static String getContentsOfURL(String url, HashMap properties, String method) {
		if(debug) {
			System.out.println("AppletUtilities.getContentsOfURL(): "
					+ url);
		}
		String r = null;
		String       lineDelimiter = "\n";

		StringBuffer sb = new StringBuffer();
		try {
			if(method.equals(HandyConstants.GET)) {
				url = getURLString(url, properties);
				//				System.out.println("formatted url: " + url);
			}

			URL urlToContact = new URL(url);
			HttpURLConnection urlConnection = 
				(HttpURLConnection) urlToContact.openConnection();

			urlConnection.setRequestMethod(method);
			urlConnection.setRequestProperty("Accept", "text/plain");
			urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			urlConnection.setRequestProperty("User-Agent", "CIG-Java-application");
			urlConnection.setRequestProperty("Cookie", "keyCookie=valueCookie");

			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			String parameterString = getParameterString(properties);
			if(debug) {
				System.out.println("AppletUtilitles encoded parameter string: "
						+ parameterString);
			}

			if(method.equals(HandyConstants.POST)){
				urlConnection.setRequestProperty("Content-Length", 
						""+parameterString.length());
			}

			urlConnection.connect();

			if(method.equals(HandyConstants.POST)) {
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new BufferedOutputStream(urlConnection.getOutputStream())));

				writer.write(parameterString);
				writer.flush();
				writer.close();
			}

			if(debug) {
				System.out.println("established URLConnection: " + urlConnection);
			}
			Object urlContent;// = urlToContact.getContent();

			urlContent = urlConnection.getContent();
			if (urlContent instanceof InputStream) {
				InputStreamReader isReader =
					new InputStreamReader((InputStream) urlContent);
				BufferedReader    br       = new BufferedReader(isReader);
				String            line;

				sb.append(br.readLine());

				while ((line = br.readLine()) != null) {
					sb.append(lineDelimiter);
					sb.append(line);
				}

				isReader.close();
			}

			urlConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		r = sb.toString();
		return r;
	}

	private static String getParameterString(HashMap parameters) {
		String r = "";
		StringBuffer sb = new StringBuffer();
		if(parameters != null) {
			//append parameters to url string
			Set keys = parameters.keySet();
			Iterator keyIterator = keys.iterator();
			//			sb.append("?");
			try {
				while(keyIterator.hasNext()) {
					String nextKey = (String)keyIterator.next();
					String nextValue = (String)parameters.get(nextKey);
					sb.append(URLEncoder.encode(nextKey, "UTF-8"));
					sb.append("=");
					sb.append(URLEncoder.encode(nextValue,
					"UTF-8"));
					if(keyIterator.hasNext()) {
						sb.append("&");
					}
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		r = sb.toString();
		return r;
	}

	public static String getPageName(RenderRequest request) {
		String r = null;
		String windowID = request.getWindowID();
		String[] fields = windowID.split("/");
		r = fields[2];
		return r;
	}

	public static String getJSONString(String key, String[] values) {
		String r = null;
		StringBuffer sb = new StringBuffer();
		if(key != null && values != null) {
			sb.append("{'");
			sb.append(key);
			sb.append("':");
			if(values.length > 0) {
				sb.append("['");
				sb.append(values[0]);
				sb.append("'");
				for(int i = 1; i < values.length; i++) {
					sb.append(",'");
					sb.append(values[i]);
					sb.append("'");
				}
				
				sb.append("]");
			}
			sb.append("}");
		}

		r = sb.toString();
		return r;
	}

	public static String getJSONString(String[] values) {
		String r = null;
		StringBuffer sb = new StringBuffer();
		if(values != null) {
			if(values.length > 0) {
				sb.append("['");
				sb.append(values[0]);
				sb.append("'");
				for(int i = 1; i < values.length; i++) {
					sb.append(",'");
					sb.append(values[i]);
					sb.append("'");
				}
				
				sb.append("]");
			}
		}

		r = sb.toString();
		return r;
	}
}
