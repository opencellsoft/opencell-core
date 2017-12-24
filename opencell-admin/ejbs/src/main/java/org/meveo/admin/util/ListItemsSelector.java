/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Keeps multiselect information on server side. This allows to use multiselect
 * with pagination.
 * 
 * @author Ignas Lelys
 * @since Apr 6, 2011
 * 
 * @param <E> enity.
 */
public class ListItemsSelector<E> implements Serializable {

    private static final long serialVersionUID = -1L;

    private Set<E> list = new HashSet<E>();

    private boolean modeAllSelected;

    public ListItemsSelector() {
    }

    public ListItemsSelector(boolean modeAllSelected) {
        this.modeAllSelected = modeAllSelected;
    }

    public void reset() {
        list.clear();
        modeAllSelected = false;
    }

    public void switchMode() {
        list.clear();
        modeAllSelected = !modeAllSelected;
    }

    public void add(E item) {
        this.list.add(item);
    }

    public void remove(E item) {
        this.list.remove(item);
    }

    public void check(E item) {
        if (this.list.contains(item))
            remove(item);
        else
            add(item);
    }

    public boolean isSelected(E item) {
        return (modeAllSelected && !list.contains(item)) || (!modeAllSelected && list.contains(item));
    }

    public Set<E> getList() {
        return list;
    }

    public boolean isModeAllSelected() {
        return modeAllSelected;
    }

    public boolean isEmpty() {
        return !modeAllSelected && list.isEmpty();
    }

    public int getSize() {
        return list.size();
    }
}
