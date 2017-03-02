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

package net.jrkatz.minero.budget.budget;

import android.content.Context;

import net.jrkatz.minero.budget.BudgetDbHelper;
import net.jrkatz.minero.budget.period.MonthlyPeriodDefinition;

/**
 * @Author jrkatz
 * @Date 3/1/2017.
 */
public class BudgetProvider {
    private BudgetDbHelper mDbHelper;
    public BudgetProvider(final Context context) {
        mDbHelper = new BudgetDbHelper(context);
    }

    public Budget getBudget() {
        return  new Budget(new MonthlyPeriodDefinition(1),
                400,
                "default"
        );
    }
}
