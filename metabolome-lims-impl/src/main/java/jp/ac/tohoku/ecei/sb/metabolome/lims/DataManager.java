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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.table.TableUtils;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Compound;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Injection;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.OperationHistory;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by yasu on 15/06/01.
 */

public class DataManager {
    @Getter
    private Dao<InjectionImpl, Integer> injections;

    @Getter
    private Dao<PlateImpl, Integer> plates;

    @Getter
    private Dao<StudyImpl, Integer> studies;

    @Getter
    private Dao<SampleImpl, Integer> samples;

    @Getter
    private Dao<CompoundImpl, Integer> compounds;

    @Getter
    private Dao<OperationHistoryImpl, Integer> operationHistories;

    /**
     * The matrix of intensities.
     *
     * The key of a column is FileName of injections.
     * The key of a row is Compound ID such as "COMPOUND1". The suffix number of ID corresponds to ID of compounds.
     *
     * Warning: This data is not stored in the database. You have to load manually.
     */
    @Getter
    private IntensityMatrixImpl intensityMatrix;

    protected JdbcConnectionSource connectionSource = null;

    public DataManager() throws SQLException {
        this("jdbc:h2:mem:");
    }

    public DataManager(String jdbcUrl) throws SQLException {
        try {
            connectionSource = new JdbcConnectionSource(jdbcUrl, "sa", "");

            injections = DaoManager.createDao(connectionSource, InjectionImpl.class);
            plates = DaoManager.createDao(connectionSource, PlateImpl.class);
            studies = DaoManager.createDao(connectionSource, StudyImpl.class);
            samples = DaoManager.createDao(connectionSource, SampleImpl.class);
            compounds = DaoManager.createDao(connectionSource, CompoundImpl.class);
            operationHistories = DaoManager.createDao(connectionSource, OperationHistoryImpl.class);

            Class[] classList = new Class[]{InjectionImpl.class, PlateImpl.class, StudyImpl.class, SampleImpl.class, CompoundImpl.class, OperationHistoryImpl.class};
            for (Class clazz: classList)
                TableUtils.createTableIfNotExists(connectionSource, clazz);

        } catch (SQLException e) {
            if (connectionSource != null)
                connectionSource.close();
            throw e;
        }
    }

    public void close() throws SQLException {
        if (connectionSource != null)
            connectionSource.close();
    }

    public void setIntensityMatrix(IntensityMatrixImpl intensityMatrix) throws SQLException {
        this.intensityMatrix = intensityMatrix;
        List<Injection> newInjections = intensityMatrix.getColumnKeys();
        List<InjectionImpl> oldInjections = injections.queryForAll();

        for (InjectionImpl old : oldInjections) {
            if (!newInjections.contains(old))
                injections.delete(old);
        }

        for (Injection one : newInjections) {
            injections.update((InjectionImpl) one);
        }

        List<Compound> newCompounds = intensityMatrix.getRowKeys();
        List<CompoundImpl> oldCompounds = compounds.queryForAll();
        for (CompoundImpl old : oldCompounds)
            if (!newCompounds.contains(old))
                compounds.delete(old);
        for (Compound one : newCompounds)
            compounds.update((CompoundImpl) one);

    }

    public void addOperationHistory(OperationHistoryImpl operationHistory) throws SQLException {
        operationHistories.create(operationHistory);
    }
}
