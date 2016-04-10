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

import jp.ac.tohoku.ecei.sb.metabolome.lims.CSVDataLoader;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Compound;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Plate;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Sample;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Study;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

/**
 * Created by yasu on 15/06/09.
 */
public class CopyConstructorTest {

    Compound compound;
    StudyImpl study;
    SampleImpl sample;
    PlateImpl plate;
    InjectionImpl injection;

    @Before
    public void setUp() throws Exception {
        compound = new CompoundImpl(10, 20, null, 1);
        study = new StudyImpl("Hello");
        sample = new SampleImpl(Sample.SampleType.QC, UUID.fromString("20ED0E82-1866-4CD7-BF5B-78726E884186"), "foo");
        plate = new PlateImpl(study, "foo", CSVDataLoader.DATE_FORMAT.parse("2015-06-02 16:50:36.058"));
        injection = new InjectionImpl(plate, "hoge", "bar", sample, 1, false, 1);
    }

    @Test
    public void testCompoundImpl() throws Exception{
        Assert.assertEquals(new CompoundImpl(10, 20, null, 1), new CompoundImpl(compound));
    }

    @Test
    public void testStudyImpl() throws Exception {
        Assert.assertEquals(new StudyImpl("Hello"), new StudyImpl(study));
    }

    @Test
    public void testSampleImpl() {
        Assert.assertEquals(new SampleImpl(Sample.SampleType.QC, UUID.fromString("20ED0E82-1866-4CD7-BF5B-78726E884186"), "foo"),
                new SampleImpl(sample));
    }

    @Test
    public void testPlateImpl() throws Exception{
        Assert.assertEquals(new PlateImpl(study, "foo", CSVDataLoader.DATE_FORMAT.parse("2015-06-02 16:50:36.058")),
                new PlateImpl(plate));
    }

    @Test
    public void testInjectionImpl() throws Exception {
        Assert.assertEquals(new InjectionImpl(plate, "hoge", "bar", sample, 1, false, 1),
                new InjectionImpl(injection));
    }
}
