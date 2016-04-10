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

package jp.ac.tohoku.ecei.sb.metabolome.lims.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by yasu on 15/06/09.
 */
@Slf4j
public class LIMSDataImplTest {

    LIMSDataImplObj[] objs = new LIMSDataImplObj[]{
            new LIMSDataImplObj(),
            new LIMSDataImplObj(),
            new LIMSDataImplObj(),
            new LIMSDataImplObj()
    };

    @Before
    public void tearUp() {
        for (LIMSDataImplObj o : objs) {
            o.setId(10);
            o.setAttribute("Hello", "World");
            o.setAttribute("OK", "foo");
        }

        objs[2].setAttribute("OK", "bar");
        objs[3].setId(2);

        log.info("Attribute Json {}", objs[0].getAttributeJson());
    }

    @Test
    public void testHashCode() throws Exception {
        Assert.assertEquals(objs[0].hashCode(), objs[1].hashCode());
        Assert.assertNotEquals(objs[0].hashCode(), objs[2].hashCode());
        Assert.assertNotEquals(objs[0].hashCode(), objs[3].hashCode());
        Assert.assertNotEquals(objs[2].hashCode(), objs[3].hashCode());
    }

    @Test
    public void testEquals() throws Exception {
        Assert.assertTrue(objs[0].equals(objs[1]));
        Assert.assertFalse(objs[0].equals(objs[2]));
        Assert.assertFalse(objs[0].equals(objs[3]));
        Assert.assertFalse(objs[2].equals(objs[3]));
        Assert.assertTrue(objs[0].equals(objs[0]));
        Assert.assertFalse(objs[0].equals(new Object()));
    }

    @Test
    public void testAttribute() throws Exception {
        LIMSDataImplObj obj = new LIMSDataImplObj();

        Assert.assertEquals("{}", obj.getAttributeJson());

        obj.setAttributeJson("{\"A\": 12}");
        Assert.assertEquals(12, obj.getAttribute("A"));
        obj.setAttribute("B", 10.4);
        Assert.assertEquals("{\"A\":12,\"B\":10.4}", obj.getAttributeJson());

        Assert.assertNull(obj.getAttribute("hogehoge"));
    }

    public static class LIMSDataImplObj extends LIMSDataImpl {

    }
}