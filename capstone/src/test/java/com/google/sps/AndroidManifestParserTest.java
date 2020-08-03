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
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.util.ArrayList; 
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;

    //This test the overall functionality of the parser. It checks if it converts the bytes
    //to readable xml and retunrs the permission
    /* TODO
    Will split function to test for converting to human readable xml and returning permissions
    */
@RunWith(JUnit4.class)
public class AndroidManifestParserTest{
    
    //This function converts the file to bytes
    public static byte[] readContentIntoByteArray(File file) {

      FileInputStream fileInputStream = null;
      byte[] bFile = new byte[(int) file.length()];

      try {
        //convert file into array of bytes
        fileInputStream = new FileInputStream(file);
        fileInputStream.read(bFile);
        fileInputStream.close();
    }
    catch (Exception e) {
        e.printStackTrace();
    }

    return bFile;
    }
    
    //This Test if the bytes  is converting to human readable XML format
    @Test
    public void testIfBytesConvertsToReadableXml() {
        AndroidManifestParser ManifestParser = new AndroidManifestParser();
        
        String filePath = Resources.getResource("AndroidManifest.xml").getPath();
        File file = new File(filePath);
        File returnedFile = ManifestParser.decompressXML(readContentIntoByteArray(file));
        byte[] expected = readContentIntoByteArray(returnedFile);

        String filePath2 = Resources.getResource("AndroidManifestReadFile.txt").getPath();
        File file2 = new File(filePath2);
        byte[] actual = readContentIntoByteArray(file2);
        
        
        for(int i=0; i < expected.length; i++){
            assertEquals(expected[i], actual[i]);
        }
    }

    //This test if decompress XML gets attribute tags and can read each position
    @Test
    public void testIfByteIsConvertedToXMLTagPosition () {
        AndroidManifestParser ManifestParser = new AndroidManifestParser();
        int off = 166840;
        int lewResult = 1048835;
        String filePath = Resources.getResource("AndroidManifest.xml").getPath();
        File file = new File(filePath);

        Assert.assertEquals(lewResult, ManifestParser.LEW((readContentIntoByteArray(file)),off));
    }

    // This test if bytes is been converted to attribute name based 
    // on the position generated in previous function
    @Test
    public void testIfBytesIsConvertedToAttributeName () {
        AndroidManifestParser ManifestParser = new AndroidManifestParser();
        int sitOff = 36;
        int stOff = 2788;
        int strInd36 = 3;
        String  compXmlStringResult = "name";
        String filePath = Resources.getResource("AndroidManifest.xml").getPath();
        File file = new File(filePath);
        Assert.assertEquals(compXmlStringResult, ManifestParser.compXmlString((readContentIntoByteArray(file)), sitOff, stOff,strInd36));
    }

    // This test if bytes is converted from string and permissions is extracted correctly
    @Test
    public void testIfBytesReturnsPermissions() {
        AndroidManifestParser ManifestParser = new AndroidManifestParser();
        ArrayList<String> permissionsList = new ArrayList();
        permissionsList.add("TEST");
        String param = "android.permission.TEST";

        Assert.assertEquals(permissionsList, ManifestParser.getPermissions(param));
    }
}

