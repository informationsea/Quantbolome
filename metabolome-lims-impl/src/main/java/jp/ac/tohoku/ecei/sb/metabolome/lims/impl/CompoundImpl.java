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
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Compound;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by yasu on 15/06/01.
 */

@EqualsAndHashCode(callSuper = false)
@Data @AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "Compound")
public class CompoundImpl extends LIMSDataImpl implements Compound {
    @DatabaseField
    private double MZ;

    @DatabaseField
    private double retentionTime;

    @DatabaseField
    private Double neutralMass;

    @DatabaseField
    private int charge;

    public CompoundImpl(Compound compound) {
        super(compound);
        MZ = compound.getMZ();
        retentionTime = compound.getRetentionTime();
        neutralMass = compound.getNeutralMass();
        charge = compound.getCharge();
    }

    @Override
    public String toString() {
        return String.format("M/Z: %f / Retention Time: %f / neutralMass: %f / Charge: %d", MZ, retentionTime, neutralMass, charge);
    }
}
