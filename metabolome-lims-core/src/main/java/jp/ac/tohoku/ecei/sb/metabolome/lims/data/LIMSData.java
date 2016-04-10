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

import java.util.Set;
import java.util.UUID;

/**
 * Created by yasu on 15/04/17.
 */
public interface LIMSData {
    /**
     * Get a generated ID
     * @return unique ID
     */
    int getId();

    /**
     * Get an attribute associated to this data and key
     * @param key attribute key
     * @return an associated value
     */
    Object getAttribute(String key);

    /**
     * Set an attribute associated to this data and key
     * @param key attribute key
     * @param value an associated value
     */
    void setAttribute(String key, Object value);

    /**
     * Get a set of attribute keys
     * @return A set of attribute keys
     */
    Set<String> getAttributeKeySet();
}
