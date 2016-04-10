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
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Sample;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Created by yasu on 15/04/17.
 */
@EqualsAndHashCode(callSuper = false)
@Data @AllArgsConstructor @NoArgsConstructor @DatabaseTable(tableName = "Sample")
public class SampleImpl extends LIMSDataImpl implements Sample {
    @DatabaseField(canBeNull = false)
    private SampleType sampleType;

    @DatabaseField(canBeNull = false)
    private UUID uuid;

    @DatabaseField
    private String name;

    public SampleImpl(Sample sample) {
        super(sample);
        sampleType = sample.getSampleType();
        uuid = sample.getUuid();
        name = sample.getName();
    }

    @Override
    public String toString() {
        return String.format("[%04d] %s", getId(), name);
    }
}
