/*******************************************************************************
 * Copyright (c) 2013 aegif.
 *
 * This file is part of NemakiWare.
 *
 * NemakiWare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NemakiWare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with NemakiWare.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     linzhixing(https://github.com/linzhixing) - initial API and implementation
 ******************************************************************************/
package jp.aegif.nemaki.api.resources;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jp.aegif.nemaki.model.User;
import jp.aegif.nemaki.service.node.PrincipalService;
import jp.aegif.nemaki.util.NemakiPropertyManager;
import jp.aegif.nemaki.util.PasswordHasher;
import jp.aegif.nemaki.util.constant.PropertyKey;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class AuthenticationFilter implements Filter {

	private PrincipalService principalService;
	private NemakiPropertyManager propertyManager;
	private final String TOKEN_FALSE = "false";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
		
		principalService = (PrincipalService) context
		.getBean("principalService");
		
		propertyManager = (NemakiPropertyManager)context
				.getBean("nemakiPropertyManager");
		
	}

	public static UserInfo getUserInfo(HttpServletRequest httpRequest){
		return (UserInfo)httpRequest.getSession().getAttribute("USER_INFO");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest hreq = (HttpServletRequest) req;
		HttpServletResponse hres = (HttpServletResponse) res;

		HttpSession session = hreq.getSession();
		
		if(!checkResourceEnabled(hreq)){
			//FIXME
			throw new ServletException("This REST API is not supported");
		}
		
		if (session.getAttribute("USER_INFO") == null) {
			String auth = hreq.getHeader("Authorization");

			if (auth == null) {
				requireAuth(hres);
				return;
			} else {
				try {
					String decoded = decodeAuthHeader(auth);

					int pos = decoded.indexOf(":");
					String username = decoded.substring(0, pos);
					String password = decoded.substring(pos + 1);

					UserInfo user = authenticateUser(username, password);

					if (user.userId == null || user.userId.equals("")) {
						requireAuth(hres);
						return;

					} else {
						session.setAttribute("USER_INFO", user);
					}

				} catch (Exception ex) {
					requireAuth(hres);
					return;

				}
			}

		}

		chain.doFilter(req, res);

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	private void requireAuth(HttpServletResponse hres) throws IOException {
		hres.setHeader("WWW-Authenticate",
				"BASIC realm=\"Authentication Test\"");
		hres.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	public static String decodeAuthHeader(String header) {
		String ret = "";

		try {
			String encStr = header.substring(6);
			sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
			byte[] dec = decoder.decodeBuffer(encStr);
			ret = new String(dec);
		} catch (Exception ex) {
			ret = "";
		}
		return ret;
	}

	private UserInfo authenticateUser(String username, String password) {
		UserInfo u = new UserInfo();

		User user = principalService.getUserById(username);
		Boolean match = PasswordHasher.isCompared(password,
				user.getPasswordHash());
		if (match) {
			u.userId = username;
			u.password = password;
			u.roles = new String[] { "Users" };
		}
		return u;
	}
	
	private boolean checkResourceEnabled(HttpServletRequest request){
		boolean enabled = true;
		
		String pathInfo = request.getPathInfo();
		if(pathInfo.startsWith("/user")){
			String userResourceEnabled = propertyManager.readValue(PropertyKey.REST_USER_ENABLED);
			enabled = TOKEN_FALSE.equals(userResourceEnabled) ? false : true;
		}else if(pathInfo.startsWith("/group")){
			String groupResourceEnabled = propertyManager.readValue(PropertyKey.REST_GROUP_ENABLED);
			enabled = TOKEN_FALSE.equals(groupResourceEnabled) ? false : true;
		}else if(pathInfo.startsWith("/type")){
			String typeResourceEnabled = propertyManager.readValue(PropertyKey.REST_TYPE_ENABLED);
			enabled = TOKEN_FALSE.equals(typeResourceEnabled) ? false : true;
		}else if(pathInfo.startsWith("/archive")){
			String archiveResourceEnabled = propertyManager.readValue(PropertyKey.REST_ARCHIVE_ENABLED);
			enabled = TOKEN_FALSE.equals(archiveResourceEnabled) ? false : true;
		}else{
			enabled = false;
		}
		
		return enabled;
	}
}
