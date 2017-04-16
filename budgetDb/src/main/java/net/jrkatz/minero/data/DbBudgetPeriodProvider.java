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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @Author jrkatz
 * @Date 3/1/2017.
 */

public class DbBudgetPeriodProvider extends BudgetPeriodProvider<DbDataContext> {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("YYYY-MM-dd");

    private static BudgetPeriod atCursor(@NonNull final DbDataContext context, @NonNull final Cursor cursor) throws ProviderException{
        final long id = cursor.getLong(0);
        final long budgetId = cursor.getLong(1);
        final long distribution = cursor.getLong(2);
        final LocalDate start = DATE_FORMAT.parseLocalDate(cursor.getString(3));
        final LocalDate end = DATE_FORMAT.parseLocalDate(cursor.getString(4));
        final ImmutableList<Debit> debits = context.getDebitProvider().readDebits(context, id);

        return new BudgetPeriod(id, budgetId, new Period(start, end), distribution, debits);
    }

    @Override
    @Nullable
    public BudgetPeriod getBudgetPeriod(final DbDataContext context,
                                               final long budgetId,
                                               final LocalDate date) throws ProviderException {
        final String dateString = DATE_FORMAT.print(date);
        try(final Cursor cursor = context.getDb().query(
                "budget_period",
                new String[] {"id", "budget_id", "distribution", "start", "end"},
                "budget_id = ? AND start <= ? AND end > ?",
                new String[] {Long.toString(budgetId), dateString, dateString},
                null,
                null,
                null)) {
            if (cursor.moveToFirst()) {
                return atCursor(context, cursor);
            }
            return null;
        }
    }

    @Nullable
    @Override
    public BudgetPeriod getBudgetPeriod(@NonNull DbDataContext context,
                                        long budgetPeriodId) throws ProviderException {
        try(final Cursor cursor = context.getDb().query(
                "budget_period",
                new String[] {"id", "budget_id", "distribution", "start", "end"},
                "id = ?",
                new String[] {Long.toString(budgetPeriodId)},
                null,
                null,
                null)) {
            if (cursor.moveToFirst()) {
                return atCursor(context, cursor);
            }
            return null;
        }
    }

    @NonNull
    @Override
    BudgetPeriod updateBudgetAmount(@NonNull DbDataContext context, long budgetPeriodId, long amount) throws ProviderException{
        ContentValues values = new ContentValues();
        values.put("distribution", amount);
        context.getDb().update("budget_period",
                values,
                "id = ?",
                new String[] {Long.toString(budgetPeriodId)});

        return getBudgetPeriod(context, budgetPeriodId);
    }

    @Override
    @Nullable
    public BudgetPeriod getLatestBudgetPeriod(@NonNull final DbDataContext context,
                                              final long budgetId) throws ProviderException {
        //TODO the query planner might suck and not notice how easy this is. Hacks may be needed.
        try(final Cursor cursor = context.getDb().query(
                "budget_period",
                new String[] {"id", "budget_id", "distribution", "start", "end"},
                "id = ?",
                new String[] {Long.toString(budgetId)},
                null,
                null,
                "start DESC",
                "1")) {
            if (cursor.moveToFirst()) {
                return atCursor(context, cursor);
            }
            return null;
        }
    }

    @Override
    @NonNull
    public BudgetPeriod createBudgetPeriod(@NonNull final DbDataContext context,
                                                     final long budgetId,
                                                     final long distribution,
                                                     @NonNull final Period period) {
        ContentValues values = new ContentValues();
        values.put("budget_id", budgetId);
        values.put("distribution", distribution);
        values.put("start", DATE_FORMAT.print(period.getStart()));
        values.put("end", DATE_FORMAT.print(period.getEnd()));
        long id = context.getDb().insertOrThrow("budget_period", null, values);
        return new BudgetPeriod(id, budgetId, period, distribution, ImmutableList.<Debit>of());
    }

    @Override
    public void clearBudgetPeriods(@NonNull DbDataContext context) {
        context.getDb().delete("budget_period", null, new String[0]);
    }
}