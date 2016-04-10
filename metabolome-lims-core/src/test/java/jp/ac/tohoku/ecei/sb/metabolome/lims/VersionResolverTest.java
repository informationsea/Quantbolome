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

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by yasu on 15/04/01.
 */
public class VersionResolverTest {

    @Test
    public void testGetGitCommit() throws Exception {
        Assert.assertNotNull(VersionResolver.getGitCommit(VersionResolver.class));
    }

    @Test
    public void testGetVersion() throws Exception {
        Assert.assertNotNull(VersionResolver.getVersion(VersionResolver.class));
    }

    @Test
    public void testGetBuildDate() throws Exception {
        Assert.assertNotNull(VersionResolver.getBuildDate(VersionResolver.class));
    }
}