/**
 * Copyright 2006 OCLC Online Computer Library Center Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or
 * agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.oclc.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * General-purpose static utility methods.
 * 
 * @author Jeffrey A. Young
 * @deprecated Use info.openurl.oom.util.OOMUtil instead
 */
public class OOMUtil {

    /**
     * Gets a parameter map.
     * 
     * @param queryString
     * @return
     */
    public static SortedMap getParameterMap(String queryString) {
        return getParameterMap(new String[] {
            queryString
        });
    }

    /**
     * Transforms a queryString into a Map of String/Object[]
     * 
     * @param queryStrings
     * @return a Map of key/values from the queryString.
     */
    public static SortedMap getParameterMap(String[] queryStrings) {
        HashMap tempMap = new HashMap();

        if (queryStrings != null) {
            for (int i = 0; i < queryStrings.length; ++i) {
                String[] parameters = queryStrings[i].split("&");
                for (int j = 0; j < parameters.length; ++j) {
                    String[] parameter = parameters[j].split("=", 2);
                    ArrayList list = (ArrayList) tempMap.get(parameter[0]);
                    if (list == null) {
                        list = new ArrayList();
                        tempMap.put(parameter[0], list);
                    }
                    if (parameter.length == 2) {
                        list.add(parameter[1]);
                    } else {
                        list.add("");
                    }
                }
            }
        }

        // Switch the values from ArrayList to String[]
        SortedMap parameterMap = new TreeMap();
        Iterator iter = tempMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Entry) iter.next();
            String key = (String) entry.getKey();
            ArrayList value = (ArrayList) entry.getValue();
            parameterMap.put(key, value.toArray(new String[value.size()]));
        }
        return parameterMap;
    }

    /**
     * Get the bytes out of an InputStream.
     * 
     * @param is the stream to be read.
     * @return the bytes found in the stream.
     * @throws IOException
     */
    public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int len;
        while ((len = is.read(bytes)) != -1) {
            baos.write(bytes, 0, len);
        }
        return baos.toByteArray();
    }
}
