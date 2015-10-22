package it.hubzilla.hubchart.servlet;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.BusinessException;
import it.hubzilla.hubchart.LookupUtil;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.business.HubBusiness;
import it.hubzilla.hubchart.model.Hubs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = -2723999233245743020L;

	private final Logger LOG = LoggerFactory.getLogger(RegisterServlet.class);
	
	public static final String BASE_URL_PARAM = "baseUrl"; 
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "";
		boolean success = false;
		String baseUrlParam = request.getParameter(BASE_URL_PARAM);
		//Look for hub baseUrl and che
		String baseUrl = null;
		String pollUrl = null;
		Integer hubId = null;
		try {
			if (baseUrlParam != null) {
				baseUrlParam = HubBusiness.cleanBaseUrl(baseUrlParam);
				pollUrl = baseUrlParam+AppConstants.JSON_SUFFIX_RED;
				if (urlExists(pollUrl)) {
					baseUrl = baseUrlParam;
				//} else {
				//	if (!AppConstants.USE_ONLY_RED_SITEINFO) {
				//		pollUrl = baseUrlParam+AppConstants.JSON_SUFFIX_DIASPORA;
				//		if (urlExists(pollUrl)) {
				//			baseUrl = baseUrlParam;
				//		}
				//	}
				}
			}
			
			LOG.debug("baseUrl = "+baseUrl);
			LOG.debug("response from pollUrl = "+pollUrl);
			
			if (baseUrl != null) {
				//Statistics URL exists
				try {
					hubId = HubBusiness.initHub(baseUrl, true);
					message = "Your hub have been correctly registered.<br />"
							+ "It will be included in global statistics within 24 hours.";
					LOG.debug(message);
					success = true;
				} catch (BusinessException e) {
					message = e.getMessage();
				} catch (OrmException e) {
					message = e.getMessage();
					LOG.error(message, e);
				}
			} else {
				message = "The hub base URL is incorrect";
			}
		} catch (ProtocolException e) {
			message = e.getMessage();
			LOG.debug(e.getMessage(), e);
		} catch (IOException e) {
			message = e.getMessage();
			LOG.debug(e.getMessage(), e);
		}

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
				"<html> \n" + "<head> \n" +
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"> \n" + "<title>red#matrix hub registration</title> \n" +
				"<meta name='viewport' content='width=device-width, initial-scale=1'>"+
				"<link href='css/bootstrap.min.css' rel='stylesheet'>"+
				"</head> \n" + "<body> \n" +
				"<div class='container'>"+
				"<img src='images/red_statistics_400.png' /><br />&nbsp;<br />");
		if (success) {
			out.println("<p>"+message+"</p>");
			try {
				out.println("<p>");
				Hubs hub = HubBusiness.findHubById(hubId);
				out.println("Name: "+hub.getName()+"<br />");
				out.println("Base URL: "+hub.getBaseUrl()+"<br />");
				out.println("Network: <img src='"+
						AppConstants.NETWORK_ICONS.get(hub.getNetworkType())+
						"' border='0'/> "+
						AppConstants.NETWORK_DESCRIPTIONS.get(hub.getNetworkType())+"<br />");
				out.println("Server location: <img src='"+LookupUtil.decodeCountryToFlag(hub.getCountryCode())+"' /> "+
						hub.getCountryName()+"<br />");
				out.println("Registration: "+AppConstants.REGISTRATION_DESCRIPTIONS
						.get(hub.getRegistrationPolicy())+"<br />");
				out.println("Version: "+hub.getVersion()+"<br />");
				out.println("</p>");
			} catch (OrmException e) {
				out.println("<p style='color:#c60032;'>ERROR: "+e.getMessage()+"</p>");
			}
		} else {
			out.println("<p style='color:#c60032;'>ERROR: "+message+"</p>");
			out.println("<p>You must provide a correct and working base URL in the form <br />"+
					"<code>https://&lt;domain&gt;</code><br />"+
					"<code>http://&lt;domain&gt;</code><br />"+
					"<code>http(s)://&lt;domain&gt;/&lt;base_dir&gt;</code><br /><br />"+
					"Please check the <b>http</b> or <b>https</b> prefix!<br /><br />"+
					"If you find a bug please contact the author</a>.<br /><br />"
				);
		}
		out.println("<a href='index.jsp'><b>&lt;- back</b></a>");
		out.println("</div><!-- /container -->" );
		out.println("</body> \n" + "</html>" );
	}

	private boolean urlExists(String urlString) throws ProtocolException, IOException {
		if (urlString == null) return false;
		try {
			final URL url = new URL(urlString);
			int responseCode = 0;
			if (urlString.startsWith("https")) {
				// SSL
				HttpsURLConnection hc = (HttpsURLConnection) url.openConnection();
				hc.setRequestMethod("HEAD");
				responseCode = hc.getResponseCode();
			} else {
				// NO SSL
				HttpURLConnection hc = (HttpURLConnection) url.openConnection();
				hc.setRequestMethod("HEAD");
				responseCode = hc.getResponseCode();
			}
			return (responseCode == 200);
		}
		catch (MalformedURLException e) {
			LOG.debug(e.getMessage(), e);
		}
		return false;
	}
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
