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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.BlobInfo;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.cloud.storage.StorageOptions;
import javax.servlet.annotation.MultipartConfig;

@WebServlet("/binary_upload")
@MultipartConfig
public class APKUploadServlet extends HttpServlet {

  private final String PROJECTID = "step-2020-team-2";
  private final String BUCKETNAME = "vaderker-uploadedstoragebucket";

  private String filePath;
  private String objectName;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    filePath = "Hello"; //Just a dummy.
  }
}