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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.Date;

/**
 * @Author jrkatz
 * @Date 3/1/2017.
 */

public class DbDebitProvider extends DebitProvider<DbDataContext> {
    private static final String[] COLUMNS = new String[]{"id", "budget_id", "budget_period_id", "amount", "description", "time", "zone", "parent_id"};
    private static final String TABLE_NAME = "debit";

    @Override
    @NonNull
    public ImmutableList<Debit> readDebits(@NonNull final DbDataContext context,
                                                     final long budgetPeriodId) {
        ImmutableList.Builder<Debit> debits = ImmutableList.builder();
        try(Cursor cursor = debitQuery(context.getDb(),
                "budget_period_id = ?",
                Long.toString(budgetPeriodId)
        )) {
            while (cursor.moveToNext()) {
                debits.add(atCursor(cursor));
            }
        }
        return debits.build();
    }

    @Nullable
    @Override
    public Debit getDebit(@NonNull DbDataContext context, long debitId) {
        try (Cursor cursor = debitQuery(context.getDb(),
                "id = ?",
                Long.toString(debitId))) {
            if (cursor.moveToFirst()) {
                return atCursor(cursor);
            }
        }

        return null;
    }

    @NonNull
    Debit createDebit(@NonNull final DbDataContext context,
                             final long budgetId,
                             final long budgetPeriodId,
                             final int amount,
                             final String description,
                             final DateTime time,
                             final Long parentId) {
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("budget_id", budgetId);
        values.put("budget_period_id", budgetPeriodId);
        values.put("description", description);
        //without the explicit Long boxing this is boxed & truncated into an Integer.
        values.put("time", Long.valueOf(time.toDate().getTime()));
        values.put("zone", time.getZone().getID());
        values.put("parent_id", parentId);
        long id = context.getDb().insertOrThrow("debit", null, values);
        return new Debit(id, budgetId, budgetPeriodId, amount, description, time, parentId);
    }

    @Override
    public void clearDebits(@NonNull final DbDataContext context) {
        context.getDb().delete(TABLE_NAME, null, new String[0]);
    }

    Cursor debitQuery(@NonNull final SQLiteDatabase db,
                      @NonNull final String where,
                      @NonNull final String ... whereArgs) {
        return db.query(TABLE_NAME,
                COLUMNS,
                where,
                whereArgs,
                null, null, null);
    }

    Debit atCursor(@NonNull final Cursor cursor) {
        return new Debit(cursor.getLong(0),
                cursor.getLong(1),
                cursor.getLong(2),
                cursor.getInt(3),
                cursor.getString(4),
                new DateTime(cursor.getLong(5))
                        .withZone(DateTimeZone.forID(cursor.getString(6))),
                cursor.isNull(7) ? null : cursor.getLong(7));
    }
}
