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
import com.google.common.io.Resources;
import com.google.sps.data.AndroidManifestParser;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList; 
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

    //This test the overall functionality of the parser. It checks if it converts the bytes
    //to readable xml and retunrs the permission
    /* TODO
    Will split function to test for converting to human readable xml and returning permissions
    */
@RunWith(JUnit4.class)
public class AndroidManifestParserTest{
    @Test
    public void testIfBytesConvertsToXmlAndReturnsPermissions() {
        String filePath = Resources.getResource("AndroidManifest.xml").getPath();
        System.out.print(filePath);
        AndroidManifestParser ManifestParser = new AndroidManifestParser();
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
        ArrayList<String> permissionsList = new ArrayList();
        permissionsList.add("READ_CONTACTS");
        permissionsList.add("WRITE_CONTACTS");
        permissionsList.add("VIBRATE");
        permissionsList.add("ACCESS_COARSE_LOCATION");
        permissionsList.add("INTERNET");
        permissionsList.add("SET_WALLPAPER");
        permissionsList.add("WRITE_EXTERNAL_STORAGE");
        permissionsList.add("SEND_SMS");
        permissionsList.add("RECEIVE_SMS");
        permissionsList.add("NFC");
        permissionsList.add("RECORD_AUDIO");
        permissionsList.add("CAMERA");
        permissionsList.add("BIND_ACCESSIBILITY_SERVICE");
        permissionsList.add("BIND_ACCESSIBILITY_SERVICE");
        permissionsList.add("BIND_DEVICE_ADMIN");
        Assert.assertEquals(permissionsList, ManifestParser.decompressXML(bFile));
    }
}

