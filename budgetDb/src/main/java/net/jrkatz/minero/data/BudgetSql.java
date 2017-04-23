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
}
