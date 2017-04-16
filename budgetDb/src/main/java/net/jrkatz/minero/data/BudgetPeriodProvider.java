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

import com.google.common.collect.ImmutableList;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @Author jrkatz
 * @Date 3/1/2017.
 */

public class BudgetPeriodProvider {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("YYYY-MM-dd");

    public static BudgetPeriod getCurrentBudgetPeriod(SQLiteDatabase db, final long budgetId) {
        BudgetPeriod budgetPeriod = getLatestBudgetPeriod(db, budgetId);
        //if this budgetPeriod is too old it may be necessary to create a new one or _several_
        //new ones.
        final LocalDate now = LocalDate.now();
        if (budgetPeriod == null) {
            final Budget budget = BudgetProvider.getBudget(db, budgetId);
            final PeriodDefinition periodDefinition = budget.getPeriodDefinition();
            final Period period = periodDefinition.periodForDate(now);
            budgetPeriod = createBudgetPeriod(db, budgetId, budget.getDistribution(), period);
        }
        else if (budgetPeriod.getPeriod().getEnd().isBefore(now)) {
            final Budget budget = BudgetProvider.getBudget(db, budgetId);
            final PeriodDefinition periodDefinition = budget.getPeriodDefinition();
            do {
                final Period nextPeriod = periodDefinition.periodForDate(budgetPeriod.getPeriod().getEnd());
                budgetPeriod = createBudgetPeriod(db, budgetId, budget.getDistribution(), nextPeriod);
            } while (budgetPeriod.getPeriod().getEnd().isBefore(now));
        }
        return budgetPeriod;
    }

    private static BudgetPeriod atCursor(final SQLiteDatabase db, @NonNull final Cursor cursor) {
        final long id = cursor.getLong(0);
        final long budgetId = cursor.getLong(1);
        final long distribution = cursor.getLong(2);
        final LocalDate start = DATE_FORMAT.parseLocalDate(cursor.getString(3));
        final LocalDate end = DATE_FORMAT.parseLocalDate(cursor.getString(4));
        final ImmutableList<Debit> debits = DebitProvider.readDebits(db, id);

        return new BudgetPeriod(id, budgetId, new Period(start, end), distribution, debits);
    }

    public static BudgetPeriod getBudgetPeriod(final SQLiteDatabase db, final long budgetId, final LocalDate date) {
        final String dateString = DATE_FORMAT.print(date);
        try(final Cursor cursor = db.query(
                "budget_period",
                new String[] {"id", "budget_id", "distribution", "start", "end"},
                "budget_id = ? AND start <= ? AND end > ?",
                new String[] {Long.toString(budgetId), dateString, dateString},
                null,
                null,
                null)) {
            if (cursor.moveToFirst()) {
                return atCursor(db, cursor);
            }
            return null;
        }
    }

    public static BudgetPeriod getLatestBudgetPeriod(final SQLiteDatabase db,final long budgetId) {
        //TODO the query planner might suck and not notice how easy this is. Hacks may be needed.
        try(final Cursor cursor = db.query(
                "budget_period",
                new String[] {"id", "budget_id", "distribution", "start", "end"},
                "id = ?",
                new String[] {Long.toString(budgetId)},
                null,
                null,
                "start DESC",
                "1")) {
            if (cursor.moveToFirst()) {
                return atCursor(db, cursor);
            }
            return null;
        }
    }

    public static BudgetPeriod createBudgetPeriod(@NonNull final SQLiteDatabase db,
                                                     final long budgetId,
                                                     final long distribution,
                                                     @NonNull final Period period) {
        ContentValues values = new ContentValues();
        values.put("budget_id", budgetId);
        values.put("distribution", distribution);
        values.put("start", DATE_FORMAT.print(period.getStart()));
        values.put("end", DATE_FORMAT.print(period.getEnd()));
        long id = db.insertOrThrow("budget_period", null, values);
        return new BudgetPeriod(id, budgetId, period, distribution, ImmutableList.<Debit>of());
    }

    public static void clearBudgetPeriods(final SQLiteDatabase db) {
        db.delete("budget_period", null, new String[0]);
    }
}