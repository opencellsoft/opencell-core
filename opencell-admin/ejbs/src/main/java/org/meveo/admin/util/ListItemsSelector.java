/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
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
