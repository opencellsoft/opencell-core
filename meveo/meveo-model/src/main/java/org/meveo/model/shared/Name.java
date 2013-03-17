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
package org.meveo.model.shared;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;


/**
 * @author Ignas Lelys
 * @created Oct 31, 2010
 * 
 */
@Embeddable
public class Name implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    @ManyToOne()
    @JoinColumn(name = "TITLE_ID")
    private Title title;

    @Column(name = "FIRSTNAME", length = 50)
    @Size(max = 50)
    protected String firstName;

    @Column(name = "LASTNAME", length = 50)
	@Size(max = 50)
    protected String lastName;

    public Name() {
        // this.title = Title.MR;
    }

    public Name(Title title, String firstName, String lastName) {
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return (title != null ? (title.getCode() + " " + (firstName != null ? firstName : "") + (lastName != null ? " "
                + lastName : "")) : "");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
    }
}
