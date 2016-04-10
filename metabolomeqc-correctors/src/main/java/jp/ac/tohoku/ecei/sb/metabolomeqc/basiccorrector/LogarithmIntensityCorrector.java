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

package jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector;

import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import lombok.Getter;
import lombok.Setter;

/**
 * MetabolomeQC
 * Copyright (C) 2014 Yasunobu OKAMURA
 * Created at 14/12/01
 */
public class LogarithmIntensityCorrector extends LoggingIntensityCorrector {
    @Getter @Setter
    private int logBase;

    @Getter @Setter
    private double pseudoCount;

    public LogarithmIntensityCorrector() {
        logBase = 2;
        pseudoCount = 1;
    }

    public LogarithmIntensityCorrector(int logbase) {
        logBase = logbase;
        pseudoCount = 1;
    }

    public LogarithmIntensityCorrector(int logbase, double pseudoCount) {
        logBase = logbase;
        this.pseudoCount = pseudoCount;
    }

    @Override
    public IntensityMatrix doCorrection(IntensityMatrix original) {
        int nrow = original.getSize()[0];
        int ncol = original.getSize()[1];
        IntensityMatrix mim = new IntensityMatrixImpl(nrow, ncol);

        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < ncol; j++) {
                mim.put(i, j, Math.log(original.get(i, j) + pseudoCount)/Math.log(logBase));
            }
        }
        mim.setColumnKeys(original.getColumnKeys());
        mim.setRowKeys(original.getRowKeys());

        return mim;
    }
}
