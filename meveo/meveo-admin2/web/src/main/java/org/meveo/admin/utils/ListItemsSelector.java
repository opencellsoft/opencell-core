/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.admin.utils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Keeps multiselect information on server side. This allows to use multiselect
 * with pagination.
 * 
 * @author Ignas Lelys
 * @created Apr 6, 2011
 * 
 * @param <E>
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
