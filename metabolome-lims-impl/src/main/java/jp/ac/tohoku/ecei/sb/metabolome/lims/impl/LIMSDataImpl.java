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

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.LIMSData;
import lombok.*;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by yasu on 15/04/17.
 */
@NoArgsConstructor
public abstract class LIMSDataImpl implements LIMSData {
    @DatabaseField(generatedId = true) @Getter @Setter
    private int id;

    @DatabaseField(dataType = DataType.LONG_STRING) @Getter
    private String attributeJson = "{}";

    public LIMSDataImpl(LIMSData limsData) {
        id = limsData.getId();

        JSONObject jsonObject = new JSONObject();
        for (String one : limsData.getAttributeKeySet()) {
            jsonObject.put(one, limsData.getAttribute(one));
        }
        attributeJson = jsonObject.toString();
    }

    public void setAttributeJson(String data) {
        attributeJson = data;
    }

    @Override
    public Object getAttribute(String key) {
        JSONObject jsonObject = new JSONObject(attributeJson);
        if (jsonObject.keySet().contains(key))
            return jsonObject.get(key);
        return null;
    }

    public void setAttribute(String key, Object value) {
        JSONObject jsonObject = new JSONObject(attributeJson);
        jsonObject.put(key, value);
        attributeJson = jsonObject.toString();
    }

    @Override
    public Set<String> getAttributeKeySet() {
        JSONObject jsonObject = new JSONObject(attributeJson);
        return jsonObject.keySet();
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = (result*PRIME) + id;
        result = (result*PRIME) + attributeJson.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (!(obj instanceof LIMSDataImpl)) return false;
        LIMSDataImpl limsData = (LIMSDataImpl) obj;
        if (id != limsData.id)  return false;
        if (!attributeJson.equals(limsData.attributeJson)) return false;
        return true;
    }
}
