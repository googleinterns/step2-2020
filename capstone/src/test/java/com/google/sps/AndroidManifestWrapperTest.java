// Copyright 2020 Google LLC
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

package com.google.sps;

import com.google.sps.data.AndroidManifestWrapper;
import com.google.common.io.Resources;

import java.io.File;
import java.io.FileInputStream;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AndroidManifestWrapperTest{
    @Test
    public void testIfFileConvertsToBytes() {
        String filePath = Resources.getResource("AndroidManifest.xml").getPath();
        System.out.print(filePath);
        AndroidManifestWrapper ManifestTester = new AndroidManifestWrapper();
        File file = new File(filePath);
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
         try{
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < bFile.length; i++) {
            Assert.assertEquals(bFile[i], (ManifestTester.readContentIntoByteArray(file))[i]);
        }
    }
}