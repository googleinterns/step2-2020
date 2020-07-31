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
    //This Test if the bytes  is converting to XML format and 
    // Prints permission.This is the entire functional test of the parser 
    // Library. The other Test tests sub functions
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

    @Test
    //This test if decompress XML gets attribute tags and can read each position
    public void testIfByteIsConvertedToXMLTagPosition () {
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
        int off = 166840;
        int lewResult = 1048835;

        Assert.assertEquals(lewResult, ManifestParser.LEW(bFile,off));
    }

    @Test
    // This test if bytes is been converted to attribute name based 
    // on the position generated in previous function
    public void testIfBytesIsConvertedToAttributeName () {
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

        int sitOff = 36;
        int stOff = 2788;
        int strInd36 = 3;
        String  compXmlStringResult = "name";
        Assert.assertEquals(compXmlStringResult, ManifestParser.compXmlString(bFile, sitOff, stOff,strInd36));
    }

    @Test
    // This test if bytes is converted from string and permissions is extracted correctly
    public void testIfBytesReturnsPermissions() {
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
        permissionsList.add("TEST");
        String param = "android.permission.TEST";

        Assert.assertEquals(permissionsList, ManifestParser.getPermissions(param));
    }
}

