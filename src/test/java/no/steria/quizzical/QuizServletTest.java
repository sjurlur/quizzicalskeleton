package no.steria.quizzical;


import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

public class QuizServletTest {
	
	@Test
    public void shouldAddNumbers() throws Exception {
	    QuizzicalServlet servlet = new QuizzicalServlet();
        
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getMethod()).thenReturn("POST");
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"one\":3,\"two\":4}")));
        StringWriter htmlDoc = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(htmlDoc));
    
        servlet.service(req, resp);
        
        assertThat(htmlDoc.toString()).isEqualTo("{\"result\":7}");
    }

}
