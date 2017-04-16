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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableList;

/**
 * @Author jrkatz
 * @Date 3/1/2017.
 */
public class BudgetProvider {
    private static final String [] COLUMNS = new String[]{"id", "period_definition",  "distribution", "name"};
    private static final String TABLE_NAME = "budget";

    @NonNull
    private static Cursor budgetQuery(SQLiteDatabase db, String where, String ... whereArgs) {
        return db.query(TABLE_NAME, COLUMNS, where, whereArgs, null, null, null);
    }

    public static Budget getDefaultBudget(SQLiteDatabase db) {
        try(Cursor c = db.query(TABLE_NAME, COLUMNS, null, null, null, null, "id ASC", "1")) {
            if (c.moveToFirst()) {
                return atCursor(c);
            }
            else {
                //just make some crappy budget and call it square.
                return createBudget(db, new MonthlyPeriodDefinition(1), 500, "default");
            }
        }
    }

    private static Budget atCursor(final Cursor cursor) {
        try {
            return new Budget(cursor.getLong(0), PeriodDefinition.deserialize(cursor.getString(1)), cursor.getLong(2), cursor.getString(3));
        } catch (PeriodDefinition.PeriodParseException e) {
            return new Budget(cursor.getLong(0), new MonthlyPeriodDefinition(1), cursor.getLong(2), cursor.getString(3));
        }
    }

    @NonNull
    private static ImmutableList<Budget> drainCursor(@NonNull final Cursor cursor) {
        final ImmutableList.Builder<Budget> builder = ImmutableList.builder();
        while(cursor.moveToNext()) {
            builder.add(atCursor(cursor));
        }
        return builder.build();
    }

    @NonNull
    public static ImmutableList<Budget> readBudgets(SQLiteDatabase db) {
        try(Cursor cursor = budgetQuery(db, "")) {
            return drainCursor(cursor);
        }
    }

    @Nullable
    public static Budget getBudget(final SQLiteDatabase db, final long id) {
        try (Cursor cursor = budgetQuery(db, "id = ?", Long.toString(id))) {
            if (cursor.moveToFirst()) {
                return atCursor(cursor);
            }
        }
        return null;
    }

    @NonNull
    public static Budget createBudget(@NonNull final SQLiteDatabase db,
                                         @NonNull final PeriodDefinition periodDefinition,
                                         final long distribution,
                                         @NonNull final String name) {
        ContentValues values = new ContentValues();
        values.put("distribution", distribution);
        values.put("name", name);
        values.put("period_definition", periodDefinition.serialize());

        long id = db.insertOrThrow("budget", null, values);
        return new Budget(id, periodDefinition, distribution, name);
    }
}