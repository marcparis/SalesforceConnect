package com.codescience.salesforceconnect.web;

import com.auth0.client.auth.AuthAPI;
import com.auth0.json.auth.UserInfo;
import com.auth0.net.Request;
import jakarta.servlet.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class JWTFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String parameter = servletRequest.getParameter("Authorization");
        AuthAPI auth = AuthAPI.newBuilder("dev-fb8z2pemqexsu1va.us.auth0.com", "0FpknJj3oWZw1NAtTTIXHg1mF3OlJ7FP", "hJ_Hg1DT8d4iunmHMNTNzCiwjBz6p_0as55u1JfxyzoFSNKh3zYImauAaGbdHK_V").build();
        Request<UserInfo> uinfo = auth.userInfo("Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkY3c2pxR1VYSENGV2lfb05wOFh1UiJ9.eyJpc3MiOiJodHRwczovL2Rldi1mYjh6MnBlbXFleHN1MXZhLnVzLmF1dGgwLmNvbS8iLCJzdWIiOiIwRnBrbkpqM29XWncxTkF0VFRJWEhnMW1GM09sSjdGUEBjbGllbnRzIiwiYXVkIjoiaHR0cHM6Ly9kZXYtZmI4ejJwZW1xZXhzdTF2YS51cy5hdXRoMC5jb20vYXBpL3YyLyIsImlhdCI6MTc2OTEyMzI5OCwiZXhwIjoxNzY5MjA5Njk4LCJzY29wZSI6ImNyZWF0ZTpjbGllbnRzIHJlYWQ6Y2xpZW50X2NyZWRlbnRpYWxzIiwiZ3R5IjoiY2xpZW50LWNyZWRlbnRpYWxzIiwiYXpwIjoiMEZwa25KajNvV1p3MU5BdFRUSVhIZzFtRjNPbEo3RlAifQ.fi3m5Waa4ffbaapyfaIVD38aOj0TZH4a2GCyF5b3uksbl4yv7cwBnmBgxBguBwD1JIys0gl9t5rYVeXnEydiD9eGbLvqLxaRNDKTy4mYHF-NWifWWvgmeCa2_OI7Xj5jjFWpNTRJEihixO3-07hEFzn3IMg7pJivB04OA59DXXlwY_Hf0ENieSqhljWZvafqCbr1atbDy7y5PWqeIBVhlpj6s5rD-r-0X01se68to7fSGAOIqhyAoF9lsib5yWq51pmDn00blzB2jvZd43GzoEEZSdquUDtvuQx0YeZEn8kxxpeHHlFMZweHoZ_bCx-fXjCwTOTnTFINY4WslR9ApQ");
        uinfo.toString();

        URL url = new URL("https://dev-fb8z2pemqexsu1va.us.auth0.com/oauth/token");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("authorization","Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkY3c2pxR1VYSENGV2lfb05wOFh1UiJ9.eyJpc3MiOiJodHRwczovL2Rldi1mYjh6MnBlbXFleHN1MXZhLnVzLmF1dGgwLmNvbS8iLCJzdWIiOiIwRnBrbkpqM29XWncxTkF0VFRJWEhnMW1GM09sSjdGUEBjbGllbnRzIiwiYXVkIjoiaHR0cHM6Ly9kZXYtZmI4ejJwZW1xZXhzdTF2YS51cy5hdXRoMC5jb20vYXBpL3YyLyIsImlhdCI6MTc2OTEyMzI5OCwiZXhwIjoxNzY5MjA5Njk4LCJzY29wZSI6ImNyZWF0ZTpjbGllbnRzIHJlYWQ6Y2xpZW50X2NyZWRlbnRpYWxzIiwiZ3R5IjoiY2xpZW50LWNyZWRlbnRpYWxzIiwiYXpwIjoiMEZwa25KajNvV1p3MU5BdFRUSVhIZzFtRjNPbEo3RlAifQ.fi3m5Waa4ffbaapyfaIVD38aOj0TZH4a2GCyF5b3uksbl4yv7cwBnmBgxBguBwD1JIys0gl9t5rYVeXnEydiD9eGbLvqLxaRNDKTy4mYHF-NWifWWvgmeCa2_OI7Xj5jjFWpNTRJEihixO3-07hEFzn3IMg7pJivB04OA59DXXlwY_Hf0ENieSqhljWZvafqCbr1atbDy7y5PWqeIBVhlpj6s5rD-r-0X01se68to7fSGAOIqhyAoF9lsib5yWq51pmDn00blzB2jvZd43GzoEEZSdquUDtvuQx0YeZEn8kxxpeHHlFMZweHoZ_bCx-fXjCwTOTnTFINY4WslR9ApQ");
        urlConnection.connect();
        String message = urlConnection.getResponseMessage();
        int code = urlConnection.getResponseCode();
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
