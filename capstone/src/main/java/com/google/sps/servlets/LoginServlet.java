// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.util.Collections;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;


@WebServlet("/logon")
public class LoginServlet extends HttpServlet {

  private String userID;
  private String id_token;
  private String userIsSignedin = "false";

  private final String CLIENT_ID = "204911942473-790k5jierj6hf5gtstj1rr8tuabji68g.apps.googleusercontent.com";

  private GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
  .Builder(new NetHttpTransport(), new JacksonFactory())
  .setAudience(Collections.singletonList(CLIENT_ID)).build();


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (userIsSignedin == "true" || userIsSignedin == "false") {

      response.setContentType("text/html;");
      response.getWriter().println(userIsSignedin);

    } else {

      response.setContentType("text/html;");
      response.getWriter().println("false");

    }

  }


  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) 
  throws IOException {

    userIsSignedin = request.getParameter("login_status");

    if (userIsSignedin != "true") {return;}

    id_token = request.getParameter("token");

    try {

      GoogleIdToken idToken = verifier.verify(id_token);

      Payload payload = idToken.getPayload();

      userID = payload.getSubject();

      userIsSignedin = "true";

      response.setContentType("text/html;");
      response.getWriter().println("Authenticated");

    } catch (Exception e) {

      response.setContentType("text/html;");
      response.getWriter().println("Invalid token");

    }

  }
}