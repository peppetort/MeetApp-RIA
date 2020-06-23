package it.polimi.tiw.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter")
public class AuthFilter implements Filter {

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String loginPath = req.getServletContext().getContextPath() + "/index.html";

		HttpSession session = request.getSession();

		if(session.isNew() || session.getAttribute("user") == null){
			response.sendRedirect(loginPath);
			return;
		}

		chain.doFilter(req, resp);
	}
}
