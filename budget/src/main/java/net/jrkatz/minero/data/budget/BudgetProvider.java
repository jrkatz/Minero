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

package net.jrkatz.minero.data.budget;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.jrkatz.minero.data.period.MonthlyPeriodDefinition;

/**
 * @Author jrkatz
 * @Date 3/1/2017.
 */
public class BudgetProvider {
    private SharedPreferences mPreferences;
    public BudgetProvider(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Budget getBudget() {
        return new Budget(new MonthlyPeriodDefinition(1),
                Integer.parseInt(mPreferences.getString("monthly_amt", "400")),
                "default");
    }
}
