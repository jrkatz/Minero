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

package net.jrkatz.minero;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import net.jrkatz.minero.data.BudgetDbHelper;
import net.jrkatz.minero.data.BudgetPeriodProvider;
import net.jrkatz.minero.data.DebitProvider;

/**
 * @Author jrkatz
 * @Date 2/26/2017.
 */

public class ClearDataPreference extends DialogPreference{
    public ClearDataPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        try (SQLiteDatabase db = new BudgetDbHelper(this.getContext()).getWritableDatabase()) {
            if (positiveResult) {
                DebitProvider.clearDebits(db);
                BudgetPeriodProvider.clearBudgetPeriods(db);
            }
        }
    }
}
