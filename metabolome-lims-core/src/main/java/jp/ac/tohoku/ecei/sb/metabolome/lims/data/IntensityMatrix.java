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

package jp.ac.tohoku.ecei.sb.metabolome.lims.data;

import info.informationsea.dataclustering4j.matrix.LabeledMatrix;

import java.util.List;
import java.util.Map;

/**
 * Created by yasu on 15/06/02.
 */
public interface IntensityMatrix extends LabeledMatrix<Double, Compound, Injection> {
    /**
     * Return a list of normal injections in this matrix
     * @return a list of normal injections
     */
    List<Injection> getNormalInjections();

    /**
     * Return a list of injections with the sample
     * @return a list of injections with the sample
     */
    List<Injection> getInjectionsBySample(Sample sample);

    /**
     * Return a map of QC ID and samples
     * @return a map
     */
    Map<Sample, List<Injection>> getQCInjections();

    /**
     * Return a list of QC IDs that are contained in all runs
     * @return a list of QC IDs
     */
    List<Sample> getGlobalQCSamples();

    /**
     * Classify samples by run
     * @return a map of run and samples
     */
    Map<Plate, List<Injection>> getInjectionsByPlate();
    Map<Plate, Map<Sample.SampleType, List<Injection>>> getInjectionsByPlateAndType();
}
