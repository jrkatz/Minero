/*
 *     Minero is a minimal budget application
 *     Copyright (C) 2017 Jacob Katz
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jrkatz.minero.data;

/**
 * @Author jrkatz
 * @Date 4/23/2017.
 */

public abstract class BudgetSql {
    public static final String BUDGET_UPDATE_2 =
            "ALTER TABLE budget\n" +
            "ADD COLUMN running_total INTEGER" +
            ";\n" +
            "UPDATE budget\n" +
            "SET running_total = (\n" +
            "    SELECT\n" +
            "        d.total - bp.total\n" +
            "        FROM (\n" +
            "            SELECT\n" +
            "                budget_id,\n" +
            "                SUM(amount) AS total\n" +
            "            FROM debit\n" +
            "            WHERE\n" +
            "                debit.budget_id = budget.id\n" +
            "            GROUP BY\n" +
            "                debit.budget_id\n" +
            "        ) AS d\n" +
            "        JOIN (\n" +
            "            SELECT\n" +
            "                budget_id,\n" +
            "                SUM(distribution) AS total\n" +
            "            FROM budget_period\n" +
            "            WHERE\n" +
            "                budget_period.budget_id = budget.id\n" +
            "                AND budget_period.end < STRFTIME('YYYY-MM-DD', 'now')\n" +
            "            GROUP BY budget_period.budget_id\n" +
            "        ) AS bp ON\n" +
            "            bp.budget_id = d.budget_id\n" +
            ")";

    public static final String BUDGET_UPDATE_7_1 =
            "UPDATE debit\n" +
                    "SET budget_period_id = (\n" +
                    "    SELECT MIN(bp.id)\n" +
                    "    FROM budget_period bp\n" +
                    "    INNER JOIN (\n" +
                    "        SELECT\n" +
                    "            start,\n" +
                    "            end\n" +
                    "        FROM budget_period\n" +
                    "        WHERE budget_period_id = debit.budget_period_id\n" +
                    "    ) bpse ON\n" +
                    "        bpse.start = bp.start\n" +
                    "        AND bpse.end = bp.end\n" +
                    "    WHERE \n" +
                    "        bp.budget_id = debit.budget_id\n" +
                    ");";
    //--look up periods that can now be removed
    public static final String BUDGET_UPDATE_7_2 =
                    "DELETE FROM budget_period\n" +
                    "WHERE id NOT IN (\n" +
                    "    SELECT MIN(id)\n" +
                    "    FROM budget_period\n" +
                    "    GROUP BY\n" +
                    "        start,\n" +
                    "        end,\n" +
                    "        budget_id\n" +
                    ")\n" +
                    ";\n";
    //OK, now update the remaining amount but better this time.
    public static final String BUDGET_UPDATE_7_3 =
                    "UPDATE budget\n" +
                    "SET running_total = (\n" +
                    "    SELECT\n" +
                    "        bp.total - d.total\n" +
                    "    FROM (\n" +
                    "        SELECT\n" +
                    "            budget_id,\n" +
                    "            SUM(amount) AS total\n" +
                    "        FROM debit\n" +
                    "        WHERE\n" +
                    "            debit.budget_id = budget.id\n" +
                    "        GROUP BY\n" +
                    "            debit.budget_id\n" +
                    "    ) AS d\n" +
                    "    JOIN (\n" +
                    "        SELECT\n" +
                    "            budget_id,\n" +
                    "            SUM(distribution) AS total\n" +
                    "        FROM budget_period\n" +
                    "        WHERE\n" +
                    "            budget_period.budget_id = budget.id\n" +
                    "            AND budget_period.id NOT IN (SELECT MAX(id) FROM budget_period GROUP BY budget_id)\n" +
                    "        GROUP BY budget_period.budget_id\n" +
                    "    ) AS bp ON\n" +
                    "        bp.budget_id = d.budget_id\n" +
                    ")\n" +
                    ";";
}
