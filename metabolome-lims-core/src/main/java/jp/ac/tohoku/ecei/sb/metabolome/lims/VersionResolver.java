/*
 *  Quantbolome
 *    Copyright (C) 2016 Yasunobu OKAMURA All Rights Reserved.
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Affero General Public License as
 *    published by the Free Software Foundation, either version 3 of the
 *    License, or (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jp.ac.tohoku.ecei.sb.metabolome.lims;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

public class VersionResolver {

    private static Properties getProperties(Class clazz) {
        Properties properties = new Properties();
        try {
            properties.load(clazz.getResourceAsStream("/META-INF/metabolome-lims/version.properties"));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getGitCommit(Class clazz) {
        return getProperties(clazz).getProperty("git.commit");
    }

    public static String getVersion(Class clazz) {
        return getProperties(clazz).getProperty("version");
    }

    public static String getBuildDate(Class clazz) {
        return getProperties(clazz).getProperty("build.date");
    }
}
