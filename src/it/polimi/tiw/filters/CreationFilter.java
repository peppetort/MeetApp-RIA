package it.polimi.tiw.filters;

import it.polimi.tiw.beans.Meeting;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "CreationFilter")
public class CreationFilter implements Filter {

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		HttpSession session = request.getSession();

		Meeting meeting = (Meeting) session.getAttribute("meeting");

		if(meeting == null){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("No active meeting creation found");
			return;
		}

		chain.doFilter(req, resp);
	}

}
