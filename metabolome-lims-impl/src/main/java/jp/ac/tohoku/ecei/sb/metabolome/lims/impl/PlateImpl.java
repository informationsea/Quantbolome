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
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Plate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by yasu on 15/04/17.
 */
@EqualsAndHashCode(callSuper = false)
@Data @AllArgsConstructor @NoArgsConstructor @DatabaseTable(tableName = "Plate")
public class PlateImpl extends LIMSDataImpl implements Plate {
    @DatabaseField (foreign = true, canBeNull = false, foreignAutoRefresh = true)
    StudyImpl study;

    @DatabaseField
    String name;

    @DatabaseField
    Date dateTime;

    public PlateImpl(Plate plate) {
        super(plate);
        study = new StudyImpl(plate.getStudy());
        name = plate.getName();
        dateTime = plate.getDateTime();
    }

    @Override
    public String toString() {
        return String.format("[%d] %s", getId(), name);
    }
}
