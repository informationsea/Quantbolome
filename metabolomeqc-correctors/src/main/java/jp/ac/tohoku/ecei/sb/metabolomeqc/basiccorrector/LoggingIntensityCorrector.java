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

import jp.ac.tohoku.ecei.sb.metabolome.lims.DataManager;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.OperationHistoryImpl;
import jp.ac.tohoku.ecei.sb.metabolomeqc.IntensityCorrector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * metabolomeqc-next
 * Copyright (C) 2015 OKAMURA Yasunobu
 * Created on 15/07/26.
 */
public abstract class LoggingIntensityCorrector implements IntensityCorrector {

    public void doCorrection(DataManager dataManager) throws SQLException {
        IntensityMatrix corrected = doCorrection(dataManager.getIntensityMatrix());
        dataManager.setIntensityMatrix((IntensityMatrixImpl) corrected);
        OperationHistoryImpl operationHistory = new OperationHistoryImpl(getClass(), getClass().getSimpleName());

        for (Method method : getClass().getMethods()) {
            if (method.getName().equals("getClass")) continue;
            if (!method.getName().startsWith("get")) continue;
            if (method.getParameterCount() != 0) continue;

            try {
                operationHistory.setAttribute(method.getName().substring(3), method.invoke(this).toString());
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        dataManager.getOperationHistories().create(operationHistory);
    }
}
