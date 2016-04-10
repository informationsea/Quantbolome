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
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Injection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by yasu on 15/04/17.
 */
@EqualsAndHashCode(callSuper = false)
@Data @AllArgsConstructor @NoArgsConstructor @DatabaseTable(tableName = "Injection")
public class InjectionImpl extends LIMSDataImpl implements Injection {

    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
    PlateImpl plate;

    @DatabaseField
    String name;

    @DatabaseField(canBeNull = false, unique = true)
    String fileName;

    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
    SampleImpl sample;

    @DatabaseField(unique = true, uniqueIndex = true)
    int runIndex;

    @DatabaseField(canBeNull = false, index = true)
    boolean ignored;

    @DatabaseField(index = true)
    Integer QCIndex;

    public InjectionImpl(Injection injection) {
        super(injection);
        plate = new PlateImpl(injection.getPlate());
        name = injection.getName();
        fileName = injection.getFileName();
        sample = new SampleImpl(injection.getSample());
        runIndex = injection.getRunIndex();
        ignored = injection.isIgnored();
        QCIndex = injection.getQCIndex();
    }

    @Override
    public String toString() {
        return String.format("[%04d] %s - %s", runIndex, sample.getName(), fileName);
    }
}
