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
package org.meveo.model.bi;

/**
 * File status enum : the status that a file can have from its upload to its registration in the database
 *
 * @author Abdellatif BARI
 * @since 7.3.0
 */
public enum FileStatusEnum {

    /**
     * indicates that syntactic incorrectness and the file content dosen't respect the configuration template (beanIO or flatWorm)
     */
    BAD_FORMED,
    /**
     * indicates syntactic correctness and the file content respect the configuration template (beanIO or flatWorm)
     * Note : the file is only controlled by the fileFormat validator and not yet validated by the appropriate job.
     */
    WELL_FORMED,
    /**
     * indicates that the file is weel formed and it is validated by the appropriate job (business validation)
     */
    VALID,
    /**
     * indicates that the file is weel formed but it's not valid (it's rejected by the appropriate job (business validation))
     */
    REJECTED,
    /**
     * not implemented
     */
    ARCHIVED
}
