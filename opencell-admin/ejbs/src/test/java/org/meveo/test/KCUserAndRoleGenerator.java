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

package org.meveo.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class KCUserAndRoleGenerator {

    long nbRecords, shift;
    long startTime;

    public static void main(String[] args) {

        try (PrintWriter usersOut = new PrintWriter(new FileOutputStream("C:\\Users\\explo\\Downloads\\users.txt"));) {
            for (int i = 0; i < 10000; i++) {
                usersOut.println(",{\"id\": \"6067f356-7d11-47bb-a2ec-cde41cb9ee1e" + (100000 + i) + "\",\"createdTimestamp\": 1508163776686,\"username\": \"user_" + i
                        + "\",\"enabled\": true,\"totp\": false,\"emailVerified\": true,\"attributes\": {\"locale\": [\"en\"]},\"credentials\": [{\"id\": \"04a04c9d-506f-49d7-8ded-848bdfc08820" + (100000 + i)
                        + "\",\"type\": \"password\",\"createdDate\": 1513859053958,\"secretData\": \"{\\\"value\\\":\\\"Pcd6q8qBTCAlCMlMOpyymthpjXNJPz/dv8RZ/e4xow1n9TryXFTIB9ARkfDE07JuGPid8wCqAjQ4s6GLRmJkEw==\\\",\\\"salt\\\":\\\"bANpIaO59vVGwdhRSUSA1Q==\\\"}\",\"credentialData\": \"{\\\"hashIterations\\\":20000,\\\"algorithm\\\":\\\"pbkdf2\\\"}\"}],\"realmRoles\": [\"test_1\"],\"clientRoles\": {\"opencell-web\": [\"administrateur\"]}}");
            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }

        try (PrintWriter rolesOut = new PrintWriter(new FileOutputStream("C:\\Users\\explo\\Downloads\\roles.txt"));
                PrintWriter policiesOut = new PrintWriter(new FileOutputStream("C:\\Users\\explo\\Downloads\\policies.txt"));) {
            for (int i = 0; i < 30000; i++) {
                rolesOut.println(",{\"id\": \"0e715a52-4254-4de8-a38b-3f5ad2" + (100000 + i) + "\",\"name\": \"test_" + i + "\",\"containerId\": \"opencell\"}");
                policiesOut.println("{\"id\": \"028c6d6f-48fa-43b6-a250-789c2f" + (100000 + i) + "\",\"name\": \"Role test_" + i
                        + "\",\"type\": \"role\",\"logic\": \"POSITIVE\",\"decisionStrategy\": \"UNANIMOUS\",\"config\": {\"roles\": \"[{\\\"id\\\":\\\"test_" + i + "\\\",\\\"required\\\":false}]\"}}");

            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }

        try (PrintWriter resourcesOut = new PrintWriter(new FileOutputStream("C:\\Users\\explo\\Downloads\\resources.txt"));
                PrintWriter permissionsOut = new PrintWriter(new FileOutputStream("C:\\Users\\explo\\Downloads\\permissions.txt"))

        ) {
            for (int i = 0; i < 600000; i++) {

                int roleId = i > 300000 ? i - 300000 : 0;

                resourcesOut.println(",{\"name\": \"SE:Seller:-3:SELLER_" + i + ":READ\",\"owner\": {\"name\": \"opencell.admin\"},\"_id\": \"43f6b49e-570f-42ce-adac-cf982a" + (100000 + i) + "\"}");
                permissionsOut.println(",{ \"id\": \"bbdea3d4-ae8b-40bf-ba04-f1294b" + (100000 + i) + "\",\"name\": \"SE:Seller:-3:SELLER_" + i
                        + ":READ\", \"type\": \"resource\",\"logic\": \"POSITIVE\", \"decisionStrategy\": \"AFFIRMATIVE\", \"config\": {\"resources\": \"[\\\"SE:Seller:-3:SELLER_" + i
                        + ":READ\\\"]\",\"applyPolicies\": \"[\\\"Role test_" + roleId + "\\\"]\"}}");
            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }

    }
}