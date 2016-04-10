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

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import jp.ac.tohoku.ecei.sb.metabolome.lims.VersionResolver;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.OperationHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by yasu on 15/06/09.
 */

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "History")
public class OperationHistoryImpl extends LIMSDataImpl implements OperationHistory {
    @DatabaseField
    private String operator;

    @DatabaseField
    private String workerClassName;

    @DatabaseField
    private Date date;

    @DatabaseField
    private String content;

    public OperationHistoryImpl(Class clazz, String content) {
        operator = System.getProperty("user.name");
        workerClassName = clazz.getCanonicalName();
        date = new Date();

        try {
            setAttribute("GIT-COMMIT", VersionResolver.getGitCommit(clazz));
            setAttribute("BUILD-DATE", VersionResolver.getBuildDate(clazz));
            setAttribute("VERSION", VersionResolver.getVersion(clazz));
        } catch (Exception e) {
            // ignore
        }

        this.content = content;
    }

    public OperationHistoryImpl(OperationHistory operationHistory) {
        super(operationHistory);
        operator = operationHistory.getOperator();
        workerClassName = operationHistory.getWorkerClassName();
        date = operationHistory.getDate();
        content = operationHistory.getContent();
    }
}
