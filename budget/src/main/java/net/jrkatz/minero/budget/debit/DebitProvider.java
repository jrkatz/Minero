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

package net.jrkatz.minero.budget.debit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.google.common.collect.ImmutableList;

import net.jrkatz.minero.budget.BudgetDbHelper;
import net.jrkatz.minero.budget.period.Period;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.Date;

/**
 * @Author jrkatz
 * @Date 3/1/2017.
 */

public class DebitProvider {
    private static final String[] COLUMNS = new String[]{"id", "amount", "description", "time"};
    private static final String TABLE_NAME = "debit";

    private final BudgetDbHelper mDbHelper;
    public DebitProvider(Context context) {
        mDbHelper = new BudgetDbHelper(context);
    }

    private static Cursor debitQuery(SQLiteDatabase db, String where, String ... whereArgs) {
        return db.query(TABLE_NAME,
                COLUMNS,
                where,
                whereArgs,
                null, null, null);
    }

    private static Debit atCursor(final Cursor cursor) {
        return new Debit(cursor.getLong(0),
                cursor.getInt(1),
                cursor.getString(2),
                LocalDateTime.fromDateFields(new Date(cursor.getLong(3))));
    }

    public ImmutableList<Debit> readDebits(final Period period) {
        return readDebits(mDbHelper.getReadableDatabase(), period);
    }

    protected static ImmutableList<Debit> readDebits(final SQLiteDatabase db,
                                                     final LocalDateTime start,
                                                     final LocalDateTime end) {
        ImmutableList.Builder<Debit> debits = ImmutableList.builder();
        try(Cursor cursor = debitQuery(db,
                "time >= ? AND time < ?",
                Long.toString(start.toDate().getTime()),
                Long.toString(end.toDate().getTime())
        )) {
            while (cursor.moveToNext()) {
                debits.add(atCursor(cursor));
            }
        }
        return debits.build();
    }

    protected static ImmutableList<Debit> readDebits(final SQLiteDatabase db, final Period period) {
        final LocalTime startOfDay = new LocalTime(0,0);
        return readDebits(db,
                period.getStart().toLocalDateTime(startOfDay),
                period.getEnd().toLocalDateTime(startOfDay)
        );
    }

    public Debit createDebit(final int amount, final String description, final LocalDateTime time) {
        return createDebit(mDbHelper.getWritableDatabase(), amount, description, time);
    }
    protected static Debit createDebit(final SQLiteDatabase db,
                                       final long amount,
                                       final String description,
                                       final LocalDateTime time) throws SQLiteException {
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("description", description);
        //without the explicit Long boxing this is boxed & truncated into an Integer.
        values.put("time", Long.valueOf(time.toDate().getTime()));
        long id = db.insertOrThrow("debit", null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        return new Debit(id, amount, description, time);
    }

    public void clearDebits() {
        clearDebits(mDbHelper.getWritableDatabase());
    }
    protected static void clearDebits(final SQLiteDatabase db) {
        db.beginTransaction();
        db.delete(TABLE_NAME, null, new String[0]);
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
